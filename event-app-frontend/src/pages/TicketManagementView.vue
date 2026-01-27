<script setup>
import { ref, onMounted } from 'vue'
import { http } from '@/api/http'
import MoleculePageHeader from '@/components/molecules/MoleculePageHeader.vue'
import MoleculeDataTable from '@/components/molecules/MoleculeDataTable.vue'
import MoleculeConfirmModal from '@/components/molecules/MoleculeConfirmModal.vue'
import AtomAlert from '@/components/atoms/AtomAlert.vue'
import AtomSpinner from '@/components/atoms/AtomSpinner.vue'

const tickets = ref([])
const loading = ref(false)
const error = ref('')

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'event', label: 'Event' },
  { key: 'user', label: 'User' },
  { key: 'date', label: 'Date' },
  { key: 'status', label: 'Status' },
  { key: 'actions', label: 'Actions' },
]

// Modal State
const showDeleteModal = ref(false)
const ticketToDelete = ref(null)

const fetchTickets = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await http.get('/tickets')
    tickets.value = response.data
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load tickets.'
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('de-DE', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// --- STATUS UPDATE LOGIK ---
const updateStatus = async (ticket, newStatus) => {
  const originalStatus = ticket.status
  // Optimistisches Update im UI
  ticket.status = newStatus

  try {
    // Payload vorbereiten: Backend braucht alle @NotNull Felder
    const payload = {
      id: ticket.id,
      eventId: ticket.eventId,
      userId: ticket.userId,
      purchaseDate: ticket.purchaseDate,
      status: newStatus, // Der neue Status (ACTIVE, USED, CANCELLED)
    }

    await http.put(`/tickets/${ticket.id}`, payload)
  } catch (err) {
    console.error('Status update failed', err)
    // Bei Fehler zurücksetzen
    ticket.status = originalStatus
    alert('Failed to update ticket status.')
  }
}

// --- DELETE LOGIK ---
const promptDelete = (ticket) => {
  ticketToDelete.value = ticket
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!ticketToDelete.value) return

  try {
    await http.delete(`/tickets/${ticketToDelete.value.id}`)
    tickets.value = tickets.value.filter((t) => t.id !== ticketToDelete.value.id)
    showDeleteModal.value = false
    ticketToDelete.value = null
  } catch (err) {
    console.error(err)
    alert('Failed to delete ticket.')
  }
}

onMounted(() => {
  fetchTickets()
})
</script>

<template>
  <div class="p-6 min-h-screen bg-base-200">
    <div class="max-w-7xl mx-auto">
      <MoleculePageHeader title="Ticket Management" @refresh="fetchTickets" />

      <AtomAlert v-if="error" type="error" :message="error" />

      <MoleculeDataTable
        :columns="columns"
        :items="tickets"
        :loading="loading"
        empty-message="No tickets found."
      >
        <template #rows="{ items }">
          <tr v-for="ticket in items" :key="ticket.id" class="hover">
            <td class="text-center font-mono text-xs opacity-50">
              {{ ticket.id.substring(0, 8) }}...
            </td>

            <td class="font-bold text-center">
              {{ ticket.eventName || ticket.eventId }}
            </td>

            <td class="text-center">
              {{ ticket.userName || ticket.userId }}
            </td>

            <td class="text-center">
              {{ formatDate(ticket.purchaseDate) }}
            </td>

            <td class="text-center">
              <select
                class="select select-bordered select-sm w-32"
                :value="ticket.status"
                @change="updateStatus(ticket, $event.target.value)"
                :class="{
                  'select-success': ticket.status === 'ACTIVE',
                  'select-info': ticket.status === 'USED',
                  'select-error': ticket.status === 'CANCELLED',
                }"
              >
                <option value="ACTIVE">ACTIVE</option>
                <option value="USED">USED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </td>

            <td class="text-center">
              <button class="btn btn-ghost btn-xs text-error" @click="promptDelete(ticket)">
                Delete
              </button>
            </td>
          </tr>
        </template>
      </MoleculeDataTable>

      <AtomSpinner v-if="loading" class="mt-4" />
    </div>

    <MoleculeConfirmModal
      :show="showDeleteModal"
      title="Delete Ticket"
      message="Are you sure you want to delete this ticket? This action cannot be undone."
      confirm-label="Delete Ticket"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

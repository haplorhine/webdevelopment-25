<script setup>
import { ref, onMounted } from 'vue'
import { http } from '@/api/http'

const tickets = ref([])
const loading = ref(false)
const error = ref('')

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
    minute: '2-digit'
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
        status: newStatus // Der neue Status (ACTIVE, USED, CANCELLED)
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
    tickets.value = tickets.value.filter(t => t.id !== ticketToDelete.value.id)
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
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">Ticket Management</h1>
        <button class="btn btn-primary btn-sm" @click="fetchTickets">Refresh</button>
      </div>

      <div v-if="error" class="alert alert-error mb-4 shadow-lg">
        <span>{{ error }}</span>
      </div>

      <div class="overflow-x-auto bg-base-100 shadow-xl rounded-box">
        <table class="table w-full">
          <thead>
            <tr>
              <th class="text-center">ID</th>
              <th class="text-center">Event</th>
              <th class="text-center">User</th>
              <th class="text-center">Date</th>
              <th class="text-center">Status</th>
              <th class="text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ticket in tickets" :key="ticket.id" class="hover">
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
                    'select-error': ticket.status === 'CANCELLED'
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
            <tr v-if="tickets.length === 0 && !loading">
              <td colspan="6" class="text-center py-8 text-gray-500">No tickets found.</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="loading" class="flex justify-center mt-4">
        <span class="loading loading-spinner loading-lg"></span>
      </div>
    </div>

    <dialog class="modal" :class="{ 'modal-open': showDeleteModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg text-error mb-4">Delete Ticket</h3>
        
        <p class="py-4">
          Are you sure you want to delete this ticket?
          <br/>
          <span class="text-sm opacity-70">This action cannot be undone.</span>
        </p>
        
        <div class="modal-action">
          <button class="btn" @click="showDeleteModal = false">Cancel</button>
          <button class="btn btn-error" @click="confirmDelete">Delete Ticket</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
         <button @click="showDeleteModal = false">close</button>
      </form>
    </dialog>

  </div>
</template>
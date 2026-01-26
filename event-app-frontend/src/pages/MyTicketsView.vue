<script setup>
import { ref, onMounted } from 'vue'
import { http } from '@/api/http'
import { useUserStore } from '@/state/user'
import MoleculePageHeader from '@/components/molecules/MoleculePageHeader.vue'
import MoleculeDataTable from '@/components/molecules/MoleculeDataTable.vue'
import AtomAlert from '@/components/atoms/AtomAlert.vue'
import AtomBadge from '@/components/atoms/AtomBadge.vue'
import AtomSpinner from '@/components/atoms/AtomSpinner.vue'

const tickets = ref([])
const loading = ref(false)
const error = ref('')
const userStore = useUserStore()

const columns = [
  { key: 'id', label: 'Ticket ID' },
  { key: 'event', label: 'Event' },
  { key: 'purchaseDate', label: 'Purchase Date' },
  { key: 'status', label: 'Status' },
]

const fetchMyTickets = async () => {
  loading.value = true
  error.value = ''
  try {
    const userId = userStore.userId
    if (!userId) {
      throw new Error('User ID not found')
    }
    const response = await http.get(`/users/${userId}/tickets`)
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

onMounted(() => {
  fetchMyTickets()
})
</script>

<template>
  <div class="p-6 min-h-screen bg-base-200">
    <div class="max-w-7xl mx-auto">
      <MoleculePageHeader title="My Tickets" @refresh="fetchMyTickets" />

      <AtomAlert v-if="error" type="error" :message="error" />

      <MoleculeDataTable
        :columns="columns"
        :items="tickets"
        :loading="loading"
        empty-message="You haven't bought any tickets yet."
      >
        <template #rows="{ items }">
          <tr v-for="ticket in items" :key="ticket.id" class="hover">
            <td class="text-center font-mono text-xs opacity-50">
              {{ ticket.id.substring(0, 8) }}...
            </td>
            <td class="font-bold text-center">
              {{ ticket.eventName || 'Unknown Event' }}
            </td>
            <td class="text-center">
              {{ formatDate(ticket.purchaseDate) }}
            </td>
            <td class="text-center">
              <AtomBadge
                :variant="
                  ticket.status === 'ACTIVE'
                    ? 'success'
                    : ticket.status === 'USED'
                      ? 'info'
                      : 'error'
                "
                size="sm"
              >
                {{ ticket.status }}
              </AtomBadge>
            </td>
          </tr>
        </template>
      </MoleculeDataTable>

      <AtomSpinner v-if="loading" class="mt-4" />
    </div>
  </div>
</template>

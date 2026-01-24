<script setup>
import { ref, onMounted } from 'vue'
import { http } from '@/api/http'

const tickets = ref([])
const loading = ref(false)
const error = ref('')

const fetchMyTickets = async () => {
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

onMounted(() => {
  fetchMyTickets()
})
</script>

<template>
  <div class="p-6 min-h-screen bg-base-200">
    <div class="max-w-7xl mx-auto">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">My Tickets</h1>
        <button class="btn btn-primary btn-sm" @click="fetchMyTickets">Refresh</button>
      </div>

      <div v-if="error" class="alert alert-error mb-4 shadow-lg">
        <span>{{ error }}</span>
      </div>

      <div class="overflow-x-auto bg-base-100 shadow-xl rounded-box">
        <table class="table w-full">
          <thead>
            <tr>
              <th class="text-center">Ticket ID</th>
              <th class="text-center">Event</th>
              <th class="text-center">Purchase Date</th>
              <th class="text-center">Status</th>
              </tr>
          </thead>
          <tbody>
            <tr v-for="ticket in tickets" :key="ticket.id" class="hover">
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
                <span :class="{
                  'badge badge-sm': true,
                  'badge-success': ticket.status === 'ACTIVE',
                  'badge-info': ticket.status === 'USED',
                  'badge-error': ticket.status === 'CANCELLED'
                }">{{ ticket.status }}</span>
              </td>
            </tr>
            <tr v-if="tickets.length === 0 && !loading">
              <td colspan="4" class="text-center py-8 text-gray-500">You haven't bought any tickets yet.</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="loading" class="flex justify-center mt-4">
        <span class="loading loading-spinner loading-lg"></span>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { http } from '@/api/http'
import { jwtDecode } from 'jwt-decode'
import AtomButton from '@/components/atoms/AtomButton.vue'
import LabeledInput from '@/components/molecules/LabeledInput.vue'
import MoleculeFieldset from '@/components/molecules/MoleculeFieldset.vue'

const route = useRoute()
const event = ref(null)
const hostName = ref('Loading...')
const ticketAmount = ref(1)
const loading = ref(false)
const message = ref('')
const isError = ref(false)

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('de-DE', {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}

onMounted(async () => {
  try {
    const response = await http.get(`/events/${route.params.id}`)
    event.value = response.data

    if (event.value.hostId) {
      try {
        const userResponse = await http.get(`/users/${event.value.hostId}`)
        hostName.value = userResponse.data.username
      } catch (error) {
        console.warn('Could not fetch host details (likely permission restricted):', error)
        hostName.value = 'Unknown Host'
      }
    } else {
      hostName.value = 'Unknown Host'
    }

  } catch (error) {
    console.error('Failed to fetch event details:', error)
    message.value = 'Event not found or failed to load.'
    isError.value = true
  }
})

const buyTickets = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    message.value = 'Please login to buy tickets.'
    isError.value = true
    return
  }

  let userId = null
  try {
    const decoded = jwtDecode(token)
    userId = decoded.sub 
  } catch (e) {
    message.value = 'Invalid session. Please login again.'
    isError.value = true
    return
  }

  loading.value = true
  message.value = ''
  isError.value = false

  try {
    const promises = []
    for (let i = 0; i < ticketAmount.value; i++) {
      const ticketDto = {
        eventId: event.value.id,
        userId: userId,
        purchaseDate: new Date().toISOString(),
        status: 'ACTIVE'
      }
      promises.push(http.post('/tickets', ticketDto))
    }

    await Promise.all(promises)
    
    message.value = `Successfully purchased ${ticketAmount.value} ticket(s)!`
    isError.value = false
  } catch (error) {
    console.error(error)
    message.value = 'Purchase failed. Please try again.'
    isError.value = true
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-base-200 py-8 px-4">
    <div v-if="event" class="max-w-6xl mx-auto">
      
      <div class="w-full h-64 md:h-96 rounded-2xl overflow-hidden shadow-2xl mb-8 relative">
        <img
          :src="event.imageURL"
          class="w-full h-full object-cover"
          alt="Event Image"
        />
        <div class="absolute top-4 right-4">
           <span class="badge badge-lg badge-primary font-bold">{{ event.category }}</span>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <div class="lg:col-span-2 space-y-6">
          
          <div>
            <h1 class="text-4xl md:text-5xl font-extrabold text-base-content">{{ event.title }}</h1>
            <p class="text-lg text-base-content/70 mt-2 flex items-center gap-2">
              Hosted by <span class="font-semibold text-primary">{{ hostName }}</span>
            </p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 bg-base-100 p-6 rounded-xl shadow-sm">
            
            <div class="flex items-start gap-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-primary">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1 1 15 0Z" />
              </svg>
              <div>
                <p class="font-bold text-sm uppercase text-base-content/60">Location</p>
                <p class="font-medium">{{ event.location }}</p>
              </div>
            </div>

             <div class="flex items-start gap-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-primary">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5" />
              </svg>
              <div>
                <p class="font-bold text-sm uppercase text-base-content/60">Date & Time</p>
                <p class="font-medium">{{ formatDate(event.startDate) }}</p>
                <p class="text-sm text-base-content/60">to {{ formatDate(event.endDate) }}</p>
              </div>
            </div>

            <div class="flex items-start gap-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-primary">
                <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 0 0 3.741-.479 3 3 0 0 0-4.682-2.72m.94 3.198.001.031c0 .225-.012.447-.037.666A11.944 11.944 0 0 1 12 21c-2.17 0-4.207-.576-5.963-1.584A6.062 6.062 0 0 1 6 18.719m12 0a5.971 5.971 0 0 0-.941-3.197m0 0A5.995 5.995 0 0 0 12 12.75a5.995 5.995 0 0 0-5.058 5.223m0 0a9.093 9.093 0 0 0-3.746.477 3 3 0 0 0 4.682 2.72m.943-3.198a5.971 5.971 0 0 0-.941-3.197M12 12.75a6 6 0 1 1 0-12 6 6 0 0 1 0 12Z" />
              </svg>
              <div>
                <p class="font-bold text-sm uppercase text-base-content/60">Participants</p>
                <p class="font-medium">Max: {{ event.maxParticipants || 'Unlimited' }}</p>
              </div>
            </div>

             <div class="flex items-start gap-3">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-primary">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
              </svg>
              <div>
                <p class="font-bold text-sm uppercase text-base-content/60">Sales Period</p>
                <p class="text-sm">Start: {{ formatDate(event.salesStart) }}</p>
                <p class="text-sm">End: {{ formatDate(event.salesEnd) }}</p>
              </div>
            </div>

          </div>

          <div class="bg-base-100 p-6 rounded-xl shadow-sm">
             <h2 class="text-2xl font-bold mb-4">About this Event</h2>
             <p class="whitespace-pre-line text-lg leading-relaxed">{{ event.description }}</p>
          </div>

        </div>

        <div class="lg:col-span-1">
          <div class="card bg-base-100 shadow-xl sticky top-24">
            <div class="card-body">
              <h2 class="card-title text-2xl mb-2">Tickets</h2>
              
              <div class="flex justify-between items-center mb-6">
                <span class="text-base-content/70">Price per Ticket</span>
                <span class="text-3xl font-bold text-primary">{{ event.ticketPrice ? event.ticketPrice.toFixed(2) + ' €' : 'Free' }}</span>
              </div>
              
              <div class="divider my-0"></div>

              <form @submit.prevent="buyTickets" class="mt-4">
                <MoleculeFieldset>
                  <LabeledInput
                    v-model="ticketAmount"
                    type="number"
                    input-class="w-full input-lg"
                    placeholder="1"
                    label="How many tickets?"
                    id="ticket-amount"
                    name="amount"
                    min="1"
                  />
                  
                  <div v-if="message" :class="['alert mt-4 shadow-sm text-sm', isError ? 'alert-error' : 'alert-success']">
                    <span>{{ message }}</span>
                  </div>

                  <AtomButton 
                    class="btn-primary btn-lg w-full mt-6 shadow-lg" 
                    :label="loading ? 'Processing...' : 'Buy Tickets Now'" 
                    type="submit" 
                    :disabled="loading"
                  />
                </MoleculeFieldset>
              </form>
              <p class="text-xs text-center mt-4 text-base-content/50">Secure payment processing</p>
            </div>
          </div>
        </div>

      </div>
    </div>

    <div v-else class="flex justify-center items-center min-h-[50vh]">
      <span class="loading loading-spinner loading-lg text-primary"></span>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { http } from '@/api/http'
import { RouterLink } from 'vue-router'
import { useUserStore } from '@/state/user'

const events = ref([])
const showOwnEvents = ref(false)
const loading = ref(false)
const userStore = useUserStore()

const getImageUrl = (imageId) => {
  return `http://localhost:8080/images/${imageId}`
}

const filteredEvents = computed(() => {
  if (userStore.role === 'HOST' && showOwnEvents.value) {
    return events.value.filter((e) => e.hostId === userStore.userId)
  }
  return events.value
})

const fetchEvents = async () => {
  loading.value = true
  try {
    const response = await http.get('/events')
    events.value = response.data
  } catch (error) {
    console.error('Failed to fetch events:', error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  userStore.loadFromStorage()
  await fetchEvents()
})
</script>

<template>
  <div class="p-6">
    <h1 class="text-4xl font-bold mb-6">Events</h1>

    <div v-if="userStore.role === 'HOST'" class="mb-6 flex items-center gap-4">
      <label class="cursor-pointer flex items-center gap-2">
        <input type="checkbox" class="checkbox checkbox-primary" v-model="showOwnEvents" />
        <span class="font-medium">Show only my events</span>
      </label>
      <button class="btn btn-sm btn-outline" @click="fetchEvents" :disabled="loading">
        Refresh
      </button>
    </div>

    <div v-if="loading" class="flex justify-center my-8">
      <span class="loading loading-spinner loading-lg"></span>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="event in filteredEvents" :key="event.id">
        <div class="card bg-base-100 shadow-xl h-full flex flex-col">
          <figure>
            <img
              v-if="event.imageId"
              :src="getImageUrl(event.imageId)"
              :alt="event.title"
              class="w-full h-48 object-cover"
            />
            <div
              v-else
              class="w-full h-48 bg-base-200 flex items-center justify-center text-base-content/30"
            >
              <span class="text-5xl">📸❌</span>
            </div>
          </figure>
          <div class="card-body grow">
            <div class="badge badge-primary mb-2">{{ event.category }}</div>
            <h2 class="card-title">{{ event.title }}</h2>
            <p>{{ event.description }}</p>
            <div class="card-actions justify-end mt-4">
              <RouterLink :to="`/events/${event.id}`" class="btn btn-primary">
                Tickets kaufen
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { http } from '@/api/http'
import { RouterLink } from 'vue-router'

const events = ref([])

onMounted(async () => {
  try {
    const response = await http.get('/events')
    events.value = response.data
  } catch (error) {
    console.error('Failed to fetch events:', error)
  }
})
</script>

<template>
  <div class="p-6">
    <h1 class="text-4xl font-bold mb-6">Events</h1>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="event in events" :key="event.id">
        <div class="card bg-base-100 shadow-xl h-full flex flex-col">
          <figure>
            <img
              :src="event.imageURL"
              :alt="event.title"
              class="w-full h-48 object-cover"
            />
          </figure>
          <div class="card-body flex-grow">
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

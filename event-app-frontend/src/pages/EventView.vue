<script setup>
import { ref } from 'vue'

//static events
const events = ref([
  {
    id: 1,
    title: "Summer Music Festival",
    category: "Music",
    image: new URL('../assets/images/music-festival.jpg', import.meta.url).href,
    description: "Join us for a fun-filled music festival with live bands and DJs all day long!"
  },
  {
    id: 2,
    title: "Art Exhibition",
    category: "Art",
    image: new URL('../assets/images/art-exhibition.jpg', import.meta.url).href,
    description: "Explore the latest art pieces from local and international artists."
  },
  {
    id: 3,
    title: "Tech Conference 2025",
    category: "Technology",
    image: new URL('../assets/images/tech_conference.jpg', import.meta.url).href,
    description: "Learn about the newest trends in technology and network with industry leaders."
  },
  {
    id: 4,
    title: "Cooking Workshop",
    category: "Food",
    image: new URL('../assets/images/cooking-workshop.jpeg', import.meta.url).href,
    description: "Hands-on cooking workshop for beginners and experienced cooks."
  }
])
</script>

<template>
  <div class="p-6">
    <h1 class="text-4xl font-bold mb-6">Events</h1>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="event in events" :key="event.id">
        <div class="card bg-base-100 shadow-xl">
          <figure>
            <img :src="event.image" :alt="event.title" class="w-full h-48 object-cover" />
          </figure>
          <div class="card-body">
            <div class="badge badge-primary mb-2">{{ event.category }}</div>
            <h2 class="card-title">{{ event.title }}</h2>
            <p>{{ event.description }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

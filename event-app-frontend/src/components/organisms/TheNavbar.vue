<script setup>
import { RouterLink, useRouter } from 'vue-router'
import LoginButton from '../molecules/LoginButton.vue'
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/state/user'

const props = defineProps({
  links: {
    type: Array,
    default: () => [],
  },
})

const userStore = useUserStore()
const router = useRouter()

const isLoggedIn = computed(() => userStore.isAuthenticated)

const getImageUrl = (imageId) => {
  if (!imageId) return null
  return `http://localhost:8080/images/${imageId}`
}

const displayedLinks = computed(() => {
  if (userStore.isAdmin) {
    return [
      { to: '/events', label: 'Events' },
      { to: '/event-management', label: 'Event Management' },
      { to: '/user-management', label: 'User Management' },
      { to: '/ticket-management', label: 'Ticket Management' },
    ]
  }

  let currentLinks = [...props.links]

  if (isLoggedIn.value) {
    currentLinks = currentLinks.filter((link) => link.label !== 'Register')
  }

  if (userStore.isHost) {
    currentLinks.push({ to: '/event-management', label: 'Event Management' })
    currentLinks.push({ to: '/create-event', label: 'Create Event' })
  }

  return currentLinks
})

const logout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.loadFromStorage()
})
</script>
<template>
  <div class="navbar bg-base-100 shadow-sm">
    <div class="navbar-start">
      <div class="dropdown">
        <div tabindex="0" role="button" class="btn btn-ghost lg:hidden">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 6h16M4 12h8m-8 6h16"
            />
          </svg>
        </div>
        <ul
          tabindex="-1"
          class="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
        >
          <li v-for="link in displayedLinks" :key="link.to">
            <RouterLink :to="link.to">{{ link.label }}</RouterLink>
          </li>
        </ul>
      </div>
      <a class="btn btn-ghost text-xl">Event App</a>
    </div>
    <div class="navbar-center hidden lg:flex">
      <ul class="menu menu-horizontal px-1">
        <li v-for="link in displayedLinks" :key="link.to">
          <RouterLink :to="link.to">{{ link.label }}</RouterLink>
        </li>
      </ul>
    </div>
    <div class="navbar-end flex gap-2">
      <div v-if="isLoggedIn" class="dropdown dropdown-end">
        <div tabindex="0" role="button" class="btn btn-ghost btn-circle avatar">
          <div class="w-10 rounded-full">
            <img
              v-if="userStore.imageId"
              :src="getImageUrl(userStore.imageId)"
              alt="User Profile"
              class="object-cover w-full h-full"
            />
            <img
              v-else
              alt="Tailwind CSS Navbar component"
              src="https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp"
            />
          </div>
        </div>
        <ul
          tabindex="-1"
          class="menu menu-sm dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow"
        >
          <li>
            <RouterLink to="/profile" class="justify-between">
              Profile
              <span class="badge">Customize</span>
            </RouterLink>
          </li>
          <li>
            <RouterLink to="/my-tickets" class="justify-between"> My Tickets </RouterLink>
          </li>
          <li><a @click="logout">Logout</a></li>
        </ul>
      </div>
      <RouterLink v-else to="/login">
        <LoginButton />
      </RouterLink>
    </div>
  </div>
</template>

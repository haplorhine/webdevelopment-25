<script setup>
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import { useUserStore } from '@/state/user'
import CreateEventHero from '@/components/organisms/CreateEventHero.vue'

const router = useRouter()
const userStore = useUserStore()

onMounted(() => {
  userStore.loadFromStorage()
  if (userStore.role !== 'HOST') {
    router.replace('/')
  }
})
</script>

<template>
  <CreateEventHero />
</template>

<style scoped></style>

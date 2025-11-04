<script setup>
import TheNavbar from './components/organisms/TheNavbar.vue'
import TheFooter from './components/organisms/TheFooter.vue'

const navLinks = [
  { to: '/', label: 'Home' },
  { to: '/about', label: 'About' },
  { to: '/register', label: 'register' },
]
</script>

<template>
  <div>
    <TheNavbar :links="navLinks" />
    <main>
      <RouterView />
    </main>
    <TheFooter />
  </div>
</template>

<style scoped></style>

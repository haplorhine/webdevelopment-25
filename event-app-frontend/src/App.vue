<script setup>
import TheFooter from './components/organisms/TheFooter.vue'
import TheFooter from './components/organisms/TheFooter.vue'
import TheNavbar from './components/organisms/TheNavbar.vue'

const navLinks = [
  { to: '/', label: 'Home' },
  { to: '/about', label: 'About' },
  { to: '/login', label: 'Login' },
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

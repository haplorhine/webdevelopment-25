<script setup>
import MainFooter from './components/organisms/MainFooter.vue'
import TheNavbar from './components/organisms/TheNavbar.vue'

const navLinks = [
  { to: '/', label: 'Home' },
  { to: '/about', label: 'About' },
  { to: '/login', label: 'Login' },
]
</script>

<template>
  <TheNavbar :links="navLinks" />
  <main>
    <RouterView />
  </main>
  <MainFooter />
</template>

<style scoped></style>

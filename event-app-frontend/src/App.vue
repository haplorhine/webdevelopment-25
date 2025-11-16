<script setup>
import TheNavbar from './components/organisms/TheNavbar.vue'
import TheFooter from './components/organisms/TheFooter.vue'

const navLinks = [
  { to: '/', label: 'Home' },
  { to: '/about', label: 'About' },
  { to: '/imprint', label: 'Imprint' },
  { to: '/help', label: 'Help' },
  { to: '/register', label: 'Register' },
]
</script>

<template>
  <div data-theme="cupcake">
    <TheNavbar :links="navLinks" />
    <main>
      <RouterView />
    </main>
    <TheFooter />
  </div>
</template>

<style scoped></style>

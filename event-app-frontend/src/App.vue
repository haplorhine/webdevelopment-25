<script setup>
import TheNavbar from './components/organisms/TheNavbar.vue'
</script>

<template>
  <TheNavbar />
  <nav>
    <ul>
      <li>
        <RouterLink to="/">Home</RouterLink>
      </li>
      <li>
        <RouterLink to="/about">About</RouterLink>
      </li>
      <li>
        <RouterLink to="/login">Login</RouterLink>
      </li>
    </ul>
  </nav>
  <main>
    <RouterView />
  </main>
  <footer>This is the footer</footer>
</template>

<style scoped></style>

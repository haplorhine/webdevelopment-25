<script setup>
import TheHero from '@/components/organisms/TheHero.vue'

const heading = 'This is the best event page ever!'
const text =
  'Enjoy participating or hosting an unforgettable event!'
</script>

<template>
  <TheHero :heading="heading" :text="text" data-theme="cupcake" />
</template>
<style scoped></style>

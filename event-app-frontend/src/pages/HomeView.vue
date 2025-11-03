<script setup>
import TheHero from '@/components/organisms/TheHero.vue'

const heading = 'This is the best event page ever!'
const text =
  'Provident cupiditate voluptatem et in. Quaerat fugiat ut assumenda excepturi exercitationem quasi. In deleniti eaque aut repudiandae et a id nisi.'
</script>

<template>
  <TheHero :heading="heading" :text="text" data-theme="cupcake" />
</template>
<style scoped></style>

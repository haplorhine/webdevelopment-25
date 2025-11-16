<script setup>
defineProps(['heading', 'text'])
</script>
<template>
  <div class="hero bg-base-200 min-h-screen">
    <div class="hero-content text-center">
      <div class="max-w-2xl d-flex">
        <h1 class="text-7xl font-bold">{{ heading }}</h1>
        <p class="py-6">{{ text }}</p>
        <button class="btn btn-primary">Get Started</button>
      </div>
    </div>
  </div>
</template>

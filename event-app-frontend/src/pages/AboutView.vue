<script setup>
console.log('hello about')
</script>

<template>
  <!-- Full background section -->
  <section
    class="relative min-h-screen flex items-center bg-cover bg-center bg-no-repeat"
    style="background-image: url('/images/about-bg.jpg')"
  >
    <!-- Overlay for readability -->
    <div class="absolute inset-0 bg-black/40"></div>

    <!-- CONTENT -->
    <div class="relative z-10 max-w-3xl mx-auto px-6 text-white space-y-6">

      <h1 class="text-5xl md:text-6xl font-extrabold drop-shadow-lg">
        About Our Event Platform
      </h1>

      <p class="text-lg md:text-xl leading-relaxed">
        We are an innovative event platform where people can discover, explore,
        and take part in different events happening around them. Whether you're
        looking for concerts, workshops, festivals, exhibitions, or exclusive
        parties — we help you find the experiences that truly matter to you.
      </p>

      <p class="text-lg md:text-xl leading-relaxed">
        Our mission is simple: connect people with unforgettable moments.
        From community gatherings to large-scale shows, we make events easier
        to find, share, and enjoy — all in one place.
      </p>

    </div>
  </section>
</template>

<style scoped>
/* No extra CSS needed thanks to Tailwind */
</style>


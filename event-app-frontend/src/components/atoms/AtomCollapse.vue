<script setup>
defineProps({
  title: String,
})
</script>

<template>
  <div class="collapse bg-base-100 border border-base-300">
    <input type="checkbox" />
    <div class="collapse-title font-semibold">
      {{ title }}
    </div>
    <div class="collapse-content text-sm">
      <slot></slot>
    </div>
  </div>
</template>

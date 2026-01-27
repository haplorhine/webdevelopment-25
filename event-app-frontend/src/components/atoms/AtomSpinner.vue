<script setup>
defineProps({
  size: {
    type: String,
    default: 'lg',
    validator: (value) => ['xs', 'sm', 'md', 'lg'].includes(value),
  },
})
</script>

<template>
  <div class="flex justify-center">
    <span :class="['loading loading-spinner', `loading-${size}`]"></span>
  </div>
</template>

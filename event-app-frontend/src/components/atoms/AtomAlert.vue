<script setup>
defineProps({
  type: {
    type: String,
    default: 'info',
    validator: (value) => ['info', 'success', 'warning', 'error'].includes(value),
  },
  message: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div :class="['alert', `alert-${type}`, 'mb-4 shadow-lg']">
    <span>{{ message }}</span>
  </div>
</template>

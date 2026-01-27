<script setup>
defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (value) =>
      ['primary', 'secondary', 'success', 'error', 'info', 'warning', 'ghost'].includes(value),
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['xs', 'sm', 'md', 'lg'].includes(value),
  },
})
</script>

<template>
  <span :class="['badge', `badge-${variant}`, size !== 'md' && `badge-${size}`]">
    <slot></slot>
  </span>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  showRefresh: {
    type: Boolean,
    default: true,
  },
})

defineEmits(['refresh'])
</script>

<template>
  <div class="flex justify-between items-center mb-6">
    <h1 class="text-3xl font-bold">{{ title }}</h1>
    <div class="flex gap-2">
      <slot name="actions"></slot>
      <button v-if="showRefresh" class="btn btn-primary btn-sm" @click="$emit('refresh')">
        Refresh
      </button>
    </div>
  </div>
</template>

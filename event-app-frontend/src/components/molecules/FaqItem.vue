<script setup>
import AtomCollapse from '@/components/atoms/AtomCollapse.vue'

defineProps({
  question: String,
  answer: String
})
</script>

<template>
  <AtomCollapse :title="question">
    {{ answer }}
  </AtomCollapse>
</template>

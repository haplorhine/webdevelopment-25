<script setup>
import FaqItem from '@/components/molecules/FaqItem.vue'

const faqs = [
  {
    question: "How do I create an account?",
    answer: "Click the 'Sign Up' button in the top right corner and follow the registration process."
  },
  {
    question: "How do I buy tickets?",
    answer: "Open an event and click 'Buy Ticket'. You need to be registered to buy tickets."
  },
  {
    question: "How can I create my own event?",
    answer: "In your profile choose 'Create Event'. This is only possible if you have registered your account as a host and not a user."
  }
]
</script>

<template>
  <div class="space-y-4">
    <FaqItem
      v-for="f in faqs"
      :key="f.question"
      :question="f.question"
      :answer="f.answer"
    />
  </div>
</template>

<template>
  <div class="mb-4">
    <AtomHeading text="Contact" />
    <AtomParagraph>Email: <Link href="mailto:info@awesome.com">info@awesome.com</Link></AtomParagraph>
    <AtomParagraph>Phone: <Link href="tel:+4312345678">+43 1 234 5678</Link></AtomParagraph>
  </div>
</template>

<script setup>
import AtomHeading from '@/components/atoms/AtomHeading.vue'
import AtomParagraph from '@/components/atoms/AtomParagraph.vue'
import Link from '@/components/atoms/AtomLink.vue'
</script>
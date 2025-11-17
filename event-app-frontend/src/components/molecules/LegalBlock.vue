<template>
  <div class="mb-4">
    <AtomHeading text="Legal Information" />
    <AtomParagraph>Managing Directors: Anna, Fritz & Markus</AtomParagraph>
    <AtomParagraph>Company Registration Number: FN 123456a</AtomParagraph>
    <AtomParagraph>VAT-ID: ATU12345678</AtomParagraph>
    <AtomParagraph>Website: <Link href="https://www.awesomeevents.com">www.awesomeevents.com</Link></AtomParagraph>
  </div>
</template>

<script setup>
import AtomHeading from '@/components/atoms/AtomHeading.vue'
import AtomParagraph from '@/components/atoms/AtomParagraph.vue'
import Link from '@/components/atoms/AtomLink.vue'
</script>
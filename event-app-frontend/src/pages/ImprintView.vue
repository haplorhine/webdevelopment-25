<template>
  <div class="max-w-3xl mx-auto p-6">
    <AtomTitle>Imprint</AtomTitle>
    
    <AddressBlock />
    <ContactBlock />
    <LegalBlock />
  </div>
</template>

<script setup>
import AtomTitle from '@/components/atoms/AtomTitle.vue';
import AddressBlock from '@/components/molecules/AddressBlock.vue'
import ContactBlock from '@/components/molecules/ContactBlock.vue'
import LegalBlock from '@/components/molecules/LegalBlock.vue'
</script>
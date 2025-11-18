<script setup>
import AtomInput from '../atoms/AtomInput.vue'
import AtomLabel from '../atoms/AtomLabel.vue'

const props = defineProps(['type', 'inputClass', 'placeholder', 'label', 'id', 'name'])
const model = defineModel()
</script>
<template>
  <AtomLabel :for="id">{{ label }}</AtomLabel>
  <AtomInput
    :type
    :class="props.inputClass"
    :placeholder
    :id
    :name
    v-model="model"
  />
</template>

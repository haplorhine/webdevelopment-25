<script setup>
import AtomButton from '../atoms/AtomButton.vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <dialog class="modal" :open="props.show">
    <div class="modal-box">
      <slot />
      <div class="modal-action">
        <form method="dialog">
          <AtomButton label="close" />
        </form>
      </div>
    </div>
  </dialog>
</template>

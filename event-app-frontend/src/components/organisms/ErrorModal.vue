<script setup>
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <dialog v-if="props.show" class="modal" open>
    <div class="modal-box">
      <slot />
      <div class="modal-action">
        <form method="dialog">
          <button class="btn">close</button>
        </form>
      </div>
    </div>
  </dialog>
</template>

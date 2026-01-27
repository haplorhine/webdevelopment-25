<template>
  <dialog class="modal" :class="{ 'modal-open': show }">
    <div class="modal-box" :class="maxWidth">
      <h3 class="font-bold text-lg mb-4">{{ title }}</h3>

      <div v-if="successMessage" class="alert alert-success text-sm mb-4">
        <span>{{ successMessage }}</span>
      </div>
      <div v-if="errorMessage" class="alert alert-error text-sm mb-4">
        <span>{{ errorMessage }}</span>
      </div>

      <form @submit.prevent="$emit('submit')">
        <slot></slot>
      </form>
    </div>
    <form method="dialog" class="modal-backdrop">
      <button @click="$emit('close')" :disabled="disabled">close</button>
    </form>
  </dialog>
</template>

<script>
export default {
  name: 'EditModal',
  props: {
    show: {
      type: Boolean,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
    successMessage: {
      type: String,
      default: '',
    },
    errorMessage: {
      type: String,
      default: '',
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    maxWidth: {
      type: String,
      default: '',
    },
  },
}
</script>

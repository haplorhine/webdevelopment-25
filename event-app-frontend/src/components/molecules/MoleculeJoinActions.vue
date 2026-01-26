<template>
  <div class="join">
    <button v-if="canEdit" class="btn btn-ghost btn-xs join-item" @click="$emit('edit', item)">
      Edit
    </button>
    <button
      v-if="canDelete"
      class="btn btn-ghost btn-xs text-error join-item"
      @click="$emit('delete', item)"
    >
      Delete
    </button>
  </div>
</template>

<script>
export default {
  name: 'MoleculeJoinActions',
  props: {
    item: {
      type: Object,
      required: true,
    },
    canEdit: {
      type: Boolean,
      default: true,
    },
    canDelete: {
      type: Boolean,
      default: true,
    },
  },
}
</script>

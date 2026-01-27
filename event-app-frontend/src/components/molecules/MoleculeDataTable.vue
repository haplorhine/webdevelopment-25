<script setup>
defineProps({
  columns: {
    type: Array,
    required: true,
  },
  items: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  emptyMessage: {
    type: String,
    default: 'No items found.',
  },
})
</script>

<template>
  <div class="overflow-x-auto bg-base-100 shadow-xl rounded-box">
    <table class="table w-full">
      <thead>
        <tr>
          <th v-for="col in columns" :key="col.key" class="text-center">
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <slot name="rows" :items="items"></slot>
        <tr v-if="items.length === 0 && !loading">
          <td :colspan="columns.length" class="text-center py-8 text-gray-500">
            {{ emptyMessage }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

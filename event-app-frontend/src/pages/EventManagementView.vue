<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import * as yup from 'yup'
import { http } from '@/api/http'
import { useUserStore } from '@/state/user'
import LabeledInput from '@/components/molecules/LabeledInput.vue'
import MoleculeFieldset from '@/components/molecules/MoleculeFieldset.vue'
import MoleculePageHeader from '@/components/molecules/MoleculePageHeader.vue'
import MoleculeDataTable from '@/components/molecules/MoleculeDataTable.vue'
import MoleculeConfirmModal from '@/components/molecules/MoleculeConfirmModal.vue'
import EditModal from '@/components/organisms/EditModal.vue'
import AtomButton from '@/components/atoms/AtomButton.vue'
import MoleculeJoinActions from '@/components/molecules/MoleculeJoinActions.vue'
import AtomAlert from '@/components/atoms/AtomAlert.vue'
import AtomBadge from '@/components/atoms/AtomBadge.vue'
import AtomSpinner from '@/components/atoms/AtomSpinner.vue'

const events = ref([])
const userStore = useUserStore()

const filteredEvents = computed(() => {
  if (userStore.role === 'HOST') {
    return events.value.filter((e) => e.hostId === userStore.userId)
  }
  return events.value
})
const loading = ref(false)
const error = ref('')

const columns = [
  { key: 'image', label: 'Image' },
  { key: 'event', label: 'Event' },
  { key: 'category', label: 'Category' },
  { key: 'dates', label: 'Dates' },
  { key: 'price', label: 'Price' },
  { key: 'actions', label: 'Actions' },
]

// --- STATES ---
const showEditModal = ref(false)
const showDeleteModal = ref(false)

const updating = ref(false)
const successMessage = ref('')
const errorMessage = ref('')
const errors = reactive({})

const eventToDelete = ref(null)

const getImageUrl = (imageId) => {
  if (!imageId) return null
  const baseUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
  return `${baseUrl}/images/${imageId}`
}

const imageFile = ref(null)
const handleFileChange = (e) => {
  const files = e.target.files
  if (files.length > 0) {
    imageFile.value = files[0]
  }
}

const form = reactive({
  id: '',
  title: '',
  category: 'CHARITY',
  description: '',
  location: '',
  startDate: '',
  endDate: '',
  maxParticipants: 0,
  salesStart: '',
  salesEnd: '',
  ticketPrice: 0,
  imageId: null,
})

const categories = [
  'CHARITY',
  'COMMUNITY',
  'COMPETITION',
  'CONCERT',
  'CONFERENCE',
  'EXHIBITION',
  'FAIR',
  'FAMILY',
  'FESTIVAL',
  'FITNESS_CLASS',
  'FOOD_FEST',
  'LECTURE',
  'MEETUP',
  'MOVIE',
  'NETWORKING',
  'OUTDOOR_ACTIVITY',
  'RELIGIOUS',
  'SEMINAR',
  'SPORT_EVENT',
  'THEATER',
  'TOURNAMENT',
  'TRAINING',
  'WEBINAR',
  'WORKSHOP',
]

// Validation
const schema = yup.object({
  title: yup.string().required('Title is required'),
  category: yup.string().required('Category is required'),
  description: yup.string().required('Description is required'),
  location: yup.string().required('Location is required'),
  salesStart: yup.date().typeError('Invalid date').required('Required'),
  salesEnd: yup
    .date()
    .typeError('Invalid date')
    .required('Required')
    .min(yup.ref('salesStart'), 'Sales end must be after sales start'),
  startDate: yup
    .date()
    .typeError('Invalid date')
    .required('Required')
    .min(yup.ref('salesEnd'), 'Event start must be after sales end'),
  endDate: yup
    .date()
    .typeError('Invalid date')
    .required('Required')
    .min(yup.ref('startDate'), 'Event end must be after start date'),
  maxParticipants: yup
    .number()
    .transform((v) => (isNaN(v) ? undefined : v))
    .min(1)
    .required('Required'),
  ticketPrice: yup
    .number()
    .transform((v) => (isNaN(v) ? undefined : v))
    .min(0)
    .required('Required'),
})

const fetchEvents = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await http.get('/events')
    events.value = response.data
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load events. Please try again later.'
  } finally {
    loading.value = false
  }
}

const promptDelete = (event) => {
  eventToDelete.value = event
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!eventToDelete.value) return

  try {
    await http.delete(`/events/${eventToDelete.value.id}`)
    events.value = events.value.filter((e) => e.id !== eventToDelete.value.id)
    showDeleteModal.value = false
    eventToDelete.value = null
  } catch (err) {
    console.error(err)
    alert('Failed to delete event. It might be linked to existing tickets.')
  }
}

const openEditModal = (event) => {
  successMessage.value = ''
  errorMessage.value = ''
  Object.keys(errors).forEach((key) => delete errors[key])
  imageFile.value = null

  form.id = event.id
  form.title = event.title
  form.category = event.category
  form.description = event.description
  form.location = event.location
  form.startDate = formatDateForInput(event.startDate)
  form.endDate = formatDateForInput(event.endDate)
  form.maxParticipants = event.maxParticipants
  form.salesStart = formatDateForInput(event.salesStart)
  form.salesEnd = formatDateForInput(event.salesEnd)
  form.ticketPrice = event.ticketPrice
  form.imageId = event.imageId
  form.hostId = event.hostId

  showEditModal.value = true
}

const formatDateForInput = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('de-DE', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const saveEvent = async () => {
  updating.value = true
  errorMessage.value = ''
  successMessage.value = ''
  Object.keys(errors).forEach((key) => delete errors[key])

  try {
    await schema.validate(form, { abortEarly: false })

    if (imageFile.value) {
      const formData = new FormData()
      formData.append('file', imageFile.value)
      const imageResponse = await http.post('/images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      form.imageId = imageResponse.data.id
    }

    const payload = {
      id: form.id,
      hostId: form.hostId,
      title: form.title,
      category: form.category,
      description: form.description,
      location: form.location,
      startDate: form.startDate,
      endDate: form.endDate,
      maxParticipants: form.maxParticipants,
      salesStart: form.salesStart,
      salesEnd: form.salesEnd,
      ticketPrice: form.ticketPrice,
      imageId: form.imageId,
    }
    console.log('Payload to be sent:', payload)
    await http.put(`/events/${form.id}`, payload)

    successMessage.value = 'Changes saved successfully!'
    await fetchEvents()

    setTimeout(() => {
      showEditModal.value = false
    }, 1500)
  } catch (err) {
    console.error(err)
    if (err.inner) {
      err.inner.forEach((e) => {
        errors[e.path] = e.message
      })
    } else {
      errorMessage.value = 'Update failed. ' + (err.response?.data?.message || err.message)
    }
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  fetchEvents()
})
</script>

<template>
  <div class="p-6 min-h-screen bg-base-200">
    <div class="max-w-7xl mx-auto">
      <MoleculePageHeader title="Event Management" @refresh="fetchEvents" />

      <AtomAlert v-if="error" type="error" :message="error" />

      <MoleculeDataTable
        :columns="columns"
        :items="filteredEvents"
        :loading="loading"
        empty-message="No events found."
      >
        <template #rows="{ items }">
          <tr v-for="event in items" :key="event.id" class="hover">
            <td class="text-center">
              <div class="avatar">
                <div class="w-16 rounded">
                  <img v-if="event.imageId" :src="getImageUrl(event.imageId)" :alt="event.title" />
                  <div v-else class="w-full h-full bg-base-200 flex items-center justify-center">
                    <span class="text-2xl">📸</span>
                  </div>
                </div>
              </div>
            </td>
            <td class="text-center">
              <div class="font-bold">{{ event.title }}</div>
              <div class="text-xs opacity-50">{{ event.location }}</div>
            </td>
            <td class="text-center">
              <AtomBadge variant="primary" size="sm">
                {{ event.category }}
              </AtomBadge>
            </td>
            <td class="text-center">
              <div class="text-xs">
                <div>{{ formatDate(event.startDate) }}</div>
                <div class="opacity-50">to {{ formatDate(event.endDate) }}</div>
              </div>
            </td>
            <td class="text-center font-bold">
              {{ event.ticketPrice ? event.ticketPrice.toFixed(2) + ' €' : 'Free' }}
            </td>
            <td class="text-center">
              <MoleculeJoinActions :item="event" @edit="openEditModal" @delete="promptDelete" />
            </td>
          </tr>
        </template>
      </MoleculeDataTable>

      <AtomSpinner v-if="loading" class="mt-4" />
    </div>

    <EditModal
      :show="showEditModal"
      title="Edit Event"
      :success-message="successMessage"
      :error-message="errorMessage"
      :disabled="updating"
      max-width="max-w-2xl"
      @submit="saveEvent"
      @close="showEditModal = false"
    >
      <MoleculeFieldset>
        <div class="flex flex-col items-center gap-4 mb-6">
          <div class="avatar">
            <div class="w-32 rounded-lg ring ring-primary ring-offset-base-100 ring-offset-2">
              <img v-if="form.imageId" :src="getImageUrl(form.imageId)" />
              <div
                v-else
                class="bg-gray-200 w-full h-full flex items-center justify-center font-bold text-4xl"
              >
                📸
              </div>
            </div>
          </div>
          <input
            type="file"
            class="file-input file-input-bordered file-input-sm w-full max-w-xs"
            accept="image/*"
            @change="handleFileChange"
          />
        </div>

        <div>
          <LabeledInput
            v-model="form.title"
            type="text"
            :input-class="['w-full', { 'input-error': errors.title }]"
            placeholder="Event Title"
            label="Title"
            id="edit-title"
            name="title"
          />
          <span v-if="errors.title" class="text-error text-xs mt-1 ml-1">{{ errors.title }}</span>
        </div>

        <div class="form-control w-full">
          <label class="label"><span class="label-text font-semibold">Category</span></label>
          <select
            v-model="form.category"
            :class="['select select-bordered w-full', { 'select-error': errors.category }]"
          >
            <option v-for="cat in categories" :key="cat" :value="cat">
              {{ cat.replace('_', ' ') }}
            </option>
          </select>
          <span v-if="errors.category" class="text-error text-xs mt-1">{{ errors.category }}</span>
        </div>

        <div>
          <LabeledInput
            v-model="form.description"
            type="text"
            :input-class="['w-full', { 'input-error': errors.description }]"
            placeholder="Description"
            label="Description"
            id="edit-description"
            name="description"
          />
          <span v-if="errors.description" class="text-error text-xs mt-1 ml-1">{{
            errors.description
          }}</span>
        </div>

        <div>
          <LabeledInput
            v-model="form.location"
            type="text"
            :input-class="['w-full', { 'input-error': errors.location }]"
            placeholder="Location"
            label="Location"
            id="edit-location"
            name="location"
          />
          <span v-if="errors.location" class="text-error text-xs mt-1 ml-1">{{
            errors.location
          }}</span>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <LabeledInput
              v-model="form.salesStart"
              type="datetime-local"
              :input-class="['w-full', { 'input-error': errors.salesStart }]"
              label="Sales Start"
              id="edit-salesStart"
              name="salesStart"
            />
            <span v-if="errors.salesStart" class="text-error text-xs mt-1 ml-1">{{
              errors.salesStart
            }}</span>
          </div>
          <div>
            <LabeledInput
              v-model="form.salesEnd"
              type="datetime-local"
              :input-class="['w-full', { 'input-error': errors.salesEnd }]"
              label="Sales End"
              id="edit-salesEnd"
              name="salesEnd"
            />
            <span v-if="errors.salesEnd" class="text-error text-xs mt-1 ml-1">{{
              errors.salesEnd
            }}</span>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <LabeledInput
              v-model="form.startDate"
              type="datetime-local"
              :input-class="['w-full', { 'input-error': errors.startDate }]"
              label="Event Start"
              id="edit-startDate"
              name="startDate"
            />
            <span v-if="errors.startDate" class="text-error text-xs mt-1 ml-1">{{
              errors.startDate
            }}</span>
          </div>
          <div>
            <LabeledInput
              v-model="form.endDate"
              type="datetime-local"
              :input-class="['w-full', { 'input-error': errors.endDate }]"
              label="Event End"
              id="edit-endDate"
              name="endDate"
            />
            <span v-if="errors.endDate" class="text-error text-xs mt-1 ml-1">{{
              errors.endDate
            }}</span>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <LabeledInput
              v-model="form.maxParticipants"
              type="number"
              :input-class="['w-full', { 'input-error': errors.maxParticipants }]"
              placeholder="Max Participants"
              label="Max Participants"
              id="edit-maxParticipants"
              name="maxParticipants"
              min="1"
            />
            <span v-if="errors.maxParticipants" class="text-error text-xs mt-1 ml-1">{{
              errors.maxParticipants
            }}</span>
          </div>
          <div>
            <LabeledInput
              v-model="form.ticketPrice"
              type="number"
              :input-class="['w-full', { 'input-error': errors.ticketPrice }]"
              placeholder="Ticket Price"
              label="Ticket Price (€)"
              id="edit-ticketPrice"
              name="ticketPrice"
              min="0"
              step="0.01"
            />
            <span v-if="errors.ticketPrice" class="text-error text-xs mt-1 ml-1">{{
              errors.ticketPrice
            }}</span>
          </div>
        </div>

        <div class="modal-action">
          <button type="button" class="btn" @click="showEditModal = false" :disabled="updating">
            Cancel
          </button>
          <AtomButton
            class="btn-primary"
            :label="updating ? 'Saving...' : 'Save Changes'"
            type="submit"
            :disabled="updating"
          />
        </div>
      </MoleculeFieldset>
    </EditModal>

    <MoleculeConfirmModal
      :show="showDeleteModal"
      title="Confirm Deletion"
      :message="`Are you sure you want to delete ${eventToDelete?.title}? This action cannot be undone.`"
      confirm-label="Delete Event"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

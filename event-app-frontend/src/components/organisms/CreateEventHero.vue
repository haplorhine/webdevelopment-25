<script setup>
import { ref, reactive } from 'vue'
import { http } from '@/api/http'
import * as yup from 'yup'
import AtomButton from '../atoms/AtomButton.vue'
import LabeledInput from '../molecules/LabeledInput.vue'
import MoleculeFieldset from '../molecules/MoleculeFieldset.vue'
import { useUserStore } from '@/state/user'
import { storeToRefs } from 'pinia'

const userStore = useUserStore()
const { userId } = storeToRefs(userStore)

const title = ref('')
const category = ref('CHARITY')
const imageURL = ref('')
const description = ref('')
const location = ref('')
const startDate = ref('')
const endDate = ref('')
const maxParticipants = ref(0)
const salesStart = ref('')
const salesEnd = ref('')
const ticketPrice = ref(0)

const errors = reactive({})
const submitError = ref('')
const submitSuccess = ref(false)

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

const eventSchema = yup.object({
  title: yup.string().required('Title is required'),
  category: yup.string().required('Category is required'),
  imageURL: yup.string().url('Must be a valid URL').required('Image URL is required'),
  description: yup.string().required('Description is required'),
  location: yup.string().required('Location is required'),
  startDate: yup.string().required('Start date is required'),
  endDate: yup.string().required('End date is required'),
  maxParticipants: yup
    .number()
    .min(0, 'Must be 0 or more')
    .required('Max participants is required'),
  salesStart: yup.string().required('Sales start is required'),
  salesEnd: yup.string().required('Sales end is required'),
  ticketPrice: yup.number().min(0, 'Must be 0 or more').required('Ticket price is required'),
})

const handleCreateEvent = async () => {
  submitError.value = ''
  submitSuccess.value = false
  Object.keys(errors).forEach((key) => (errors[key] = ''))
  const eventData = {
    title: title.value,
    category: category.value,
    imageURL: imageURL.value,
    description: description.value,
    location: location.value,
    startDate: startDate.value,
    endDate: endDate.value,
    maxParticipants: maxParticipants.value,
    salesStart: salesStart.value,
    salesEnd: salesEnd.value,
    ticketPrice: ticketPrice.value,
    hostId: userId.value,
  }
  try {
    await eventSchema.validate(eventData, { abortEarly: false })
    await http.post('/events', eventData)
    submitSuccess.value = true
  } catch (err) {
    if (Array.isArray(err?.inner)) {
      err.inner.forEach((e) => {
        if (!errors[e.path]) errors[e.path] = e.message
      })
      return
    }
    submitError.value = err?.message || 'Validation failed.'
  }
}
</script>
<template>
  <div class="hero bg-base-200 min-h-screen">
    <div class="hero-content flex-col lg:flex-row-reverse gap-12 w-full">
      <div class="text-center lg:text-left">
        <h1 class="text-5xl font-bold">Create Event</h1>
        <p class="py-6">Fill out the form below to create a new event.</p>
      </div>
      <div class="card bg-base-100 w-full max-w-sm shrink-0 shadow-2xl">
        <div class="card-body">
          <form @submit.prevent="handleCreateEvent">
            <MoleculeFieldset>
              <LabeledInput
                v-model="title"
                type="text"
                inputClass="w-full"
                placeholder="Event Title"
                label="Title"
                id="event-title-input"
                name="title"
              />
              <p v-if="errors.title" class="text-error text-xs mt-1">{{ errors.title }}</p>
              <div class="form-control w-full mb-2">
                <label class="label" for="event-category-input">
                  <span class="label-text">Category</span>
                </label>
                <select
                  v-model="category"
                  id="event-category-input"
                  name="category"
                  class="select select-bordered w-full"
                >
                  <option v-for="cat in categories" :key="cat" :value="cat">
                    {{ cat.replace('_', ' ') }}
                  </option>
                </select>
              </div>
              <LabeledInput
                v-model="imageURL"
                type="text"
                inputClass="w-full"
                placeholder="Image URL"
                label="Image URL"
                id="event-image-url-input"
                name="imageURL"
              />
              <p v-if="errors.imageURL" class="text-error text-xs mt-1">{{ errors.imageURL }}</p>
              <LabeledInput
                v-model="description"
                type="text"
                inputClass="w-full"
                placeholder="Description"
                label="Description"
                id="event-description-input"
                name="description"
              />
              <p v-if="errors.description" class="text-error text-xs mt-1">
                {{ errors.description }}
              </p>
              <LabeledInput
                v-model="location"
                type="text"
                inputClass="w-full"
                placeholder="Location"
                label="Location"
                id="event-location-input"
                name="location"
              />
              <p v-if="errors.location" class="text-error text-xs mt-1">{{ errors.location }}</p>
              <LabeledInput
                v-model="startDate"
                type="datetime-local"
                inputClass="w-full"
                placeholder="Start Date"
                label="Start Date"
                id="event-start-date-input"
                name="startDate"
              />
              <p v-if="errors.startDate" class="text-error text-xs mt-1">{{ errors.startDate }}</p>
              <LabeledInput
                v-model="endDate"
                type="datetime-local"
                inputClass="w-full"
                placeholder="End Date"
                label="End Date"
                id="event-end-date-input"
                name="endDate"
              />
              <p v-if="errors.endDate" class="text-error text-xs mt-1">{{ errors.endDate }}</p>
              <LabeledInput
                v-model="maxParticipants"
                type="number"
                inputClass="w-full"
                placeholder="Max Participants"
                label="Max Participants"
                id="event-max-participants-input"
                name="maxParticipants"
                min="0"
              />
              <p v-if="errors.maxParticipants" class="text-error text-xs mt-1">
                {{ errors.maxParticipants }}
              </p>
              <LabeledInput
                v-model="salesStart"
                type="datetime-local"
                inputClass="w-full"
                placeholder="Sales Start"
                label="Sales Start"
                id="event-sales-start-input"
                name="salesStart"
              />
              <p v-if="errors.salesStart" class="text-error text-xs mt-1">
                {{ errors.salesStart }}
              </p>
              <LabeledInput
                v-model="salesEnd"
                type="datetime-local"
                inputClass="w-full"
                placeholder="Sales End"
                label="Sales End"
                id="event-sales-end-input"
                name="salesEnd"
              />
              <p v-if="errors.salesEnd" class="text-error text-xs mt-1">{{ errors.salesEnd }}</p>
              <LabeledInput
                v-model="ticketPrice"
                type="number"
                inputClass="w-full"
                placeholder="Ticket Price"
                label="Ticket Price (€)"
                id="event-ticket-price-input"
                name="ticketPrice"
                min="0"
                step="0.01"
              />
              <p v-if="errors.ticketPrice" class="text-error text-xs mt-1">
                {{ errors.ticketPrice }}
              </p>
              <p v-if="submitError" class="text-error text-sm">{{ submitError }}</p>
              <p v-if="submitSuccess" class="text-success text-sm">Event created successfully.</p>
              <AtomButton class="btn-neutral mt-4" label="Create Event" type="submit" />
            </MoleculeFieldset>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped></style>

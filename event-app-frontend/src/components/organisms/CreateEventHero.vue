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

// State
const title = ref('')
const category = ref('CHARITY')
const imageFile = ref(null) // DATEI, keine URL
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
const isSubmitting = ref(false)

const categories = [
  'CHARITY', 'COMMUNITY', 'COMPETITION', 'CONCERT', 'CONFERENCE',
  'EXHIBITION', 'FAIR', 'FAMILY', 'FESTIVAL', 'FITNESS_CLASS',
  'FOOD_FEST', 'LECTURE', 'MEETUP', 'MOVIE', 'NETWORKING',
  'OUTDOOR_ACTIVITY', 'RELIGIOUS', 'SEMINAR', 'SPORT_EVENT',
  'THEATER', 'TOURNAMENT', 'TRAINING', 'WEBINAR', 'WORKSHOP',
]

// Validation
const eventSchema = yup.object({
  title: yup.string().required('Title is required'),
  category: yup.string().required('Category is required'),
  description: yup.string().required('Description is required'),
  location: yup.string().required('Location is required'),
  
  salesStart: yup.date().typeError('Invalid date').required('Required'),
  salesEnd: yup.date().typeError('Invalid date').required('Required')
    .min(yup.ref('salesStart'), 'Sales end must be after sales start'),
  startDate: yup.date().typeError('Invalid date').required('Required')
    .min(yup.ref('salesEnd'), 'Event start must be after sales end'),
  endDate: yup.date().typeError('Invalid date').required('Required')
    .min(yup.ref('startDate'), 'Event end must be after start date'),

  maxParticipants: yup.number()
    .transform((v) => (isNaN(v) ? undefined : v))
    .min(1).required('Required'),
  ticketPrice: yup.number()
    .transform((v) => (isNaN(v) ? undefined : v))
    .min(0).required('Required'),

  imageFile: yup.mixed()
    .nullable()
    .notRequired()
    .test('fileSize', 'Max 20MB', (value) => {
      if (!value) return true
      return value.size <= 20 * 1024 * 1024
    })
    .test('fileType', 'Only .jpg/.png', (value) => {
      if (!value) return true
      return ['image/jpeg', 'image/png'].includes(value.type)
    })
})

const handleFileChange = (event) => {
  const file = event.target.files[0]
  imageFile.value = file || null

  if (file) {
     yup.reach(eventSchema, 'imageFile')
       .validate(file)
       .then(() => delete errors.imageFile)
       .catch((err) => errors.imageFile = err.message)
  } else {
     delete errors.imageFile
  }
}

const handleCreateEvent = async () => {
  submitError.value = ''
  submitSuccess.value = false
  isSubmitting.value = true
  Object.keys(errors).forEach((key) => delete errors[key])

  const validationData = {
    title: title.value,
    category: category.value,
    description: description.value,
    location: location.value,
    startDate: startDate.value,
    endDate: endDate.value,
    maxParticipants: maxParticipants.value,
    salesStart: salesStart.value,
    salesEnd: salesEnd.value,
    ticketPrice: ticketPrice.value,
    imageFile: imageFile.value
  }

  try {
    await eventSchema.validate(validationData, { abortEarly: false })

    let uploadedImageId = null
    if (imageFile.value) {
      const formData = new FormData()
      formData.append('file', imageFile.value)
      
      const imageResponse = await http.post('/images', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      uploadedImageId = imageResponse.data.id
    }


    const eventPayload = {
        title: title.value,
        category: category.value,
        description: description.value,
        location: location.value,
        startDate: startDate.value,
        endDate: endDate.value,
        maxParticipants: maxParticipants.value,
        salesStart: salesStart.value,
        salesEnd: salesEnd.value,
        ticketPrice: ticketPrice.value,
        hostId: userId.value,
        imageId: uploadedImageId
    }

    await http.post('/events', eventPayload)
    submitSuccess.value = true
    
  } catch (err) {
    console.error(err)
    if (err.inner) {
      err.inner.forEach((e) => {
        errors[e.path] = e.message
      })
    } else {
      submitError.value = err?.response?.data?.message || err?.message || 'Something went wrong.'
    }
  } finally {
    isSubmitting.value = false
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
                <label class="label"><span class="label-text">Category</span></label>
                <select v-model="category" class="select select-bordered w-full">
                  <option v-for="cat in categories" :key="cat" :value="cat">
                    {{ cat.replace('_', ' ') }}
                  </option>
                </select>
              </div>

              <div class="form-control w-full mb-2">
                <label class="label"><span class="label-text">Event Image (Optional)</span></label>
                <input
                  type="file"
                  class="file-input file-input-bordered w-full"
                  @change="handleFileChange"
                  accept="image/png, image/jpeg"
                />
                <p v-if="errors.imageFile" class="text-error text-xs mt-1">{{ errors.imageFile }}</p>
                <p class="text-xs text-base-content/60 mt-1">Max 20MB, .jpg or .png</p>
              </div>

              <LabeledInput
                v-model="description"
                type="text"
                inputClass="w-full"
                placeholder="Description"
                label="Description"
                id="event-description-input"
                name="description"
              />
              <p v-if="errors.description" class="text-error text-xs mt-1">{{ errors.description }}</p>

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
                v-model="salesStart"
                type="datetime-local"
                inputClass="w-full"
                label="Sales Start"
                id="salesStart" name="salesStart"
              />
              <p v-if="errors.salesStart" class="text-error text-xs mt-1">{{ errors.salesStart }}</p>

              <LabeledInput
                v-model="salesEnd"
                type="datetime-local"
                inputClass="w-full"
                label="Sales End"
                id="salesEnd" name="salesEnd"
              />
              <p v-if="errors.salesEnd" class="text-error text-xs mt-1">{{ errors.salesEnd }}</p>

              <LabeledInput
                v-model="startDate"
                type="datetime-local"
                inputClass="w-full"
                label="Start Date"
                id="startDate" name="startDate"
              />
              <p v-if="errors.startDate" class="text-error text-xs mt-1">{{ errors.startDate }}</p>

              <LabeledInput
                v-model="endDate"
                type="datetime-local"
                inputClass="w-full"
                label="End Date"
                id="endDate" name="endDate"
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
              <p v-if="errors.maxParticipants" class="text-error text-xs mt-1">{{ errors.maxParticipants }}</p>
              
              <LabeledInput
                v-model="ticketPrice"
                type="number"
                inputClass="w-full"
                placeholder="Ticket Price"
                label="Ticket Price (€)"
                id="event-ticket-price-input"
                name="ticketPrice"
                min="0" step="0.01"
              />
              <p v-if="errors.ticketPrice" class="text-error text-xs mt-1">{{ errors.ticketPrice }}</p>
              
              <p v-if="submitError" class="text-error text-sm">{{ submitError }}</p>
              <p v-if="submitSuccess" class="text-success text-sm">Event created successfully.</p>
              
              <AtomButton 
                class="btn-neutral mt-4" 
                :label="isSubmitting ? 'Uploading...' : 'Create Event'" 
                type="submit" 
                :disabled="isSubmitting"
              />
            </MoleculeFieldset>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
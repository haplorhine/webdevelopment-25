<script setup>
import { reactive, ref, onMounted } from 'vue'
import * as yup from 'yup'
import { http } from '@/api/http'
import { jwtDecode } from 'jwt-decode'
import MoleculeFieldset from '@/components/molecules/MoleculeFieldset.vue'
import AtomButton from '@/components/atoms/AtomButton.vue'

const loading = ref(true)
const updating = ref(false)
const userId = ref(null)
const successMessage = ref('')
const errorMessage = ref('')
const errors = reactive({})

const getImageUrl = (imageId) => {
  if (!imageId) return 'https://placehold.co/150x150?text=Avatar'
  return `http://localhost:8080/images/${imageId}`
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
  salutation: '',
  salutationOther: '',
  email: '',
  username: '',
  country: '',
  userType: 'USER',
  password: '',
  repeatPassword: '',
  imageId: null
})


const schema = yup.object({
  salutation: yup.string().required(),
  salutationOther: yup.string().when('salutation', {
    is: (value) => value === 'other',
    then: (schema) => schema.required().max(30),
    otherwise: (schema) => schema.max(30),
  }),
  email: yup.string().required().email(),
  username: yup.string().required().min(4),
  country: yup.string().required(),
  password: yup.string().test('min-length', 'Password must be at least 5 characters',
    val => !val || val.length >= 5
  ).nullable(),
  repeatPassword: yup.string().when('password', {
    is: (val) => val && val.length > 0,
    then: (schema) => schema.required('Please repeat your password').oneOf([yup.ref('password')], 'Passwords must match'),
    otherwise: (schema) => schema.notRequired()
  })
})

const dachCountries = ['Germany', 'Austria', 'Switzerland']
const otherCountries = [
  'Afghanistan', 'Albania', 'Algeria', 'Andorra', 'Angola', 'Argentina', 'Armenia', 'Australia',
  'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Belarus', 'Belgium', 'Brazil', 'Canada',
  'China', 'Denmark', 'Egypt', 'Finland', 'France', 'Greece', 'India', 'Indonesia', 'Iran',
  'Iraq', 'Ireland', 'Israel', 'Italy', 'Japan', 'Kenya', 'Mexico', 'Netherlands', 'Norway',
  'Poland', 'Portugal', 'Russia', 'Saudi Arabia', 'Spain', 'Sweden', 'Thailand', 'Turkey',
  'Ukraine', 'United Kingdom', 'United States', 'Vietnam', 'Zambia', 'Zimbabwe',
]

const showDeleteModal = ref(false)
const deleteBlocked = ref(false)
const deleteBlockReason = ref('')
const precheckLoading = ref(false)
const deleting = ref(false)
const deleteStep = ref(1)

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    errorMessage.value = 'Not logged in.'
    loading.value = false
    return
  }

  try {
    const decoded = jwtDecode(token)
    userId.value = decoded.sub

    const response = await http.get(`/users/${userId.value}`)
    const userData = response.data

    form.id = userData.id
    form.email = userData.email
    form.username = userData.username
    form.country = userData.country
    form.userType = userData.userType
    form.imageId = userData.imageId

    if (['MR', 'MS'].includes(userData.salutation)) {
      form.salutation = userData.salutation.toLowerCase()
    } else {
      if (userData.salutation === 'OTHER') {
        form.salutation = 'other'
      } else {
        form.salutation = userData.salutation.toLowerCase()
      }
    }

  } catch (err) {
    console.error(err)
    errorMessage.value = 'Failed to load user data.'
  } finally {
    loading.value = false
  }
})

const handleUpdate = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  updating.value = true


  Object.keys(errors).forEach(key => delete errors[key])

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
      salutation: form.salutation.toUpperCase(),
      email: form.email,
      username: form.username,
      country: form.country,
      userType: form.userType,
      active: true,
      imageId: form.imageId
    }

    if (form.password) {
      payload.password = form.password
    } else {
      payload.password = null
    }

    payload.country = payload.country.toUpperCase().replace(/ /g, '_')

    await http.put(`/users/${userId.value}`, payload)

    successMessage.value = 'Profile updated successfully!'
    form.password = ''
    form.repeatPassword = ''
    imageFile.value = null

  } catch (err) {
    console.error(err)


    if (err.inner) {
      err.inner.forEach(e => {
        errors[e.path] = e.message
      })
    } else {
      errorMessage.value = 'Update failed. ' + (err.response?.data?.message || err.message)
    }
  } finally {
    updating.value = false
  }
}

const openDeleteModal = async () => {
  showDeleteModal.value = true
  deleteStep.value = 1
  deleteBlocked.value = false
  deleteBlockReason.value = ''
  await precheckDeleteConstraints()
}

const closeDeleteModal = () => {
  if (deleting.value) return
  showDeleteModal.value = false
  deleteStep.value = 1
  deleteBlocked.value = false
  deleteBlockReason.value = ''
}

const precheckDeleteConstraints = async () => {
  precheckLoading.value = true
  deleteBlocked.value = false
  deleteBlockReason.value = ''

  try {
    if (form.userType === 'USER') {
      const ticketsRes = await http.get(`/users/${userId.value}/tickets`)
      const tickets = Array.isArray(ticketsRes.data) ? ticketsRes.data : []

      if (tickets.length > 0) {
        deleteBlocked.value = true
        deleteBlockReason.value = 'tickets'
        return
      }
    }

   if (form.userType === 'HOST') {
      const eventsRes = await http.get('/events')
      const events = Array.isArray(eventsRes.data) ? eventsRes.data : []

      const myEvents = events.filter(e => String(e.hostId) === String(userId.value))
      if (myEvents.length > 0) {
        deleteBlocked.value = true
        deleteBlockReason.value = 'events'
        return
      }
    }

   deleteBlocked.value = false
    deleteBlockReason.value = ''
  } catch (err) {
    console.error(err)
    deleteBlocked.value = true
    deleteBlockReason.value = 'unknown'
  } finally {
    precheckLoading.value = false
  }
}

const goToFinalDeleteStep = () => {
  if (precheckLoading.value) return
  if (deleteBlocked.value) return
  deleteStep.value = 2
}

const handleDeleteAccount = async () => {
  if (deleting.value) return
  deleting.value = true

  try {
    await http.delete(`/users/${userId.value}`)
    localStorage.removeItem('token')
    window.location.href = '/'
  } catch (err) {
    console.error(err)
    deleteBlocked.value = true
    deleteBlockReason.value = 'unknown'
    deleteStep.value = 1
  } finally {
    deleting.value = false
  }
}

const deleteModalMessage = () => {
  if (precheckLoading.value) return 'Checking your tickets/events...'

  if (deleteBlocked.value) {
    if (deleteBlockReason.value === 'tickets') {
      return 'You cannot delete your account because you have tickets.'
    }
    if (deleteBlockReason.value === 'events') {
      return 'You cannot delete your account because you have events listed.'
    }
    return "Account deletion is unavailable right now because we can't confirm your ticket/event status. Please try again later."
  }

  if (deleteStep.value === 1) {
    return 'Warning: This action will permanently delete your account. This cannot be undone.'
  }

  return 'Final warning: Your account will be permanently deleted. This cannot be undone.'
}
</script>

<template>
  <div class="min-h-screen bg-base-200 py-10 px-4">
    <div class="max-w-2xl mx-auto">

      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold">My Profile</h1>
        <p class="py-2 text-base-content/70">Manage your account settings</p>
      </div>

      <div v-if="loading" class="flex justify-center">
        <span class="loading loading-spinner loading-lg text-primary"></span>
      </div>

      <div v-else class="card bg-base-100 shadow-xl">
        <div class="card-body">
          <form @submit.prevent="handleUpdate">
            <MoleculeFieldset>

              <div v-if="successMessage" class="alert alert-success mb-4 text-sm">
                <span>{{ successMessage }}</span>
              </div>
              <div v-if="errorMessage" class="alert alert-error mb-4 text-sm">
                <span>{{ errorMessage }}</span>
              </div>

              <div class="flex flex-col items-center gap-4 mb-6">
                <div class="avatar">
                  <div class="w-24 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
                    <img :src="getImageUrl(form.imageId)" alt="Profile" />
                  </div>
                </div>
                <div class="form-control w-full max-w-xs">
                  <input type="file" class="file-input file-input-bordered w-full file-input-sm" @change="handleFileChange"
                    accept="image/*" />
                </div>
              </div>

              <div class="form-control w-full">
                <label class="label"><span class="label-text font-semibold">Salutation</span></label>
                <select v-model="form.salutation"
                  :class="['select select-bordered w-full', { 'select-error': errors.salutation }]">
                  <option value="mr">Mr</option>
                  <option value="ms">Ms</option>
                  <option value="other">Other</option>
                </select>
                <span v-if="errors.salutation" class="text-error text-xs mt-1">{{ errors.salutation }}</span>
              </div>

              <div v-if="form.salutation === 'other'" class="form-control w-full">
                <label class="label"><span class="label-text font-semibold">Specify</span></label>
                <input v-model="form.salutationOther" type="text" class="input input-bordered w-full" />
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="label"><span class="label-text font-semibold">Username</span></label>
                  <input v-model="form.username" type="text"
                    :class="['input input-bordered w-full', { 'input-error': errors.username }]" />
                  <span v-if="errors.username" class="text-error text-xs mt-1">{{ errors.username }}</span>
                </div>
                <div>
                  <label class="label"><span class="label-text font-semibold">Email</span></label>
                  <input v-model="form.email" type="email"
                    :class="['input input-bordered w-full', { 'input-error': errors.email }]" />
                  <span v-if="errors.email" class="text-error text-xs mt-1">{{ errors.email }}</span>
                </div>
              </div>

              <div class="form-control w-full">
                <label class="label"><span class="label-text font-semibold">Country</span></label>
                <select v-model="form.country"
                  :class="['select select-bordered w-full', { 'select-error': errors.country }]">
                  <optgroup label="DACH">
                    <option v-for="c in dachCountries" :key="c" :value="c.toUpperCase()">{{ c }}</option>
                  </optgroup>
                  <optgroup label="International">
                    <option v-for="c in otherCountries" :key="c" :value="c.toUpperCase()">{{ c }}</option>
                  </optgroup>
                </select>
                <span v-if="errors.country" class="text-error text-xs mt-1">{{ errors.country }}</span>
              </div>

              <div class="form-control w-full">
                <label class="label"><span class="label-text font-semibold">Account Type</span></label>
                <input :value="form.userType" readonly
                  class="input input-bordered w-full bg-base-200 text-base-content/50 cursor-not-allowed" />
                <span class="text-xs text-base-content/50 mt-1 pl-1">Role cannot be changed directly.</span>
              </div>

              <div class="divider">Change Password (Optional)</div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="label"><span class="label-text font-semibold">New Password</span></label>
                  <input v-model="form.password" type="password" placeholder="Leave empty to keep current"
                    :class="['input input-bordered w-full', { 'input-error': errors.password }]" />
                  <span v-if="errors.password" class="text-error text-xs mt-1">{{ errors.password }}</span>
                </div>
                <div>
                  <label class="label"><span class="label-text font-semibold">Repeat New Password</span></label>
                  <input v-model="form.repeatPassword" type="password" placeholder="********"
                    :class="['input input-bordered w-full', { 'input-error': errors.repeatPassword }]" />
                  <span v-if="errors.repeatPassword" class="text-error text-xs mt-1">{{ errors.repeatPassword }}</span>
                </div>
              </div>

              <div class="card-actions justify-between mt-6">
                 <button type="button" class="btn btn-error w-full md:w-auto" @click="openDeleteModal">
                  Delete account
                </button>

                <AtomButton class="btn-primary w-full md:w-auto" :label="updating ? 'Saving...' : 'Save Changes'"
                  type="submit" :disabled="updating" />
              </div>

            </MoleculeFieldset>
          </form>
        </div>
      </div>
    </div>

    <div class="modal" :class="{ 'modal-open': showDeleteModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg">Delete account?</h3>

        <p class="py-4 text-sm">
          <strong v-if="!deleteBlocked && (deleteStep === 1 || deleteStep === 2)">Warning:</strong>
          {{ deleteModalMessage() }}
        </p>

        <div class="modal-action">
          <button class="btn" :disabled="deleting" @click="closeDeleteModal">Cancel</button>

          <button
            v-if="deleteStep === 1"
            class="btn btn-outline"
            :disabled="precheckLoading || deleteBlocked"
            @click="goToFinalDeleteStep"
          >
            Continue
          </button>

          <button
            v-if="deleteStep === 2"
            class="btn btn-error"
            :disabled="deleting"
            @click="handleDeleteAccount"
          >
            {{ deleting ? 'Deleting...' : 'Delete account' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>





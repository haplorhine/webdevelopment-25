<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as yup from 'yup'
import { http } from '@/api/http'
import LabeledInput from '@/components/molecules/LabeledInput.vue'
import MoleculeFieldset from '@/components/molecules/MoleculeFieldset.vue'
import AtomButton from '@/components/atoms/AtomButton.vue'

const users = ref([])
const loading = ref(false)
const error = ref('')

// --- STATES ---
const showEditModal = ref(false)
const showDeleteModal = ref(false)

const updating = ref(false)
const successMessage = ref('')
const errorMessage = ref('')
const errors = reactive({}) 

const userToDelete = ref(null)

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
  salutation: '',
  salutationOther: '',
  email: '',
  username: '',
  country: '',
  userType: 'USER',
  isActive: true, 
  imageId: null
})

const userTypes = ['USER', 'HOST', 'ADMIN']
const countries = ['AUSTRIA', 'GERMANY', 'SWITZERLAND', 'FRANCE', 'ITALY']

// Validation
const schema = yup.object({
  salutation: yup.string().required('Salutation is required'),
  salutationOther: yup.string().when('salutation', {
    is: (value) => value === 'other',
    then: (schema) => schema.required('Please specify').max(30),
    otherwise: (schema) => schema.max(30),
  }),
  email: yup.string().required('Email is required').email('Invalid email format'),
  username: yup.string().required('Username is required').min(4, 'Min 4 characters'),
  country: yup.string().required('Country is required'),
  userType: yup.string().required('Role is required'),
})

const fetchUsers = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await http.get('/users')
    users.value = response.data
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load users. Please try again later.'
  } finally {
    loading.value = false
  }
}

const promptDelete = (user) => {
  userToDelete.value = user
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  if (!userToDelete.value) return
  
  try {
    await http.delete(`/users/${userToDelete.value.id}`)
    users.value = users.value.filter(u => u.id !== userToDelete.value.id)
    showDeleteModal.value = false
    userToDelete.value = null
  } catch (err) {
    console.error(err)
    alert('Failed to delete user. They might be linked to existing events or tickets.')
  }
}

const openEditModal = (user) => {
  successMessage.value = ''
  errorMessage.value = ''
  Object.keys(errors).forEach(key => delete errors[key])
  imageFile.value = null
  
  form.id = user.id
  form.email = user.email
  form.username = user.username
  form.country = user.country
  form.userType = user.userType
  form.imageId = user.imageId
  form.isActive = user.isActive 

  if (['MR', 'MS'].includes(user.salutation)) {
      form.salutation = user.salutation.toLowerCase()
      form.salutationOther = ''
  } else {
      form.salutation = 'other'
      form.salutationOther = user.salutation === 'OTHER' ? '' : user.salutation
  }

  showEditModal.value = true
}

const saveUser = async () => {
  updating.value = true
  errorMessage.value = ''
  successMessage.value = ''
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
      email: form.email,
      username: form.username,
      country: form.country,
      userType: form.userType,
      imageId: form.imageId,
      isActive: form.isActive 
    }

    payload.salutation = form.salutation.toUpperCase()

    await http.put(`/users/${form.id}`, payload)
    
    successMessage.value = 'Changes saved successfully!'
    await fetchUsers()
    
    setTimeout(() => {
        showEditModal.value = false
    }, 1500)

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

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="p-6 min-h-screen bg-base-200">
    <div class="max-w-7xl mx-auto">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">User Management</h1>
        <button class="btn btn-primary btn-sm" @click="fetchUsers">Refresh</button>
      </div>

      <div v-if="error" class="alert alert-error mb-4 shadow-lg">
        <span>{{ error }}</span>
      </div>

      <div class="overflow-x-auto bg-base-100 shadow-xl rounded-box">
        <table class="table w-full">
          <thead>
            <tr>
              <th class="text-center">Avatar</th>
              <th class="text-center">User</th>
              <th class="text-center">Role</th>
              <th class="text-center">Country</th>
              <th class="text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" class="hover">
              <td class="text-center">
                <div class="avatar placeholder">
                  <div class="w-10 rounded-full bg-neutral-focus text-neutral-content ring ring-primary ring-offset-base-100 ring-offset-2">
                    <img v-if="user.imageId" :src="getImageUrl(user.imageId)" />
                    <span v-else class="text-xs">{{ user.username?.substring(0,2).toUpperCase() }}</span>
                  </div>
                </div>
              </td>
              <td class="text-center">
                <div class="font-bold">{{ user.username }}</div>
                <div class="text-xs opacity-50">{{ user.email }}</div>
              </td>
              <td class="text-center">
                <span :class="{
                  'badge badge-sm': true,
                  'badge-primary': user.userType === 'ADMIN',
                  'badge-secondary': user.userType === 'HOST',
                  'badge-ghost': user.userType === 'USER'
                }">{{ user.userType }}</span>
              </td>
              <td class="text-center">{{ user.country }}</td>
              <td class="text-center">
                <div class="join">
                    <button class="btn btn-ghost btn-xs join-item" @click="openEditModal(user)">
                    Edit
                    </button>
                    <button class="btn btn-ghost btn-xs text-error join-item" @click="promptDelete(user)">
                    Delete
                    </button>
                </div>
              </td>
            </tr>
            <tr v-if="users.length === 0 && !loading">
              <td colspan="6" class="text-center py-8 text-gray-500">No users found.</td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="loading" class="flex justify-center mt-4">
        <span class="loading loading-spinner loading-lg"></span>
      </div>
    </div>

    <dialog class="modal" :class="{ 'modal-open': showEditModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg mb-4">Edit User</h3>
        
        <div v-if="successMessage" class="alert alert-success text-sm mb-4">
            <span>{{ successMessage }}</span>
        </div>
        <div v-if="errorMessage" class="alert alert-error text-sm mb-4">
            <span>{{ errorMessage }}</span>
        </div>
        
        <form @submit.prevent="saveUser">
          <MoleculeFieldset>
            
            <div class="flex flex-col items-center gap-4 mb-6">
                <div class="avatar">
                    <div class="w-20 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
                        <img v-if="form.imageId" :src="getImageUrl(form.imageId)" />
                        <div v-else class="bg-gray-200 w-full h-full flex items-center justify-center font-bold text-xl">
                            {{ form.username?.charAt(0).toUpperCase() }}
                        </div>
                    </div>
                </div>
                <input type="file" class="file-input file-input-bordered file-input-xs w-full max-w-xs" accept="image/*" @change="handleFileChange" />
            </div>

            <div class="form-control w-full mb-2 p-2 bg-base-200 rounded-lg">
                <label class="cursor-pointer label justify-between">
                    <span class="label-text font-bold">Account Active?</span> 
                    <input type="checkbox" v-model="form.isActive" class="checkbox checkbox-success" />
                </label>
            </div>

            <div class="form-control w-full">
              <label class="label"><span class="label-text font-semibold">Salutation</span></label>
              <select 
                v-model="form.salutation" 
                :class="['select select-bordered w-full', {'select-error': errors.salutation}]"
              >
                <option value="mr">Mr</option>
                <option value="ms">Ms</option>
                <option value="other">Other</option>
              </select>
              <span v-if="errors.salutation" class="text-error text-xs mt-1">{{ errors.salutation }}</span>
            </div>

            <div v-if="form.salutation === 'other'" class="form-control w-full">
                 <label class="label"><span class="label-text font-semibold">Specify Salutation</span></label>
                 <input 
                   v-model="form.salutationOther" 
                   type="text" 
                   :class="['input input-bordered w-full', {'input-error': errors.salutationOther}]"
                 />
                 <span v-if="errors.salutationOther" class="text-error text-xs mt-1">{{ errors.salutationOther }}</span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                 <div>
                    <LabeledInput
                        v-model="form.username"
                        type="text"
                        :input-class="['w-full', {'input-error': errors.username}]"
                        placeholder="Username"
                        label="Username"
                        id="edit-username"
                        name="username"
                    />
                    <span v-if="errors.username" class="text-error text-xs mt-1 ml-1">{{ errors.username }}</span>
                 </div>
                 <div>
                    <LabeledInput
                        v-model="form.email"
                        type="email"
                        :input-class="['w-full', {'input-error': errors.email}]"
                        placeholder="Email"
                        label="Email"
                        id="edit-email"
                        name="email"
                    />
                    <span v-if="errors.email" class="text-error text-xs mt-1 ml-1">{{ errors.email }}</span>
                 </div>
            </div>

            <div class="form-control w-full">
              <label class="label"><span class="label-text font-semibold">Role</span></label>
              <select v-model="form.userType" class="select select-bordered w-full">
                <option v-for="role in userTypes" :key="role" :value="role">{{ role }}</option>
              </select>
            </div>

            <div class="form-control w-full">
              <label class="label"><span class="label-text font-semibold">Country</span></label>
              <select 
                v-model="form.country" 
                :class="['select select-bordered w-full', {'select-error': errors.country}]"
              >
                <option v-for="c in countries" :key="c" :value="c">{{ c }}</option>
              </select>
              <span v-if="errors.country" class="text-error text-xs mt-1">{{ errors.country }}</span>
            </div>

            <div class="modal-action">
              <button type="button" class="btn" @click="showEditModal = false" :disabled="updating">Cancel</button>
              <AtomButton 
                 class="btn-primary" 
                 :label="updating ? 'Saving...' : 'Save Changes'" 
                 type="submit" 
                 :disabled="updating"
               />
            </div>
          </MoleculeFieldset>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
         <button @click="showEditModal = false" :disabled="updating">close</button>
      </form>
    </dialog>

    <dialog class="modal" :class="{ 'modal-open': showDeleteModal }">
      <div class="modal-box">
        <h3 class="font-bold text-lg text-error">Confirm Deletion</h3>
        <p class="py-4">
          Are you sure you want to delete <span class="font-bold">{{ userToDelete?.username }}</span>?
          This action cannot be undone.
        </p>
        <div class="modal-action">
          <button class="btn" @click="showDeleteModal = false">Cancel</button>
          <button class="btn btn-error" @click="confirmDelete">Delete User</button>
        </div>
      </div>
      <form method="dialog" class="modal-backdrop">
         <button @click="showDeleteModal = false">close</button>
      </form>
    </dialog>

  </div>
</template>
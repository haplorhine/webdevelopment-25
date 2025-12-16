<script setup>
import { reactive, ref, onMounted } from 'vue'
import * as yup from 'yup'
import { http } from '@/api/http'
import { jwtDecode } from 'jwt-decode'
//import LabeledInput from '@/components/molecules/LabeledInput.vue'
import MoleculeFieldset from '@/components/molecules/MoleculeFieldset.vue'
import AtomButton from '@/components/atoms/AtomButton.vue'

const loading = ref(true)
const updating = ref(false)
const userId = ref(null)
const successMessage = ref('')
const errorMessage = ref('')
const errors = reactive({})

// Form State
const form = reactive({
  id: '',
  salutation: '',
  salutationOther: '',
  email: '',
  username: '',
  country: '',
  userType: 'USER',
  password: '',       // Optional
  repeatPassword: ''  // Optional
})

// Validation Schema
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
  // Passwort Logik: Optional, aber wenn eingegeben, dann Validierung
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
  'France', 'Italy', 'Spain', 'United Kingdom', 'United States',
  'Poland', 'Netherlands', 'Belgium'
]

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    errorMessage.value = 'Not logged in.'
    loading.value = false
    return
  }

  try {
    const decoded = jwtDecode(token)
    userId.value = decoded.sub // User ID aus dem Token (subject)
    
    // Daten vom Backend laden
    const response = await http.get(`/users/${userId.value}`)
    const userData = response.data

    // Formular befüllen
    form.id = userData.id
    form.email = userData.email
    form.username = userData.username
    form.country = userData.country
    form.userType = userData.userType // Rolle (meistens readonly, aber wir zeigen sie an)

    // Salutation Mapping
    if (['MALE', 'FEMALE'].includes(userData.salutation)) {
      form.salutation = userData.salutation.toLowerCase()
    } else {
      // Annahme: Alles andere ist "other" oder spezifischer String im Backend
      // Falls das Backend "OTHER" speichert, müssen wir hier aufpassen
       if(userData.salutation === 'OTHER') {
           form.salutation = 'other'
           // Falls backend den spezifischen Text speichert, müsste er hier geladen werden
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
  
  // Reset errors
  Object.keys(errors).forEach(key => delete errors[key])

  try {
    // 1. Validieren
    await schema.validate(form, { abortEarly: false })

    // 2. Payload bauen
    const payload = {
      id: form.id,
      salutation: form.salutation.toUpperCase(),
      email: form.email,
      username: form.username,
      country: form.country, // Enum Values sind im Backend oft UPPERCASE, Frontend hat Mix
      userType: form.userType,
      active: true // Backend braucht das feld oft
    }
    
    // Passwort nur senden wenn gesetzt
    if (form.password) {
      payload.password = form.password
    } else {
      payload.password = null // oder weglassen, je nach Backend Toleranz
    }

    // Country Formatting (wenn Enum im Backend UPPERCASE_UNDERSCORE erwartet)
    // Einfache Heuristik: Leerzeichen durch _ ersetzen und Uppercase
    payload.country = payload.country.toUpperCase().replace(/ /g, '_')

    // 3. Request senden
    await http.put(`/users/${userId.value}`, payload)
    
    successMessage.value = 'Profile updated successfully!'
    form.password = ''
    form.repeatPassword = ''

  } catch (err) {
    console.error(err)

    // Validation Errors (Yup)
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

              <div class="form-control w-full">
                <label class="label"><span class="label-text font-semibold">Salutation</span></label>
                <select 
                  v-model="form.salutation" 
                  :class="['select select-bordered w-full', {'select-error': errors.salutation}]"
                >
                  <option value="male">Male</option>
                  <option value="female">Female</option>
                  <option value="other">Other</option>
                </select>
                <span v-if="errors.salutation" class="text-error text-xs mt-1">{{ errors.salutation }}</span>
              </div>
              
              <div v-if="form.salutation === 'other'" class="form-control w-full">
                 <label class="label"><span class="label-text font-semibold">Specify</span></label>
                 <input 
                   v-model="form.salutationOther" 
                   type="text" 
                   class="input input-bordered w-full" 
                 />
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                   <label class="label"><span class="label-text font-semibold">Username</span></label>
                   <input 
                     v-model="form.username" 
                     type="text" 
                     :class="['input input-bordered w-full', {'input-error': errors.username}]"
                   />
                   <span v-if="errors.username" class="text-error text-xs mt-1">{{ errors.username }}</span>
                </div>
                <div>
                   <label class="label"><span class="label-text font-semibold">Email</span></label>
                   <input 
                     v-model="form.email" 
                     type="email" 
                     :class="['input input-bordered w-full', {'input-error': errors.email}]"
                   />
                   <span v-if="errors.email" class="text-error text-xs mt-1">{{ errors.email }}</span>
                </div>
              </div>

              <div class="form-control w-full">
                <label class="label"><span class="label-text font-semibold">Country</span></label>
                <select 
                  v-model="form.country" 
                  :class="['select select-bordered w-full', {'select-error': errors.country}]"
                >
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
                <input 
                  :value="form.userType" 
                  readonly 
                  class="input input-bordered w-full bg-base-200 text-base-content/50 cursor-not-allowed" 
                />
                <span class="text-xs text-base-content/50 mt-1 pl-1">Role cannot be changed directly.</span>
              </div>

              <div class="divider">Change Password (Optional)</div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                   <label class="label"><span class="label-text font-semibold">New Password</span></label>
                   <input 
                     v-model="form.password" 
                     type="password" 
                     placeholder="Leave empty to keep current"
                     :class="['input input-bordered w-full', {'input-error': errors.password}]"
                   />
                   <span v-if="errors.password" class="text-error text-xs mt-1">{{ errors.password }}</span>
                </div>
                <div>
                   <label class="label"><span class="label-text font-semibold">Repeat New Password</span></label>
                   <input 
                     v-model="form.repeatPassword" 
                     type="password" 
                     placeholder="********"
                     :class="['input input-bordered w-full', {'input-error': errors.repeatPassword}]"
                   />
                   <span v-if="errors.repeatPassword" class="text-error text-xs mt-1">{{ errors.repeatPassword }}</span>
                </div>
              </div>

              <div class="card-actions justify-end mt-6">
                <AtomButton 
                  class="btn-primary w-full md:w-auto" 
                  :label="updating ? 'Saving...' : 'Save Changes'" 
                  type="submit" 
                  :disabled="updating"
                />
              </div>

            </MoleculeFieldset>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

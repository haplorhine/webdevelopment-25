<script setup>
import { ref, onMounted } from 'vue'
import { http } from '@/api/http'
import LabeledInput from '@/components/molecules/LabeledInput.vue'
import MoleculeFieldset from '@/components/molecules/MoleculeFieldset.vue'

const users = ref([])
const loading = ref(false)
const error = ref('')

const showEditModal = ref(false)
const editingUser = ref(null)

const userTypes = ['USER', 'HOST', 'ADMIN']
const countries = ['AUSTRIA', 'GERMANY', 'SWITZERLAND', 'FRANCE', 'ITALY']
const salutations = ['MR', 'MRS', 'MX']

const fetchUsers = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await http.get('/users')
    users.value = response.data
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load users. Ensure you are logged in as ADMIN.'
  } finally {
    loading.value = false
  }
}

const openEditModal = (user) => {
  editingUser.value = { ...user }
  showEditModal.value = true
}

const saveUser = async () => {
  if (!editingUser.value) return

  try {
    await http.put(`/users/${editingUser.value.id}`, editingUser.value)
    
    // Liste neu laden
    await fetchUsers()
    
    showEditModal.value = false
  } catch (err) {
    console.error(err)
    alert('Failed to save changes. Please check your inputs.')
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
      </div>

      <div v-if="error" class="alert alert-error mb-4 shadow-lg">
        <span>{{ error }}</span>
      </div>

      <div class="overflow-x-auto bg-base-100 shadow-xl rounded-box">
        <table class="table w-full">
          <thead>
            <tr>
              <th class="text-center">Username</th>
              <th class="text-center">Email</th>
              <th class="text-center">Role</th>
              <th class="text-center">Country</th>
              <th class="text-center">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id" class="hover">
              <td class="font-bold text-center">{{ user.username }}</td>
              <td class="text-center">{{ user.email }}</td>
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
                <button class="btn btn-ghost btn-xs" @click="openEditModal(user)">
                  Edit
                </button>
              </td>
            </tr>
            <tr v-if="users.length === 0 && !loading">
              <td colspan="5" class="text-center py-8 text-gray-500">No users found.</td>
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
        
        <form v-if="editingUser" @submit.prevent="saveUser">
          <MoleculeFieldset>
            
            <div class="form-control w-full">
              <label class="label"><span class="label-text">Salutation</span></label>
              <select v-model="editingUser.salutation" class="select select-bordered w-full">
                <option v-for="sal in salutations" :key="sal" :value="sal">{{ sal }}</option>
              </select>
            </div>

            <LabeledInput
              v-model="editingUser.username"
              type="text"
              input-class="w-full"
              placeholder="Username"
              label="Username"
              id="edit-username"
              name="username"
            />

            <LabeledInput
              v-model="editingUser.email"
              type="email"
              input-class="w-full"
              placeholder="Email"
              label="Email"
              id="edit-email"
              name="email"
            />

            <div class="form-control w-full">
              <label class="label"><span class="label-text">Role</span></label>
              <select v-model="editingUser.userType" class="select select-bordered w-full">
                <option v-for="role in userTypes" :key="role" :value="role">{{ role }}</option>
              </select>
            </div>

            <div class="form-control w-full">
              <label class="label"><span class="label-text">Country</span></label>
              <select v-model="editingUser.country" class="select select-bordered w-full">
                <option v-for="c in countries" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>

            <div class="modal-action">
              <button type="button" class="btn" @click="showEditModal = false">Cancel</button>
              <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
          </MoleculeFieldset>
        </form>
      </div>
      <form method="dialog" class="modal-backdrop">
         <button @click="showEditModal = false">close</button>
      </form>
    </dialog>

  </div>
</template>
import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'
import { http } from '@/api/http'

export const useUserStore = defineStore('user', {
  state: () => ({
    data: {
      id: null,
      username: null,
      role: null,
      imageId: null,
    },
    token: null,
    error: null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    userId: (state) => state.data.id,
    username: (state) => state.data.username,
    role: (state) => state.data.role,
    imageId: (state) => state.data.imageId,
    isAdmin: (state) => state.data.role === 'ADMIN',
  },
  actions: {
    async setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      try {
        const decoded = jwtDecode(token)

        this.data.id = decoded.sub
        this.data.username = decoded.username || null
        this.data.role = decoded.role || null
        
        await this.fetchProfile()

      } catch {
        this.data = { id: null, username: null, role: null, imageId: null }
        this.error = 'Invalid token'
      }
    },
        async fetchProfile() {
      if (!this.data.id) return
      try {
        const response = await http.get(`/users/${this.data.id}`)
        this.data.imageId = response.data.imageId
      } catch (e) {
        console.error('Failed to fetch profile:', e)
      }
    },

    logout() {
      this.token = null
      this.data = { id: null, username: null, role: null, imageId: null }
      localStorage.removeItem('token')
    },
    loadFromStorage() {
      const token = localStorage.getItem('token')
      if (token) {
        this.setToken(token)
      }
    },
  },
})
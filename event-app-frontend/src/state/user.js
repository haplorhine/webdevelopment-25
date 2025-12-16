import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'

export const useUserStore = defineStore('user', {
  state: () => ({
    data: {
      id: null,
      username: null,
      role: null,
    },
    token: null,
    error: null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    userId: (state) => state.data.id,
    username: (state) => state.data.username,
    role: (state) => state.data.role,
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      try {
        const decoded = jwtDecode(token)

        this.data.id = decoded.sub
        this.data.username = decoded.username || null
        this.data.role = decoded.role || null
      } catch {
        this.data = { id: null, username: null, role: null }
        this.error = 'Invalid token'
      }
    },
    logout() {
      this.token = null
      this.data = { id: null, username: null, role: null }
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

import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/pages/HomeView.vue'
import EventView from '@/pages/EventView.vue'

// lazy load other routes
const AboutView = () => import('@/pages/AboutView.vue')
const LoginView = () => import('@/pages/LoginView.vue')
const RegisterView = () => import('@/pages/RegisterView.vue')
const ImprintView = () => import('@/pages/ImprintView.vue')
const HelpView = () => import('@/pages/HelpView.vue')

const routes = [
  { path: '/', component: HomeView },
  { path: '/events', component: EventView },
  { path: '/about', component: AboutView },
  { path: '/imprint', component: ImprintView},
  { path: '/help', component: HelpView},
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router

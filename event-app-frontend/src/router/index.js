import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/state/user'

import HomeView from '@/pages/HomeView.vue'

// lazy load other routes
const AboutView = () => import('@/pages/AboutView.vue')
const LoginView = () => import('@/pages/LoginView.vue')
const RegisterView = () => import('@/pages/RegisterView.vue')
const ImprintView = () => import('@/pages/ImprintView.vue')
const HelpView = () => import('@/pages/HelpView.vue')
const EventView = () => import('@/pages/EventView.vue')
const CreateEventView = () => import('@/pages/CreateEventView.vue')
const EventDetailView = () => import('@/pages/EventDetailView.vue')
const UserManagementView = () => import('@/pages/UserManagementView.vue')
const TicketManagementView = () => import('@/pages/TicketManagementView.vue')
const EventManagementView = () => import('@/pages/EventManagementView.vue')
const ForgotPasswordView = () => import('@/pages/ForgotPasswordView.vue')
const ProfileView = () => import('@/pages/ProfileView.vue')
const MyTicketsView = () => import('@/pages/MyTicketsView.vue')

const routes = [
  { path: '/', component: HomeView },
  { path: '/events', component: EventView },
  { path: '/events/:id', component: EventDetailView },
  { path: '/create-event', component: CreateEventView, meta: { requiresAuth: true } },
  { path: '/about', component: AboutView },
  { path: '/imprint', component: ImprintView },
  { path: '/help', component: HelpView },
  { path: '/login', component: LoginView },
  { path: '/register', component: RegisterView },
  { path: '/forgot-password', component: ForgotPasswordView },
  { path: '/profile', component: ProfileView, meta: { requiresAuth: true } },
  { path: '/my-tickets', component: MyTicketsView, meta: { requiresAuth: true } },
  { path: '/user-management', component: UserManagementView, meta: { requiresAdmin: true } },
  { path: '/ticket-management', component: TicketManagementView, meta: { requiresAdmin: true } },
  { path: '/event-management', component: EventManagementView, meta: { requiresAdminOrHost: true } },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return next('/') 
  }

  if (to.meta.requiresAdmin) {
    if (!userStore.isAdmin) {
      return next('/')
    }
  }

  if (to.meta.requiresAdminOrHost) {
    if (!(userStore.isAdmin || userStore.isHost)) {
      return next('/')
    }
  }
  next()
})

export default router
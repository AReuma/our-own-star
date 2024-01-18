import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import OAuthView from "@/views/auth/OAuthView.vue";
import MainView from "@/views/main/MainView.vue";

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: HomeView
  },
  {
    path: '/our-own-star',
    name: 'MainView',
    component: MainView
  },
  {
    path: '/our-own-star/login',
    name: 'LoginView',
    component: LoginView
  },
  {
    path: '/our-own-star/join',
    name: 'RegisterView',
    component: RegisterView
  },
  {
    path: '/auth/oauth-response',
    name: 'OAuthView',
    component: OAuthView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import OAuthView from "@/views/auth/OAuthView.vue";
import MainView from "@/views/main/MainView.vue";
import ArtistView from "@/views/artist/ArtistView.vue";
import ArtistMarketPageView from "@/components/artist/ArtistMarketPageView.vue";
import ArtistBookMarkPageView from "@/components/artist/ArtistBookMarkPageView.vue";
import ArtistPopularPostPageView from "@/components/artist/ArtistPopularPostPageView.vue";

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: HomeView
  },
  {
    path: '/our-own-star',
    name: 'MainView',
    component: MainView,
    props: { page: 1 }
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
  },
  {
    path: '/our-own-star/:artist',
    name: 'ArtistView',
    component: ArtistView,
    props: true,
    children: [
      {
        path: 'market',
        component: ArtistMarketPageView
      },
      {
        path: 'bookmark',
        component: ArtistBookMarkPageView
      },
      {
        path: 'popularPost',
        component: ArtistPopularPostPageView
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

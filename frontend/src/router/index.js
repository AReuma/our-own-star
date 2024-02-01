import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import OAuthView from "@/views/auth/OAuthView.vue";
import MainView from "@/views/main/MainView.vue";
import ArtistView from "@/views/artist/ArtistView.vue";
import ArtistMarketPageView from "@/components/artist/ArtistMarketPageView.vue";
import ArtistPopularPostPageView from "@/components/artist/ArtistPopularPostPageView.vue";
import ArtistBookMarkPageView from "@/components/artist/ArtistBookMarkPageView.vue";
import ArtistMyInfoBoardView from "@/components/artist/myPage/ArtistMyInfoBoardView.vue";
import ArtistMyInfoView from "@/views/artist/myPage/ArtistMyInfoView.vue";
import ArtistMyInfoCommentView from "@/components/artist/myPage/ArtistMyInfoCommentView.vue";
import ArtistMyInfoMediaView from "@/components/artist/myPage/ArtistMyInfoMediaView.vue";
import ArtistMyInfoLikeView from "@/components/artist/myPage/ArtistMyInfoLikeView.vue";
import ArtistHomePageView from "@/components/artist/ArtistHomePageView.vue";
import ArtistBoardReadView from "@/views/artist/ArtistBoardReadView.vue";

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
    redirect: { name: 'ArtistHomePageView' },
    children: [
      {
        path: 'home',
        name: 'ArtistHomePageView',
        component: ArtistHomePageView
      },
      {
        path: 'market',
        name: 'ArtistMarketPageView',
        component: ArtistMarketPageView
      },
      {
        path: 'bookmark',
        name: 'ArtistBookMarkPageView',
        component: ArtistBookMarkPageView
      },
      {
        path: 'popularPost',
        name: 'ArtistPopularPostPageView',
        component: ArtistPopularPostPageView
      },
      {
        path: 'myPage',
        name: 'ArtistMyInfoView',
        component: ArtistMyInfoView,
        props: route => ({ artist: route.params.artist }),
        redirect: {name: 'ArtistMyInfoBoardView'},
        children: [
          {
            path: 'board',
            name: 'ArtistMyInfoBoardView',
            component: ArtistMyInfoBoardView
          },
          {
            path: 'comment',
            name: 'ArtistMyInfoCommentView',
            component: ArtistMyInfoCommentView
          },
          {
            path: 'media',
            name: 'ArtistMyInfoMediaView',
            component: ArtistMyInfoMediaView
          },
          {
            path: 'like',
            name: 'ArtistMyInfoLikeView',
            component: ArtistMyInfoLikeView
          }
        ]
      }
    ]
  },
  {
    path: '/our-own-star/:artist/board/:boardNum',
    name: 'ArtistBoardReadView',
    component: ArtistBoardReadView,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

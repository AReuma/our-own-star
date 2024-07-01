import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from "@/views/auth/RegisterView.vue";
import OAuthView from "@/views/auth/OAuthView.vue";
import MainView from "@/views/main/MainView.vue";
import ArtistView from "@/views/artist/ArtistView.vue";
import ArtistMarketPageView from "@/components/artist/market/ArtistMarketPageView.vue";
import ArtistPopularPostPageView from "@/components/artist/ArtistPopularPostPageView.vue";
import ArtistMapMarkerPageView from "@/components/artist/ArtistMapMarkerPageView.vue";
import ArtistMyInfoBoardView from "@/components/artist/myPage/ArtistMyInfoBoardView.vue";
import ArtistMyInfoView from "@/views/artist/myPage/ArtistMyInfoView.vue";
import ArtistMyInfoCommentView from "@/components/artist/myPage/ArtistMyInfoCommentView.vue";
import ArtistMyInfoMarketView from "@/components/artist/myPage/ArtistMyInfoMarketView.vue";
import ArtistMyInfoLikeView from "@/components/artist/myPage/ArtistMyInfoLikeView.vue";
import ArtistHomePageView from "@/components/artist/ArtistHomePageView.vue";
import ArtistBoardReadView from "@/views/artist/ArtistBoardReadView.vue";
import ArtistMarketReadView from "@/views/artist/market/ArtistMarketReadView.vue";
import ArtistMarketModifyView from "@/views/artist/market/ArtistMarketModifyView.vue";
import ArtistChattingRoomView from "@/views/chat/ArtistChattingRoomView.vue";
import ArtistChatListView from "@/views/chat/ArtistChatListView.vue";
import MainSearchView from "@/views/main/MainSearchView.vue";
import UserProfileView from "@/views/user/UserProfileView.vue";
import UserInfoBoardView from "@/components/user/UserInfoBoardView.vue";
import UserInfoCommentView from "@/components/user/UserInfoCommentView.vue";
import UserInfoMarketView from "@/components/user/UserInfoMarketView.vue";
import UserInfoLikeView from "@/components/user/UserInfoLikeView.vue";
import ArtistSearchView from "@/views/artist/search/ArtistSearchView.vue";
import SearchMarketView from "@/components/artist/search/SearchMarketView.vue";
import SearchBoardView from "@/components/artist/search/SearchBoardView.vue";
import ArtistSearchUserListPageView from "@/components/artist/search/ArtistSearchUserListPageView.vue";

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
    path: '/our-own-star/artist/search',
    name: 'MainSearchView',
    component: MainSearchView,
    props: route => ({ search: route.query.search })
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
        path: 'map',
        name: 'ArtistMapMarkerPageView',
        component: ArtistMapMarkerPageView
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
            path: 'market',
            name: 'ArtistMyInfoMarketView',
            component: ArtistMyInfoMarketView
          },
          {
            path: 'like',
            name: 'ArtistMyInfoLikeView',
            component: ArtistMyInfoLikeView
          }
        ]
      },
      {
        path: 'chat',
        name: 'ArtistChatListView',
        component: ArtistChatListView
      },
    ]
  },
  {
    path: '/our-own-star/:artist/board/:boardNum',
    name: 'ArtistBoardReadView',
    component: ArtistBoardReadView,
    props: true
  },
  {
    path: '/our-own-star/:artist/market/:sellNum',
    name: 'ArtistMarketReadView',
    component: ArtistMarketReadView,
    props: true
  },
  {
    path: '/our-own-star/:artist/market/modify/:sellNum',
    name: 'ArtistMarketModifyView',
    component: ArtistMarketModifyView,
    props: true
  },
  {
    path: '/our-own-star/:artist/chat/:roomId',
    name: 'ArtistChattingRoomView',
    component: ArtistChattingRoomView,
    props: true
  },
  {
    path: '/our-own-star/:artist/user',
    name: 'UserProfileView',
    components: {
      default: UserProfileView
    },
    props: {
      default: route => ({
        profile: route.query.profile,
        artist: route.params.artist
      })
    },
    redirect: {name: 'UserInfoBoardView'},
    children: [
      {
        path: 'board',
        name: 'UserInfoBoardView',
        component: UserInfoBoardView
      },
      {
        path: 'comment',
        name: 'UserInfoCommentView',
        component: UserInfoCommentView
      },
      {
        path: 'market',
        name: 'UserInfoMarketView',
        component: UserInfoMarketView
      },
      {
        path: 'like',
        name: 'UserInfoLikeView',
        component: UserInfoLikeView
      }
    ]
  },
  {
    path: '/our-own-star/:artist/search',
    name: 'ArtistSearchView',
    component: ArtistSearchView,
    props: route => ({
      keyword: route.query.keyword,
      artist: route.params.artist
    }),
    redirect: {name: 'SearchBoardView'},
    children: [
      {
        path: 'board',
        name: 'SearchBoardView',
        component: SearchBoardView
      },
      {
        path: 'market',
        name: 'SearchMarketView',
        component: SearchMarketView
      },
      {
        path: 'user',
        name: 'ArtistSearchUserListPageView',
        component: ArtistSearchUserListPageView
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

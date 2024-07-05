<template>
  <div id="main-layout">
    <main-page-header-view></main-page-header-view>
    <main-page-search-bar-view></main-page-search-bar-view>
    <main-page-view
        @searchArtist="searchArtist"
        @addIdolCategory="addIdolCategory"
        @movePage="movePage"
        @joinArtist="joinArtist"
        :searchIdolInfo="searchIdolInfo"
        :searchIdolLoading="searchIdolLoading"
        :idolCategory="idolCategory"
        :idolCategoryTotalPage="idolCategoryTotalPage"
        :pageNum="page"
        ref="mainPageView"
    ></main-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {VueCookieNext} from "vue-cookie-next";
import MainPageHeaderView from "@/components/main/MainPageHeaderView.vue";
import MainPageView from "@/components/main/MainPageView.vue";
import MainPageSearchBarView from "@/components/main/MainPageSearchBarView.vue";
import {mapActions, mapState} from "vuex";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY} from "@/constant/ApiUrl/ApiUrl";
import router from "@/router";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

export default defineComponent({
  name: "MainView",
  props: {
    page: {
      type: Number,
      required: true,
    },
  },
  components: {MainPageSearchBarView, MainPageView, MainPageHeaderView},
  data(){
    return {
      username: VueCookieNext.getCookie('email')
    }
  },
  methods: {
    ...mapActions(['fetchIdolCategory', 'fetchIdolCategoryTotalPage', 'fetchUserIdolCategory']),
    searchArtist(payload){
      const {artist} = payload
      console.log('artist: '+artist)
      this.$store.dispatch('fetchSearchIdolInfo', artist)
    },
    addIdolCategory(payload){
      const {artist, artistImg, artistGenre, artistType} = payload
      console.log({artist, artistImg, artistGenre, artistType})
      axios.post(API_BASE_URL+"/api/v1/idol/addCategory", {artist, artistImg, artistGenre, artistType}, config)
          .then((res) => {
            console.log(res)
            location.reload();
          })
          .catch((res) => {
            console.error(res)
          })
    },
    movePage(page){
      console.log(page)
      if (VueCookieNext.isCookieAvailable('accessToken')){
        this.fetchUserIdolCategory({page})
      }else {
        this.fetchIdolCategory(page)
      }
    },
    joinArtist(payload){
      const {id} = payload
      axios.post(API_BASE_URL+ARTIST_CATEGORY+"/join", {id}, config)
          .then((res) => {
            console.log(res)
            alert(res.data.artist+"님 페이지 가입완료")

            router.push({name: 'ArtistMyInfoView', params: {artist: res.data.artist}})
          })
          .catch((err) => {
            console.error(err.response)
            alert(err.response.data.errorMessage)
            this.$refs.mainPageView.joinArtistDialog = false;
          })
    }
  },
  mounted() {
    console.log(this.page)
    if (VueCookieNext.isCookieAvailable('accessToken')){
      const {page, username} = this;
      this.fetchUserIdolCategory({page, username})
    }else {
      this.fetchIdolCategory(this.page)
    }
    this.fetchIdolCategoryTotalPage()
  },
  computed:{
    ...mapState(['searchIdolInfo', 'searchIdolLoading', 'idolCategory', 'idolCategoryTotalPage']),
  }
})
</script>

<style scoped>
#main-layout{
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 10px 200px 0 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
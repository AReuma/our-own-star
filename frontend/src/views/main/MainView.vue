<template>
  <div id="main-layout">
    <main-page-header-view></main-page-header-view>
    <main-page-search-bar-view></main-page-search-bar-view>
    <main-page-view
        @searchArtist="searchArtist"
        @addIdolCategory="addIdolCategory"
        :searchIdolInfo="searchIdolInfo"
        :searchIdolLoading="searchIdolLoading"
    ></main-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {VueCookieNext} from "vue-cookie-next";
import MainPageHeaderView from "@/components/main/MainPageHeaderView.vue";
import MainPageView from "@/components/main/MainPageView.vue";
import MainPageSearchBarView from "@/components/main/MainPageSearchBarView.vue";
import {mapState} from "vuex";
import axios from "axios";
import {API_BASE_URL} from "@/constant/ApiUrl/ApiUrl";
export default defineComponent({
  name: "MainView",
  components: {MainPageSearchBarView, MainPageView, MainPageHeaderView},
  data(){
    return {
      username: VueCookieNext.getCookie('nickname')
    }
  },
  methods: {
    searchArtist(payload){
      const {artist} = payload
      console.log('artist: '+artist)
      this.$store.dispatch('fetchSearchIdolInfo', artist)
    },
    addIdolCategory(payload){

      const config = {
        headers: {
          'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
          'Accept' : 'application/json',
          'Content-Type': 'application/json'
        }
      };

      const {artist, artistImg, artistGenre, artistType} = payload
      console.log({artist, artistImg, artistGenre, artistType})
      axios.post(API_BASE_URL+"/api/v1/idol/addCategory", {artist, artistImg, artistGenre, artistType}, config)
          .then((res) => {
            console.log(res)
          })
          .catch((res) => {
            console.error(res)
          })
    }
  },
  computed:{
    ...mapState(['searchIdolInfo', 'searchIdolLoading']),
  }
})
</script>

<style scoped>
#main-layout{
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 10px 100px 0 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
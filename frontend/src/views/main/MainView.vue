<template>
  <div id="main-layout">
    <main-page-header-view></main-page-header-view>
    <main-page-search-bar-view></main-page-search-bar-view>
    <main-page-view
        @searchArtist="searchArtist"
        @addIdolCategory="addIdolCategory"
        @movePage="movePage"
        :searchIdolInfo="searchIdolInfo"
        :searchIdolLoading="searchIdolLoading"
        :idolCategory="idolCategory"
        :idolCategoryTotalPage="idolCategoryTotalPage"
        :pageNum="page"
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
import {API_BASE_URL} from "@/constant/ApiUrl/ApiUrl";
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
      username: VueCookieNext.getCookie('nickname')
    }
  },
  methods: {
    ...mapActions(['fetchIdolCategory', 'fetchIdolCategoryTotalPage']),
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
          })
          .catch((res) => {
            console.error(res)
          })
    },
    movePage(page){
      console.log(page)
      router.push({name: 'MainView',  query: { page: page }})
      this.fetchIdolCategory(page)
    }
  },
  mounted() {
    console.log(this.page)
    this.fetchIdolCategory(this.page)
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
  padding: 10px 100px 0 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
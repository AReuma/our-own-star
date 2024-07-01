<template>
  <div id="main-layout">
    <artist-market-modify-page-view
        :artist="artist"
        :sellNum="sellNum"
        :artistMarketOneBoard="artistMarketOneBoard"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        @modify="modify"
    ></artist-market-modify-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {mapActions, mapState} from "vuex";
import ArtistMarketModifyPageView from "@/components/artist/market/ArtistMarketModifyPageView.vue";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";
import router from "@/router";

const imageConfig = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Content-Type': 'multipart/form-data'
  }
};
export default defineComponent({
  name: "ArtistMarketModifyView",
  components: {ArtistMarketModifyPageView},
  props: {
    artist: {
      type: String,
      default: ''
    },
    sellNum: {
      type: String,
      default: ''
    }
  },
  methods: {
    ...mapActions(['fetchIdolCategoryJoinIsFirst', 'fetchIdolCategoryModifyMarketBoard']),
    modify(payload){
      const {artist, sellNum} = this;

      axios.put(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/modify`, payload, imageConfig)
          .then((res) => {
            console.log(res)
            router.push({name: 'ArtistMarketReadView', params: {artist: artist, sellNum: sellNum}})
          })
          .catch((err) => {
            console.error(err)
          })
    }
  },
  computed: {
    ...mapState(['joinCategoryUserInfo', 'artistMarketOneBoard'])
  },
  created() {
    const {artist, sellNum} = this;
    this.fetchIdolCategoryJoinIsFirst(artist);
    this.fetchIdolCategoryModifyMarketBoard({artist, sellNum})
  }
})
</script>

<style scoped>
#main-layout {
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 0 250px;
}
</style>
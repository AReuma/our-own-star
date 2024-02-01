<template>
  <div id="main-layout">
    <artist-page-view
        ref="artistPageView"
        :artist="artist"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        @post="post"
        @postImg="postImg"
        @postVote="postVote"
    ></artist-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistPageView from "@/components/artist/ArtistPageView.vue";
import {mapActions, mapState} from "vuex";
import router from "@/router";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

const imageConfig = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Content-Type': 'multipart/form-data'
  }
};

export default defineComponent({
  name: "ArtistView",
  components: {ArtistPageView},
  props: {
    artist: {
      type: String,
      default: ''
    },
  },
  data(){
    return {
      cookie: '',
    }
  },
  methods: {
    ...mapActions(['fetchIdolCategoryJoinIsFirst']),
    post(payload){
      const  {content} = payload;
      let artist = this.artist;

      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist, {content}, config)
          .then((res) => {
            console.log(res.data)

          });
    },
    postImg(formData) {
      let artist = this.artist;
      console.log(formData)

      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist + "/image", formData, imageConfig)
          .then((res) => {
            console.log(res.data)
            this.$refs.artistPageView.resetPostDialog();
          });
    },
    postVote(payload){
      const {voteHour, voteDay, content, voteChoice} = payload;
      let artist = this.artist;

      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist + "/vote", {voteHour, voteDay, content, voteChoice},config)
          .then((res) => {
            console.log(res.data)
          });
    }
  },
  computed: {
    ...mapState(['joinCategoryUserInfo'])
  },
  created() {
    this.fetchIdolCategoryJoinIsFirst(this.artist)
    this.cookie = VueCookieNext.getCookie('accessToken');

    if(this.joinCategoryUserInfo.isFirst){
      //let artist = this.artist
      //const decodedArtist = decodeURIComponent(artist);
      router.push({ name: 'ArtistMyInfoView', params: { artist: this.artist } });
    }
  }
})
</script>

<style scoped>
#main-layout{
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 0 250px;
}
</style>
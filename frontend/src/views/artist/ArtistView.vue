<template>
  <div id="main-layout">
    <artist-page-view
        :artist="artist"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        @postImg="postImg"
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
    postImg(formData) {

      console.log([...formData.entries()]);
      let artist = this.artist;
      console.log(artist);
      console.log(formData.get('content'));
      let cookie = this.cookie;

      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist + "/savePost/image", formData,
          {
            headers: {
              'Authorization': cookie ? 'Bearer ' + cookie : null,
              'Content-Type': 'multipart/form-data'
            }
          })
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
      let artist = this.artist
      const decodedArtist = decodeURIComponent(artist);
      router.push({ name: 'ArtistMyInfoView', params: { artist: decodedArtist } });
    }
  }
})
</script>

<style scoped>
#main-layout{
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 0 100px;
}
</style>
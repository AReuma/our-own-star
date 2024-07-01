<template>
  <div>
    <artist-my-info-page-view
        ref="myInfoPage"
        v-if="myPageProfile != null"
        :artist="artist"
        :myPageProfile="myPageProfile"
        @checkNicknameDub="checkNicknameDub"
        :isDubNickname="isDubNickname"
        @modifyUserInfoSave="modifyUserInfoSave"
    ></artist-my-info-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistMyInfoPageView from "@/components/artist/myPage/ArtistMyInfoPageView.vue";
import {mapActions, mapState} from "vuex";
import axios from "axios";
import {API_BASE_URL, ARTIST_MY_PAGE} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";

/*const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};*/

const imageConfig = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Content-Type': 'multipart/form-data'
  }
};

export default defineComponent({
  name: "ArtistMyInfoView",
  props: {
    artist: {
      type: String,
      default: ''
    }
  },
  components: {ArtistMyInfoPageView},
  data(){
    return {

    }
  },
  created() {
    const {artist} = this;
    this.fetchMyPageProfile({artist});
  },
  methods: {
    ...mapActions(['fetchMyPageProfile', 'fetchNicknameDubCheck']),
    checkNicknameDub(payload){
      const {nickname} = payload
      const {artist} = this;
      this.fetchNicknameDubCheck({artist, nickname})
    },
    modifyUserInfoSave(formData){
      const {artist} = this;
      axios.patch(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo/modify`, formData, imageConfig)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.error(err)
          })
    }
  },
  computed: {
    ...mapState(['myPageProfile', 'isDubNickname'])
  },
})
</script>

<style scoped>
::-webkit-scrollbar {
  width: 12px;
  height: 10px;
}
::-webkit-scrollbar-track {
  background-color: rgba(255, 20, 147, 0.4);
  border-radius: 10px;
}
::-webkit-scrollbar-thumb {
  background-color: rgba(52, 152, 219, 0.85);
  border-radius: 10px;
}
</style>
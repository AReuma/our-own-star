<template>
  <div id="main-layout">
    <user-profile-page-view
        v-if="userPageProfile"
        :profile="profile"
        :artist="artist"
        :userPageProfile="userPageProfile"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        @followUser="followUser"
    ></user-profile-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import UserProfilePageView from "@/components/user/UserProfilePageView.vue";
import {mapActions, mapState} from "vuex";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

export default defineComponent({
  name: "UserProfileView",
  components: {UserProfilePageView},
  props: {
    profile: {
      type: String,
      default: ''
    },
    artist: {
      type: String,
      default: ''
    }
  },
  created() {
    let artist = this.artist;
    let nickname = this.profile;
    this.fetchIdolCategoryJoinIsFirst(artist)
    this.fetchUserProfile({artist, nickname})
  },
  methods: {
    ...mapActions(['fetchUserProfile', 'fetchIdolCategoryJoinIsFirst']),
    followUser(payload){
      const {followUser, artist} = payload;
      axios.post(API_BASE_URL+ARTIST_CATEGORY+'/follow/user', {followUser, artist}, config)
          .then((res) => {
            alert(res.data)
            window.location.reload()
          })
          .catch(() => {
            alert('팔로우 실패')
          })
    }
  },
  computed: {
    ...mapState(['userPageProfile', 'joinCategoryUserInfo'])
  }
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
#main-layout {
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 0 250px;
}
</style>
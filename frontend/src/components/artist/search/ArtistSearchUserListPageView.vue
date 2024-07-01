<template>
  <div id="chat-div">
    <div v-for="(user, index) in searchUser" :key="index" @click="moveUserInfo(user.nickname)" style="width: 100%; display: flex; padding: 28px; border-bottom: 1px solid #BCBCBC; align-items: center" >
      <v-avatar size="58">
        <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+user.userProfile"></v-img>
      </v-avatar>
      <div style="display: flex; flex-direction: column; flex-grow: 1; padding: 0 12px; height: 58px;">
        <p style="font-size: 20px;">{{ user.nickname }}</p>

        <div style="display: flex; width: 100%; flex-grow: 1; align-items: end">
          <div v-if="user.userInfo" style="flex: 8; color: rgba(60,60,60,0.73);">{{user.userInfo}}</div>
          <div v-else style="flex: 8; color: rgba(60,60,60,0.73);">소개글 없음</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import moment from "moment/moment";
import router from "@/router";
import {mapActions, mapState} from "vuex";

export default defineComponent({
  name: "ArtistSearchUserListPageView",
  props: {
    artist: {
      type: String,
      default: ''
    },
    keyword: {
      type: String,
      default: ''
    },
  },
  created() {
    const {artist, keyword} = this
    this.fetchSearchUser({artist, keyword})
  },
  methods: {
    ...mapActions(['fetchSearchUser']),
    formattedDate(date) {
      return moment(date).format('MM/DD · hh:mm a');
    },
    moveUserInfo(profile){
      const {artist} = this;
      router.push({
        name: 'UserProfileView',
        params: { artist: artist }, // params 설정
        query: { profile: profile } // query 설정
      });
    }
  },
  computed: {
    ...mapState(['searchUser'])
  }
})
</script>

<style>
#chat-div {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: start;
  overflow-y: auto;
  position: relative;
}


</style>
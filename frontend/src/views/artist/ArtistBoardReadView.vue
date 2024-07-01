<template>
  <div id="main-layout">
    <artist-board-read-page-view
        v-if="artistOneBoard"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        :artist="artist"
        :artistOneBoard="artistOneBoard"
        :artistBoardComment="artistBoardComment"
        @vote="vote"
        @post="post"
        @deleteComment="deleteComment"
        @lickBoard="lickBoard"
        @bookmarkBoard="bookmarkBoard"
        @likeComment="likeComment"
        @bookmarkComment="bookmarkComment"
        @addChat="addChat"
        @moveProfile="moveProfile"
    ></artist-board-read-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardReadPageView from "@/components/artist/ArtistBoardReadPageView.vue";
import {mapActions, mapState} from "vuex";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD, ARTIST_CHAT} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";
import router from "@/router";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

export default defineComponent({
  name: "ArtistBoardReadView",
  components: {ArtistBoardReadPageView},
  props: {
    artist: {
      type: String,
      default: ''
    },
    boardNum: {
      type: String,
      default: ''
    }
  },
  methods: {
    ...mapActions(['fetchIdolCategoryJoinIsFirst', 'fetchIdolCategoryOne']),
    vote(payload){
      const {choice} = payload;
      const {boardNum} = this;

      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/vote/${boardNum}`, {choice}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((res) => {
            console.log(res)
          })
    },
    post(payload){
      const {boardNum, artist} = this;
      const {comment, boardCommentId} = payload;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment`, {comment, boardCommentId}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((res) => {
            console.log(res)
          })
    },
    deleteComment(payload){
      const {boardNum, artist} = this;
      const {commentId} = payload;
      console.log(commentId)

      axios.delete(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment/${commentId}`, config)
          .then((res) => {
            console.log(res)
            alert('댓글 삭제 완료')
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    lickBoard(){
      const {boardNum, artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/like`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    bookmarkBoard(){
      const {boardNum, artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/bookmark`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    likeComment(payload){
      const {commentId} = payload;
      const {boardNum, artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment/${commentId}/like`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })

    },
    bookmarkComment(payload){
      const {commentId} = payload;
      const {boardNum, artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment/${commentId}/bookmark`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })

    },
    addChat(payload){
      const {artist, messageReceiver} = payload;
      console.log("addChat: "+messageReceiver)

      axios.post(API_BASE_URL+ARTIST_CHAT+`/${artist}/isSub`, {messageReceiver}, config)
          .then((res) => {
            console.log(res)
            let roomId = res.data;
            router.push({name: 'ArtistChattingRoomView',  params: {artist: artist, roomId: roomId}})
          })
          .catch((err) => {
            console.error(err)
          })
    },
    moveProfile(payload){
      const {artist, profile} = payload;
      if(profile === this.joinCategoryUserInfo.nickname){
        router.push({
          name: 'ArtistMyInfoView',
          params: { artist: artist }
        })
      }else {
        router.push({
          name: 'UserProfileView',
          params: { artist: artist }, // params 설정
          query: { profile: profile } // query 설정
        });
      }
    }
  },
  computed: {
    ...mapState(['joinCategoryUserInfo', 'artistOneBoard', 'artistBoardComment'])
  },
  created() {
    const {artist, boardNum} = this;
    this.fetchIdolCategoryJoinIsFirst(artist)
    this.fetchIdolCategoryOne({artist, boardNum})
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
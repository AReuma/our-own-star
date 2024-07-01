<template>
  <div id="main-layout">
    <artist-market-read-page-view
        v-if="artistMarketOneBoard !== null && artistMarketOneBoard.image"
        :artist="artist"
        :sellNum="sellNum"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        :artistMarketOneBoard="artistMarketOneBoard"
        @likeBoard="likeBoard"
        @bookmarkBoard="bookmarkBoard"
        @modifyBoardStatus="modifyBoardStatus"
        @deleteMarketBoard="deleteMarketBoard"
        @addChat="addChat"
        @addComment="addComment"
        @likeComment="likeComment"
        @bookmarkComment="bookmarkComment"
        @deleteComment="deleteComment"
        @moveProfile="moveProfile"
    ></artist-market-read-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistMarketReadPageView from "@/components/artist/market/ArtistMarketReadPageView.vue";
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
  name: "ArtistMarketReadView",
  components: {ArtistMarketReadPageView},
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
    ...mapActions(['fetchIdolCategoryJoinIsFirst', 'fetchIdolCategoryOneMarketBoard']),
    moveProfile(payload){
      const {artist, profile} = payload;
      //alert(profile)

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
    },
    likeBoard(){
      // @PostMapping("/{artist}/market/{boardId}/like")
      const {artist, sellNum} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/like`, {}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    bookmarkBoard(){
      //@PostMapping("/{artist}/market/{boardId}/bookmark")
      const {artist, sellNum} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/bookmark`, {}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    modifyBoardStatus(payload){
      //@PatchMapping("/{artist}/market/{boardId}/modifyBoardStatus")
      const {artist, sellNum} = this;
      const {boardStatus} = payload;
      axios.patch(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/modifyBoardStatus`, {boardStatus}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    deleteMarketBoard(){
      //@DeleteMapping("/{artist}/market/{boardId}")
      const {artist, sellNum} = this;
      axios.delete(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}`, config)
          .then((res) => {
            console.log(res)
            router.push({name: 'ArtistMarketPageView'})
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
    addComment(payload){
      const {comment, boardCommentId} = payload;
      const {artist, sellNum} = this;

      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${sellNum}/marketComment`, {comment, boardCommentId}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.error(err)
          })
    },
    likeComment(payload){
      const {commentId} = payload;
      const {sellNum, artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${sellNum}/MarketComment/${commentId}/like`,{}, config)
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
      const {sellNum, artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${sellNum}/MarketComment/${commentId}/bookmark`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    deleteComment(payload){
      const {sellNum, artist} = this;
      const {commentId} = payload;
      console.log(commentId)

      axios.delete(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${sellNum}/MarketComment/${commentId}`, config)
          .then((res) => {
            console.log(res)
            alert('댓글 삭제 완료')
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
  },
  computed: {
    ...mapState(['joinCategoryUserInfo', 'artistMarketOneBoard'])
  },
  created() {
    const {artist, sellNum} = this;
    this.fetchIdolCategoryJoinIsFirst(artist);
    this.fetchIdolCategoryOneMarketBoard({artist, sellNum});
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
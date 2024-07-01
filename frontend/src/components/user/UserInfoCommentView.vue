<template>
  <div style="height: auto; width: 100%;">
    <div v-for="(comment, index) in userInfoComment" :key="index">
      <v-hover v-slot="{ isHovering, props }">
        <v-sheet
            @click="moveBoard(comment.boardId)"
            v-bind="props"
            :class="{ 'on-hover': isHovering }"
            :color="isHovering ? 'rgba(229,229,229,0.8)' : 'backColor'"
            style="display: flex; align-items: start; flex-direction: column; padding-top: 15px;"
        >
          <div style="display: flex; flex-direction: column; align-items: start; width: 100%; margin-bottom: 20px;">
            <div style="display: flex; justify-content: space-between; width: 100%; align-items: center; padding: 0 16px;">
              <div style="display: flex; align-items: center; width: 100%">
                <div>
                  <v-avatar size="50">
                    <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/' + comment.writerProfile"></v-img>
                  </v-avatar>
                </div>
                <div id="comment-writer">
                  {{comment.writer}}
                </div>
              </div>
            </div>

            <div id="comment-content-div">
              <div style="margin-left: 66px">
                {{ comment.content }}
              </div>
            </div>
          </div>
          <div style="width: 100%;">
            <div class="board-btn-list" style="border-top: 1px solid #D9D9D9; border-bottom: 1px solid #D9D9D9; height: 50px;">
              <div>
                <v-btn class="disable-btn" density="compact" icon="mdi-message-outline" variant="plain"></v-btn> {{comment.commentCount}}
              </div>
              <div v-if="comment.isLike">
                <v-btn @click="commentLike(comment.id, comment.boardId)" class="disable-btn" density="compact" icon="mdi-heart" color="red" variant="plain"></v-btn>{{comment.likeCount}}
              </div>
              <div v-else>
                <v-btn @click="commentLike(comment.id, comment.boardId)" class="disable-btn" density="compact" icon="mdi-heart-outline" variant="plain"></v-btn>{{comment.likeCount}}
              </div>
              <div v-if="comment.isBookmark">
                <v-btn @click="commentBookmark(comment.id, comment.boardId)" class="disable-btn" density="compact" icon="mdi-bookmark" color="blue" variant="plain"></v-btn>{{comment.bookmarkCount}}
              </div>
              <div v-else>
                <v-btn @click="commentBookmark(comment.id, comment.boardId)" class="disable-btn" density="compact" icon="mdi-bookmark-outline" variant="plain"></v-btn>{{comment.bookmarkCount}}
              </div>
              <div>
                <v-btn class="disable-btn" density="compact" icon="mdi-export-variant" disabled="true" variant="plain"></v-btn>
              </div>
            </div>
          </div>
        </v-sheet>
      </v-hover>
    </div>
    <div></div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
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

export default defineComponent({
  name: "UserInfoCommentView",
  props: {
    artist: {
      type: String,
      default: ''
    },
    profile: {
      type: String,
      default: ''
    }
  },
  data(){
    return {

    }
  },
  created() {
    const {artist} = this;
    let nickname = this.profile
    this.fetchUserInfoComment({artist, nickname})
  },
  methods: {
    ...mapActions(['fetchUserInfoComment']),
    moveBoard(index){
      const {artist} = this;
      router.push({name: 'ArtistBoardReadView', params: {artist: artist, boardNum: index}})
    },
    commentLike(commentId, boardNum){
      const {artist} = this;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment/${commentId}/like`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })

    },
    commentBookmark(commentId, boardNum){
      const {artist} = this;

      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment/${commentId}/bookmark`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })

    },
  },
  computed: {
    ...mapState(['userInfoComment'])
  }
})
</script>

<style scoped>
#comment-content-div {
  display: flex;
  flex-direction: column;
  width: 100%;
  font-size: 1.2vw;
  padding: 0 16px;
}
#comment-writer {
  font-size: 1.2vw;
  height: 60px;
  text-align: center;
  display: flex;
  align-items: center;
  margin-left: 20px;
}
.board-btn-list {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px 0 80px;
}
.disable-btn {
  opacity: 1;
  color: black;
}
</style>
<template>
  <div id="artist-layout">
    <div id="idol-category">
      <artist-board-category :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"></artist-board-category>
    </div>

    <div id="board-content-div">
      <div id="search-div">
        <artist-search-bar :artist="artist"></artist-search-bar>
      </div>

      <div class="pt-1" id="board-data" style="height: 100%; display: flex; align-items: center">
        <div id="board-title" style="display: flex; justify-content: start; width: 100%;">
          <v-btn @click="moveBack" variant="plain" color="blue" icon="mdi-arrow-left"></v-btn>
          <p> market </p>
        </div>

        <div style="border-radius: 12px; background-color: #D9D9D9; width: 90%">
          <div style="display: flex; align-items: center" class="pa-3">
            <div>
              <v-menu max-width="150px">
                <template v-slot:activator="{ props }">
                  <v-btn v-bind="props" variant="text" max-width="50px" max-height="50px">
                    <v-avatar max-size="50">
                      <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+artistMarketOneBoard.writerProfile"></v-img>
                    </v-avatar>
                  </v-btn>
                </template>

                <v-card>
                  <v-card-text>
                    <div class="mx-auto text-center">
                      <v-btn variant="text" rounded @click="moveProfile(artistMarketOneBoard.writer)"> 프로필 이동 </v-btn>
                      <v-divider v-if="artistMarketOneBoard.writer !== joinCategoryUserInfo.nickname" class="my-3"></v-divider>
                      <v-btn v-if="artistMarketOneBoard.writer !== joinCategoryUserInfo.nickname" @click="addChat(artistMarketOneBoard.writer)" variant="text" rounded> 채팅하기 </v-btn>
                    </div>
                  </v-card-text>
                </v-card>
              </v-menu>
            </div>

            <div id="category-user-nickname">
              {{ artistMarketOneBoard.writer }}
            </div>
            <v-spacer></v-spacer>

            <div v-if="artistMarketOneBoard.writer === joinCategoryUserInfo.nickname" style="display: flex; width: 100%;">
              <div class="user-btn-list pr-3" style="width: 100%;">
                <v-btn @click="openDialog" prepend-icon="mdi-refresh" variant="text" density="compact">상태 변경</v-btn>
                <v-btn @click="modifyMarketBoard" prepend-icon="mdi-pencil-box-outline" variant="text"  density="compact">상품 수정</v-btn>
                <v-btn @click="deleteMarketBoard" prepend-icon="mdi-trash-can-outline" variant="text" density="compact">삭제</v-btn>
              </div>
            </div>

            <div v-else>
              <v-chip v-if="artistMarketOneBoard.marketBoardStatus === 'SELL'" color="pink" variant="elevated">{{artistMarketOneBoard.boardStatusString}}</v-chip>
              <v-chip v-else-if="artistMarketOneBoard.marketBoardStatus === 'RESERVATION'" color="yellow" variant="elevated">{{artistMarketOneBoard.boardStatusString}}</v-chip>
              <v-chip v-else-if="artistMarketOneBoard.marketBoardStatus === 'SOLD_OUT'" color="red" variant="elevated">{{artistMarketOneBoard.boardStatusString}}</v-chip>
            </div>
          </div>


          <div style="position: relative;">
            <div style="width: 100%; height: 100%">
              <v-carousel
                  height="400"
                  hide-delimiters
                  :show-arrows="artistMarketOneBoard.image.length > 1"
                  color="blue"
                  class="px-9"
              >
                <template v-slot:prev="{ props }">
                  <v-btn
                      icon="mdi-chevron-left"
                      @click.stop="props.onClick"
                      color="blue"
                      variant="plain"
                  ></v-btn>
                </template>
                <template v-slot:next="{ props }">
                  <v-btn
                      icon="mdi-chevron-right"
                      @click.stop="props.onClick"
                      color="blue"
                      variant="plain"
                  ></v-btn>
                </template>
                <v-carousel-item
                    style="border-radius: 12px;"
                    v-for="(slide, i) in artistMarketOneBoard.image"
                    :key="i"
                    :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/' + slide"
                >
                </v-carousel-item>
              </v-carousel>
            </div>

            <div v-if="artistMarketOneBoard.marketBoardStatus === 'SOLD_OUT' || artistMarketOneBoard.marketBoardStatus === 'RESERVATION'" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 40px; background-color: rgba(0,0,0,0.61); color: #FF1493">
              {{artistMarketOneBoard.boardStatusString}}
            </div>
          </div>

          <div class="pl-4 pt-4">
            <p style="font-size: 25px">{{artistMarketOneBoard.title}}</p>
          </div>
          <div class="pl-5 pb-4" style="font-weight: bold; border-bottom: 1px solid rgba(51,51,51,0.11);">
            <p style="font-size: 25px">{{priceParser(artistMarketOneBoard.price)}}원</p>
          </div>

          <div v-html="convertNewLineToBr(artistMarketOneBoard.content)" class="d-flex pa-6" style="display: flex;">
          </div>

          <div class="pa-4" style="display: flex; justify-content: end; font-size: 14px; color: #868686">
            {{formattedDate(artistMarketOneBoard.createDate)}}
          </div>

          <div style="display: flex; width: 100%; margin-top: 12px">
            <div class="board-btn-list px-16" style="width: 100%;">
              <div>
                <v-btn class="disable-btn" density="compact" icon="mdi-message-outline" variant="plain"></v-btn> {{artistMarketOneBoard.commentCount}}
              </div>
              <div v-if="artistMarketOneBoard.like">
                <v-btn @click="likeBoard" class="disable-btn" density="compact" icon="mdi-heart" color="red" variant="plain"></v-btn>{{artistMarketOneBoard.likeCount}}
              </div>
              <div v-else>
                <v-btn @click="likeBoard" class="disable-btn" density="compact" icon="mdi-heart-outline" variant="plain"></v-btn>{{artistMarketOneBoard.likeCount}}
              </div>
              <div v-if="artistMarketOneBoard.bookmark">
                <v-btn @click="bookmarkBoard" class="disable-btn" density="compact" icon="mdi-bookmark" color="blue" variant="plain"></v-btn>{{artistMarketOneBoard.bookmarkCount}}
              </div>
              <div v-else>
                <v-btn @click="bookmarkBoard" class="disable-btn" density="compact" icon="mdi-bookmark-outline" variant="plain"></v-btn>{{artistMarketOneBoard.bookmarkCount}}
              </div>
              <div>
                <v-btn class="disable-btn" density="compact" icon="mdi-export-variant" :disabled="true" variant="plain"></v-btn>
              </div>
            </div>
          </div>
        </div>


        <div class="mt-4 pa-4" id="write-comment-div" style="width: 100%;">
          <div style="display: flex">
            <v-avatar size="50">
              <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+joinCategoryUserInfo.userProfileImg"></v-img>
            </v-avatar>

            <div id="comment-div">
              <div id="comment-textarea">
                <v-textarea
                    v-model="comment"
                    no-resize
                    variant="plain" density="compact" placeholder="답글 작성" style="text-align: center; padding-top: 8px"></v-textarea>
              </div>

              <div id="comment-btn">
                <v-btn @click="addComment" color="blue" variant="flat">작성</v-btn>
              </div>
            </div>
          </div>
        </div>

        <div id="comment-list-div" v-for="comment in artistMarketComment" :key="comment" style="width: 100%">
          <comment-view
              :commentInfo="comment"
              :joinCategoryUserInfo="joinCategoryUserInfo"
              @saveReComment="saveReMarketComment"
              @likeComment="likeComment"
              @bookmarkComment="bookmarkComment"
              @deleteComment="deleteComment"
          ></comment-view>
        </div>
      </div>
    </div>

    <v-dialog v-model="modifyBoardStatusDialog" width="500" height="300">
      <v-card  width="500" height="300" style="display: flex; justify-content: center; font-family: Dovemayo-Medium, sans-serif;">
        <v-card-title style="display: flex; justify-content: center; border-bottom: 1px solid #D9D9D9;" class="pa-4">상품 상태 변경</v-card-title>

        <div style="display: flex; border-bottom: 1px solid #D9D9D9; height: 200px">
          <v-radio-group v-model="boardStatus" color="pink" true-icon="mdi-check" style="display: flex; justify-content: center; align-items: center;" density="comfortable">
            <v-radio class="radio-label" value="SELL" :style="{ color: boardStatus === 'SELL' ? '#FF1493' : 'black' }">
              <template v-slot:label>
                <p style="font-size: 28px">판매중</p>
              </template>
            </v-radio>
            <v-radio class="radio-label" value="RESERVATION" :style="{ color: boardStatus === 'RESERVATION' ? '#FF1493' : 'black' }">
              <template v-slot:label>
                <p style="font-size: 28px">예약중</p>
              </template>
            </v-radio>
            <v-radio class="radio-label" value="SOLD_OUT" :style="{ color: boardStatus === 'SOLD_OUT' ? '#FF1493' : 'black' }">
              <template v-slot:label>
                <p style="font-size: 28px">판매 완료</p>
              </template>
            </v-radio>
          </v-radio-group>
        </div>

        <v-card-actions class="pa-2" style="display: flex; justify-content: end">
          <v-btn @click="modifyBoardStatus" color="blue">변경</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";
import moment from "moment/moment";
import {priceParser} from "@/constant/time/priceParser";
import router from "@/router";
import {mapActions, mapState} from "vuex";
import CommentView from "@/components/artist/comment/CommentView.vue";
import ArtistSearchBar from "@/components/artist/search/ArtistSearchBar.vue";

export default defineComponent({
  name: "ArtistMarketReadPageView",
  components: {ArtistSearchBar, CommentView, ArtistBoardCategory},
  props: {
    artist: {
      type: String,
      default: '',
    },
    joinCategoryUserInfo: {
      type: Object,
      default: null,
    },
    sellNum: {
      type: String,
      default: ''
    },
    artistMarketOneBoard: {
      type: Object,
      default: null
    }
  },
  data(){
    return {
      modifyBoardStatusDialog: false,
      boardStatus: '',
      comment: '',
    }
  },
  created() {
    const {sellNum, artist} = this;
    this.fetchMarketComment({artist, sellNum})
  },
  methods: {
    ...mapActions(['fetchMarketComment']),
    priceParser,
    moveBack(){
      this.$router.go(-1)
    },
    convertNewLineToBr(text) {
      return text.replace(/\n/g, '<br>');
    },
    likeBoard(){
      this.$emit('likeBoard')
    },
    bookmarkBoard(){
      this.$emit('bookmarkBoard')
    },
    formattedDate(date) {
      return moment(date).format('hh:mm a · yyyy/MM/DD');
    },
    openDialog() {
      this.boardStatus = this.artistMarketOneBoard.marketBoardStatus;
      this.modifyBoardStatusDialog = true;
    },
    modifyBoardStatus(){
      const {boardStatus} = this;
      this.$emit('modifyBoardStatus', {boardStatus})
    },
    deleteMarketBoard(){
      this.$emit('deleteMarketBoard')
    },
    modifyMarketBoard(){
      const {artist, sellNum} = this;
      router.push({name: 'ArtistMarketModifyView', params: {artist: artist, sellNum: sellNum}})
    },
    addChat(writer){
      let artist = this.artist;
      let messageReceiver = writer;

      this.$emit('addChat', {artist, messageReceiver})
    },
    moveProfile(writer){
      let artist = this.artist;
      let profile = writer;
      this.$emit('moveProfile', {artist, profile})
    },
    addComment(){
      if(this.comment !== '') {
        const {comment} = this;
        let boardCommentId = "null";
        this.$emit('addComment', {comment, boardCommentId})
      }else {
        alert('내용을 작성해주세요')
      }
    },
    saveReMarketComment(payload){
      alert('saveReMarketComment')
      const {boardCommentId, comment} = payload
      this.$emit('addComment', {boardCommentId, comment})
    },
    likeComment(payload){
      const {commentId} = payload;
      this.$emit('likeComment', {commentId})
    },
    bookmarkComment(payload){
      const {commentId} = payload;
      this.$emit('bookmarkComment', {commentId})
    },
    deleteComment(payload) {
      const {commentId} = payload;
      this.$emit('deleteComment', {commentId})
    }
  },
  computed: {
    ...mapState(['artistMarketComment'])
  }
})
</script>

<style scoped>
#artist-layout {
  height: 100vh;
  display: flex;
  position: relative;
  font-family: Dovemayo-Medium, sans-serif;
}
#idol-category {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #BCBCBC;
}
#board-title{
  display: flex;
  font-size: 25px;
  align-items: center;
  border-bottom: 1px solid #D9D9D9;
  margin-bottom: 12px;
  font-family: Dovemayo_wild, sans-serif;
}
#board-content-div {
  flex: 3;
  border-right: 1px solid #D9D9D9;
  width: 100%;
  display: flex;
  flex-direction: column;
}
#search-div {
  flex: 1;
  display: flex;
  height: 10%;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #BCBCBC;
}
#search-box {
  width: 50%;
  height: 55px;
  background-color: #D9D9D9;
  border-top-left-radius: 12px;
  border-bottom-left-radius: 12px;
  text-align: center;
  font-size: 20px;
  outline: none
}
#search-btn {
  height: 55px;
  width: 10%;
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
  font-size: 20px
}
#board-data {
  flex: 8;
  display: flex;
  flex-direction: column;
  height: auto;
  overflow-y: scroll;
}
#category-user-nickname {
  margin-left: 10px;
  max-font-size: 22px;
  font-weight: 400;
}
.board-btn-list {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px 0 20px;
}
.user-btn-list {
  display: flex;
  justify-content: end;
  align-items: end;
}
#write-comment-div {
  border-top: 1px solid #BCBCBC;
  border-bottom: 1px solid #BCBCBC;
}
#comment-div {
  width: 100%;
  display: flex;
  align-items: center;
  padding-left: 12px;
}
#comment-textarea {
  width: 100%;
  display: flex;
  align-items: center;
}
#comment-btn {
  height: 100%;
  display: flex;
  justify-items: start;
  padding-top: 10px;
}
#comment-list-div {
  border-bottom: 1px solid #D9D9D9;
}
</style>
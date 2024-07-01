<template>
  <div>
    <div>
      <v-hover v-slot="{ isHovering, props }">
        <v-sheet
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
                    <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/' + commentInfo.writerProfile"></v-img>
                  </v-avatar>
                </div>
                <div id="comment-writer">
                  {{commentInfo.writer}}
                </div>
              </div>

              <div v-if="joinCategoryUserInfo.nickname === commentInfo.writer" id="removeComment" style="display: flex; justify-content: end;">
                <v-btn @click="deleteContent(commentInfo.id)" icon="mdi-trash-can-outline" variant="text"></v-btn>
              </div>
            </div>

            <div id="comment-content-div">
              <div style="margin-left: 66px">
                {{ commentInfo.comment }}
              </div>
            </div>
          </div>
          <div style="width: 100%;">
            <div class="board-btn-list" style="border-top: 1px solid #D9D9D9; height: 50px;">
              <div>
                <v-btn @click="writeComment" class="disable-btn" density="compact" icon="mdi-message-outline" variant="plain"></v-btn> {{commentInfo.commentCount}}
              </div>
              <div v-if="commentInfo.isLike">
                <v-btn @click="commentLike(commentInfo.id)" class="disable-btn" density="compact" icon="mdi-heart" color="red" variant="plain"></v-btn>{{commentInfo.likeCount}}
              </div>
              <div v-else>
                <v-btn @click="commentLike(commentInfo.id)" class="disable-btn" density="compact" icon="mdi-heart-outline" variant="plain"></v-btn>{{commentInfo.likeCount}}
              </div>
              <div v-if="commentInfo.isBookmark">
                <v-btn @click="commentBookmark(commentInfo.id)" class="disable-btn" density="compact" icon="mdi-bookmark" color="blue" variant="plain"></v-btn>{{commentInfo.bookmarkCount}}
              </div>
              <div v-else>
                <v-btn @click="commentBookmark(commentInfo.id)" class="disable-btn" density="compact" icon="mdi-bookmark-outline" variant="plain"></v-btn>{{commentInfo.bookmarkCount}}
              </div>
              <div>
                <v-btn class="disable-btn" density="compact" icon="mdi-export-variant" disabled="true" variant="plain"></v-btn>
              </div>
            </div>
          </div>
        </v-sheet>
      </v-hover>

      <div v-if="addCommentFlag">
        <div id="comment-div">
          <div id="comment-textarea">
            <v-textarea
                no-resize
                v-model="comment"
                :rows="textareaRow"
                @click:control="handleAppendInner"
                variant="plain" density="compact" placeholder="답글 작성" style="text-align: center; padding-top: 8px"></v-textarea>
          </div>

          <div id="comment-btn">
            <v-btn @click="addComment(commentInfo.id, this.comment)" color="blue" variant="flat">작성</v-btn>
          </div>
        </div>
      </div>
    </div>

    <div v-if="commentInfo.commentCount > 0">
      <div id="comment-list-div" v-for="a in commentInfo.commentResponseDTO" :key="a" style="margin-left: 30px; display: flex;">
        <v-icon icon="mdi-arrow-right-bottom" size="30" style="padding-top: 35px;"></v-icon>
        <comment-view
            style="width: 100%"
            :commentInfo="a"
            :joinCategoryUserInfo="joinCategoryUserInfo"
            @saveReComment="saveReComment"
            @deleteComment="deleteComment"
            @likeComment="likeReComment"
            @bookmarkComment="bookmarkReComment"
        ></comment-view>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: "CommentView",
  props: ['commentInfo', 'joinCategoryUserInfo'],
  data(){
    return {
      postItems: [
        {icon: 'mdi-message-outline', value: 0},
        {icon: 'mdi-heart-outline', value: 1},
        {icon: 'mdi-bookmark-outline', value: 2},
        {icon: 'mdi-export-variant',},
      ],
      comment: "",
      textareaRow:1,
      addCommentFlag: false,
      routeName: '',

    }
  },
  methods: {
    checkRouteName(){
      if(this.$route.name === 'ArtistMarketReadView'){
        return 1;
      }else if(this.$route.name === 'ArtistBoardReadView'){
        return 2;
      }

      return 0;
    },
    writeComment(){
      this.addCommentFlag = !this.addCommentFlag;
    },
    handleAppendInner(){
      this.textareaRow = 5;
    },
    addComment(id, comment){
      let boardCommentId = id;
      this.$emit('saveReComment', {boardCommentId, comment})
    },
    saveReComment(payload){
      const {boardCommentId, comment} = payload
      this.addComment(boardCommentId, comment);
    },
    deleteContent(id){
      let commentId = id;
      this.$emit('deleteComment', {commentId})
    },
    deleteComment(payload){
      const {commentId} = payload;
      this.deleteContent(commentId);
    },
    commentLike(commentId){
      this.$emit('likeComment', {commentId})
    },
    likeReComment(payload){
      const {commentId} = payload;
      this.commentLike(commentId)
    },
    commentBookmark(commentId){
      this.$emit('bookmarkComment', {commentId})
    },
    bookmarkReComment(payload){
      const {commentId} = payload;
      this.commentBookmark(commentId)
    }
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
#comment-list-div {
  border-bottom: 1px solid #D9D9D9;
}
#comment-div {
  padding: 0 20px;
  width: 100%;
  display: flex;
  align-items: center;
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
</style>
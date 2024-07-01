<template>
    <div id="artist-layout">
      <div id="idol-category">
        <artist-board-category
            :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"
        ></artist-board-category>
      </div>

      <div id="board-content-div">
        <div id="search-div">
         <artist-search-bar :artist="artist"></artist-search-bar>
        </div>

        <div id="board-data">
          <div id="board-title" class="py-2">
            <v-btn @click="moveArtistHome" variant="plain" color="blue" icon="mdi-arrow-left"></v-btn> <p>POST</p>
          </div>
          <div id="board-div" style="padding-left: 12px">
            <v-sheet style="display: flex; width: 100%" color="backColor">
              <div>
                <v-menu max-width="150px">
                  <template v-slot:activator="{ props }">
                    <v-btn v-bind="props" variant="text" max-width="60px" max-height="60px">
                      <v-avatar max-size="60">
                        <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+artistOneBoard.writerProfile"></v-img>
                      </v-avatar>
                    </v-btn>
                  </template>

                  <v-card>
                    <v-card-text>
                      <div class="mx-auto text-center">
                        <v-btn variant="text" rounded @click="moveProfile(artistOneBoard.writer)"> 프로필 이동 </v-btn>
                        <v-divider v-if="artistOneBoard.writer !== joinCategoryUserInfo.nickname"  class="my-3"></v-divider>
                        <v-btn v-if="artistOneBoard.writer !== joinCategoryUserInfo.nickname" @click="addChat(artistOneBoard.writer)" variant="text" rounded> 채팅하기 </v-btn>
                      </div>
                    </v-card-text>
                  </v-card>
                </v-menu>
              </div>

              <div id="board-content">
                <div id="board-writer">
                  {{ artistOneBoard.writer }}
                </div>
                <div id="content-div">
                  <div style="padding-bottom: 10px" v-html="convertNewLineToBr(artistOneBoard.content)"></div>
                  <div v-if="artistOneBoard.boardType === 'IMAGE'" style="height: auto">
                    <v-carousel
                        height="400"
                        hide-delimiters
                        color="blue"
                        style="background-color: rgba(0,0,0,0.02); border-radius: 12px"
                    >
                      <template v-slot:prev="{ props }">
                        <v-btn
                            icon="mdi-chevron-left"
                            @click="props.onClick"
                            color="blue"
                            variant="plain"
                        ></v-btn>
                      </template>
                      <template v-slot:next="{ props }">
                        <v-btn
                            icon="mdi-chevron-right"
                            @click="props.onClick"
                            color="blue"
                            variant="plain"
                        ></v-btn>
                      </template>
                      <v-carousel-item
                          v-for="(slide, i) in artistOneBoard.image"
                          :key="i"
                          :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/' + slide"
                      >
                      </v-carousel-item>
                    </v-carousel>
                  </div>

                  <div v-if="artistOneBoard.boardType === 'VOTE'" style="padding-right: 72px">
                    <div v-if="artistOneBoard.postVoteResponseDTO.expireVote">
                      <p style="text-align: center; font-size: large; margin-bottom: 10px"> 투표 종료</p>
                      <div style="display: flex; width: 100%;">
                        <div style="display: flex; flex-direction: column; height: 100%; width: 100%">
                          <div
                              style="display: flex; flex-direction: column; height: 100%; width: 100%"
                              v-for="(c, index) in artistOneBoard.postVoteResponseDTO.choiceCount" :key="c"
                          >
                            <v-btn
                                v-if="index == artistOneBoard.postVoteResponseDTO.userVoteChoice"
                                :disabled="true"
                                color="pink"
                                variant="tonal" class="ma-1" density="comfortable" :value="index" style="height: 40px; border: 1px solid hotpink; opacity: 1" >
                              <v-icon icon="mdi-check"></v-icon>
                              {{artistOneBoard.postVoteResponseDTO.choice[index]}}
                            </v-btn>
                            <v-btn
                                v-else
                                :disabled="true"
                                color="pink"
                                variant="text" class="ma-1" density="comfortable" :value="index" style="height: 40px; border: 1px solid hotpink; opacity: 1">
                              {{artistOneBoard.postVoteResponseDTO.choice[index]}}
                            </v-btn>
                          </div>
                        </div>

                        <div style="display: flex; flex-direction: column; height: 100%;">
                          <v-btn class="ma-1" density="comfortable" variant="text" :disabled="true" v-for="(c, index) in artistOneBoard.postVoteResponseDTO.choiceCount" :key="c" style="height: 40px; opacity: 1" color="black">
                            <p v-if="artistOneBoard.postVoteResponseDTO.voteResults[index] != null">{{artistOneBoard.postVoteResponseDTO.voteResults[index]}}%</p>
                            <p v-else> 0%</p>
                          </v-btn>
                        </div>
                      </div>
                    </div>
                    <div v-else>
                      <div v-if="artistOneBoard.postVoteResponseDTO.hasVoted === false">
                        <v-btn-toggle
                            v-model="voteItem"
                            color="pink"
                            variant="text"
                            mandatory
                            style="display: flex; flex-direction: column; height: 100%"
                        >
                          <v-btn class="ma-1" density="comfortable" v-for="(c, index) in artistOneBoard.postVoteResponseDTO.choiceCount" :key="c" :value="index" style="height: 40px; border: 1px solid hotpink">
                            {{artistOneBoard.postVoteResponseDTO.choice[index]}}
                          </v-btn>
                        </v-btn-toggle>

                        <div style="padding-top: 6px">
                          {{formattedDate(artistOneBoard.postVoteResponseDTO.expireTime)}}
                        </div>

                        <div style="display: flex; justify-content: end; padding: 10px 10px 0 10px">
                          <v-btn variant="tonal" color="blue" style="margin-right: 20px" @click="vote(voteItem)">
                            투표하기
                          </v-btn>
                          <v-btn variant="tonal" color="blue">
                            결과 확인
                          </v-btn>
                        </div>
                      </div>
                      <div v-else>
                        <div style="display: flex; width: 100%;">
                          <v-btn-toggle
                              :model-value="artistOneBoard.postVoteResponseDTO.userVoteChoice"
                              v-model="voteItem"
                              color="pink"
                              variant="text"
                              mandatory
                              style="display: flex; flex-direction: column; height: 100%; width: 100%"
                          >
                            <v-btn class="ma-1" density="comfortable" v-for="(c, index) in artistOneBoard.postVoteResponseDTO.choiceCount" :key="c" :value="index" style="height: 40px; border: 1px solid hotpink">
                              {{artistOneBoard.postVoteResponseDTO.choice[index]}}
                            </v-btn>
                          </v-btn-toggle>

                          <div style="display: flex; flex-direction: column; height: 100%;">
                            <v-btn class="ma-1" density="comfortable" variant="text" :disabled="true" v-for="(c, index) in artistOneBoard.postVoteResponseDTO.choiceCount" :key="c" style="height: 40px; opacity: 1" color="black">
                              <p v-if="artistOneBoard.postVoteResponseDTO.voteResults[index] != null">{{artistOneBoard.postVoteResponseDTO.voteResults[index]}}%</p>
                              <p v-else> 0%</p>
                            </v-btn>
                          </div>
                        </div>

                        <div style="padding-top: 6px">
                          {{formattedDate(artistOneBoard.postVoteResponseDTO.expireTime)}}
                        </div>

                        <div style="display: flex; justify-content: end; padding: 10px 0">
                          <v-btn variant="tonal" color="blue" style="margin-right: 20px" @click="reVote(voteItem)">
                            재투표하기
                          </v-btn>
                        </div>
                      </div>
                    </div>


                  </div>
                </div>

                <div id="board-create-date">
                  {{ formattedDate(artistOneBoard.createDate) }} ·  20 views
                </div>
                <div class="board-btn-list">
                  <div>
                    <v-btn class="disable-btn" density="compact" icon="mdi-message-outline" variant="plain"></v-btn> {{artistOneBoard.commentCount}}
                  </div>
                  <div v-if="artistOneBoard.like">
                    <v-btn @click="lickBoard" class="disable-btn" density="compact" icon="mdi-heart" color="red" variant="plain"></v-btn>{{artistOneBoard.likeCount}}
                  </div>
                  <div v-else>
                    <v-btn @click="lickBoard" class="disable-btn" density="compact" icon="mdi-heart-outline" variant="plain"></v-btn>{{artistOneBoard.likeCount}}
                  </div>
                  <div v-if="artistOneBoard.bookmark">
                    <v-btn @click="bookmarkBoard" class="disable-btn" density="compact" icon="mdi-bookmark" color="blue" variant="plain"></v-btn>{{artistOneBoard.bookmarkCount}}
                  </div>
                  <div v-else>
                    <v-btn @click="bookmarkBoard" class="disable-btn" density="compact" icon="mdi-bookmark-outline" variant="plain"></v-btn>{{artistOneBoard.bookmarkCount}}
                  </div>
                  <div>
                    <v-btn class="disable-btn" density="compact" icon="mdi-export-variant" :disabled="true" variant="plain"></v-btn>
                  </div>
                </div>
              </div>
            </v-sheet>
          </div>
          <div class="pa-4" id="write-comment-div">
            <div style="display: flex">
              <v-avatar size="40">
                <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+joinCategoryUserInfo.userProfileImg"></v-img>
              </v-avatar>

              <div id="comment-div">
                <div id="comment-textarea">
                  <v-textarea
                      v-model="comment"
                      @click:control="handleAppendInner"
                      no-resize
                      :rows="textareaRow"
                      variant="plain" density="compact" placeholder="답글 작성" style="text-align: center; padding-top: 8px"></v-textarea>
                </div>

                <div id="comment-btn">
                  <v-btn @click="addComment" color="blue" variant="flat">작성</v-btn>
                </div>
              </div>
            </div>

            <div class="container-image">
              <div style="display: flex; flex-wrap: wrap; width: 100%;">
                <div v-for="(image, index) in previewPostImageURL" :key="index" class="preview-container" style="position: relative; height: 150px; overflow: hidden;  padding: 10px" :style="{ width: calcImageWidth() }">
                  <v-icon @click="deletePostImage(index)" style="position: absolute; z-index: 3; background-color: #bbbbbb">mdi-window-close</v-icon>
                  <img :src="image" alt="이미지 미리보기" style="width: 100%; height: 100%; object-fit: cover; margin: 5px; ">
                </div>
              </div>
              <input multiple type="file" style="display: none" accept=".jpg, .jpeg, .png" ref="uploadItemFile" @change="previewPostImage" />
            </div>

            <div class="container-image">
              <div v-if="previewPostGifURL" style="position: relative; display: flex; overflow: hidden; overflow-x: auto; flex-wrap: wrap;">
                <v-icon @click="deletePostGif" style="position: absolute; z-index: 3; background-color: #bbbbbb">mdi-window-close</v-icon>
                <img :src="this.previewPostGifURL" alt="이미지 미리보기" style="width: 150px; height: 150px; object-fit: cover; margin: 5px;">
              </div>
              <input multiple type="file" style="display: none" accept="image/gif" ref="uploadGIFItemFile" @change="previewPostGif" />
            </div>

            <div v-if="textareaRow > 1">
              <v-btn v-for="(item, index) in postWriteItems" variant="text" :key="index" color="blue" :icon="item.icon" :disabled="item.isDisabled" @click="postItem(index)"></v-btn>
            </div>
          </div>

          <div id="comment-list-div" v-for="comment in artistBoardComment" :key="comment">
            <comment-view
                :commentInfo="comment"
                :joinCategoryUserInfo="joinCategoryUserInfo"
                @saveReComment="saveReComment"
                @deleteComment="deleteComment"
                @likeComment="likeComment"
                @bookmarkComment="bookmarkComment"
            ></comment-view>
          </div>
        </div>
      </div>

  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";
import {timeForToday} from "@/constant/time/timeParser";
import moment from 'moment';
import router from "@/router";
import CommentView from "@/components/artist/comment/CommentView.vue";
import ArtistSearchBar from "@/components/artist/search/ArtistSearchBar.vue";

export default defineComponent({
  props: ['joinCategoryUserInfo', 'artist', 'artistOneBoard', 'artistBoardComment'],
  name: "ArtistBoardReadPageView",
  components: {ArtistSearchBar, CommentView, ArtistBoardCategory},
  data() {
    return {
      textareaRow: 1,
      postWriteItems: [
        {icon: 'mdi-image-outline', isDisabled: false, api: 'image'},
        {icon: 'mdi-file-gif-box', isDisabled: false, api: 'gif'},
        {icon: 'mdi-map-marker-outline', isDisabled: false, api: 'map'},
      ],
      postItems: [
        {icon: 'mdi-message-outline', value: 0},
        {icon: 'mdi-heart-outline', value: 1},
        {icon: 'mdi-bookmark-outline', value: 2},
        {icon: 'mdi-export-variant',},
      ],
      voteItem: undefined,
      comment: "",
      previewPostImageURL: [],
      selectPostImageFile: [],
      previewPostGifURL: '',
      selectPostGifFIle: [],
    }
  },
  methods: {
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
    handleAppendInner(){
      this.textareaRow = 5;
    },
    timeForToday,
    formattedDate(date) {
      return moment(date).format('hh:mm a · yyyy/MM/DD');
    },
    moveArtistHome(){
      //let artist = this.$route.params.artist;
      router.push({name: 'ArtistHomePageView'})
    },
    postItem(index) {
      if (index === 0) {
        // 이미지 업로드
        this.addPostImage();
      }else if(index === 1){
        this.addPostGif();
      }else if(index === 2){
        this.isAddVote = true;
      }

      for (let i = 0; i < this.postWriteItems.length; i++) {
        if (index !== i) {
          this.postWriteItems[i].isDisabled = true;
        }
      }
    },
    addPostImage(){
      this.$refs.uploadItemFile.click();
    },
    previewPostImage(event){
      const files = event.target.files;

      console.log('test1')
      if (files.length + this.previewPostImageURL.length > 4) {
        alert('이미지는 4개까지 작성할 수 있습니다');

        if (this.previewPostImageURL.length === 0){
          this.postWriteItems.map((itme) => {
            itme.isDisabled = false
          })
        }
      } else {
        for (let i = 0; i < files.length; i++) {
          console.log('test2')
          const reader = new FileReader();

          reader.onload = () => {
            this.previewPostImageURL.push(reader.result);
          };
          reader.readAsDataURL(files[i]);
        }
        this.selectPostImageFile.push(files);
      }
    },
    deletePostImage(index){
      console.log('preview: '+ this.previewPostImageURL.length)
      this.previewPostImageURL.splice(index, 1)
      this.selectPostImageFile.splice(index, 1)
      console.log('preview: '+ this.previewPostImageURL.length)

      if (this.previewPostImageURL.length === 0){
        this.postWriteItems.map((item) => {
          item.isDisabled = false
        })
        this.$refs.uploadItemFile.value = null;
      }

    },
    addPostGif(){
      this.$refs.uploadGIFItemFile.click();
    },
    previewPostGif(event) {
      const files = event.target.files;

      console.log(files)
      if (files.length + this.previewPostGifURL.length > 1) {
        alert('움짤은 1개만 작성할 수 있습니다');
        if (this.previewPostGifURL.length === 0){
          this.postWriteItems.map((itme) => {
            itme.isDisabled = false
          })
        }
      } else {
        const reader = new FileReader();
        reader.onload = () => {
          this.previewPostGifURL = reader.result;
        };

        reader.readAsDataURL(files[0]);

        this.selectPostGifFIle.push(files[0]);
      }
    },
    deletePostGif(){
      this.previewPostGifURL = '';
      this.selectPostGifFIle = [];

      if (this.previewPostImageURL.length === 0){
        this.postWriteItems.map((itme) => {
          itme.isDisabled = false
        })

        this.$refs.uploadGIFItemFile.value = null;
      }
    },
    calcImageWidth() {
      const percentage = 100 / Math.min(this.previewPostImageURL.length, 4);
      return {
        width: `${percentage}%`, // 이미지 너비 설정
        height: 'auto' // 이미지 높이는 자동으로 조절됨
      };
    },
    vote(choice){
      if(choice === undefined){
        alert('선택지를 선택해주세요.')
      }else {
        this.$emit('vote', {choice})
      }
    },
    reVote(choice){
      if(choice === this.artistOneBoard.postVoteResponseDTO.userVoteChoice){
        alert('이미 투표한 선택지입니다.')
      }else {
        this.$emit('vote', {choice})
      }
    },
    addComment(){
      const disabledItems = this.postWriteItems.filter(item => !item.isDisabled);
      let apiName = disabledItems[0].api;
      console.log("apiName: "+apiName)

      if(this.comment !== '') {
        const formData = new FormData();
        formData.append('comment', this.comment);
        if (disabledItems.length === 3) {
          const {comment} = this;
          console.log(comment)
          //comment, boardId
          let boardCommentId = "null";
          this.$emit('post', {comment, boardCommentId})
        } else if (apiName === 'image') { // 이미지
          if (this.selectPostImageFile.length > 0) {
            for (let i = 0; i < this.selectPostImageFile[0].length; i++) {
              let selectPostImageFileElementElement = this.selectPostImageFile[0][i];
              formData.append('postImg', selectPostImageFileElementElement);
            }
          }

          /*for (const pair of formData.entries()) {
            console.log(pair[0]); // 키
            console.log(pair[1]); // 값
          }*/
        } else if (apiName === 'gif') {
          console.log('gif')
          let selectPostImageFileElementElement = this.selectPostGifFIle[0]
          formData.append('postImg', selectPostImageFileElementElement)

        }
      }else {
        alert("내용 작성 후 게시글을 저장해주세요")
      }
    },
    handleClickOutside(event) {
      const writeCommentDiv = document.getElementById('write-comment-div');
      if (!writeCommentDiv.contains(event.target)) {
        this.textareaRow = 1;
      }
    },
    saveReComment(payload){
      const formData = new FormData();
      formData.append('comment', this.comment);

      const {comment, boardCommentId} = payload;
      this.$emit('post', {comment, boardCommentId})
    },
    deleteComment(payload){
      const {commentId} = payload;
      this.$emit('deleteComment', {commentId})
    },
    lickBoard(){
      this.$emit('lickBoard')
    },
    likeComment(payload){
      const {commentId} = payload;
      this.$emit('likeComment', {commentId})
    },
    bookmarkBoard(){
      this.$emit('bookmarkBoard')
    },
    bookmarkComment(payload){
      const {commentId} = payload;
      this.$emit('bookmarkComment', {commentId})
    },
    convertNewLineToBr(text) {
      return text.replace(/\n/g, '<br>');
    }
  },
  created() {
    if (this.artistOneBoard && this.artistOneBoard.postVoteResponseDTO && this.artistOneBoard.postVoteResponseDTO.hasVoted && !this.artistOneBoard.postVoteResponseDTO.expireVote) {
      this.voteItem = this.artistOneBoard.postVoteResponseDTO.userVoteChoice;
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside);
  }
})
</script>

<style scoped>
#artist-layout {
  height: 100vh;
  display: flex;
  position: relative;
}
#idol-category {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #BCBCBC;
}
#board-content-div {
  flex: 3;
  //border: 3px solid cadetblue;
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
  width: 100%;
  display: flex;
  flex-direction: column;
  height: auto;
  overflow-y: scroll;
}
#board-title{
  display: flex;
  font-size: 30px;
  align-items: center;
  border-bottom: 1px solid #D9D9D9;
  margin-bottom: 12px;
}
#board-content {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
#board-writer {
  height: 50px;
  display: flex;
  align-items: center;
  padding-left: 12px;
  font-size: 20px;
}
#content-div {
  width: 100%;
  flex-grow: 1;
  padding: 0 12px 12px 12px;
}
#board-create-date {
  width: 100%;
  display: flex;
  padding-left: 12px;
  color: #868686;
  margin-top: 10px;
}
.board-btn-list {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px 0 20px;
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
.container-image {
  height: auto;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}
</style>
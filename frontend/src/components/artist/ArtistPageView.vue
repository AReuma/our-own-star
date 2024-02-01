<template>
  <div id="artist-layout">
    <div id="idol-category">
      <artist-board-category :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"></artist-board-category>
    </div>

    <div style="flex: 3; width: 100%; display: flex; flex-direction: column">
      <div style="height: 10%; border-bottom: 1px solid #BCBCBC; flex: 1;  display: flex; align-items: center; justify-content: center;">
        <input id="search-box" type="text">
        <v-btn id="search-btn" color="blue" :ripple="false">검색</v-btn>
      </div>
      <router-view style="flex: 8; overflow-y: scroll" v-slot="{ Component }">
        <component :is="Component" :joinCategoryUserInfo="joinCategoryUserInfo"/>
      </router-view>
    </div>

    <v-dialog persistent width="800" v-model="postDialog">
      <v-card width="800" height="calc(60vh)" style="border-radius: 12px; display: flex; flex-direction: column; font-family: Dovemayo-Medium, sans-serif">
        <v-card-actions class="text-start" style="height: auto">
          <v-btn icon="mdi-close" @click="closePostiDialog"></v-btn>
        </v-card-actions>

        <v-card-text style="display: flex; flex: 3; flex-direction: column;">
          <div style="display: flex; width: 100%;">
            <div style="width: auto;">
              <v-avatar size="60">
                <v-img src="@/assets/profile/profile-test.jpg"></v-img>
              </v-avatar>
            </div>
            <div class="pl-3" style="flex-grow: 1; width: 100%;">
              <v-textarea v-model="content" id="textBoc" variant="plain" no-resize rows="5" placeholder="작성중.."></v-textarea>
            </div>
          </div>
          <div style="display: flex; flex-direction: column;">
            <div class="container-image">
              <div style="display: flex; flex-wrap: wrap; width: 100%;">
                <div v-for="(image, index) in previewPostImageURL" :key="index" class="preview-container" style="height: 150px; overflow: hidden;  padding: 10px" :style="{ width: calcImageWidth() }">
                  <v-icon @click="deletePostImage(index)" style="position: absolute; z-index: 3; background-color: #bbbbbb">mdi-window-close</v-icon>
                  <img :src="image" alt="이미지 미리보기" style="width: 100%; height: 100%; object-fit: cover; margin: 5px;">
                </div>
              </div>
              <input multiple type="file" style="display: none" accept=".jpg, .jpeg, .png" ref="uploadItemFile" @change="previewPostImage" />
            </div>

            <div class="container-image">
              <div v-if="previewPostGifURL" style="display: flex; overflow: hidden; overflow-x: auto; flex-wrap: wrap;">
                <v-icon @click="deletePostGif" style="position: absolute; z-index: 3; background-color: #bbbbbb">mdi-window-close</v-icon>
                <img :src="this.previewPostGifURL" alt="이미지 미리보기" style="width: 150px; height: 150px; object-fit: cover; margin: 5px;">
              </div>
              <input multiple type="file" style="display: none" accept="image/gif" ref="uploadGIFItemFile" @change="previewPostGif" />
            </div>

            <div v-if="isAddVote" class="container-vote" style=" border: 1px solid #BCBCBC; border-radius: 12px; padding: 10px">
                <div style="display: flex; flex-direction: column;">
                  <v-btn @click="addChoice" variant="outlined" color="blue" style="width: 100px; margin-left: auto; margin-bottom: 15px">선택지 추가</v-btn>
                  <div style="display: flex; flex-direction: column; align-items: center;">
                    <v-text-field v-for="(choice, index) in voteChoice" :key="index" v-model="choice.value" style="width: 100%;" density="compact" variant="outlined" label="" append-icon="mdi-close" @click:append="deleteVoteChoice(index)"></v-text-field>
                  </div>
                </div>

                <div style="display: flex">
                  <v-select
                      v-model="voteDay"
                      class="ma-1"
                      label="Day"
                      variant="outlined"
                      :items="[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"
                      density="compact"
                  ></v-select>
                  <v-select
                      v-model="voteHour"
                      class="ma-1"
                      label="Hours"
                      variant="outlined"
                      :items="[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"
                      density="compact"
                  ></v-select>
                </div>

                <div style="width: 100%; display: flex; justify-content: center">
                  <v-btn variant="outlined" color="pink" style="width: 100%" @click="deleteVote"> 투표 삭제하기 </v-btn>
                </div>
              </div>
          </div>
        </v-card-text>
        <div style="border-top: 1px solid #BCBCBC; width: 100%;" class="px-4">
          <v-card-actions style="height: auto; margin: 0">
            <v-btn v-for="(item, index) in postItems" :key="index" color="blue" :icon="item.icon" :disabled="item.isDisabled" @click="postItem(index)"></v-btn>
            <v-spacer></v-spacer>
            <v-btn @click="savePost" variant="flat" color="blue" style="font-size: 18px; width: 80px">작성</v-btn>
          </v-card-actions>
        </div>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";

export default defineComponent({
  name: "ArtistPageView",
  components: {ArtistBoardCategory},
  props: ['artist', 'joinCategoryUserInfo'],
  data(){
    return {
      content: '',
      postItems: [
        {icon: 'mdi-image-outline', isDisabled: false, api: 'image'},
        {icon: 'mdi-file-gif-box', isDisabled: false, api: 'gif'},
        {icon: 'mdi-vote-outline', isDisabled: false, api: 'vote'},
        {icon: 'mdi-map-marker-outline', isDisabled: false, api: 'map'},
      ],
      postDialog: false,
      previewPostImageURL: [],
      selectPostImageFile: [],
      previewPostGifURL: '',
      selectPostGifFIle: [],
      isAddVote: false,
      voteChoice: [
        {value: ''},
        {value: ''},
      ],
      voteDay: 1,
      voteHour: 1,
    }
  },
  methods: {
    savePost(){
      const disabledItems = this.postItems.filter(item => !item.isDisabled);
      let apiName = disabledItems[0].api;
      console.log("apiName: "+apiName)

      if(this.content !== '') {
        const formData = new FormData();
        formData.append('content', this.content);
        if (disabledItems.length === 4) {
          const {content} = this;
          this.$emit('post', {content})
        } else if (apiName === 'image') { // 이미지
          if (this.selectPostImageFile.length > 0) {
            for (let i = 0; i < this.selectPostImageFile[0].length; i++) {
              let selectPostImageFileElementElement = this.selectPostImageFile[0][i];
              formData.append('postImg', selectPostImageFileElementElement);
            }
          }

          this.$emit('postImg', formData)
        } else if (apiName === 'gif') {
          console.log('gif')
          let selectPostImageFileElementElement = this.selectPostGifFIle[0]
          formData.append('postImg', selectPostImageFileElementElement)

          this.$emit('postImg', formData)
        } else if (apiName === 'vote') {
          console.log('vote')
          const {voteHour, voteDay, content, voteChoice} = this;

          let isVote = this.voteChoice.some(choice => choice.value === '');

          if (voteChoice.length >= 2 && !isVote && !(this.voteDay === 0 && this.voteHour === 0)){
            this.$emit('postVote', {voteHour, voteDay, content, voteChoice});
          } else {
            alert('vote를 채워주세요')
          }


        } else if (apiName === 'map') {
          console.log('map')
        }
      }else {
        alert("내용 작성 후 게시글을 저장해주세요")
      }
      // gif
      // 투표
      // 주소
    },
    closePostiDialog(){
      this.postDialog = false
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

      for (let i = 0; i < this.postItems.length; i++) {
        if (index !== i) {
          this.postItems[i].isDisabled = true;
        }
      }
    },
    addPostImage(){
      this.$refs.uploadItemFile.click();
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
          this.postItems.map((itme) => {
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
    previewPostImage(event){
      const files = event.target.files;

      if (files.length + this.previewPostImageURL.length > 4) {
        alert('이미지는 4개까지 작성할 수 있습니다');

        if (this.previewPostImageURL.length === 0){
          this.postItems.map((itme) => {
            itme.isDisabled = false
          })
        }
      } else {
        for (let i = 0; i < files.length; i++) {
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
        this.postItems.map((itme) => {
          itme.isDisabled = false
        })
      }
    },
    deletePostGif(){
      this.previewPostGifURL = '';
      this.selectPostGifFIle = [];

      if (this.previewPostImageURL.length === 0){
        this.postItems.map((itme) => {
          itme.isDisabled = false
        })
      }
    },
    calcImageWidth() {
      const percentage = 100 / Math.min(this.previewPostImageURL.length, 4);
      return `${percentage}%`;
    },
    addChoice(){
      if(this.voteChoice.length > 3){
        alert('투표 선택지는 4개까지 만들 수 있습니다.')
      }else {
        this.voteChoice.push({value: ''});
      }
    },
    deleteVoteChoice(index){
      if (this.voteChoice.length < 3) {
        alert("투표 선택지는 2개보다 작을 수 없습니다.")
      }else {
        this.voteChoice.splice(index, 1);
      }
    },
    deleteVote(){
      this.voteChoice = [
        {value: ''},
        {value: ''},
      ];

      this.postItems.map((itme) => {
        itme.isDisabled = false;
      })

      this.isAddVote = false;
    },
    resetPostDialog(){
      this.content = '';
      this.selectPostImageFile = [];
      this.previewPostImageURL = [];
      this.selectPostGifFIle = [];
      this.previewPostGifURL = '';
      this.voteChoice = [
        {value: ''},
        {value: ''},
      ];

      this.postItems.map((itme) => {
        itme.isDisabled = false;
      })

      this.postDialog = false;
    }
  },
  beforeRouteUpdate(to, from, next) {
    console.log("testtesttest");
    // 현재 라우터와 이전 라우터가 다르다면 필요한 동작 수행
    if (to.fullPath !== from.fullPath) {
      // 여기에서 직접 필요한 로직을 작성
      console.log("라우터가 변경되었습니다.");

      // 예: 페이지 이동 시 초기화 또는 데이터 로딩 등
      if (this.$route.name === to.name) {
        // 현재 라우터와 이동하려는 라우터의 name이 같으면 처리
        this.$router.push(to); // 라우터 이동
      }
    }

    next(); // next() 호출 필수
  }

})
</script>

<style scoped>
>>> #textBoc{
  font-size: 20px;
  font-weight: 100;
  text-transform: capitalize;
}
#artist-layout {
  width: 100%;
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
.container-image {
  height: auto;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}
.container-vote {
  width: 100%;
  display: flex;
  justify-content: center;
  flex-direction: column;
  padding:  10px 40px;
}
</style>
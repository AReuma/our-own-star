<template>
  <div id="artist-layout">
    <div id="idol-category">
      <div id="idol-category-title">
        <div style="flex: 1;">
          <v-btn @click="backPage" size="70" variant="text">
            <v-icon size="50" icon="mdi-chevron-left" color="blue"></v-icon>
          </v-btn>
        </div>

        <div id="idol-category-artist-name">
         {{artist}}
        </div>
      </div>

      <div style="margin-top: 25px">
        <v-list lines="two" style="background-color: #F0F0F0; text-align: center;">
          <v-list-item
              v-for="(item, i) in items"
              :key="i"
              :value="item"
              color="primary"
              variant="text"
              @clickOnce="category(item.routerName)"
          >
            <v-list-item-title style=" font-size: 20px" v-text="item.name"></v-list-item-title>
          </v-list-item>
        </v-list>
      </div>

      <div style="display: flex; justify-content: center; margin-top: 30px;">
        <v-btn @click="writePost" color="blue" style="font-size: 18px" width="85%" height="60px">게시글 작성</v-btn>
      </div>


      <div class="pa-5" id="category-user-info">
        <div style="flex: 1">
          <v-avatar size="60">
            <v-img src="@/assets/profile/profile-test.jpg"></v-img>
          </v-avatar>
        </div>

        <div id="category-user-nickname">
          {{ joinCategoryUserInfo.nickname }}
        </div>

        <div style="position: relative;  flex: 1">
          <v-icon size="50" icon="mdi-chevron-right" color="blue"></v-icon>
        </div>
      </div>
    </div>

    <div style="flex: 3; border: 3px solid cadetblue; width: 100%; display: flex; flex-direction: column">
      <div style="height: 10%; border: 1px solid red; flex: 1;  display: flex; align-items: center; justify-content: center;">
        <input id="search-box" type="text">
        <v-btn id="search-btn" color="blue" :ripple="false">검색</v-btn>
      </div>
      <router-view style="flex: 8; overflow-y: scroll;" v-slot="{ Component }">
        <component style="border: 1px solid green" :is="Component" />
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

            <div v-if="isAddVote" class="container-vote">
                <div style="display: flex; flex-direction: column;">
                  <v-btn @click="addChoice" style="width: 100px; margin-left: auto; margin-bottom: 15px">선택지 추가</v-btn>
                  <div style="display: flex; flex-direction: column; align-items: center;">
                    <v-text-field v-for="(choice, index) in voteChoice" :key="index" v-model="choice.value" style="width: 100%;" density="compact" variant="outlined" label="" append-icon="mdi-close" @click:append="deleteVoteChoice(index)"></v-text-field>
                  </div>
                </div>

                <div style="display: flex">
                  <v-select
                      class="ma-1"
                      label="Day"
                      variant="outlined"
                      :items="[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"
                      density="compact"
                  ></v-select>
                  <v-select
                      class="ma-1"
                      label="Hours"
                      variant="outlined"
                      :items="[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"
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
import router from "@/router";

export default defineComponent({
  name: "ArtistPageView",
  props: ['artist', 'joinCategoryUserInfo'],
  data(){
    return {
      content: '',
      items: [
        {name: '홈', routerName: 'home', },
        {name: this.artist+' 마켓', routerName: 'ArtistMarketPageView',},
        {name: '북마크', routerName: 'ArtistBookMarkPageView',},
        {name: '인기글', routerName: 'ArtistPopularPostPageView',},
        {name: '마이페이지', routerName: 'ArtistMyInfoView',},
        {name: '내 챗팅', routerName: 5,},
      ],
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
      selectPostGifFIle: '',
      isAddVote: false,
      voteChoice: [
        {value: ''},
        {value: ''},
      ],
    }
  },
  methods: {
    savePost(){
      const disabledItems = this.postItems.filter(item => !item.isDisabled);
      let apiName = disabledItems[0].api;

      const formData = new FormData();
      formData.append('content', this.content);

      // 이미지
      if(apiName === 'image'){
        if (this.selectPostImageFile.length > 0){
          for(let i = 0; i < this.selectPostImageFile[0].length; i++){
            let selectPostImageFileElementElement = this.selectPostImageFile[0][i];
            formData.append('postImg', selectPostImageFileElementElement);
          }
        }

        this.$emit('postImg', formData)
      }else if(apiName === 'gif'){
        console.log('gif')
      }else if(apiName === 'vote'){
        console.log('vote')
      }else if(apiName === 'map'){
        console.log('map')
      }
      // gif
      // 투표
      // 주소
    },
    backPage(){
      router.go(-1)
    },
    category(routerName){
      console.log(routerName)
      router.push({name: routerName, params: {artist: this.artist}})
    },
    writePost(){
      this.postDialog = true
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

        this.selectPostGifFIle = files[0];
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
      this.selectPostGifFIle = '';

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
    }
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
#idol-category-title {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
}
#idol-category-artist-name {
  flex: 5;
  text-align: center;
  color: #3498DB;
  font-family: EF_jejudoldam, sans-serif;
  font-size: 25px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#category-user-nickname {
  margin-left: 10px;
  color: #000;
  flex: 3;
  text-align: center;
  font-size: 22px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#category-user-info {
  position:absolute;
  bottom: 0;
  display: flex;
  align-items: center;
  width: 25%;
  margin-bottom: 10px;
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
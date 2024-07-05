<template>
  <div style="width: 100%;">
    <div id="idol-category-title">
      <div style="flex: 1;">
        <v-btn @click="backPage" size="50" variant="text">
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
            @click="category(item.routerName)"
        >
          <v-list-item-title style=" font-size: 20px" v-text="item.name"></v-list-item-title>
        </v-list-item>
      </v-list>
    </div>

    <div style="display: flex; justify-content: center; margin-top: 30px;">
      <v-btn v-if="newRouteName === 'ArtistMarketPageView' || newRouteName === 'ArtistMarketModifyView' || newRouteName === 'ArtistMarketReadView'" @click="writeSellPost" color="blue" style="font-size: 18px" width="85%" height="60px">판매 게시글 작성</v-btn>
      <v-btn v-else @click="writePost" color="pink" style="font-size: 18px" width="85%" height="60px">게시글 작성</v-btn>
    </div>

    <div class="pa-5" id="category-user-info">
      <div style="flex: 1">
        <v-avatar max-size="50">
          <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+joinCategoryUserInfo.userProfileImg"></v-img>
        </v-avatar>
      </div>

      <div id="category-user-nickname">
        {{ joinCategoryUserInfo.nickname }}
      </div>

      <div style="position: relative;  flex: 1">
        <v-icon size="28" icon="mdi-chevron-right" color="blue"></v-icon>
      </div>
    </div>

    <v-dialog persistent width="800" v-model="postDialog">
      <v-card width="800" height="calc(60vh)" style="border-radius: 12px; display: flex; flex-direction: column; font-family: Dovemayo-Medium, sans-serif">
        <v-card-actions class="text-start" style="height: auto">
          <v-btn icon="mdi-close" @click="closePostiDialog"></v-btn>
        </v-card-actions>

        <v-card-text style="display: flex; flex: 3; flex-direction: column;">
          <div style="display: flex; width: 100%;">
            <div style="width: auto;">
              <v-avatar max-size="60">
                <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+joinCategoryUserInfo.userProfileImg"></v-img>
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

    <v-dialog persistent width="800" v-model="sellPostDialog">
      <v-card width="800" height="calc(60vh)" style="border-radius: 12px; display: flex; flex-direction: column; font-family: Dovemayo-Medium, sans-serif">
        <v-card-item title="판매 게시글 작성" style="border-bottom: 1px solid #BCBCBC; padding-bottom: 5px; ">
          <template v-slot:append>
            <v-btn icon="mdi-close" variant="text" @click="closeSellPostDialog" style="opacity: 1" color="pink"></v-btn>
          </template>
        </v-card-item>

        <div class="pl-5 mt-5" style="display: flex; ">
          <div style="display: flex; flex: 1; align-items: center; font-size: 18px; padding-right: 20px">
            판매상품 타이틀
          </div>
          <div style="display: flex; flex: 4; align-items: center; justify-content: end; width: 90%;" class="mr-9">
            <v-text-field v-model="title" counter="10" maxlength="10" variant="outlined" color="blue" type="text" style="height: 35px;" density="compact"></v-text-field>
          </div>
        </div>

        <v-card-text style="display: flex; flex: 3; flex-direction: column;">
          <div style="display: flex; width: 100%;">
            <div style="width: auto;">
              <v-avatar size="60">
                <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+joinCategoryUserInfo.userProfileImg"></v-img>
              </v-avatar>
            </div>
            <div class="pl-3" style="flex-grow: 1; width: 100%;">
              <v-textarea v-model="content" id="textBoc" variant="plain" no-resize rows="5" placeholder="작성중.."></v-textarea>
            </div>
          </div>

          <div style="display: flex; justify-content: end">
            <div style="display: flex; align-items: center; font-size: 25px; padding-right: 20px">
              가격:
            </div>
            <div style="display: flex; justify-content: end; align-items: center;">
              <v-checkbox v-model="nonePrice" label="가격미정" style="height: 35px; width: 100px" density="compact"></v-checkbox>
            </div>
            <div style="display: flex; align-items: center; justify-content: end;">
              <v-text-field v-model="price" variant="outlined" color="blue" type="text" :disabled="nonePrice" style="height: 35px; width: 100px" density="compact"></v-text-field>원
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
          </div>
        </v-card-text>
        <div style="border-top: 1px solid #BCBCBC; width: 100%;" class="px-4">
          <v-card-actions style="height: auto; margin: 0">
            <v-btn color="blue" icon="mdi-image-outline" @click="addSellImg"></v-btn>
            <v-spacer></v-spacer>
            <v-btn @click="saveSellPost" variant="flat" color="blue" style="font-size: 18px; width: 80px">작성</v-btn>
          </v-card-actions>
        </div>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";
import {VueCookieNext} from "vue-cookie-next";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

const imageConfig = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Content-Type': 'multipart/form-data'
  }
};

export default defineComponent({
  name: "ArtistBoardCategory",
  props: ['artist','joinCategoryUserInfo'],
  data(){
    return {
      items: [
        {name: '홈', routerName: 'ArtistHomePageView', },
        {name: '마켓', routerName: 'ArtistMarketPageView',},
        {name: '지도', routerName: 'ArtistMapMarkerPageView',},
        {name: '인기글', routerName: 'ArtistPopularPostPageView',},
        {name: '마이페이지', routerName: 'ArtistMyInfoView',},
        {name: '내 챗팅', routerName: 'ArtistChatListView',},
      ],
      postItems: [
        {icon: 'mdi-image-outline', isDisabled: false, api: 'image'},
        {icon: 'mdi-file-gif-box', isDisabled: false, api: 'gif'},
        {icon: 'mdi-vote-outline', isDisabled: false, api: 'vote'},
        {icon: 'mdi-map-marker-outline', isDisabled: false, api: 'map'},
      ],
      nonePrice: false,
      title: '',
      price: '',
      newRouteName: '',
      postDialog: false,
      sellPostDialog: false,
      content: '',
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
    backPage(){
      router.push({name: 'MainView'})
    },
    writePost(){
      this.postDialog = true;
    },
    writeSellPost(){
      this.sellPostDialog = true;
    },
    category(routerName){
      if(routerName === 'ArtistMarketPageView'){
        let page = 1;
        router.push({name: routerName, query: {page: page}})
      }else {
        router.push({name: routerName})
      }
    },
    closePostiDialog(){
      this.postDialog = false;
      this.resetPostDialog();
    },
    closeSellPostDialog(){
      this.sellPostDialog = false;
      this.resetPostDialog();
    },
    post(){
      const {content, artist} = this;
      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist, {content}, config)
          .then((res) => {
            console.log(res.data)
            this.resetPostDialog();
            window.location.reload()
          });
    },
    postImg(formData){
      const {artist} = this;
      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist + "/image", formData, imageConfig)
          .then((res) => {
            console.log(res.data)
            this.resetPostDialog();
            window.location.reload()
          });
    },
    postVote(){
      const {voteHour, voteDay, content, voteChoice} = this;
      let artist = this.artist;

      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist + "/vote", {voteHour, voteDay, content, voteChoice},config)
          .then((res) => {
            console.log(res.data)
            this.resetPostDialog();
            window.location.reload()
          });
    },
    postSellImg(formData){
      const {artist} = this;
      axios.post(API_BASE_URL + ARTIST_CATEGORY_BOARD +"/"+ artist + "/market", formData, imageConfig)
          .then((res) => {
            console.log(res.data)
            this.resetPostDialog();
            window.location.reload()
          });
    },
    saveSellPost() {
      if(this.content !== ''){
        if(this.selectPostImageFile.length < 1){
          alert("판매 게시글은 이미지가 필수입니다!")
        }else {
          alert('저장 준비 완료')
          const formData = new FormData();
          formData.append('content', this.content)

          if (this.title === ''){
            alert('제목을 작성해주세요')
            return;
          }else {
            formData.append('title', this.title)
          }

          let price = '';
          if(this.nonePrice){
            price = -1;
          }else {
            if(this.price === ''){
              alert('가격을 작성해주세요')
              return;
            }
            price = this.price
          }

          formData.append('price', price)

          for (let i = 0; i < this.selectPostImageFile[0].length; i++) {
            let selectPostImageFileElementElement = this.selectPostImageFile[0][i];
            formData.append('postImg', selectPostImageFileElementElement);
          }

          this.postSellImg(formData)
        }
      }else {
        alert('내용을 작성해주세요!')
      }
    },
    savePost(){
      const disabledItems = this.postItems.filter(item => !item.isDisabled);
      let apiName = disabledItems[0].api;
      console.log("apiName: "+apiName)

      if(this.content !== '') {
        const formData = new FormData();
        formData.append('content', this.content);
        if (disabledItems.length === 4) {
          //const {content} = this;
          this.post()
        } else if (apiName === 'image') { // 이미지
          if (this.selectPostImageFile.length > 0) {

            for(let j = 0; j < this.selectPostImageFile.length; j++) {
              for (let i = 0; i < this.selectPostImageFile[j].length; i++) {
                let selectPostImageFileElementElement = this.selectPostImageFile[j][i];
                formData.append('postImg', selectPostImageFileElementElement);
              }
            }
          }
          this.postImg(formData)
          //this.$emit('postImg', formData)
        } else if (apiName === 'gif') {
          console.log('gif')
          let selectPostImageFileElementElement = this.selectPostGifFIle[0]
          formData.append('postImg', selectPostImageFileElementElement)

          this.postImg(formData)
          //this.$emit('postImg', formData)
        } else if (apiName === 'vote') {
          console.log('vote')
          const {voteChoice} = this;
          let isVote = this.voteChoice.some(choice => choice.value === '');

          if (voteChoice.length >= 2 && !isVote && !(this.voteDay === 0 && this.voteHour === 0)){
            //this.$emit('postVote', {voteHour, voteDay, content, voteChoice});
            this.postVote();
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
    addChoice(){
      if(this.voteChoice.length > 3){
        alert('투표 선택지는 4개까지 만들 수 있습니다.')
      }else {
        this.voteChoice.push({value: ''});
      }
    },
    addSellImg(){
      this.addPostImage();
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
        this.$refs.uploadItemFile.value = null;
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
      this.price = '';
      this.title = '';
      this.nonePrice = false;
      this.voteChoice = [
        {value: ''},
        {value: ''},
      ];

      this.postItems.map((itme) => {
        itme.isDisabled = false;
      })

      this.postDialog = false;
      this.sellPostDialog = false;
    },
  },
  mounted() {
    this.newRouteName = this.$route.name;
  },
  watch: {
    '$route'(newRouteName){
      this.newRouteName = newRouteName.name;
    },
    nonePrice(value){
      if(value){
        this.price = '';
      }
    }
  }
})
</script>

<style scoped>
#idol-category-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
#idol-category-artist-name {
  flex: 6;
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
  max-font-size: 22px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#category-user-info {
  position:absolute;
  bottom: 0;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
</style>
<template>
  <div id="artist-layout">
    <div id="idol-category">
      <artist-board-category :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"></artist-board-category>
    </div>

    <div id="board-content-div">
      <div id="search-div">
        <input id="search-box" type="text">
        <v-btn id="search-btn" color="blue" :ripple="false">검색</v-btn>
      </div>

      <div class="pt-1" id="board-data" style="height: 100%; display: flex; align-items: center">
        <div id="board-title" style="display: flex; justify-content: start; width: 100%;">
          <v-btn @click="moveBack" variant="plain" color="blue" icon="mdi-arrow-left"></v-btn>
          <p> modify </p>
        </div>

        <div class="pr-4" style="width: 100%; display: flex; justify-content: end">
          <v-btn @click="modifyBtn" variant="text">수정 완료</v-btn>
        </div>

        {{artistMarketOneBoard}}
        <div class="px-5" style="border: 1px solid blue; width: 100%;">
          <div class="mt-5" style="display: flex;"> <!--// 제목-->
            <div style="display: flex; flex: 1; align-items: center; font-size: 18px; padding-right: 20px">
              판매상품 타이틀:
            </div>
            <div style="display: flex; flex: 4;">
              <v-text-field v-model="title" counter="10" maxlength="10" variant="outlined" color="blue" type="text" style="height: 35px;" density="compact"></v-text-field>
            </div>
          </div>

          <div class="mt-5" style="display: flex;"> <!--// 가격-->
            <div style="display: flex; align-items: center; font-size: 18px; padding-right: 20px;">
              가격:
            </div>
            <div style="display: flex; justify-content: end; align-items: end; flex-grow: 1">
              <div style="display: flex; justify-content: end; align-items: center;">
                <v-checkbox v-model="nonePrice" label="가격미정" style="height: 35px; width: 100px" density="compact"></v-checkbox>
              </div>
              <div style="display: flex; align-items: center; justify-content: end;">
                <v-text-field v-model="price" variant="outlined" color="blue" type="text" :disabled="nonePrice" style="height: 35px; width: 180px" density="compact"></v-text-field>원
              </div>
            </div>
          </div>

        <div> <!--// 내용-->
          <div class="mt-5" style="flex-grow: 1; width: 100%;">
            <v-textarea  v-model="content" id="textBoc" variant="outlined" no-resize rows="5" placeholder="작성중.."></v-textarea>
          </div>
        </div>

        <div> <!--// 이미지 추가/ 삭제/ 이미지는 4개까지 -->
          <div style="display: flex; flex-direction: column;">

            <div>삭제할 이미지를 선택해주세요</div>
            <div style="display: flex; width: 100%; height: 250px;">
              <div class="ma-4" style="border: 1px solid black; border-radius: 8px" v-for="(image, index) in artistMarketOneBoard.imgList" :key="index" :style="{ width: 100/artistMarketOneBoard.imgList.length+'%' }">
                <div style="display: flex; flex-direction: column; justify-content: center; height: 100%">
                  <div style="height: 20%">
                    <v-checkbox
                        density="compact"
                        v-model="selected"
                        :value="image.imgId"
                    ></v-checkbox>
                  </div>
                  <v-img style="height: 80%; background-color: rgba(49,49,62,0.09);" :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+image.imgUrl"></v-img>
                </div>
              </div>
            </div>

            <div class="container-image">
              <div style="display: flex; justify-content: end">
                <v-btn stacked="" variant="text" @click="addImage">이미지 추가
                  <template v-slot:prepend>
                    <v-icon color="pink">mdi-file-image-plus-outline</v-icon>
                  </template>
                </v-btn>
              </div>

              <div style="display: flex;">
                <div class="container-image">
                  <div style="display: flex; flex-wrap: wrap; width: 100%;">
                    <div v-for="(image, index) in previewPostImageURL" :key="index" class="preview-container" style="height: 150px; overflow: hidden;  padding: 10px; position: relative;" :style="{ width: calcImageWidth() }">
                      <v-icon @click="deleteAddImage(index)" style="position: absolute; top: 5px; right: 5px; z-index: 3; background-color: #bbbbbb">mdi-window-close</v-icon>
                      <img :src="image" alt="이미지 미리보기" style="width: 100%; height: 100%; object-fit: cover; margin: 5px;">
                    </div>
                  </div>
                  <input multiple type="file" style="display: none" accept=".jpg, .jpeg, .png" ref="uploadItemFile" @change="previewPostImage" />
                </div>
              </div>
            </div>

          </div>
        </div>

        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";

export default defineComponent({
  name: "ArtistMarketModifyPageView",
  components: {ArtistBoardCategory},
  props: {
    artist: {
      type: String,
      default: '',
    },
    joinCategoryUserInfo: {
      type: Array,
      default: null,
    },
    sellNum: {
      type: String,
      default: ''
    },
    artistMarketOneBoard: {
      type: Array,
      default: null,
    }
  },
  data(){
    return {
      title: '',
      content: '',
      price: '',
      nonePrice: false,
      previewPostImageURL: [],
      selectPostImageFile: [],
      selected: [],
      imgCount: 0
    }
  },
  methods: {
    moveBack(){
      const {artist, sellNum} = this;

      this.$router.push({ name: 'ArtistMarketReadView', params: { artist: artist, sellNum: sellNum } });
    },
    addImage(){
      this.$refs.uploadItemFile.click();
    },
    calcImageWidth() {
      const percentage = 100 / Math.min(this.artistMarketOneBoard.imgList.size, 4);
      return `${percentage}%`;
    },
    previewPostImage(event){
      const files = event.target.files;

      if (files.length + this.previewPostImageURL.length > 4) {
        alert('이미지는 4개까지 작성할 수 있습니다');
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
    deleteAddImage(index){
      console.log('preview: '+ this.previewPostImageURL.length)
      this.previewPostImageURL.splice(index, 1)
      this.selectPostImageFile.splice(index, 1)
      console.log('preview: '+ this.previewPostImageURL.length)

      if (this.previewPostImageURL.length === 0){
        this.$refs.uploadItemFile.value = null;
      }
    },
    modifyBtn(){
      // 제목, 가격, 내용, 추가할 이미지, 삭제할 이미지
      let selectedLength = this.selected ? this.selected.length : 0;
      let previewPostImageURLLength = this.previewPostImageURL ? this.previewPostImageURL.length : 0;

      let count = this.imgCount - selectedLength + previewPostImageURLLength

      if(count > 4){
        alert('이미지는 최대 4장만 올릴 수 있습니다')
      }else if(count < 1){
        alert('대표 사진 필수 입니다')
      }else {
        const formData = new FormData();
        formData.append('title', this.title)
        formData.append('price', this.price)
        formData.append('content', this.content)
        this.selected.forEach((item) => {
          formData.append('deleteImgList', item)
        })

        for(let j = 0; j < this.selectPostImageFile.length; j++) {
          for (let i = 0; i < this.selectPostImageFile[j].length; i++) {
            let selectPostImageFileElementElement = this.selectPostImageFile[j][i];
            formData.append('addImgList', selectPostImageFileElementElement);
          }
        }

        this.$emit('modify', formData)
      }
    }
  },
  watch: {
    artistMarketOneBoard: {
      handler(newVal) {
        if (newVal) {
          this.title = newVal.title;
          this.content = newVal.content;
          if (newVal.price === '-1'){
            this.nonePrice = true;
          }else {
            this.nonePrice = false;
            this.price = newVal.price;
          }

          if (newVal.imgList){
            this.imgCount = newVal.imgList.length
          }
        }
      },
      immediate: true, // 초기 값이 있을 경우 즉시 호출
    },
    nonePrice(value){
      if(value){
        this.price = '';
      }else {
        this.price = this.artistMarketOneBoard.price
      }
    }
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
</style>
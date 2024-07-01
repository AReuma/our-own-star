<template>
  <div style="width: 100%; height: 100%; padding-top: 20px; display: flex; flex-direction: column;">
    <v-sheet
        rounded="xl"
        class="mx-auto pa-3"
        width="80%"
        style="display: flex; flex-direction: column;"
        color="#D9D9D9"
    >
      <div style="width: 100%; height: 100%; display: flex; align-items: center; padding-top: 10px">
        <div style="width: auto; height: 100%;">
          <v-avatar max-size="60">
            <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+myPageProfile.profileImg"></v-img>
          </v-avatar>
        </div>

        <p style="font-size: 22px; margin-left: 10px; width: auto; text-align: start;">{{ myPageProfile.nickname }}</p>

        <div class="flex-grow-1 text-end" style="height: 100%; font-size: 16px; font-style: normal; font-weight: 400; line-height: normal;">
          <v-btn color="blue" @click="modifyProfile">프로필 수정</v-btn>
        </div>
      </div>

      <div style="height: 100%">
        <v-textarea v-if="myPageProfile.userInfo !=null" :model-value="myPageProfile.userInfo" variant="plain" readonly="" no-resize rows="5"></v-textarea>
        <v-textarea v-else model-value="내용을 추가 해보세요 :>" variant="plain" readonly="" no-resize rows="5"></v-textarea>

      </div>

      <div class="text-center d-flex justify-center align-center pa-2" style="font-size: 18px">
        <div style="margin-right: 20px">팔로잉: {{ myPageProfile.follower }} 명</div>
        <div>팔로워: {{ myPageProfile.following }} 명</div>
      </div>
    </v-sheet>


    <div style="margin-top: 30px">
      <v-tabs v-model="tab" color="pink" align-tabs="center" grow="" style="border-bottom: 1px solid #D9D9D9;">
        <v-tab v-for="(item, index) in category" :key="index" :value="item.value" @click="moveCategory(item.routerName)" style="font-size: 22px">{{item.name}}</v-tab>
      </v-tabs>

      <router-view style="flex: 8" v-slot="{ Component }">
        <component :is="Component" :artist="artist"/>
      </router-view>
    </div>

    <v-dialog v-model="modifyProfileDialog" persistent width="500" height="680"  style="font-family: Dovemayo_wild, sans-serif ">
      <v-card class="pa-3" style="height: 680px; width: 500px">
        <v-card-title class="d-flex text-center justify-center" style="font-size: 32px;">
          <v-row class="align-center text-center">
            <v-col></v-col>
            <v-col>프로필 수정하기</v-col>
            <v-col>
              <v-card-actions class="justify-end">
                <v-btn variant="plain" @click="closeModifyProfileDialog">
                  <v-icon size="30">mdi-close</v-icon>
                </v-btn>
              </v-card-actions>
            </v-col>
          </v-row>
        </v-card-title>

        <v-card-actions style="width: 100%; height: 150px; display: flex; justify-content: center;">
          <div>
            <div class="profile-container" @click="modifyProfileImage">
              <v-avatar v-if="!modifyProfileCheck" size="120">
                <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+myPageProfile.profileImg" alt="Profile Image" class="profile-image"></v-img>
              </v-avatar>
              <v-avatar v-else size="120">
                <v-img :src="previewProfileImageURL" alt="이미지 미리보기" ></v-img>
              </v-avatar>
                <div class="profile-text">
                  프로필 변경
                </div>
            </div>

            <input type="file" style="display: none" accept=".jpg, .jpeg, .png" ref="uploadItemFile" @change="previewPostImage" />
          </div>
        </v-card-actions>

        <v-card-actions style="width: 100%;">
          <div style="display: flex; width: 100%; flex-direction: column;">
            <span>닉네임</span>
            <v-text-field :readonly="isDubNickname" v-model="modifyNickname" variant="outlined" style="padding-top: 5px"></v-text-field>
            <div v-if="!isDubNickname" style="width: 100%;">
              <v-btn @click="checkNicknameDub" style="height: 45px; width: 100%" variant="outlined" color="pink">닉네임 중복 체크</v-btn>
            </div>
            <div v-else style="width: 100%; display: flex">
              <v-btn style="height: 45px; width: 60%" variant="plain" readonly="" color="pink">닉네임 중복 체크 완료</v-btn>
              <v-btn @click="reNicknameCheck" style="height: 45px; flex-grow: 1" variant="outlined" color="pink">닉네임 변경</v-btn>
            </div>
          </div>
        </v-card-actions>

        <v-card-actions style="width: 100%;">
          <div style="display: flex; width: 100%; flex-direction: column">
            <span>소개 변경</span>
            <v-textarea variant="outlined" v-model="modifyUserInfo" style="padding-top: 5px" rows="5"></v-textarea>
          </div>
        </v-card-actions>

        <v-btn @click="modifyUserInfoSave" style="height: 45px" variant="flat" color="blue">프로필 수정</v-btn>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";
import {mapActions} from "vuex";

export default defineComponent({
  name: "ArtistMyInfoPageView",
  props: {
    artist: {
      type: String,
      default: '',
    },
    myPageProfile: {
      type: Object,
      default: null
    },
    isDubNickname: {
      type: Boolean,
      default: false
    }
  },
  data(){
    return {
      tab: null,
      category: [
        {name: '게시글', routerName: 'ArtistMyInfoBoardView', value: 1},
        {name: '답글', routerName: 'ArtistMyInfoCommentView', value: 2},
        {name: '마켓', routerName: 'ArtistMyInfoMarketView', value: 3},
        {name: '마음', routerName: 'ArtistMyInfoLikeView', value: 4},
      ],
      modifyProfileDialog: false,
      modifyNickname: '',
      modifyUserInfo: '',
      modifyProfileCheck: false,
      previewProfileImageURL: null,
      selectProfileImageFile: null,
    }
  },
  methods: {
    ...mapActions(['fetchUpdateIsDubNickname']),
    reNicknameCheck(){
      this.fetchUpdateIsDubNickname()
    },
    modifyProfile(){
      this.modifyProfileDialog = true;
      this.modifyNickname = this.myPageProfile.nickname;
      this.modifyUserInfo = this.myPageProfile.userInfo;
    },
    closeModifyProfileDialog(){
      this.modifyProfileDialog = false;
      this.modifyNickname = this.myPageProfile.nickname;
      this.modifyUserInfo = this.myPageProfile.userInfo;
      this.fetchUpdateIsDubNickname();
      this.previewProfileImageURL = null;
      this.selectProfileImageFile = null;
      this.modifyProfileCheck = false;
    },
    checkNicknameDub(){
      let nickname = this.modifyNickname
      this.$emit('checkNicknameDub', {nickname})
    },
    moveCategory(routerName){
      router.push({name: routerName, params: {artist: this.artist}})
    },
    updateTabValue(routeName){
      const tabMapping = {
        'ArtistMyInfoBoardView': 1,
        'ArtistMyInfoCommentView': 2,
        'ArtistMyInfoMarketView': 3,
        'ArtistMyInfoLikeView': 4,
        // 더 많은 매핑 추가
      };
      this.tab = tabMapping[routeName] || '';
    },
    modifyProfileImage(){
      this.$refs.uploadItemFile.click();
    },
    previewPostImage(event){
      const file = event.target.files[0];

      if(file) {
        const reader = new FileReader();

        reader.onload = () => {
          this.previewProfileImageURL = reader.result;
        };

        reader.readAsDataURL(file);

        this.selectProfileImageFile = file;
        this.modifyProfileCheck = true;
      }
    },
    modifyUserInfoSave(){
      // 닉네임 중복 체크 확인
      let formData = new FormData;
      let nickname = this.myPageProfile.nickname;

      if(this.modifyNickname !== this.myPageProfile.nickname){
        if(this.isDubNickname){
          nickname = this.modifyNickname;
        }
      }
      formData.append('nickname', nickname);

      let userInfo = this.modifyUserInfo;
      formData.append('userInfo', userInfo)

      if (this.modifyProfileCheck){
        let profileImg = this.selectProfileImageFile;
        formData.append('userProfileImg', profileImg);
      }else {
        formData.append('userProfileImg', null);
      }

      this.$emit('modifyUserInfoSave', formData)
    }
  },
  mounted() {
    this.updateTabValue(this.$route.name)
  }
})
</script>

<style scoped>
.profile-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  background-color: #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.profile-image {
  width: 100%;
  height: auto;
  display: block;
}

.profile-text {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 4px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 16px;
  box-sizing: border-box;
}
</style>
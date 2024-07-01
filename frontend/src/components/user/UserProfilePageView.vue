<template>
  <div id="artist-layout">
    <div id="idol-category">
      <artist-board-category
          :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"
      ></artist-board-category>
    </div>

    <div id="board-content-div">
      <div id="search-div">
        <input id="search-box" type="text">
          <v-btn id="search-btn" color="blue" :ripple="false">검색</v-btn>
      </div>

      <div id="profile-info">
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
                <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+userPageProfile.profileImg"></v-img>
              </v-avatar>
            </div>

            <p style="font-size: 22px; margin-left: 10px; width: auto; text-align: start;">{{ userPageProfile.nickname }}</p>

            <div class="flex-grow-1 text-end" style="height: 100%; font-size: 16px; font-style: normal; font-weight: 400; line-height: normal;">
              <v-btn v-if="userPageProfile.follow" color="blue" @click="followUser">팔로우 취소</v-btn>
              <v-btn v-else color="blue" @click="followUser">팔로우 하기</v-btn>
            </div>
          </div>

          <div style="height: 100%">
            <v-textarea v-if="userPageProfile.userInfo !=null" :model-value="userPageProfile.userInfo" variant="plain" readonly="" no-resize rows="5"></v-textarea>
            <v-textarea v-else model-value="..." variant="plain" readonly="" no-resize rows="5"></v-textarea>
          </div>

          <div class="text-center d-flex justify-center align-center pa-2" style="font-size: 18px">
            <div style="margin-right: 20px">팔로잉: {{ userPageProfile.follower }} 명</div>
            <div>팔로워: {{ userPageProfile.following }} 명</div>
          </div>
        </v-sheet>

    <div style="margin-top: 30px">
      <v-tabs v-model="tab" color="pink" align-tabs="center" grow="" style="border-bottom: 1px solid #D9D9D9;">
        <v-tab v-for="(item, index) in category" :key="index" :value="item.value" @click="moveCategory(item.routerName)" style="font-size: 22px">{{item.name}}</v-tab>
      </v-tabs>

      <router-view style="flex: 8" v-slot="{ Component }">
        <component :is="Component" :artist="artist" :profile="profile"/>
      </router-view>
    </div>
    </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";

export default defineComponent({
  name: "UserProfilePageView",
  components: {ArtistBoardCategory},
  props: {
    profile: {
      type: String,
      default: ''
    },
    artist: {
      type: String,
      default: ''
    },
    userPageProfile: {
      type: Object,
      default: null
    },
    joinCategoryUserInfo: {
      type: Object,
      default: null
    }
  },
  data(){
    return {
      tab: null,
      category: [
        {name: '게시글', routerName: 'UserInfoBoardView', value: 1},
        {name: '답글', routerName: 'UserInfoCommentView', value: 2},
        {name: '마켓', routerName: 'UserInfoMarketView', value: 3},
        {name: '마음', routerName: 'UserInfoLikeView', value: 4},
      ],
    }
  },
  methods: {
    moveCategory(routerName){
      router.push({name: routerName,
        params: { artist: this.artist }, // params 설정
        query: { profile: this.profile }})
    },
    updateTabValue(routeName){
      const tabMapping = {
        'UserInfoBoardView': 1,
        'UserInfoCommentView': 2,
        'UserInfoMarketView': 3,
        'UserInfoLikeView': 4,
        // 더 많은 매핑 추가
      };
      this.tab = tabMapping[routeName] || '';
    },
    followUser(){
      let followUser = this.profile;
      let artist = this.artist;

      this.$emit('followUser', {followUser, artist})
    }
  },
  mounted() {
    this.updateTabValue(this.$route.name)
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
#profile-info {
  flex: 8;
  margin-top: 20px;
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
</style>
<template>
  <div style=" width: 100%">
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
            @click="category(item.routerName)"
        >
          <v-list-item-title style=" font-size: 20px" v-text="item.name"></v-list-item-title>
        </v-list-item>
      </v-list>
    </div>

    <div style="display: flex; justify-content: center; margin-top: 30px;">
      <v-btn @click="writePost" color="pink" style="font-size: 18px" width="85%" height="60px">게시글 작성</v-btn>
    </div>

    <div class="pa-5" id="category-user-info">
      <div style="flex: 1">
        <v-avatar size="50">
          <v-img src="@/assets/profile/profile-test.jpg"></v-img>
        </v-avatar>
      </div>

      <div id="category-user-nickname">
        {{ joinCategoryUserInfo.nickname }}
      </div>

      <div style="position: relative;  flex: 1">
        <v-icon size="28" icon="mdi-chevron-right" color="blue"></v-icon>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";

export default defineComponent({
  name: "ArtistBoardCategory",
  props: ['artist','joinCategoryUserInfo'],
  data(){
    return {
      items: [
        {name: '홈', routerName: 'ArtistHomePageView', },
        {name: '마켓', routerName: 'ArtistMarketPageView',},
        {name: '북마크', routerName: 'ArtistBookMarkPageView',},
        {name: '인기글', routerName: 'ArtistPopularPostPageView',},
        {name: '마이페이지', routerName: 'ArtistMyInfoView',},
        {name: '내 챗팅', routerName: 'ArtistHomePageView',},
      ],
    }
  },
  methods: {
    backPage(){
      router.go(-1)
    },
    writePost(){
      this.postDialog = true
    },
    category(routerName){
      router.push({name: routerName})
    },
  }
})
</script>

<style scoped>
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
</style>
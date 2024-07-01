<template>
  <div id="search-page-layout">
    <div id="idol-category">
      <artist-board-category :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"></artist-board-category>
    </div>

    <div id="board-content-div">
      <div id="search-div">
        <artist-search-bar :artist="artist" :keyword="keyword"></artist-search-bar>
      </div>

      <div class="pt-1" id="board-data" style="height: 100%; display: flex; align-items: center;">
        <div id="board-title" style="display: flex; justify-content: start; width: 100%; border-bottom: 1px solid #d9d9d9">
          <v-btn @click="moveBack" variant="plain" color="blue" icon="mdi-arrow-left"></v-btn>

          <v-tabs v-model="tab" color="pink" fixed-tabs grow="">
            <v-tab v-for="(item, index) in category" :key="index" :value="item.value" @click="moveCategory(item.routerName)" style="font-size: 20px">{{item.name}}</v-tab>
          </v-tabs>
        </div>

        <router-view style="flex: 8;" v-slot="{ Component }">
          <component :is="Component" :artist="artist" :keyword="keyword"/>
        </router-view>

      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistSearchBar from "@/components/artist/search/ArtistSearchBar.vue";
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";
import router from "@/router";

export default defineComponent({
  name: "ArtistSearchPageView",
  components: {ArtistBoardCategory, ArtistSearchBar},
  props: {
    artist: {
      type: String,
      default: ''
    },
    keyword: {
      type: String,
      default: ''
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
        {name: '게시글', routerName: 'SearchBoardView', value: 1},
        {name: '마켓', routerName: 'SearchMarketView', value: 2},
        {name: '사용자', routerName: 'ArtistSearchUserListPageView', value: 3},
      ],
    }
  },
  methods: {
    moveBack(){
      this.$router.go(-1)
    },
    moveCategory(routerName){
      router.push({name: routerName,
        params: { artist: this.artist }, // params 설정
        query: { keyword: this.keyword }})
    },
    updateTabValue(routeName){
      const tabMapping = {
        'SearchBoardView': 1,
        'SearchMarketView': 2,
        'ArtistSearchUserListPageView': 3,
        // 더 많은 매핑 추가
      };
      this.tab = tabMapping[routeName] || '';
    },
    mounted() {
      this.updateTabValue(this.$route.name)
    }
  }
})
</script>

<style scoped>
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
#search-page-layout {
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
#board-data {
  flex: 8;
  display: flex;
  flex-direction: column;
  height: auto;
  overflow-y: hidden;
}
#board-title{
  display: flex;
  font-size: 25px;
  align-items: center;
  //border-bottom: 1px solid #D9D9D9;
  margin-bottom: 12px;
  font-family: Dovemayo_wild, sans-serif;
}
</style>
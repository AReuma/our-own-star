<template>
  <div id="artist-layout">
    <div id="idol-category">
      <artist-board-category
          :joinCategoryUserInfo="joinCategoryUserInfo"
          :artist="artist"
      ></artist-board-category>
    </div>

    <div style="flex: 4; width: 100%; display: flex; flex-direction: column">
      <div style="height: 10%; border-bottom: 1px solid #BCBCBC; flex: 1;  display: flex; align-items: center; justify-content: center;">
        <artist-search-bar :artist="artist"></artist-search-bar>
      </div>
      <router-view style="flex: 8; overflow-y: scroll;" v-slot="{ Component }">
        <component :is="Component" :joinCategoryUserInfo="joinCategoryUserInfo"/>
      </router-view>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";
import ArtistSearchBar from "@/components/artist/search/ArtistSearchBar.vue";

export default defineComponent({
  name: "ArtistPageView",
  components: {ArtistSearchBar, ArtistBoardCategory},
  props: ['artist', 'joinCategoryUserInfo'],
  data(){
    return {
    }
  },

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
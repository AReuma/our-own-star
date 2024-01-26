<template>
  <div id="artist-layout">
    <div id="idol-category">
      <div id="idol-category-title">
        <div style="flex: 1">
          <v-btn @click="backPage" size="70" variant="text">
            <v-icon size="50" icon="mdi-chevron-left" color="blue"></v-icon>
          </v-btn>
        </div>

        <div id="idol-category-artist-name">
         {{artist}}
        </div>
      </div>

      <div style="margin-top: 25px">
        <v-list lines="three" style="background-color: #F0F0F0; text-align: center;">
          <v-list-item
              v-for="(item, i) in items"
              :key="i"
              :value="item"
              color="primary"
              variant="text"
          >
            <v-list-item-title style=" font-size: 20px" v-text="item.name"></v-list-item-title>
          </v-list-item>
        </v-list>
      </div>

      <div style="display: flex; justify-content: center; margin-top: 30px;">
        <v-btn color="blue" style="font-size: 18px" width="85%" height="60px">게시글 작성</v-btn>
      </div>


      <div class="pa-5" id="category-user-info">
        <div style="flex: 1">
          <v-avatar size="60">
            <v-img src="@/assets/profile/profile-test.jpg"></v-img>
          </v-avatar>
        </div>

        <div id="category-user-nickname">
          니니야
        </div>

        <div style="position: relative;  flex: 1">
          <v-icon size="50" icon="mdi-chevron-right" color="blue"></v-icon>
        </div>
      </div>
    </div>


    <div style="flex: 3; border: 3px solid blue; width: 100%;">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";

export default defineComponent({
  name: "ArtistPageView",
  props: ['artist'],
  data(){
    return {
      items: [
        {name: this.artist+' 마켓', id: 1, icon: 'mdi-clock'},
        {name: '북마크', id: 2, icon: 'mdi-clock'},
        {name: '인기글', id: 3, icon: 'mdi-clock'},
        {name: '팔로우 게시글', id: 4, icon: 'mdi-clock'},
        {name: '내 챗팅', id: 5, icon: 'mdi-clock'},
      ]
    }
  },
  methods: {
    backPage(){
      router.go(-1)
    }
  }
})
</script>

<style scoped>
#artist-layout {
  width: 100%;
  height: 100%;
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
  padding-top: 10px;
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
  font-size: 25px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#category-user-info {
  position:absolute;
  bottom: 0;
  display: flex;
  align-items: center;
  width: 25%
}
</style>
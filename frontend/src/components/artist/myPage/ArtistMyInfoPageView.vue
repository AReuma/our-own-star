<template>
  <div style="width: 100%; height: 100%; padding-top: 20px; border: 3px solid deeppink; display: flex; flex-direction: column;">
      <v-sheet
          rounded="xl"
          class="mx-auto pa-3"
          width="80%"
          style="display: flex; flex-direction: column; "
          color="#D9D9D9"
      >
        <div style="width: 100%; height: 100%; padding: 20px; display: flex; align-items: center">
          <div style="width: auto; height: 100%;">
            <v-avatar size="60">
              <v-img src="@/assets/profile/profile-test.jpg"></v-img>
            </v-avatar>
          </div>

          <p style="font-size: 22px; margin-left: 10px; width: auto; text-align: start;">닉네임</p>

          <div class="flex-grow-1 text-end" style="height: 100%; font-size: 16px; font-style: normal; font-weight: 400; line-height: normal;">
            <v-btn color="blue">프로필 수정</v-btn>
          </div>
        </div>

        <div style="height: 100%">
          <v-textarea v-model="textareaInfo" variant="plain" readonly="" no-resize rows="4"></v-textarea>
        </div>

        <div class="text-center d-flex justify-center align-center pa-2" style="font-size: 18px">
          <div style="margin-right: 20px">팔로잉: 109 명</div>
          <div>팔로워: 200 명</div>
        </div>

      </v-sheet>


      <div style="margin-top: 30px">
        <v-tabs v-model="tab" color="pink" align-tabs="center" grow="">
          <v-tab v-for="(item, index) in category" :key="index" :value="item.value" @click="moveCategory(item.routerName)" style="font-size: 22px">{{item.name}}</v-tab>
        </v-tabs>

        <router-view style="flex: 8" v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </div>


  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";

export default defineComponent({
  name: "ArtistMyInfoPageView",
  props: ['artist'],
  data(){
    return {
      textareaInfo: '엑소를 좋아해요~\n' +
          '엑소 카이를 제일 좋아해요 \n' +
          '\n' +
          '카이 포카 구해요!',
      tab: null,
      category: [
        {name: '게시글', routerName: 'ArtistMyInfoBoardView', value: 1},
        {name: '답글', routerName: 'ArtistMyInfoCommentView', value: 2},
        {name: '미디어', routerName: 'ArtistMyInfoMediaView', value: 3},
        {name: '마음', routerName: 'ArtistMyInfoLikeView', value: 4},
      ]
    }
  },
  methods: {
    moveCategory(routerName){
      router.push({name: routerName, params: {artist: this.artist}})
    }
  }
})
</script>

<style scoped>
</style>
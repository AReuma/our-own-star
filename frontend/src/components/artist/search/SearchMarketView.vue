<template>
  <div style="height: auto; width: 100%; overflow-y: auto">
    <v-container style="position: relative; height: 100%">
      <v-row justify="start" no-gutters style="margin-top: 10px">
        <v-col v-for="(board, index) in searchMarketBoard" :key="index" class="d-flex align-center justify-center pa-2" cols="12" md="3" sm="12" style="">
          <v-hover v-slot="{ isHovering, props }">
            <v-sheet class="sheet-style d-flex flex-column"
                     @click="moveSellPage(board.id)"
                     v-bind="props"
                     :class="{ 'on-hover': isHovering }"
                     :color="isHovering ? 'rgba(217,217,217,0.34)' : 'backColor'"
                     style="position:relative; width: 100%; border-radius: 12px; outline: 1px solid #d9d9d9" height="180">
              <div class="category-profile-image">
                <div style="height: 100%; width: 100%;">
                  <v-img cover="" height="100%" style="background-size: cover; border-top-right-radius: 12px; border-top-left-radius: 12px;" :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+board.imgUrl"></v-img>
                </div>
              </div>
              <div class="category-name">
                <div style="padding: 2px 0 0 6px">
                  {{board.title}}
                </div>

                <div style="display: flex; justify-content: space-between; align-items: center; padding-right: 10px">
                  <div>
                    <v-btn @click.stop="likeBoard(board.id)" v-if="board.like" variant="text" size="x-small" icon="mdi-heart" color="pink"></v-btn>
                    <v-btn @click.stop="likeBoard(board.id)" v-else variant="text" size="x-small" icon="mdi-heart-outline"></v-btn>
                  </div>
                  <div v-if="board.price === '-1'">
                    가격 미정
                  </div>
                  <div v-else>
                    ₩ {{board.price}} 원
                  </div>
                </div>
              </div>
            </v-sheet>
          </v-hover>
        </v-col>
      </v-row>

    </v-container>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {mapActions, mapState} from "vuex";
import router from "@/router";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

export default defineComponent({
  name: "SearchMarketView",
  props: {
    artist: {
      type: String,
      default: ''
    },
    keyword: {
      type: String,
      default: ''
    },
  },
  data(){
    return {

    }
  },
  created() {
    const {artist, keyword} = this;
    this.fetchSearchMarketBoard({artist, keyword})
  },
  methods: {
    ...mapActions(['fetchSearchMarketBoard']),
    moveSellPage(id){
      const artist = this.artist;
      let sellNum = id;
      router.push({name: 'ArtistMarketReadView', params: {artist: artist, sellNum: sellNum}})
    },
    likeBoard(id){
      const artist = this.artist;
      let sellNum = id;

      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/like`, {}, config)
          .then((res) => {
            console.log(res)
            // const currentPage = this.$route.query.page || 1;
            // window.location.href = `${window.location.pathname}?page=${currentPage}`;
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    deleteBoard(id){
      const artist = this.artist;
      let sellNum = id;
      axios.delete(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}`, config)
          .then((res) => {
            console.log(res)
            //router.push({name: 'ArtistMyInfoMarketView', params: {artist: artist}})
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    }
  },
  computed: {
    ...mapState(['searchMarketBoard'])
  }
})
</script>

<style scoped>
.category-profile-image{
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  border-top-right-radius: 12px;
  border-top-left-radius: 12px;
  height: 65%;
  background-color: white;
}
.category-name {
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
  border-bottom-right-radius: 12px;
  border-bottom-left-radius: 12px;
  height: 35%;
  display: flex;
  flex-direction: column;
  max-font-size: 13px;
}
</style>
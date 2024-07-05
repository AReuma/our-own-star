<template>
  <v-container style="position: relative; height: 100%">
    <v-row justify="end" style="height: 10%;">
      <v-col cols="3" md="3" sm="12" class="justify-center d-flex align-center">
        <v-combobox
            style="height: 80%"
            variant="outlined"
            density="compact"
            v-model="searchType"
            color="blue"
            :items="searchTypeItem"
        ></v-combobox>
      </v-col>
    </v-row>
    <v-row justify="start" no-gutters style="margin-top: 10px">
      <v-col v-for="(board, index) in artistMarketBoard" :key="index" class="d-flex align-center justify-center pa-2" cols="12" md="3" sm="12" style="">
        <v-hover v-slot="{ isHovering, props }">
          <v-sheet class="sheet-style d-flex flex-column"
                   @click="moveSellPage(board.id)"
                   v-bind="props"
                   :class="{ 'on-hover': isHovering }"
                   :color="isHovering ? 'rgba(217,217,217,0.34)' : 'backColor'"
                   style="position:relative; width: 100%; border-radius: 12px; outline: 1px solid #d9d9d9" height="180">
            <div class="category-profile-image">
              <div style="height: 100%; width: 100%;">
                <v-img cover="" height="100%" style="background-size: cover; border-top-right-radius: 12px; border-top-left-radius: 12px;"
                       :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+board.imgUrl"></v-img>
<!--                <v-img cover="" height="100%" style="background-size: cover; border-top-right-radius: 12px; border-top-left-radius: 12px;"
                       :src="API_BASE_URL()+board.imgUrl"></v-img>-->
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
                  ₩ {{priceParser(board.price)}} 원
                </div>
              </div>
            </div>
          </v-sheet>
        </v-hover>
      </v-col>
    </v-row>

    <v-row style="justify-content: center; align-items: center; display: flex; position: absolute; bottom: 8px; left: 0; right: 0" no-gutters>
      <v-col cols="12" >
        <v-pagination
            v-model="page"
            color="pink"
            :total-visible="1"
            :length="artistMarketTotalPage"
            prev-icon="mdi-chevron-left"
            next-icon="mdi-chevron-right"
        ></v-pagination>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";
import {mapActions, mapState} from "vuex";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";
import {priceParser} from "../../../constant/time/priceParser";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};
export default defineComponent({
  name: "ArtistMarketPageView",
  props: ['joinCategoryUserInfo'],
  data(){
    return {
      searchType: ['인기순'],
      searchTypeItem: ['인기순', '최신순', '좋아요순', '가격순'],
      page: 1,
      artist: ''
    }
  },
  methods: {
    priceParser,
    ...mapActions(['fetchIdolCategoryMarketBoard', 'fetchIdolCategoryMarketBoardTotalPage']),
    API_BASE_URL() {
      return API_BASE_URL
    },
    moveSellPage(id){
      const artist = this.artist;
      let sellNum = id;
      router.push({name: 'ArtistMarketReadView', params: {artist: artist, sellNum: sellNum}})
    },
    movePagination(){
      //const {artist} = this;
      alert(this.page)
      let page = this.page;
      //let nickname = this.joinCategoryUserInfo.nickname
      router.push({ query: { page: page } })
      //this.fetchIdolCategoryMarketBoard({artist, nickname, page})
    },
    likeBoard(id){
      const artist = this.artist;
      let sellNum = id;

      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/like`, {}, config)
          .then((res) => {
            console.log(res)
            const currentPage = this.$route.query.page || 1;
            window.location.href = `${window.location.pathname}?page=${currentPage}`;
          })
          .catch((err) => {
            console.log(err)
          })
    }
  },
  computed: {
    ...mapState(['artistMarketTotalPage', 'artistMarketBoard'])
  },
  watch: {
    joinCategoryUserInfo: {
      handler(newVal) {
        if (newVal) {
          const artist = this.$route.params.artist;
          this.artist = artist;
          this.fetchIdolCategoryMarketBoardTotalPage({artist});

          let page = this.page;
          let nickname = newVal.nickname
          this.fetchIdolCategoryMarketBoard({artist, nickname, page})
        }
      },
      immediate: true, // 초기 값이 있을 경우 즉시 호출
    },
    page: function () {
      let page = this.page;
      router.push({ query: { page: page } })
    },
    '$route.query.page': function(newPage, oldPage) {
      if (newPage !== oldPage) {
        const artist = this.artist;
        let page = newPage;
        let nickname = this.joinCategoryUserInfo.nickname
        this.fetchIdolCategoryMarketBoard({artist, nickname, page})
      }
    }
  },
  mounted() {
    const page = parseInt(this.$route.query.page) || 1;
    this.page = page;
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
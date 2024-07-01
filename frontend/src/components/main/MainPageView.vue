<template>
  <v-container id="main-layout" class="pa-9">
    <v-row justify="center" style="width: 100%; height: 80%;">
      <v-col v-for="(artist, index) in idolCategory" :key="index" class="d-flex  align-center justify-center" cols="12" md="4" @click="artistCategoryClick(index)">
        <v-sheet class="sheet-style d-flex flex-column" style="position:relative;" color="skyblue" height="220" width="320">
          <div v-if="artist.isJoin" style="position: absolute; z-index: 6;">
            <v-icon color="pink" icon="mdi-heart-circle-outline" size="40"></v-icon>
          </div>
          <div class="category-profile-image">
            <div style="filter: brightness(90%); width: 100%; height: 100%; background-size: cover; border-top-right-radius: 12px; border-top-left-radius: 12px;" :style="{ backgroundImage: `url(${artist.artistImg})` }" >
              <v-img :src="artist.artistImg"></v-img>
            </div>
          </div>
          <div class="category-name">
            {{ artist.artist }}
          </div>
        </v-sheet>
      </v-col>

      <v-col class="d-flex  align-center justify-center" cols="12" md="4">
        <v-sheet class="sheet-style d-flex flex-column" color="skyblue" height="220" width="320" @click="addArtist">
          <div class="category-profile-image" style="background-color: #D9D9D9; font-size: 34px; font-family: EF_jejudoldam, sans-serif; text-align: center; color: #3498DB">
            Our Own<br> Star
          </div>
          <div id="add-my-star" style="background-color: #FF1493">
            add My Star
          </div>
        </v-sheet>
      </v-col>
    </v-row>

    <v-row style="width: 100%; justify-content: center; align-items: center; height: 20%">
      <v-pagination
          color="blue"
          :v-model="pageNum"
          :length="idolCategoryTotalPage"
          :total-visible="1"
          prev-icon="mdi-chevron-left"
          next-icon="mdi-chevron-right"
          @update:model-value="movePage"
      ></v-pagination>
    </v-row>

    <!--  Dialog  -->
    <v-dialog v-model="addArtistDialog" persistent width="900" height="700" style="font-family: Dovemayo_wild, sans-serif ">
      <v-card class="pa-3" style="height: 700px; width: 900px">

        <v-card-title class="d-flex text-center justify-center" style="font-size: 32px;">
          <v-row class="align-center text-center">
            <v-col></v-col>
            <v-col>내 아이돌 추가</v-col>
            <v-col>
              <v-card-actions class="justify-end">
                <v-btn variant="plain" @click="closeArtistDialog">
                  <v-icon size="30">mdi-close</v-icon>
                </v-btn>
              </v-card-actions>
            </v-col>
          </v-row>
        </v-card-title>

        <div style="width: 100%; display: flex; justify-content: center; margin-top: 20px">
          <input v-model="searchArtist" id="search-box" type="text">
          <v-btn id="search-btn" color="blue" :ripple="false" @click="searchIdol">검색</v-btn>
        </div>

        <div style="width: 100%; justify-content: end; margin-top: 20px; display: flex; height: 40px">
          <v-btn variant="outlined" color="pink" :ripple="false" style="width: 100px" @click="addIdolCategory">만들기</v-btn>
        </div>

        <div style="width: 100%; height: 500px">
          <div v-if="searchIdolLoading && searchArtist != null" style="width: 100%; height: 100%; display: flex; justify-content: center; align-items: center;">
            <v-progress-circular
                indeterminate
                color="pink"
            ></v-progress-circular>
          </div>

          <div v-else class="pa-3" style="width: 100%; height: 100%;">
            <div>
            <v-radio-group v-model="selectedRadio">
            <v-row justify="start">
              <v-col v-for="(v, index) in searchIdolInfo" :key="v" class="align-center justify-center" cols="12" md="6">
                <v-sheet class="sheet-style d-flex flex-column; align-center pl-2" color="backColor" height="130" width="100%">
                  <div style="height: 100px; width: 15%; display: flex; justify-content: center">
                    <v-radio color="pink" :value="index"></v-radio>
                  </div>
                  <v-avatar
                      variant="flat"
                      rounded="0"
                      size="120"
                  >
                    <v-img
                        style="border-radius: 12px"
                        :src="v.artistImg"
                        alt="John"
                    ></v-img>
                  </v-avatar>

                  <div class="pa-5" style="width: 100%; height: 100%">
                    <v-row style="font-size: 25px">
                      <v-col>{{ v.artist }}</v-col>
                    </v-row>
                    <v-row no-gutters>
                      <v-col>
                        {{ v.artistGenre }}
                      </v-col>
                    </v-row>

                    <v-row no-gutters>
                      <v-col>
                        {{ v.artistType }}
                      </v-col>
                    </v-row>
                  </div>
                </v-sheet>
              </v-col>
            </v-row>
            </v-radio-group>
          </div>
        </div>
        </div>
      </v-card>
    </v-dialog>

    <v-dialog v-model="joinArtistDialog" persistent width="500" height="600"  style="font-family: Dovemayo_wild, sans-serif ">
      <v-card class="pa-3" style="height: 600px; width: 500px">
        <v-card-title class="d-flex text-center justify-center" style="font-size: 32px;">
          <v-row class="align-center text-center">
            <v-col></v-col>
            <v-col>{{ joinArtistInfo.artist }}</v-col>
            <v-col>
              <v-card-actions class="justify-end">
                <v-btn variant="plain" @click="closeJoinArtistDialog">
                  <v-icon size="30">mdi-close</v-icon>
                </v-btn>
              </v-card-actions>
            </v-col>
          </v-row>
        </v-card-title>

        <v-card-text>
          <v-row justify="center">
            <v-col cols="6">
              <v-img :src="joinArtistInfo.artistImg"  max-width="100%" max-height="100%"></v-img>
            </v-col>
          </v-row>

          <v-row justify="center" style="margin-top: 50px">
            <v-col cols="4">
              artist:
            </v-col>
            <v-col cols="4">
              {{joinArtistInfo.artist}}
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="4">
              artistGenre:
            </v-col>
            <v-col cols="4">
              {{joinArtistInfo.artistGenre}}
            </v-col>
          </v-row>
          <v-row justify="center">
            <v-col cols="4">
              artistType:
            </v-col>
            <v-col cols="4">
              {{joinArtistInfo.artistType}}
            </v-col>
          </v-row>
        </v-card-text>

        <v-btn @click="joinArtist" style="height: 60px" variant="flat" color="blue">가입하기</v-btn>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";
import {VueCookieNext} from "vue-cookie-next";

export default defineComponent({
  name: "MainPageView",
  props: ['searchIdolInfo', 'searchIdolLoading', 'idolCategory', 'idolCategoryTotalPage', 'pageNum'],
  data(){
    return{
      searchArtist: '',
      addArtistDialog: false,
      data: [],
      selectedRadio: null,
      joinArtistDialog: false,
      joinArtistInfo: [],
      accessToken: VueCookieNext.isCookieAvailable('accessToken')
    }
  },
  methods: {
    searchIdol(){
      let artist = this.searchArtist;
      this.$emit('searchArtist', {artist})
    },
    addArtist(){
      // 로그인 전에는 로그인
      if(this.accessToken){
        this.addArtistDialog = true;
      } else {
        alert('로그인이 필요한 서비스 입니다')
        router.push({name: 'LoginView'})
      }
    },
    closeArtistDialog(){
      this.addArtistDialog = false;
      let resetArr = [];
      this.selectedRadio = null;
      this.$store.dispatch('fetchArtistInfoReset', resetArr)
    },
    addIdolCategory(){
      console.log(this.searchIdolInfo[this.selectedRadio])
      let searchIdolInfoElement = this.searchIdolInfo[this.selectedRadio];
      this.$emit("addIdolCategory", searchIdolInfoElement);
    },
    movePage(page){
      console.log("test: ", page)
      this.$emit('movePage', page)
    },
    artistCategoryClick(index){
      if(this.accessToken) {
        if (this.idolCategory[index].isJoin) {
          //alert("이미 가입되어있습니다")
          let artist = this.idolCategory[index].artist;
          router.push({name: 'ArtistView', params: {artist: artist}})
        } else {
          this.joinArtistInfo = this.idolCategory[index];
          this.joinArtistDialog = true
          console.log(this.joinArtistInfo)
        }
      }else {
        alert('로그인이 필요한 서비스 입니다')
        router.push({name: 'LoginView'})
      }
    },
    closeJoinArtistDialog(){
      this.joinArtistDialog = false
    },
    joinArtist(){
      let id = this.joinArtistInfo.id;
      this.$emit('joinArtist', {id})
    }
  },
  created() {
    this.page = this.pageNum;
  }
})
</script>

<style scoped>
#main-layout {
  width: 100%;
}
.sheet-style {
  border-radius: 12px;
}
.category-profile-image{
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  border-top-right-radius: 12px;
  border-top-left-radius: 12px;
  height: 70%;
  background-color: white;
}
.category-name {
  max-width: fit-content;
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
  font-family: EF_jejudoldam, sans-serif;
  border-bottom-right-radius: 12px;
  border-bottom-left-radius: 12px;
  height: 30%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 22px;
  font-weight: bolder;
  color: white;
  margin: 0 auto;
}
#add-my-star{
  font-family: EF_jejudoldam, sans-serif;
  border-bottom-right-radius: 12px;
  border-bottom-left-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 22px;
  font-weight: bolder;
  color: white;
  height: 30%;
}
#search-box {
  width: 50%;
  height: 60px;
  background-color: #D9D9D9;
  border-top-left-radius: 12px;
  border-bottom-left-radius: 12px;
  text-align: center;
  font-size: 20px;
  outline: none
}
#search-btn {
  height: 60px;
  width: 10%;
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
  font-size: 20px
}
</style>
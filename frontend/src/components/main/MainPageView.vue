<template>
  <v-container id="main-layout" class="pa-9">
    <v-row justify="space-around" style="width: 100%">
      <v-col v-for="v in 5" :key="v" class="d-flex mx-auto align-center justify-center" cols="12" md="4">
        <v-sheet class="sheet-style d-flex flex-column" color="skyblue" height="220" width="320">
          <div class="category-profile-image" style=""></div>
          <div class="category-name">
            EXO
          </div>
        </v-sheet>
      </v-col>

      <v-col class="d-flex mx-auto align-center justify-center" cols="12" md="4">
        <v-sheet class="sheet-style d-flex flex-column" color="skyblue" height="220" width="320" @click="addArtist">
          <div class="category-profile-image" style="background-color: #D9D9D9; font-size: 34px; font-family: EF_jejudoldam, sans-serif; text-align: center; color: #3498DB">
            Our Own<br> Star
          </div>
          <div class="category-name" style="background-color: #FF1493">
            add My Star
          </div>
        </v-sheet>
      </v-col>
    </v-row>

    <v-row style="width: 100%; justify-content: center; align-items: end">
      <v-pagination
          color="blue"
          v-model="page"
          :length="3"
          :total-visible="1"
          prev-icon="mdi-chevron-left"
          next-icon="mdi-chevron-right"
      ></v-pagination>
    </v-row>

    <!--  Dialog  -->
    <v-dialog v-model="addArtistDialog" persistent="true" width="900" height="700" style="font-family: Dovemayo_wild, sans-serif ">
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
          <input v-model="searchArtist" id="search-box" type="text" @keydown.enter="searchIdol">
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

      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: "MainPageView",
  props: ['searchIdolInfo', 'searchIdolLoading'],
  data(){
    return{
      searchArtist: '',
      page: 1,
      addArtistDialog: false,
      data: [],
      selectedRadio: null
    }
  },
  methods: {
    searchIdol(){
      let artist = this.searchArtist;
      this.$emit('searchArtist', {artist})
    },
    addArtist(){
      this.addArtistDialog = true;
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
    }
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
  font-family: EF_jejudoldam, sans-serif;
  width: 100%;
  border-bottom-right-radius: 12px;
  border-bottom-left-radius: 12px;
  height: 30%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  font-weight: bolder;
  color: white;
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
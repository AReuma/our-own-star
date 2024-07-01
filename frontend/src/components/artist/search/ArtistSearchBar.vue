<template>
  <div style="height: 10%; flex: 1;  display: flex; align-items: center; justify-content: center;">
    <input id="search-box" type="text" v-model="searchKeyword" @keydown.enter="search">
    <v-btn id="search-btn" color="blue" :ripple="false" @click="search">검색</v-btn>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";

export default defineComponent({
  name: "ArtistSearchBar",
  props: {
    artist: {
      type: String,
      default: ''
    },
    keyword: {
      type: String,
      default: ''
    }
  },
  data(){
    return {
      searchKeyword: this.keyword
    }
  },
  methods: {
    search(){
      if(this.searchKeyword === ''){
        alert('검색할 키워드를 추가해주세요')
      }else {
        router.push({name: 'ArtistSearchView', query: {keyword: this.searchKeyword}, params: {artist: this.artist}})
      }
    }
  },
  watch: {
    keyword(newQuery) {
      if(newQuery){
        this.searchKeyword = newQuery;
      }
    }
  }
})
</script>

<style scoped>
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
</style>
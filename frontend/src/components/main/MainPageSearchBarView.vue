<template>
  <div id="search-bar-layout">
   <input v-model="searchArtistName" @keydown.enter="searchArtist" id="search-box" type="text">
    <v-btn id="search-btn" color="blue" :ripple="false" @click="searchArtist">검색</v-btn>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";

export default defineComponent({
  name: "MainPageSearchBarView",
  props: {
    search: {
      type: String,
      default: ''
    }
  },
  data(){
    return {
      searchArtistName: this.search,
    }
  },
  methods: {
    searchArtist(){
      let search = this.searchArtistName
      //alert(search)
      router.push({name: 'MainSearchView', query: {search: search}});
    }
  },
  watch: {
    search(newQuery) {
      if(newQuery){
        this.searchArtistName = newQuery;
      }
    }
  }
})
</script>

<style scoped>
#search-bar-layout {
  margin-top: 45px;
  width: 100%;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
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
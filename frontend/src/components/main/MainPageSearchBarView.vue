<template>
  <div id="search-bar-layout">
   <input id="search-box" type="text">
    <v-btn id="search-btn" color="blue" :ripple="false">검색</v-btn>
  </div>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: "MainPageSearchBarView"
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
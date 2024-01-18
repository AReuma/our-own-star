<template>
  <div id="header-layout">
    <div id="header-title">
      <span class="text-pink">O</span>ur Own Star
    </div>
    <div id="header-btn">
      <div v-if="accessToken === null" id="before-login">
        <v-btn class="btn-style" @click="loginBtn">로그인</v-btn>
        <v-btn class="btn-style" @click="registerBtn" style="margin: 0 10px;">회원가입</v-btn>
      </div>
      <div id="after-login">
        <div style="width: 100px; text-align: center; font-size: 16px">
         반갑습니다<br>
         <span style="font-size: 20px"> {{nickname}} 님! </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {VueCookieNext} from "vue-cookie-next";

export default defineComponent({
  name: "MainPageHeaderView",
  data(){
    return {
      accessToken: null,
      nickname: null,
    }
  },
  methods:{
    loginBtn(){

    },
    registerBtn(){

    }
  },
  mounted() {
    this.accessToken = VueCookieNext.getCookie('accessToken')
    this.nickname = VueCookieNext.getCookie('nickname')
  }
})
</script>

<style scoped>
#header-layout {
  width: 100%;
  display: flex;
}
#header-title {
  color: #3498DB;
  font-family: EF_jejudoldam, sans-serif;
  font-size: 45px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  width: 60%;
}
#header-btn{
  display: flex;
  width: 40%;
}
#before-login{
  display: flex;
  justify-content: right;
  align-items: center;
  width: 100%;
}
#after-login{
  display: flex;
  justify-content: right;
  align-items: center;
  margin: 0 20px;
  width: 100%;
}
.btn-style{
  width: 80px;
  background-color: #3498DB;
  border-radius: 8px;
  color: #FFF;
  font-family: Dovemayo_wild,sans-serif;
  font-size: 15px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
</style>
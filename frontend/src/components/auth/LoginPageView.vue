<template>
  <div id="login-layout">
    <div id="our-own-star-title">
      <span class="text-pink">O</span>ur Own Star
    </div>

    <div id="sns-login">
      <div @click="kakaoBtn" id="kakao-login-btn"></div>
      <div @click="googleBtn" id="google-login-btn"></div>
      <div @click="naverBtn" id="naver-login-btn"></div>
    </div>

    <div id="login-form">
      <div id="login-field">
        <div></div>
        <v-text-field
            v-model="username"
            density="compact"
            placeholder="아이디"
            prepend-inner-icon="mdi-email-outline"
            style="min-height: 40px; text-align: center; line-height: 40px;"
            variant="outlined"
        ></v-text-field>
        <v-text-field
            v-model="password"
            density="compact"
            :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
            :type="visible ? 'text' : 'password'"
            placeholder="비밀번호"
            prepend-inner-icon="mdi-lock-outline"
            variant="outlined"
            @click:append-inner="visible = !visible"
            style="min-height: 42px; text-align: center; line-height: 42px;"
            @keydown.enter="login()"
        ></v-text-field>

      </div>
      <div>
        <v-btn @click="login" id="login-btn">로그인</v-btn>
      </div>
    </div>
    <div id="find-user">
      <div id="btn-div">
        <v-btn id="join-btn" :ripple="false" variant="plain" @click="moveJoin">회원가입</v-btn>
        <v-btn id="find-user-btn" :ripple="false" variant="plain" @click="moveFindUser">아이디/비밀번호 찾기</v-btn>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";
import {GOOGLE_AUTH_URL, KAKAO_AUTH_URL, NAVER_AUTH_URL} from "@/constant/ApiUrl/ApiUrl";

export default defineComponent({
  name: "LoginPageView",
  data(){
    return {
      visible: false, // 비밀번호 확인
      username: "",
      password: ""
    }
  },
  methods: {
    moveJoin(){
      router.push({name: 'RegisterView'})
    },
    moveFindUser(){

    },
    login(){
      const {username, password} = this;
      this.$emit("login", {username, password})
    },
    kakaoBtn(){
      window.open(KAKAO_AUTH_URL,
          "_blank", "width=480, height=720");
    },
    naverBtn(){
      window.open(NAVER_AUTH_URL,
          "_blank", "width=480, height=720");
    },
    googleBtn(){
      window.open(GOOGLE_AUTH_URL,
          "_blank", "width=480, height=720");
    },
  }
})
</script>

<style scoped>
#login-layout {
  height: 100vh;
  padding: 0 100px;
  display: flex;
  background: #F0F0F0;
  flex-direction: column;
  align-items: center;
}
#our-own-star-title {
  margin: 130px 160px 0 160px;
  color: #3498DB;
  text-align: center;
  font-family: EF_jejudoldam, sans-serif;
  font-size: 96px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#sns-login {
  display: flex;
  margin: 70px 450px 0 450px;
  height: 88px;
  justify-content: center;
  align-items: center;
}
#kakao-login-btn {
  width: 70px;
  height: 70px;
  border-radius: 88px;
  background-image: url('@/assets/snsLogin/kakao-logo.png');
  background-color: lightgray;
  background-position: 50%;
  background-size: cover;
  background-repeat: no-repeat;
  margin-right: 60px;
}
#google-login-btn {
  width: 70px;
  height: 70px;
  border-radius: 88px;
  background-image: url('@/assets/snsLogin/google-logo.png');
  background-color: lightgray;
  background-position: 50%;
  background-size: cover;
  background-repeat: no-repeat;
  margin-right: 60px;
}
#naver-login-btn {
  width: 70px;
  height: 70px;
  border-radius: 88px;
  background-image: url('@/assets/snsLogin/naver-logo.png');
  background-color: lightgray;
  background-position: 50%;
  background-size: cover;
  background-repeat: no-repeat;
}
#login-form {
  display: flex;
  width: 600px;
  align-items: center;
  margin-top: 70px;
}
#login-field {
  width: 480px;
  height: 150px;
  margin-right: 25px;
}
#login-btn{
  width: 150px;
  height: 150px;
  background-color: #3498DB;
  border-radius: 12px;
  color: #FFF;
  font-family: Dovemayo_wild,sans-serif;
  font-size: 28px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#find-user {
  width: 600px;
  margin-top: 10px;
  display: flex;
  flex-direction: row-reverse;
}
#join-btn {
  margin-right: 20px;
  font-size: 18px;
}
#find-user-btn {
  font-size: 18px;
}
</style>
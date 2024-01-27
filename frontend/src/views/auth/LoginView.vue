<template>
  <div>
    <login-page-view
        @login="login"
    ></login-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import LoginPageView from "@/components/auth/LoginPageView.vue";
import axios from "axios";
import {API_BASE_URL} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from 'vue-cookie-next';
import {ParsingInfo} from "@/constant/jwt/JwtParser";
import router from "@/router";
import {ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE} from "@/constant/jwt/JwtUtil";

export default defineComponent({
  name: "LoginView",
  components: {LoginPageView},
  data(){
    return {
      accessToken: null,
      refreshToken: null,
    };
  },
  methods: {
    login(payload){
      const {username, password} = payload
      axios.post(API_BASE_URL+"/api/v1/users/login", {username, password})
          .then((res) => {
            this.accessToken = res.data.accesstoken;
            this.refreshToken = res.data.refreshtoken;

            console.log(this.accessToken)
            console.log(this.refreshToken)


            VueCookieNext.setCookie('accessToken', this.accessToken,  {
              expire: ACCESS_TOKEN_EXPIRE, // 1시간
            });
            VueCookieNext.setCookie('refreshToken', this.refreshToken, {
              expire: REFRESH_TOKEN_EXPIRE,
            });

            ParsingInfo(this.accessToken).then(() => {
              router.push({name: 'MainView',  params: { page: 1 }})
            })

          })
          .catch((err) => {
            console.error(err)
            alert("회원이 없습니다. 확인 후 다시 로그인 부탁드려요.")
          })
      /*
      axios({
        method: "POST",
        url: API_BASE_URL+"/api/v1/users/login",
        data: payload,
        headers: {
          "Content-Type": "application/json"
        },
        withCredentials: true,
      })
          .then((res) => {
            let accesstoken = res.data.accesstoken;
            let refreshtoken = res.data.refreshtoken;

            console.log(accesstoken)
            console.log(refreshtoken)

            ParsingInfo(accesstoken)
          })
          .catch((err) => {
            console.error(err)
            alert("회원이 없습니다. 확인 후 다시 로그인 부탁드려요.")
          })*/


    }
  }
})
</script>

<style scoped>

</style>
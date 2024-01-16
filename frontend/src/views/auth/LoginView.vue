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
import {ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE} from "@/constant/jwt/JwtUtil";
import { useCookie } from 'vue-cookie-next'
import {ParsingInfo} from "@/constant/jwt/JwtParser";

export default defineComponent({
  name: "LoginView",
  components: {LoginPageView},
  data(){
    return {
      login(payload){
        const cookie = useCookie()
        cookie.setCookie('theme', 'dark')
        axios({
          method: "POST",
          url: API_BASE_URL+"/api/v1/users/login",
          data: payload,
          headers: {
            "Content-Type": "application/json"
          },
        })
            .then((res) => {
              let accesstoken = res.data.accesstoken;
              let refreshtoken = res.data.refreshtoken;

              useCookie().setCookie('accesstoken', accesstoken, ACCESS_TOKEN_EXPIRE);
              useCookie().setCookie('refreshtoken', refreshtoken, REFRESH_TOKEN_EXPIRE);

              ParsingInfo(accesstoken)
            })
            .catch((err) => {
              console.error(err)
              alert("회원이 없습니다. 확인 후 다시 로그인 부탁드려요.")
            })


      }
    }
  }
})
</script>

<style scoped>

</style>
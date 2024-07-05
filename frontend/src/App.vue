<template>
  <v-app>
    <v-main>
      <router-view/>
    </v-main>
  </v-app>
</template>

<script>

//import axios from "axios";

import axios from "axios";
import {API_BASE_URL, ARTIST_MEMBER} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";
import {ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE} from "@/constant/jwt/JwtUtil";
import router from "@/router";

export default {
  name: 'App',

  data: () => ({
    //
  }),
  created() {
    axios.interceptors.response.use(
        response => {
          // HTTP 요청 성공 시 처리
          return response;
        },
        error => {
          if (error.response.status === 401) {
            // 토큰 만료 또는 인증 오류 처리
            const errorMessage = error.response.data.message;
            if(errorMessage === "기간이 만료된 토큰"){
              let refreshToken = VueCookieNext.getCookie("refreshToken")
               axios.post(API_BASE_URL+ARTIST_MEMBER+"/refreshToken", {refreshToken})
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

                     window.location.reload();
                   })
                   .catch((err) => {
                     alert('토큰이 만료되었습니다. 다시 로그인해주세요.');
                     router.push({name: 'LoginView'})
                     console.log(err)
                   })
            }
          }
          return Promise.reject(error);
        }
    );
  }
}
</script>

<style>
.v-application {
  font-family: 'Dovemayo_wild', sans-serif !important;
}

@font-face {
  font-family: 'EF_jejudoldam';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2210-EF@1.0/EF_jejudoldam.woff2') format('woff2');
  font-weight: normal;
  font-style: normal;
}
@font-face {
  font-family: 'Dovemayo-Medium';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_four@1.0/Dovemayo-Medium.woff') format('woff');
  font-weight: normal;
  font-style: normal;
}
@font-face {
  font-family: 'Dovemayo_wild';
  src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2302@1.0/Dovemayo_wild.woff2') format('woff2');
  font-weight: normal;
  font-style: normal;
}
</style>
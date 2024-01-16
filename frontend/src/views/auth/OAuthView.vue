<template>
  <div>
    <h1>OAuth Response</h1>
    <p>Access Token: {{ accessToken }}</p>
    <p>Refresh Token: {{ refreshToken }}</p>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {useCookie} from "vue-cookie-next";
import {ACCESS_TOKEN_EXPIRE, REFRESH_TOKEN_EXPIRE} from "@/constant/jwt/JwtUtil";
import {ParsingInfo} from "@/constant/jwt/JwtParser";

export default defineComponent({
  name: "OAuthView",
  data() {
    return {
      accessToken: null,
      refreshToken: null,
    };
  },
  mounted() {
    let token = this.$route.query;
    this.accessToken = token.accessToken;
    this.refreshToken = token.refreshToken;

    useCookie().setCookie('accessToken', this.accessToken, ACCESS_TOKEN_EXPIRE);
    useCookie().setCookie('refreshToken', this.refreshToken, REFRESH_TOKEN_EXPIRE);

    const openerWindow = window.opener;
    const routeHome = this.$router.resolve(
        {
          path: '/',
          name: 'HomeView',
        });

    ParsingInfo(token.accessToken)

    openerWindow.document.location.href = routeHome.href;
    window.close();
  },
})
</script>

<style scoped>

</style>
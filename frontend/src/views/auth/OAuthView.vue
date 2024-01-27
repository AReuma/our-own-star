<template>
  <div></div>
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

    useCookie().setCookie('accessToken', this.accessToken, {
      expire: ACCESS_TOKEN_EXPIRE,
    });
    useCookie().setCookie('refreshToken', this.refreshToken, {
      expire: REFRESH_TOKEN_EXPIRE,
    });

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
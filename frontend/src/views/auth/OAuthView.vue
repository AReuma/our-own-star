<template>
  <div></div>
</template>

<script>
import {defineComponent} from 'vue'
import {useCookie} from "vue-cookie-next";
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
    this.accessToken = useCookie().getCookie('accessToken');

    const openerWindow = window.opener;
    const routeHome = this.$router.resolve(
        {
          path: '/',
          name: 'MainView',
        });

    ParsingInfo(this.accessToken)

    openerWindow.document.location.href = routeHome.href;
    window.close();
  },
})
</script>

<style scoped>

</style>
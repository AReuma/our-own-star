<template>
  <div></div>
</template>

<script>
import {defineComponent} from 'vue'
import {ParsingInfo} from "@/constant/jwt/JwtParser";
import {useCookie} from "vue-cookie-next";

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
    alert(this.accessToken)

    const openerWindow = window.opener;
    const routeHome = this.$router.resolve(
        {
          path: '/our-own-star',
          name: 'MainView'
        });

    ParsingInfo(this.accessToken)

    openerWindow.document.location.href = routeHome.href;
    window.close();
  },
})
</script>

<style scoped>

</style>
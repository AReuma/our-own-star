<template>
  <v-app>
    <v-main>
      <router-view/>
    </v-main>
  </v-app>
</template>

<script>

export default {
  name: 'App',

  data: () => ({
    //
  }),
  created() {
    let token = this.$store.getters.getToken;
    if(token.accessToken === null && token.refreshToken !== null){
      //this.$store.dispatch("refreshToken");
      // accessToken 요청
    }
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
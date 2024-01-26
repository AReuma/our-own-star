<template>
  <div id="header-layout">
    <div id="header-title">
      <span class="text-pink">O</span>ur Own Star
    </div>
    <div id="header-btn">
      <div v-if="!tokenIsAvailable" id="before-login">
        <v-btn class="btn-style" @click="loginBtn">로그인</v-btn>
        <v-btn class="btn-style" @click="registerBtn" style="margin: 0 10px;">회원가입</v-btn>
      </div>
      <div v-else id="after-login">
        <div style="width: 100px; text-align: center; font-size: 16px">
          <v-menu min-width="200px">
            <template v-slot:activator="{ props }">
              <v-btn icon v-bind="props">
                <v-avatar color="pink" size="large">
                  <span class="text-h5">{{ nickname }}</span>
                </v-avatar>
              </v-btn>
            </template>
            <v-card>
              <v-card-text>
                <div class="mx-auto text-center">
                  <v-avatar color="pink">
                    <span class="text-h5">{{ nickname }}</span>
                  </v-avatar>
                  <h3>{{ nickname }}</h3>
                  <v-divider class="my-3"></v-divider>
                  <v-btn rounded variant="text"> 마이 페이지 </v-btn>
                  <v-divider class="my-3"></v-divider>
                  <v-btn @click="logout()" rounded variant="text"> 로그아웃 </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </v-menu>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {VueCookieNext} from "vue-cookie-next";
import router from "@/router";

export default defineComponent({
  name: "MainPageHeaderView",
  data(){
    return {
      tokenIsAvailable: false,
      nickname: null,
    }
  },
  methods:{
    loginBtn(){
      router.push({name: 'LoginView'})
    },
    registerBtn(){
      router.push({name: 'RegisterView'})
    },
    logout(){

    }
  },
  mounted() {
    this.tokenIsAvailable = VueCookieNext.isCookieAvailable('accessToken')
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
  font-size: 42px;
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
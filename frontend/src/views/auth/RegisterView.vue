<template>
  <div>
    <register-page-view
        ref="registerPageView"
        @register="register"
        @sendEmail="sendEmail"
        @emailCheck="emailCheck"
    ></register-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import RegisterPageView from "@/components/auth/RegisterPageView.vue";
import axios from "axios";
import {API_BASE_URL} from "@/constant/ApiUrl/ApiUrl";

export default defineComponent({
  name: "RegisterView",
  components: {RegisterPageView},
  methods: {
    sendEmail(payload){
      const {username} = payload;

      axios.post(API_BASE_URL+`/api/v1/users/email-certification`, {username})
          .then((res) =>  {
            if (res.status === 200){
              this.$refs.registerPageView.loderDialog=false;
              this.$refs.registerPageView.usernameCodeFlag=true;
            }
            console.log(res)
          })
          .catch((res) => {
            this.$refs.registerPageView.loderDialog=false;
            alert(res.response.data.errorMessage);
            this.$refs.registerPageView.username="";
            console.error(res.response.data)
          })
    },
    register(payload){
      const {username, password, nickname, phoneNum} = payload;

      axios.post(API_BASE_URL+"/api/v1/users/register", {username, password, nickname, phoneNum})
          .then((res) => {
            console.log(res)
          })
          .catch((res) => {
            console.error(res)
          })
    },
    emailCheck(payload){
      const {username, usernameCode} = payload;
      let code = usernameCode;
      axios.post(API_BASE_URL+"/api/v1/users/email-certification/code", {username, code})
          .then((res) => {
            console.log(res)
            if (res.data === true){
              this.$refs.registerPageView.usernameCodeFlag = false;
              this.$refs.registerPageView.usernameCheck = false;
            }
          })
          .catch((err) => {
            console.error(err)
          })
    }
  }
})
</script>

<style scoped>

</style>
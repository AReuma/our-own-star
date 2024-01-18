<template>
  <div id="register-layout">
    <div id="title">
      <span class="text-pink">O</span>ur Own Star
    </div>

    <div id="register-form">
      <div id="id-form" class="d-flex flex-column" style="max-height: 200px;">
        <span style="font-size: 18px; margin-bottom: 2px;">아이디</span>
          <div class="d-flex" style="height: 50px;">
            <div style="width: 80%">
              <v-text-field
                  v-model="username"
                  density="comfortable"
                  placeholder="아이디를 입력해주세요."
                  variant="outlined"
                  style="min-height: 28px; text-align: center; line-height: 28px;"
              ></v-text-field>
            </div>

            <div  style="width: 18%; margin-left: 2%">
              <v-btn @click="sendEmail" style="width: 100%; height: 50px; font-size: 15px" color="blue">
                인증
              </v-btn>
            </div>
          </div>

        <div v-if="usernameCodeFlag" class="d-flex" style="height: 60px; margin-top: 5px">
          <div class="d-flex" style="height: 60px; width: 100%">
            <div style="width: 80%;">
              <v-text-field
                  v-model="usernameCode"
                  density="comfortable"
                  placeholder="인증번호를 입력해주세요"
                  variant="outlined"
                  style="min-height: 28px; text-align: center; line-height: 28px;"
              ></v-text-field>
            </div>
            <div  style="width: 18%; margin-left: 2%">
              <v-btn @click="emailCheck" style="width: 100%; height: 50px; font-size: 15px" color="blue">
                확인
              </v-btn>
            </div>
          </div>
        </div>

        <div>
        <div v-if="!usernameCheck" style="margin-bottom: 10px; color: red">
          아이디 인증 완료
        </div>
        <div v-else style="margin-bottom: 10px"></div>
        </div>
      </div>

      <div id="password-form" class="d-flex flex-column">
        <span style="font-size: 18px; margin-bottom: 4px;">비밀번호</span>
        <div class="d-flex flex-column">
          <v-text-field
              v-model="password"
              :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
              :type="visible ? 'text' : 'password'"
              @click:append-inner="visible = !visible"
              density="comfortable"
              placeholder="(영문+숫자+특수문자 8자 이상)를 입력해주세요."
              variant="outlined"
              style="max-height: 60px"
          ></v-text-field>

          <v-text-field
              v-model="confirmPassword"
              :append-inner-icon="confirmVisible ? 'mdi-eye-off' : 'mdi-eye'"
              :type="confirmVisible ? 'text' : 'password'"
              @click:append-inner="confirmVisible = !confirmVisible"
              density="comfortable"
              placeholder="비밀번호를 다시 입력해주세요."
              variant="outlined"
              style="max-height: 50px;"
          ></v-text-field>
          <div v-if="!passwordsMatch && passwordCheck" style="margin-bottom: 20px; padding-top: 5px; color: red">
            입력한 비밀번호가 다릅니다. 다시 확인해주세요.
          </div>
          <div v-else style="margin-bottom: 15px"></div>
          <div v-if="!passwordPatternCheck" style="margin-bottom: 20px; padding-top: 5px; color: red">
            비밀번호는 영어 + 숫자 + 특수문자 8자글자 이상 작성해주세요!
          </div>
        </div>
      </div>

      <div id="nickname-form" class="d-flex flex-column">
        <span style="font-size: 18px; margin-bottom: 4px;">닉네임</span>
        <div class="d-flex flex-column">
          <v-text-field
              v-model="nickname"
              density="comfortable"
              placeholder="닉네임을 입력해주세요."
              variant="outlined"
          ></v-text-field>
        </div>
      </div>

      <div id="phonenum-form" class="d-flex flex-column">
        <span style="font-size: 18px; margin-bottom: 4px;">전화번호</span>
        <div class="d-flex" style="height: 65px">
          <v-text-field
              v-model="phoneNum"
              density="comfortable"
              placeholder="-를 제외한 번호만 입력해주세요."
              variant="outlined"
              style="min-height: 28px; text-align: center; line-height: 28px;"
              @input="formatPhoneNumber"
          ></v-text-field>
        </div>
      </div>
    </div>

    <div id="register-btn-div">
      <v-btn @click="registerBtn" id="register-btn" height="50" color="blue" style="font-size: 18px; width: 100%;" :disabled="!isRegisterButtonVisible">회원가입하기</v-btn>
    </div>

    <v-dialog style="width: 100px;" v-model="loderDialog">
      <v-progress-circular
          :size="60"
          :width="5"
          color="pink"
          indeterminate
      ></v-progress-circular>
    </v-dialog>
  </div>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: "RegisterPageView",
  data() {
    return {
      username: "",
      usernameCode: "",
      usernameCodeFlag: false,
      password: "",
      confirmPassword: "",
      visible: false,
      confirmVisible: false,
      nickname: "",
      phoneNum: "",
      phoneNumCode: "",
      loderDialog: false,
      usernameCheck: true,
      passwordCheck: false,
      passwordPatternCheck: true
    }
  },
  methods: {
    sendEmail(){
      const {username} = this;
      this.loderDialog = true;
      this.$emit('sendEmail', {username})
    },
    registerBtn(){
      const {username, password, nickname, phoneNum} = this;
      this.$emit('register', {username, password, nickname, phoneNum});
    },
    emailCheck(){
      const {username, usernameCode} = this;
      this.$emit('emailCheck', {username, usernameCode})
    },
    formatPhoneNumber() {
      const cleaned = this.phoneNum.replace(/\D/g, "");

      let formatted = "";
      for (let i = 0; i < cleaned.length; i++) {
        if (i === 3 || i === 7) {
          formatted += "-";
        }
        formatted += cleaned[i];
      }

      // 최대 12자까지 입력 가능
      this.phoneNum = formatted.slice(0, 13);
    },
  },
  watch: {
    password: function () {
      const passwordPattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$/;

      this.passwordPatternCheck = passwordPattern.test(this.password)
    },
    confirmPassword: function () {

      if (this.confirmPassword.length === 0) {
        this.passwordCheck = false;
      }
      else this.passwordCheck = true;
    },
  },
  computed: {
    passwordsMatch() {
      return this.password === this.confirmPassword;
    },
    phoneNumberMatch(){
      if (this.phoneNum.length === 13) return true;
      else return false;
    },
    nicknameMatch(){
      if(this.nickname.length === 0) return false;
      else return true;
    },
    isRegisterButtonVisible() {
      return !this.usernameCheck && this.passwordPatternCheck && this.phoneNumberMatch && this.nicknameMatch;
    },

  },
})
</script>

<style scoped>
#register-layout {
  height: 100vh;
  padding: 0 10px;
  display: flex;
  background: #F0F0F0;
  flex-direction: column;
  align-items: center;
}
#title {
  width: 100%;
  margin-top: 40px;
  margin-bottom: 20px;
  color: #3498DB;
  text-align: center;
  font-family: EF_jejudoldam,sans-serif;
  font-size: 64px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#register-form {
  padding: 20px 20px;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
#register-btn-div {
  padding: 10px 150px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}


@media (min-width: 600px) {
  #register-layout {
    padding: 0 40px;
  }
  #title {
    font-size: 44px;
  }
  #register-form {
    padding: 20px 150px;
  }
  #register-btn-div {
    padding: 10px 150px;
  }
}


@media (min-width: 960px) {
  #title {
    font-size: 80px;
  }
  #register-form {
    padding: 20px 30%;
  }
  #register-btn-div {
    padding: 10px 30%;
  }
}
</style>
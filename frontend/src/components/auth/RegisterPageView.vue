<template>
  <div id="register-layout">
    <div id="title">
      <span class="text-pink">O</span>ur Own Star
    </div>

    <div id="register-form">
      <div id="id-form" class="d-flex flex-column">
        <span style="font-size: 18px; margin-bottom: 2px;">아이디</span>
        <div class="d-flex">
          <v-text-field
              density="comfortable"
              placeholder="아이디를 입력해주세요."
              variant="outlined"
              style="min-height: 28px; text-align: center; line-height: 28px;"
          ></v-text-field>

          <v-btn style="width: 150px; height: 50px; margin-left: 10px; font-size: 15px" color="blue">
            중복 체크
          </v-btn>
        </div>

        <div id="password-form" class="d-flex flex-column">
          <span style="font-size: 18px; margin-bottom: 4px;">비밀번호</span>
          <div class="d-flex flex-column">
            <v-text-field
                density="comfortable"
                placeholder="비밀번호 (영문+숫자+특수문자 8자 이상)를 입력해주세요."
                variant="outlined"
                style="max-height: 60px"
            ></v-text-field>

            <v-text-field
                density="comfortable"
                placeholder="비밀번호를 다시 입력해주세요."
                variant="outlined"
            ></v-text-field>

          </div>
        </div>

        <div id="nickname-form" class="d-flex flex-column">
          <span style="font-size: 18px; margin-bottom: 4px;">닉네임</span>
          <div class="d-flex flex-column">
            <v-text-field
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
                density="comfortable"
                placeholder="-를 제외한 번호만 입력해주세요."
                variant="outlined"
                style="min-height: 28px; text-align: center; line-height: 28px;"
            ></v-text-field>
            <v-btn style="width: 150px; height: 50px; margin-left: 10px; font-size: 15px" color="blue">
              확인
            </v-btn>
          </div>
          <div class="d-flex" style="height: 55px">
            <v-text-field
                density="comfortable"
                placeholder="인증번호를 입력해주세요"
                variant="outlined"
                style="min-height: 28px; text-align: center; line-height: 28px;"
            ></v-text-field>
            <v-btn style="width: 150px; height: 50px; margin-left: 10px; font-size: 15px" color="blue">
              확인
            </v-btn>
          </div>
        </div>

      </div>
    </div>

    <div id="register-btn-div">
      <v-btn id="register-btn" height="50" color="blue" style="font-size: 18px; width: 100%;">회원가입하기</v-btn>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: "RegisterPageView"
})
</script>

<style scoped>
#register-layout {
  height: 100vh;
  padding: 0 280px;
  display: flex;
  background: #F0F0F0;
  flex-direction: column;
  align-items: center;
}
#title {
  width: 100%;
  margin-top: 40px;
  color: #3498DB;
  text-align: center;
  font-family: EF_jejudoldam,sans-serif;
  font-size: 64px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#register-form {
  padding: 20px 150px;
  width: 100%;
}
#register-btn-div {
  padding: 10px 150px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
@media (min-width: 16in) {
  #register-form {
    padding: 20px 320px;
  }
  #register-btn-div {
    padding: 10px 320px;
  }
}
</style>
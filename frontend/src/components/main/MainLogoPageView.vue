<template>
  <div id="main-layout">
    <div id="main-title">
      <div id="main-title-content">
        <div id="top-title">
          우리 모두가 사랑하는 <span class="text-pink">아이돌</span>과 함께하는
        </div>
        <div id="title">
          'Our Own Star'!
        </div>
      </div>
    </div>

    <div id="main-info">
      <div id="main-content-1">
        우리가 소중히 여기는 <span class="text-pink">&nbsp;아이돌</span>을 이야기하고, 공유하는 특별한 공간입니다.
      </div>
      <div id="main-content-2">
        여기서는 아이돌의 음악, 모습, 활동 등에 대한 다양한 주제로 <span class="text-pink">&nbsp;소통</span>하며, 우리의 <span class="text-pink">&nbsp;팬심을 자유롭게 표현</span>할 수 있어요.<br>
      </div>
      <div id="main-content-3">
        함께 아이돌의 매력을 발견하고, 팬들 간의 <span class="text-pink">&nbsp;소중한 추억</span>을 만들어보세요
      </div>
    </div>

    <div id="start-btn">
      <v-btn id="my-btn" @click="moveMyIdol">내 아이돌 찾기</v-btn>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import router from "@/router";

export default defineComponent({
  name: "MainLogoPageView",
  methods: {
    moveMyIdol(){
      router.push({name: "MainView"})
    }
  }
})
</script>

<style scoped>
#main-layout {
  width: 100%;
  height: 100vh;
  background: #F0F0F0;
  padding: 80px 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
#main-title{
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 32px;
}
#main-title-content{
  display: flex;
  padding-top: 26px;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
}
#top-title{
  display: flex;
  width: 643px;
  height: 20px;
  flex-direction: row;
  justify-content: center;
  flex-shrink: 0;
  color: #000;
  text-align: center;
  font-family: EF_jejudoldam, sans-serif;
  font-size: 32px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  margin-bottom: 18px;
}
#title {
  display: flex;
  width: 906px;
  height: 75px;
  flex-direction: column;
  justify-content: center;
  flex-shrink: 0;
  color: #3498DB;
  text-align: center;
  font-family: EF_jejudoldam, sans-serif;
  font-size: 64px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#main-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  color: #000;
  text-align: center;
  font-size: 32px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#main-content-1{
  display: inline-flex;
  align-items: center;
  color: #000;
  text-align: center;
  font-size: 30px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
#main-content-2{
  display: inline-flex;
  padding: 30px 20px 0 20px;
  flex-direction: row;
  align-items: center;
  color: #000;
  text-align: center;
  font-size: 30px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  white-space: nowrap;
}
#main-content-3{
  font-size: 30px;
  font-style: normal;
}
#start-btn{
  width: 100%;
  height: 140px;
  display: flex;
  margin-top: 25px;
  justify-content: center;
  align-items: center;
}
#my-btn{
  display: flex;
  width: 258px;
  height: 48px;
  flex-direction: column;
  justify-content: center;
  flex-shrink: 0;
  color: #FFF;
  text-align: center;
  font-size: 28px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  background-color: #3498DB;
}
</style>
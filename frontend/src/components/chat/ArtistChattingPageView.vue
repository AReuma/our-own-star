<template>
  <div id="artist-layout">
    <div id="idol-category">
      <artist-board-category :joinCategoryUserInfo="joinCategoryUserInfo" :artist="artist"></artist-board-category>
    </div>

    <div id="board-content-div">
      <div id="search-div">
        <input id="search-box" type="text">
        <v-btn id="search-btn" color="blue" :ripple="false">검색</v-btn>
      </div>
      <div class="pt-1" id="board-data" style="display: flex; align-items: center;">
        <div v-if="messageUserInfo" style="display: flex; flex: 1; justify-content: start; width: 100%; min-height: 90px; position: relative; border-bottom: 1px solid #BCBCBC;">
          <v-btn @click="moveBack" variant="plain" color="blue" icon="mdi-arrow-left"></v-btn>

          <div id="chat-user-info" style="position: absolute; display: flex; flex-direction: column; left: 50%; transform: translateX(-50%);">
            <div style="display: flex; justify-content: center">
              <v-avatar size="60">
                <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+messageUserInfo.receiverProfile"></v-img>
              </v-avatar>
            </div>
            <div style="text-align: center">{{messageUserInfo.receiver}}</div>
          </div>
        </div>

        <div id="chat-list-div" ref="chatList" @scroll="handleScroll">
          <div v-for="(m, index) in chatMessage" :key="index" style="display: flex; flex-direction: column; height: auto">
            <div class="chat-box-user" v-if="joinCategoryUserInfo.nickname === m.sender">
              <div>
                <div class="pull" style="justify-content: right;" v-html="convertNewLineToBr(m.message)"></div>
                <div class="chat-date-time">{{formattedDate(m.creatDate)}}</div>
              </div>
            </div>

            <div v-else class="chat-box-other">
              <div style="display: flex; padding: 10px 0 0 10px">
                <v-avatar size="45">
                  <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+m.senderProfile"></v-img>
                </v-avatar>
                <div class="pull2" style="height: 100%;" v-html="convertNewLineToBr(m.message)"></div>
              </div>
              <div class="chat-date-time">{{formattedDate(m.creatDate)}}</div>
            </div>
          </div>

          <div v-for="(m, index) in messages" :key="index" style="display: flex; flex-direction: column; height: auto">
            <div class="chat-box-user" v-if="joinCategoryUserInfo.nickname === m.sender">
              <div>
                <div class="pull" style="justify-content: right;" v-html="convertNewLineToBr(m.content)"></div>
                <div class="chat-date-time">{{formattedDate(m.creatDate)}}</div>
              </div>
            </div>

            <div v-else class="chat-box-other">
              <div style="display: flex; padding: 10px 0 0 10px">
                <v-avatar size="45">
                  <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+m.senderProfile"></v-img>
                </v-avatar>
                <div class="pull2" style="height: 100%;" v-html="convertNewLineToBr(m.content)"></div>
              </div>
              <div class="chat-date-time">{{formattedDate(m.creatDate)}}</div>
            </div>
          </div>
        </div>

        <div style="display: flex; flex: 1; width: 100%; border-top: 1px solid #BCBCBC;">
          <div id="comment-div">
            <div id="comment-textarea">
              <v-textarea
                  @keydown.enter="send"
                  v-model="messageToSend"
                  rows="3"
                  no-resize
                  variant="plain" density="compact" placeholder="..." style="text-align: center; padding-top: 8px"></v-textarea>
            </div>

            <div id="comment-btn">
              <v-btn @click="send" color="pink" variant="flat" height="100%">작성</v-btn>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistBoardCategory from "@/components/artist/ArtistBoardCategory.vue";
import moment from "moment";
import router from "@/router";

export default defineComponent({
  name: "ArtistChattingPageView",
  components: {ArtistBoardCategory},
  props: {
    joinCategoryUserInfo: {
      type: Object,
      default: null
    },
    artist: {
      type: String,
      default: ''
    },
    messages: {
      type: Array,
      default: null
    },
    chatMessage: {
      type: Array,
      default: null
    },
    messageUserInfo: {
      type: Array,
      default: null
    }
  },
  data(){
    return {
      data: 'ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ\n',
      data2: 'ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ\n돌았냐고',
      messageToSend: '',
    }
  },
  methods: {
    convertNewLineToBr(text) {
      return text.replace(/\n/g, '<br>');
    },
    send(){
      const {messageToSend} = this;
      this.$emit('send', {messageToSend})
      this.messageToSend = '';
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const chatList = this.$refs.chatList;
        chatList.scrollTop = chatList.scrollHeight;
      });
    },
    scrollToTop() {
      this.$nextTick(() => {
        const chatList = this.$refs.chatList;
        chatList.scrollTop = 0;
      });
    },
    handleScroll(){
      // 스크롤이 최상단에 도달했는지 여부 확인
      if (event.target.scrollTop === 0) {
        //alert('상단')
        //let date = this.chatMessage[0].dateLongType;
        this.$emit('getOldMsg')
      }
    },
    formattedDate(date) {
      return moment(date).format('MM/DD · hh:mm a');
    },
    moveBack(){
      router.push({name: 'ArtistChatListView'})
    }
  },
  watch: {
    messages() {
      this.scrollToBottom();
    },
    chatMessage() {
      this.scrollToTop();
    }
  },
})
</script>

<style scoped>
#artist-layout {
  height: 100vh;
  display: flex;
  position: relative;
  font-family: Dovemayo-Medium, sans-serif;
}
#idol-category {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #BCBCBC;
}
#board-content-div {
  flex: 3;
  border-right: 1px solid #D9D9D9;
  width: 100%;
  display: flex;
  flex-direction: column;
}
#search-div {
  flex: 1;
  display: flex;
  height: 10%;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #BCBCBC;
}
#search-box {
  width: 50%;
  height: 55px;
  background-color: #D9D9D9;
  border-top-left-radius: 12px;
  border-bottom-left-radius: 12px;
  text-align: center;
  font-size: 20px;
  outline: none
}
#search-btn {
  height: 55px;
  width: 10%;
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
  font-size: 20px
}
#board-data {
  flex: 8;
  display: flex;
  flex-direction: column;
  height: auto;
  overflow-y: hidden;
}
.pull {
  position:relative;
  margin: 10px 20px 2px 20px;
  padding: 12px;
  max-width: 300px;
  min-width: 160px;
  height:auto;
  color: black;
  border-radius: 10px;
  background-color: #BCBCBC;
}
#chat-list-div {
  width: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  flex: 6;

}
.pull:after {
  content:"";
  position: absolute;
  top: 10px;
  right: -15px;
  border-left: 30px solid #BCBCBC;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  transform: rotate(-23deg);
}

.pull2 {
  position:relative;
  margin: 10px 20px 2px 20px;
  padding: 12px;
  max-width: 350px;
  min-width: 200px;
  height: auto;
  color: #FFF;
  border-radius: 10px;
  background-color: #3498DB;
  z-index: 1;
}
.pull2:after {
  content:"";
  position: absolute;
  top: 10px;
  left: -15px;
  border-right: 30px solid #3498DB;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  transform: rotate(23deg);
  z-index: -1;
}
.chat-date-time{
  display: flex;
  justify-content: end;
  margin-right: 25px;
  color: rgba(62, 55, 55, 0.44);
  font-size: 14px;
}
.chat-box-user{
  width: 100%;
  display: flex;
  justify-content: end;
}
.chat-box-other {
  display: flex;
  width: fit-content;
  flex-direction: column;
}
#comment-div {
  width: 100%;
  display: flex;
  align-items: center;
  padding-left: 12px;
}
#comment-textarea {
  width: 100%;
  display: flex;
  align-items: center;
}
#comment-btn {
  height: 100%;
  display: flex;
  justify-items: start;
  padding: 10px 10px 10px 0;
}
::-webkit-scrollbar {
  width: 12px;
  height: 10px;
}
::-webkit-scrollbar-track {
  background-color: rgba(255, 20, 147, 0.4);
  border-radius: 10px;
}
::-webkit-scrollbar-thumb {
  background-color: rgba(52, 152, 219, 0.85);
  border-radius: 10px;
}
</style>
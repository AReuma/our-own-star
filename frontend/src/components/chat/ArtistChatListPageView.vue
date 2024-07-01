<template>
  <div id="chat-div">
    <div v-for="(message, index) in messageRoomList" :key="index" @click="moveChatRoom(message.roomId)" style="width: 100%; display: flex; padding: 28px; border-bottom: 1px solid #BCBCBC; align-items: center">
      <v-avatar size="58">
        <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+message.userProfile"></v-img>
      </v-avatar>
      <div style="display: flex; flex-direction: column; flex-grow: 1; padding: 0 12px; height: 58px;">
        <p style="font-size: 20px;">{{ message.nickname }}</p>

        <div style="display: flex; width: 100%; flex-grow: 1; align-items: end">
          <div style="flex: 8; color: rgba(60,60,60,0.73);">{{message.latestMessage}}</div>
          <p style="justify-content: end; color: #BCBCBC">{{ formattedDate(message.createdDate) }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import moment from "moment/moment";
import router from "@/router";

export default defineComponent({
  name: "ArtistChatListPageView",
  props: {
    artist: {
      type: String,
      default: ''
    },
    messageRoomList: {
      type: Array,
      default: null
    }
  },
  methods: {
    formattedDate(date) {
      return moment(date).format('MM/DD · hh:mm a');
    },
    moveChatRoom(roomId){
      const {artist} = this;
      router.push({name: 'ArtistChattingRoomView',  params: {artist: artist, roomId: roomId}})
    }
  }
})
</script>

<style>
#chat-div {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: start;
  overflow-y: auto;
  position: relative;
}


</style>
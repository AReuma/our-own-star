<template>
  <div id="main-layout">
<!--    {{messageTotalPage}} // {{page}}// page: {{chatMessage.length}}-->
    <artist-chatting-page-view
        ref="artistChattingPageView"
        :joinCategoryUserInfo="joinCategoryUserInfo"
        :artist="artist"
        @send="send"
        :messages="messages"
        :chatMessage="chatMessage"
        :messageUserInfo="messageUserInfo"
        @getOldMsg="getOldMsg"
    ></artist-chatting-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client'
import {mapActions, mapState} from "vuex";
import {VueCookieNext} from "vue-cookie-next";
import ArtistChattingPageView from "@/components/chat/ArtistChattingPageView.vue";
import axios from "axios";
import {API_BASE_URL, ARTIST_CHAT} from "@/constant/ApiUrl/ApiUrl";

const headers = {
  Authorization: "Bearer "+VueCookieNext.getCookie('accessToken')
};

const config = {
  headers: {
    'Authorization': "Bearer "+VueCookieNext.getCookie('accessToken'),
    'Accept': 'application/json',
    'Content-Type': 'application/json'
  }
}

export default defineComponent({
  name: "ArtistChattingRoomView",
  components: {ArtistChattingPageView},
  props: {
    artist: {
      type: String,
      default: ''
    },
    roomId: {
      type: String,
      default: ''
    }
  },
  data(){
    return {
      socket: null,
      stompClient: null,
      messages: [],
      messageToSend: '',
      lastDate: '',
      page: 0,
    }
  },
  created() {
    const {artist, roomId} = this;
    this.fetchIdolCategoryJoinIsFirst(artist);
    this.connect();
    this.page = this.messagePage;
    this.fetchMessageUserInfo({artist, roomId})
  },
  mounted() {
    this.$refs.artistChattingPageView.scrollToBottom();
  },
  methods: {
    ...mapActions(['fetchIdolCategoryJoinIsFirst', 'fetchChatMessage', 'fetchMessageTotalPage', 'fetchSetMessagePage', 'fetchMessageUserInfo']),
    connect() {
      const serverURL = "http://localhost:7777/chat-websocket"
      this.socket = new SockJS(serverURL);
      this.stompClient = Stomp.over(this.socket);

      console.log(`소켓 연결을 시도합니다. 서버 주소: ${serverURL}`)

      this.stompClient.connect(headers, frame => {
        console.log('Connected: ' + frame);

        this.stompClient.subscribe(`/sub/topic/chat/${this.roomId}`, message =>  {
          // 연결 성공 후 로직 처리
          const msg = JSON.parse(message.body);
          this.messages.push(msg);
          this.$refs.artistChattingPageView.scrollToBottom();
        },headers)

        const {artist, roomId} = this;

        // 방 입장 메시지 전송
        //let info = this.joinCategoryUserInfo.nickname +'님이 들어왔습니다.'
        //this.sendMessage(info);
        //alert('chatRomView: '+ roomId)
        this.fetchChatMessage({artist, roomId});

        setTimeout(() => {
          this.$refs.artistChattingPageView.scrollToBottom();
        }, 70); // 5초 후에 fetchChatMessage 실행


      }, function(error) {
        console.log('Error: ' + error);
      });

      this.socket.onclose = () => {
        console.log('Close Socket ' + serverURL);
      };

      const {artist, roomId} = this;
      this.fetchMessageTotalPage({artist, roomId})
    },
    send(payload){
      const {messageToSend} = payload;
      this.sendMessage(messageToSend);
    },
    sendMessage(content) {
      if (this.stompClient && this.stompClient.connected) {
        let sender = this.joinCategoryUserInfo.nickname;
        let userProfileImg = this.joinCategoryUserInfo.userProfileImg;

        const message = {
          sender: sender,
          content: content,
          room: this.roomId, // chatRoom을 문자열로 변환하여 전송
          senderProfile: userProfileImg,
        };

        const destination = `/pub/sendMessage/${this.roomId}`;
        console.log("Destination:", destination); // 전송할 경로가 올바른지 확인

        this.stompClient.send(destination, JSON.stringify(message), headers); // 메시지 전송

        console.log("메시지 전송:", message);
      } else {
        console.error("STOMP 클라이언트가 연결되어 있지 않습니다.");
      }
      console.log(this.messages)
    },
    getOldMsg(){
      let {roomId, artist, page} = this;

      axios.get(API_BASE_URL+ARTIST_CHAT+`/${artist}/getOldMsg/${roomId}/${page}`, config)
          .then((res) => {
            console.log(res)
            const {artist, roomId} = this;

            this.fetchSetMessagePage(this.page++);

            if(res.data === false){
              this.fetchSetMessagePage(this.page--);
              //alert('마지막 메세지입니다.')
            }else {
              this.fetchChatMessage({artist, roomId});
              this.$refs.artistChattingPageView.scrollToTop();
            }
          })
          .catch((err) => {
            console.error(err)
          })
    }
  },
  beforeUnmount(){
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect();
    }
  },
  computed: {
    ...mapState(['joinCategoryUserInfo', 'chatMessage', 'messageTotalPage', 'messagePage', 'messageUserInfo'])
  },
})
</script>

<style scoped>
#main-layout {
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 0 250px;
}
</style>
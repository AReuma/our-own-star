<template>
  <div>
    <artist-chat-list-page-view
        v-if="(messageRoomList !== null || messageRoomList.size > 0)"
        :artist="artist" :messageRoomList="messageRoomList"></artist-chat-list-page-view>
    <div v-else style="display: flex; width: 100%; padding-top: 40%; justify-content: center; text-align: center; color: #3498DB; min-font-size: 28px">
      대화내용이 없습니다!<br/>
      대화를 시도해보세요~
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistChatListPageView from "@/components/chat/ArtistChatListPageView.vue";
import {mapActions, mapState} from "vuex";

export default defineComponent({
  name: "ArtistChatListView",
  components: {ArtistChatListPageView},
  data(){
    return {
      artist: '',
    }
  },
  created() {
    this.artist = this.$route.params.artist;
    const {artist} = this;
    console.log("test: "+artist)
    this.fetchMessageRoomList({artist})
  },
  methods: {
    ...mapActions(['fetchMessageRoomList'])
  },
  computed: {
    ...mapState(['messageRoomList'])
  }
})
</script>

<style scoped>
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
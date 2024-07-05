<template>
  <div id="main-div">
    <div id="board-content-list" v-for="n in popularBoard" :key="n">
      <v-hover v-slot="{ isHovering, props }">
        <v-sheet
            v-bind="props"
            :class="{ 'on-hover': isHovering }"
            :color="isHovering ? 'rgba(229,229,229,0.8)' : 'backColor'"
            style="display: flex; width: 100%"
            @click="onePostClick(n.id)"
        >
          <div>
            <v-avatar size="50">
              <v-img :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/'+n.writerProfile"></v-img>
            </v-avatar>
          </div>
          <div id="board-content-div">
            <div id="board-content-writer">
              {{ n.writer }}
              <p style="margin-left: 20px; font-size: 12px;">{{timeForToday(n.createDate)}}</p>
            </div>
            <div id="content-div">
              <div v-html="convertNewLineToBr(n.content)"></div>
              <div v-if="n.boardType === 'IMAGE'" style="height: auto">
                <v-carousel
                    height="400"
                    hide-delimiters
                    color="blue"
                    style="background-color: rgba(0,0,0,0.02); border-radius: 12px"
                >
                  <template v-slot:prev="{ props }">
                    <v-btn
                        icon="mdi-chevron-left"
                        @click.stop="props.onClick"
                        color="blue"
                        variant="plain"
                    ></v-btn>
                  </template>
                  <template v-slot:next="{ props }">
                    <v-btn
                        icon="mdi-chevron-right"
                        @click.stop="props.onClick"
                        color="blue"
                        variant="plain"
                    ></v-btn>
                  </template>
                  <v-carousel-item
                      v-for="(slide, i) in n.image"
                      :key="i"
                      :src="'https://our-own-start-static-files.s3.ap-northeast-2.amazonaws.com/' + slide"
                  >
                  </v-carousel-item>
                </v-carousel>
              </div>

              <div v-if="n.boardType === 'VOTE'">
                {{n.postVoteResponseDTO.choiceCount}}
                <v-btn-toggle
                    v-model="toggle"
                    color="pink"
                    mandatory
                    style="display: flex; flex-direction: column; height: 100%"
                >
                  <v-btn :disabled="true" class="ma-1" density="comfortable" variant="text" v-for="(c, index) in n.postVoteResponseDTO.choiceCount" :key="c" :value="index" style="height: 40px; opacity: 1; border: 1px solid hotpink">
                    {{n.postVoteResponseDTO.choice[index]}}
                  </v-btn>

                </v-btn-toggle>
                {{toggle}}
              </div>
            </div>
            <div id="board-content-btn-list">
              <div><v-btn density="compact" icon="mdi-message-outline" variant="plain"></v-btn> {{n.commentCount}}</div>
              <div v-if="n.like">
                <v-btn @click.stop="lickBoard(n.id)" class="disable-btn" density="compact" icon="mdi-heart" color="red" variant="plain"></v-btn>{{n.likeCount}}
              </div>
              <div v-else>
                <v-btn @click.stop="lickBoard(n.id)" class="disable-btn" density="compact" icon="mdi-heart-outline" variant="plain"></v-btn>{{n.likeCount}}
              </div>
              <div v-if="n.bookmark">
                <v-btn @click.stop="bookmarkBoard(n.id)" density="compact" icon="mdi-bookmark" color="blue" variant="plain"></v-btn> {{n.bookmarkCount}}
              </div>
              <div v-else>
                <v-btn @click.stop="bookmarkBoard(n.id)" density="compact" icon="mdi-bookmark-outline" variant="plain"></v-btn> {{n.bookmarkCount}}
              </div>
              <div><v-btn density="compact" icon="mdi-chart-line" variant="plain"></v-btn> 1</div>
            </div>
          </div>
        </v-sheet>
      </v-hover>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {mapActions, mapState} from "vuex";
import {timeForToday} from "../../constant/time/timeParser";
import router from "@/router";
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";

const config = {
  headers: {
    'Authorization': 'Bearer '+ VueCookieNext.getCookie('accessToken'),
    'Accept' : 'application/json',
    'Content-Type': 'application/json'
  }
};

export default defineComponent({
  name: "ArtistPopularPostPageView",
  data(){
    return {
      artist: '',
      toggle: undefined,
    }
  },
  created() {
    this.artist = this.$route.params.artist;
    const artist = this.$route.params.artist;
    this.fetchPopularBoard({artist});
  },
  methods: {
    timeForToday,
    ...mapActions(['fetchPopularBoard']),
    convertNewLineToBr(text) {
      return text.replace(/\n/g, '<br>');
    },
    onePostClick(id){
      const artist = this.$route.params.artist;
      router.push({name: 'ArtistBoardReadView', params: {artist: artist, boardNum: id}})
    },lickBoard(boardNum){
      const artist = this.$route.params.artist;
      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/like`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    },
    bookmarkBoard(boardNum){
      const artist = this.$route.params.artist;

      axios.post(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/bookmark`,{}, config)
          .then((res) => {
            console.log(res)
            window.location.reload();
          })
          .catch((err) => {
            console.log(err)
          })
    }
  },
  computed: {
    ...mapState(['popularBoard'])
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
#main-div {
  display: flex;
  flex-direction: column;
  width: 100%;
  margin-top: 10px;
  align-items: start;
  overflow-y: scroll;
  position: relative;
}
#board-content-list {
  border-bottom: 1px #D9D9D9 solid;
  width: 100%;
  display: flex;
  padding: 10px;
  margin-bottom: 10px;
}
#board-content-div {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
#board-content-writer {
  height: 50px;
  display: flex;
  align-items: center;
  padding-left: 12px;
  font-size: 20px;
}
#content-div {
  padding: 0 12px 12px 12px;
  flex-grow: 1;
  width: 100%;
}
#board-content-btn-list {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
</style>
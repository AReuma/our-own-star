<template>
  <div id="main-div" style="display: flex; flex-direction: column; width: 100%; margin-top: 10px; align-items: start; overflow-y: scroll; position: relative" @scroll="handleScroll">
    <div v-for="n in artistBoard" :key="n" style="border-bottom: 1px #D9D9D9 solid; width: 100%; height: 100%; display: flex; padding: 10px; margin-bottom: 10px">
      <v-hover v-slot="{ isHovering, props }">
        <v-sheet
            v-bind="props"
            :class="{ 'on-hover': isHovering }"
            :color="isHovering ? 'rgba(229,229,229,0.8)' : 'backColor'"
            style="display: flex; width: 100%"
            @click="onePostClick(n.id)"
        >  <div>
          <v-avatar size="50">
            <v-img src="@/assets/profile/profile-test.jpg"></v-img>
          </v-avatar>
        </div>
          <div style="width: 100%; height: 100%; display: flex; flex-direction: column;">
            <div style="height: 50px; display: flex; align-items: center; padding-left: 12px; font-size: 20px;">
              {{ n.writer }}   <p style="margin-left: 20px; font-size: 12px;">{{timeForToday(n.createDate)}}</p>
            </div>
            <div id="cotent-div" style="padding: 0 12px 12px 12px; flex-grow: 1; width: 100%">
              <div>{{n.content}}</div>
              {{n.boardType}}
              <div v-if="n.boardType === 'IMAGE'" style="height: auto">
                <v-carousel
                    height="400"
                    show-arrows
                    color="blue"
                >
                  <template v-slot:prev="{ props }">
                    <v-btn
                        icon="mdi-chevron-left"
                        @click="props.onClick"
                        color="blue"
                        variant="plain"
                    ></v-btn>
                  </template>
                  <template v-slot:next="{ props }">
                    <v-btn
                        icon="mdi-chevron-right"
                        @click="props.onClick"
                        color="blue"
                        variant="plain"
                    ></v-btn>
                  </template>
                  <v-carousel-item
                      cover
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
                  <v-btn class="ma-1" density="comfortable" variant="text" v-for="(c, index) in n.postVoteResponseDTO.choiceCount" :key="c" :value="index" style="height: 40px; border: 1px solid hotpink">
                    {{n.postVoteResponseDTO.choice[index]}}
                  </v-btn>

                </v-btn-toggle>
                {{toggle}}
              </div>
            </div>
            <div style="height: 60px; display: flex; justify-content: space-between; align-items: center; padding: 0 20px">
              <div><v-btn density="compact" icon="mdi-message-outline" variant="plain"></v-btn> 1</div>
              <div><v-btn density="compact" icon="mdi-heart-outline" variant="plain"></v-btn> 1</div>
              <div><v-btn density="compact" icon="mdi-bookmark-outline" variant="plain"></v-btn> 1</div>
              <div><v-btn density="compact" icon="mdi-chart-line" variant="plain"></v-btn> 1</div>
            </div>
          </div>
        </v-sheet>
      </v-hover>
    </div>
    <div style="width: 100%;">
      <v-alert class="text-center" v-if="currentPage >= artistBoardTotalPage" variant="tonal" color="blue" width="100%" height="100">
        더 이상 결과가 없습니다!
      </v-alert>
    </div>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import {mapActions, mapState} from "vuex";
import {timeForToday} from "@/constant/time/timeParser";
import router from "@/router";

export default defineComponent({
  name: "ArtistHomePageView",
  props: ['joinCategoryUserInfo'],
  data(){
    return {
      currentPage: 1,
      toggle: undefined,
      scrolling: false,
    }
  },
  methods:{
    timeForToday,
    ...mapActions(['fetchIdolCategoryBoard', 'fetchIdolCategoryBoardTotalPage']),
    onePostClick(id){
      alert(id)
      const artist = this.$route.params.artist;
      router.push({name: 'ArtistBoardReadView', params: {artist: artist, boardNum: id}})
    },
    handleScroll() {
      if (this.scrolling) {
        return;
      }

      console.log('artistBoardTotalPage: '+this.artistBoardTotalPage)
      this.scrolling = true;

      let div = document.getElementById("main-div");
      let scrollPos = div.scrollTop / (div.scrollHeight - div.clientHeight);
      console.log('scrollPos: '+ scrollPos)

      if (scrollPos >= 0.8 && this.artistBoardTotalPage > this.currentPage) {
        let page = ++this.currentPage;
        const artist = this.$route.params.artist;

        const nickname = this.joinCategoryUserInfo.nickname;
        this.fetchIdolCategoryBoard({artist, page, nickname})
      }

      console.log(this.currentPage)

      setTimeout(() => {
        this.scrolling = false;
      }, 10);
    },
  },
  watch: {
    joinCategoryUserInfo: {
      handler(newVal) {
        // joinCategoryUserInfo가 변경될 때의 로직
        if (newVal) {
          const artist = this.$route.params.artist;
          const page = this.currentPage;
          this.fetchIdolCategoryBoard({ artist, page, nickname: newVal.nickname });
          this.fetchIdolCategoryBoardTotalPage({artist, nickname: newVal.nickname})
        }
      },
      immediate: true, // 초기 값이 있을 경우 즉시 호출
    },
  },
  created() {
    /*const artist = this.$route.params.artist;
    const page = this.currentPage;
    const nickname = this.joinCategoryUserInfo.nickname;*/
    //alert(nickname)
    //this.fetchIdolCategoryBoardTotalPage(artist)
    //this.fetchIdolCategoryBoard({artist, page, nickname})
  },
  computed: {
    ...mapState(['artistBoard', 'artistBoardTotalPage'])
  }
})
</script>

<style scoped>
::-webkit-scrollbar {
  width: 20px;
  height: 20px;
}
::-webkit-scrollbar-track {
  background-color: rgba(52, 152, 219, 0.4);
  border-radius: 10px;
}
::-webkit-scrollbar-thumb {
  background-color: rgba(255, 20, 147, 0.85);
  border-radius: 10px;
}
</style>
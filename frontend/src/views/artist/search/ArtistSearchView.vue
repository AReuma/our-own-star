<template>
  <div id="main-layout">
    <artist-search-page-view
        :artist="artist"
        :keyword="keyword"
        :joinCategoryUserInfo="joinCategoryUserInfo"
    ></artist-search-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistSearchPageView from "@/components/artist/search/ArtistSearchPageView.vue";
import {mapActions, mapState} from "vuex";

export default defineComponent({
  name: "ArtistSearchView",
  components: {ArtistSearchPageView},
  props: {
    artist: {
      type: String,
      default: ''
    },
    keyword: {
      type: String,
      default: ''
    }
  },
  created() {
    const {artist} = this
    this.fetchIdolCategoryJoinIsFirst(artist)
  },
  methods: {
    ...mapActions(['fetchIdolCategoryJoinIsFirst'])
  },
  computed: {
    ...mapState(['joinCategoryUserInfo'])
  }
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
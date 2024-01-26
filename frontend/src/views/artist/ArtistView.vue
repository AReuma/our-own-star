<template>
  <div id="main-layout">
    <artist-page-view
        :artist="artist"
    ></artist-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistPageView from "@/components/artist/ArtistPageView.vue";

export default defineComponent({
  name: "ArtistView",
  components: {ArtistPageView},
  props: {
    artist: {
      type: String,
      default: ''
    },
  }
})
</script>

<style scoped>
#main-layout{
  width: 100%;
  height: 100%;
  background: #F0F0F0;
  padding: 10px 100px 10px 100px;
}
</style>
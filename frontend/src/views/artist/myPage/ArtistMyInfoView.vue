<template>
  <div>
    <artist-my-info-page-view
        :artist="artist"
    ></artist-my-info-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import ArtistMyInfoPageView from "@/components/artist/myPage/ArtistMyInfoPageView.vue";

export default defineComponent({
  name: "ArtistMyInfoView",
  props: {
    artist: {
      type: String,
      default: ''
    }
  },
  components: {ArtistMyInfoPageView},
  mounted() {
    console.log(this.artist)
  }
})
</script>

<style scoped>

</style>
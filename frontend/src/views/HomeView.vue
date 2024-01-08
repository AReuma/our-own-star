<template>
  <div>
    <main-page-view></main-page-view>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import MainPageView from "@/components/main/MainPageView.vue";

export default defineComponent({
  name: 'HomeView',
  components: {
    MainPageView
  },
});
</script>

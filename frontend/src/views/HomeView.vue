<template>
  <div>
    <main-logo-page-view></main-logo-page-view>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import MainLogoPageView from "@/components/main/MainLogoPageView.vue";

export default defineComponent({
  name: 'HomeView',
  components: {
    MainLogoPageView
  },
});
</script>

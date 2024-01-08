<template>
  <div>
    <register-page-view></register-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import RegisterPageView from "@/components/auth/RegisterPageView.vue";

export default defineComponent({
  name: "RegisterView",
  components: {RegisterPageView}
})
</script>

<style scoped>

</style>
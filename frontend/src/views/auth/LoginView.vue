<template>
  <div>
    <login-page-view></login-page-view>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import LoginPageView from "@/components/auth/LoginPageView.vue";

export default defineComponent({
  name: "LoginView",
  components: {LoginPageView}
})
</script>

<style scoped>

</style>
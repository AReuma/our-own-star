import {VueCookieNext} from "vue-cookie-next";

export default {
    getToken () {
        let accessToken = VueCookieNext.isCookieAvailable('accessToken')
        let refreshToken = VueCookieNext.isCookieAvailable('refreshToken')
        console.log(accessToken)
        return {
            accessToken, refreshToken
        }
    },
}
import {VueCookieNext} from "vue-cookie-next";

export default {
    getToken () {
        let accessToken = VueCookieNext.isCookieAvailable('accessToken')
        let refreshToken = VueCookieNext.isCookieAvailable('refreshToken')

        return {
            accessToken, refreshToken
        }
    }
}
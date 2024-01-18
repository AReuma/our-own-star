import {
    FETCH_SEARCH_IDOL_INFO,
} from './mutation-types'
import axios from "axios";
import {API_BASE_URL} from "@/constant/ApiUrl/ApiUrl";
import {VueCookieNext} from "vue-cookie-next";

/*const config = {
    headers: {
        'Authorization': 'Bearer '+ useCookies().cookies.get('access_token'),
        'Accept' : 'application/json',
        'Content-Type': 'application/json'
    }
};*/

export default {
    fetchSearchIdolInfo({commit}, artist){
        let cookie = VueCookieNext.getCookie('accessToken');
        console.log("cookie: {}", cookie)
        commit('setLoading', true)
        return axios.get(API_BASE_URL+"/api/v1/idol/"+artist, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_SEARCH_IDOL_INFO, res.data)
            })
            .finally(() => {
                commit('setLoading', false);
            })
    },
    fetchArtistInfoReset({commit}, artistInfoReset){
        commit('setSearchIdolInfo', artistInfoReset)
    }
}
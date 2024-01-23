import {
    FETCH_IDOL_CATEGORY, FETCH_IDOL_CATEGORY_TOTAL_PAGE,
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
        console.log("cookie: {}", cookie ? 'Bearer ' + cookie : null)
        commit('setLoading', true)
        return axios.get(API_BASE_URL+"/api/v1/idol/search/"+artist, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                console.log("fetchSearchIdolInfo: "+res)
                commit(FETCH_SEARCH_IDOL_INFO, res.data)
            })
            .catch((res) => {
                console.error("fetchSearchIdolInfo: ", res)
            })
            .finally(() => {
                commit('setLoading', false);
            })
    },
    fetchArtistInfoReset({commit}, artistInfoReset){
        commit('setSearchIdolInfo', artistInfoReset)
    },
    fetchIdolCategory({commit}, page) {
        return axios.get(API_BASE_URL+"/api/v1/idol/page/"+page)
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY, res.data)
            })
            .catch((res) => {
                console.error(res)
            })
    },
    fetchIdolCategoryTotalPage({commit}){
        return axios.get(API_BASE_URL+"/api/v1/idol/getTotalPage")
            .then((res) => {
                console.log(res.data)
                commit(FETCH_IDOL_CATEGORY_TOTAL_PAGE, res.data)
            })
    }
}
import {
    FETCH_IDOL_CATEGORY,
    FETCH_IDOL_CATEGORY_BOARD,
    FETCH_IDOL_CATEGORY_BOARD_TOTAL_PAGE,
    FETCH_IDOL_CATEGORY_JOIN_IS_FIRST,
    FETCH_IDOL_CATEGORY_TOTAL_PAGE,
    FETCH_SEARCH_IDOL_INFO,
} from './mutation-types'
import axios from "axios";
import {API_BASE_URL, ARTIST_CATEGORY, ARTIST_CATEGORY_BOARD} from "@/constant/ApiUrl/ApiUrl";
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
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+"/search/"+artist, {
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
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+"/page/"+page)
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY, res.data)
            })
            .catch((res) => {
                console.error(res)
            })
    },
    fetchUserIdolCategory({commit}, {page, username}) {
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+"/page/"+page+"/username/"+username, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY, res.data)
            })
            .catch((res) => {
                console.error(res)
            })
    },
    fetchIdolCategoryTotalPage({commit}){
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+"/getTotalPage")
            .then((res) => {
                console.log(res.data)
                commit(FETCH_IDOL_CATEGORY_TOTAL_PAGE, res.data)
            })
    },
    fetchIdolCategoryJoinIsFirst({commit}, id){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+"/join/"+id+"/userInfo", {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_JOIN_IS_FIRST, res.data)
            })
    },
    fetchIdolCategoryBoard({commit}, {artist, page, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+"/"+artist+"/"+nickname+"/"+page, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_BOARD, res.data)
            })
    },
    fetchIdolCategoryBoardTotalPage({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+"/"+artist+"/"+nickname+"/getTotalPage", {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_BOARD_TOTAL_PAGE, res.data)
            })
    }
}
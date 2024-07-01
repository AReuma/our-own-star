import {
    FETCH_ARTIST_PLACE,
    FETCH_CHAT_MESSAGE,
    FETCH_IDOL_CATEGORY,
    FETCH_IDOL_CATEGORY_ADD_BOARD,
    FETCH_IDOL_CATEGORY_BOARD,
    FETCH_IDOL_CATEGORY_BOARD_COMMENT,
    FETCH_IDOL_CATEGORY_BOARD_TOTAL_PAGE,
    FETCH_IDOL_CATEGORY_JOIN_IS_FIRST,
    FETCH_IDOL_CATEGORY_MARKET_BOARD,
    FETCH_IDOL_CATEGORY_MARKET_BOARD_TOTAL_PAGE,
    FETCH_IDOL_CATEGORY_MODIFY_MARKET_BOARD,
    FETCH_IDOL_CATEGORY_ONE_BOARD,
    FETCH_IDOL_CATEGORY_ONE_MARKET_BOARD,
    FETCH_IDOL_CATEGORY_TOTAL_PAGE,
    FETCH_MARKET_COMMENT,
    FETCH_MESSAGE_ROOM_LIST,
    FETCH_MESSAGE_TOTAL_PAGE,
    FETCH_MESSAGE_USER_INFO,
    FETCH_MY_PAGE_BOARD,
    FETCH_MY_PAGE_COMMENT,
    FETCH_MY_PAGE_LIKE,
    FETCH_MY_PAGE_MARKET,
    FETCH_MY_PAGE_PROFILE,
    FETCH_NICKNAME_DUB_CHECK,
    FETCH_POPULAR_BOARD,
    FETCH_SEARCH_ARTIST_NAME, FETCH_SEARCH_BOARD,
    FETCH_SEARCH_IDOL_INFO, FETCH_SEARCH_MARKET_BOARD, FETCH_SEARCH_USER,
    FETCH_USER_INFO_BOARD,
    FETCH_USER_INFO_COMMENT,
    FETCH_USER_INFO_LIKE,
    FETCH_USER_INFO_MARKET,
    FETCH_USER_PROFILE,
} from './mutation-types'
import axios from "axios";
import {
    API_BASE_URL,
    ARTIST_CATEGORY,
    ARTIST_CATEGORY_BOARD,
    ARTIST_CHAT, ARTIST_MEMBER,
    ARTIST_MY_PAGE
} from "@/constant/ApiUrl/ApiUrl";
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
    fetchIdolCategoryBoard({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+"/"+artist+"/"+nickname+"/"+1, {
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
    fetchIdolCategoryAddBoard({commit}, {artist, page, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+"/"+artist+"/"+nickname+"/"+page, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_ADD_BOARD, res.data)
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
    },
    fetchIdolCategoryOne({commit}, {artist, boardNum}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+"/"+artist+"/"+boardNum, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_ONE_BOARD, res.data)

                axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${boardNum}/comment`,{
                    headers: {
                        'Authorization': cookie ? 'Bearer ' + cookie : null,
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    }
                })
                    .then((res) => {
                        commit(FETCH_IDOL_CATEGORY_BOARD_COMMENT, res.data)
                    })
            })
    },
    fetchIdolCategoryMarketBoard({commit}, {artist, nickname, page}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${nickname}/${page}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_MARKET_BOARD, res.data)
            })
    },
    fetchIdolCategoryMarketBoardTotalPage({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        ///{artist}/market/getTotalPage
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/getTotalPage`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_MARKET_BOARD_TOTAL_PAGE, res.data)
            })
    },
    fetchIdolCategoryOneMarketBoard({commit}, {artist, sellNum}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/board/${sellNum}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_ONE_MARKET_BOARD, res.data)
            })
    },
    fetchIdolCategoryModifyMarketBoard({commit}, {artist, sellNum}){
        ///{artist}/market/{boardId}/modify
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/market/${sellNum}/modify`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_IDOL_CATEGORY_MODIFY_MARKET_BOARD, res.data)
            })
    },
    fetchChatMessage({commit}, {artist, roomId}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CHAT+`/${artist}/chat/${roomId}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_CHAT_MESSAGE, res.data)
            })
    },
    fetchMessageTotalPage({commit}, {artist, roomId}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CHAT+`/${artist}/getTotalPage/${roomId}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MESSAGE_TOTAL_PAGE, res.data)
            })
    },
    fetchSetMessagePage({commit}, messagePage){
        commit('setMessagePage', messagePage)
    },
    fetchMessageUserInfo({commit}, {artist, roomId}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CHAT+`/${artist}/getReceiverUserInfo/${roomId}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MESSAGE_USER_INFO, res.data)
            })
    },
    fetchMessageRoomList({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CHAT+`/${artist}/getChatRoomList`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MESSAGE_ROOM_LIST, res.data)
            })
    },
    fetchPopularBoard({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/getPopularBoard`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_POPULAR_BOARD, res.data)
            })
    },
    fetchMarketComment({commit}, {artist, sellNum}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/${sellNum}/marketComment`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MARKET_COMMENT, res.data)
            })
    },
    fetchMyPageProfile({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MY_PAGE_PROFILE, res.data)
            })
    },
    fetchMyPageBoard({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo/board`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MY_PAGE_BOARD, res.data)
            })
    },
    fetchMyPageComment({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo/comment`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MY_PAGE_COMMENT, res.data)
            })
    },
    fetchMyPageLike({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo/like`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MY_PAGE_LIKE, res.data)
            })
    },
    fetchMyPageMarket({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo/market`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_MY_PAGE_MARKET, res.data)
            })
    },
    fetchNicknameDubCheck({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MY_PAGE+`/${artist}/getMyInfo/modify/${nickname}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                console.log(res)
                if (!res.data){
                    alert('중복인 닉네임입니다.')
                }
                commit(FETCH_NICKNAME_DUB_CHECK, res.data)
            })
            .catch((err) => {
                console.error(err)
            })
    },
    fetchUpdateIsDubNickname({commit}){
        commit('updateIsDubNickname')
    },
    fetchSearchArtistName({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+`/find/${artist}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_SEARCH_ARTIST_NAME, res.data)
            })
    },
    fetchBeforeLonginArtistName({commit}, {artist}){
        return axios.get(API_BASE_URL+ARTIST_CATEGORY+`/find/beforeLogin/${artist}`, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_SEARCH_ARTIST_NAME, res.data)
            })
    },
    fetchUserProfile({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MEMBER+`/getUserInfo/${artist}/${nickname}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_USER_PROFILE, res.data)
            })
    },
    fetchUserInfoBoard({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MEMBER+`/${artist}/${nickname}/board`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_USER_INFO_BOARD, res.data)
            })
    },
    fetchUserInfoComment({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MEMBER+`/${artist}/${nickname}/comment`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_USER_INFO_COMMENT, res.data)
            })
    },
    fetchUserInfoLike({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MEMBER+`/${artist}/${nickname}/like`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_USER_INFO_LIKE, res.data)
            })
    },
    fetchUserInfoMarket({commit}, {artist, nickname}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_MEMBER+`/${artist}/${nickname}/market`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_USER_INFO_MARKET, res.data)
            })
    },
    fetchArtistPlace({commit}, {artist}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/getPlace`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_ARTIST_PLACE, res.data)
            })
            .catch((err) => {
                console.error(err)
            })
    },
    fetchSearchBoard({commit}, {artist, keyword}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/search/${keyword}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_SEARCH_BOARD, res.data)
            })
            .catch((err) => {
                console.error(err)
            })
    },
    fetchSearchMarketBoard({commit}, {artist, keyword}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/searchMarket/${keyword}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_SEARCH_MARKET_BOARD, res.data)
            })
            .catch((err) => {
                console.error(err)
            })
    },
    fetchSearchUser({commit}, {artist, keyword}){
        let cookie = VueCookieNext.getCookie('accessToken');
        return axios.get(API_BASE_URL+ARTIST_CATEGORY_BOARD+`/${artist}/searchUser/${keyword}`, {
            headers: {
                'Authorization': cookie ? 'Bearer ' + cookie : null,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        })
            .then((res) => {
                commit(FETCH_SEARCH_USER, res.data)
            })
            .catch((err) => {
                console.error(err)
            })
    }
}
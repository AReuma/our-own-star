import {
    FETCH_SEARCH_IDOL_INFO,
} from "@/store/mutation-types";

export default {
    [FETCH_SEARCH_IDOL_INFO](state, searchIdolInfo){
        state.searchIdolInfo = searchIdolInfo;
    },
    setLoading(state, loading){
        state.searchIdolLoading = loading
    },
    setSearchIdolInfo(state, searchIdolInfo){
        state.searchIdolInfo = searchIdolInfo;
    }
}
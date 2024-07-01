import {
    FETCH_IDOL_CATEGORY, FETCH_IDOL_CATEGORY_JOIN_IS_FIRST,
    FETCH_IDOL_CATEGORY_TOTAL_PAGE,
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
    },
    [FETCH_IDOL_CATEGORY](state, idolCategory){
        state.idolCategory = idolCategory;
    },
    [FETCH_IDOL_CATEGORY_TOTAL_PAGE](state, idolCategoryTotalPage){
        state.idolCategoryTotalPage = idolCategoryTotalPage;
    },
    [FETCH_IDOL_CATEGORY_JOIN_IS_FIRST](state, joinCategoryUserInfo){
        state.joinCategoryUserInfo = joinCategoryUserInfo;
    }
}
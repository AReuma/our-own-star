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
    },
    [FETCH_IDOL_CATEGORY_ADD_BOARD](state, artistBoard){
        if (artistBoard.length !== 0){
            state.artistBoard.push(...artistBoard);
        }
    },
    [FETCH_IDOL_CATEGORY_BOARD](state, artistBoard){
      state.artistBoard = artistBoard;
    },
    [FETCH_IDOL_CATEGORY_BOARD_TOTAL_PAGE](state, artistBoardTotalPage){
        console.log(artistBoardTotalPage)
        state.artistBoardTotalPage = artistBoardTotalPage;
    },
    [FETCH_IDOL_CATEGORY_ONE_BOARD](state, artistOneBoard){
        state.artistOneBoard = artistOneBoard;
    },
    [FETCH_IDOL_CATEGORY_BOARD_COMMENT](state, artistBoardComment){
        state.artistBoardComment = artistBoardComment;
    },
    [FETCH_IDOL_CATEGORY_MARKET_BOARD](state, artistMarketBoard) {
        state.artistMarketBoard = artistMarketBoard;
    },
    [FETCH_IDOL_CATEGORY_MARKET_BOARD_TOTAL_PAGE](state, artistMarketTotalPage){
        state.artistMarketTotalPage = artistMarketTotalPage;
    },
    [FETCH_IDOL_CATEGORY_ONE_MARKET_BOARD](state, artistMarketOneBoard){
        state.artistMarketOneBoard = artistMarketOneBoard;
    },
    [FETCH_IDOL_CATEGORY_MODIFY_MARKET_BOARD](state, artistMarketOneBoard) {
        state.artistMarketOneBoard = artistMarketOneBoard;
    },
    [FETCH_CHAT_MESSAGE](state, chatMessage){
        state.chatMessage = chatMessage;
        /*if (chatMessage.length !== 0) {
            //state.chatMessage = [...state.chatMessage, ...chatMessage]
            state.chatMessage.push(...chatMessage);
            alert('메세지 추가 완료함. '+state.chatMessage.length)
        }*/
    },
    [FETCH_MESSAGE_TOTAL_PAGE](state, messageTotalPage) {
        state.messageTotalPage = messageTotalPage;
    },
    setMessagePage(state, messagePage){
        state.messagePage = messagePage;
    },
    [FETCH_MESSAGE_USER_INFO](state, messageUserInfo){
        state.messageUserInfo = messageUserInfo;
    },
    [FETCH_MESSAGE_ROOM_LIST](state, messageRoomList){
        state.messageRoomList = messageRoomList;
    },
    [FETCH_POPULAR_BOARD](state, popularBoard) {
        state.popularBoard = popularBoard;
    },
    [FETCH_MARKET_COMMENT](state, artistMarketComment){
        state.artistMarketComment = artistMarketComment;
    },
    [FETCH_MY_PAGE_PROFILE](state, myPageProfile){
        state.myPageProfile = myPageProfile;
    },
    [FETCH_MY_PAGE_BOARD](state, myPageBoard){
        state.myPageBoard = myPageBoard;
    },
    [FETCH_MY_PAGE_COMMENT](state, myPageComment){
        state.myPageComment = myPageComment;
    },
    [FETCH_MY_PAGE_LIKE](state, myPageLikeBoard){
        state.myPageLikeBoard = myPageLikeBoard;
    },
    [FETCH_MY_PAGE_MARKET](state, myPageMarketBoard){
        state.myPageMarketBoard = myPageMarketBoard;
    },
    [FETCH_NICKNAME_DUB_CHECK](state, isDubNickname){
        state.isDubNickname = isDubNickname;
    },
    updateIsDubNickname(state, isDubNickname){
        state.isDubNickname = isDubNickname;
    },
    [FETCH_SEARCH_ARTIST_NAME](state, searchArtistInfo){
        state.searchArtistInfo = searchArtistInfo;
    },
    [FETCH_USER_PROFILE](state, userPageProfile){
        state.userPageProfile = userPageProfile;
    },
    [FETCH_USER_INFO_BOARD](state, userInfoBoard){
        state.userInfoBoard = userInfoBoard;
    },
    [FETCH_USER_INFO_COMMENT](state, userInfoComment){
        state.userInfoComment = userInfoComment;
    },
    [FETCH_USER_INFO_LIKE](state, userInfoLikeBoard){
        state.userInfoLikeBoard = userInfoLikeBoard;
    },
    [FETCH_USER_INFO_MARKET](state, userInfoMarketBoard){
        state.userInfoMarketBoard = userInfoMarketBoard;
    },
    [FETCH_ARTIST_PLACE](state, artistPlace){
        state.artistPlace = artistPlace;
    },
    [FETCH_SEARCH_BOARD](state, searchBoard){
        state.searchBoard = searchBoard;
    },
    [FETCH_SEARCH_MARKET_BOARD](state, searchMarketBoard){
        state.searchMarketBoard = searchMarketBoard;
    },
    [FETCH_SEARCH_USER](state, searchUser){
        state.searchUser = searchUser
    }
}
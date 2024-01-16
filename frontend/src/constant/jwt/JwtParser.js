import {useCookie} from "vue-cookie-next";
import {Base64} from "js-base64";
import {ACCESS_TOKEN_EXPIRE} from "@/constant/jwt/JwtUtil";

function ParsingInfo(token){
    const accessTokenInfo = token.split(".");

    let base64Encoded = Base64.decode(accessTokenInfo[1]);

    let result = JSON.parse(base64Encoded.toString())

    console.log(result)

    let username = result.username;
    let nickName = result.nickname;
    let role = result.role;

    useCookie().setCookie('email', username, ACCESS_TOKEN_EXPIRE)
    useCookie().setCookie('role', role, ACCESS_TOKEN_EXPIRE)
    useCookie().setCookie('nickName', nickName, ACCESS_TOKEN_EXPIRE)
}

export {
    ParsingInfo,
}
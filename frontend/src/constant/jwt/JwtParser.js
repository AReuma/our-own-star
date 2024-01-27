import {VueCookieNext} from "vue-cookie-next";
import {Base64} from "js-base64";
import {ACCESS_TOKEN_EXPIRE} from "@/constant/jwt/JwtUtil";

async function ParsingInfo(token){
    return new Promise((resolve) => {
        const accessTokenInfo = token.split(".");

        let base64Encoded = Base64.decode(accessTokenInfo[1]);

        let result = JSON.parse(base64Encoded.toString())

        console.log(result)

        let username = result.username;
        let nickName = result.nickname;
        let role = result.role;

        console.log("username: " + username)
        console.log("nickName: " + nickName)
        console.log("role: " + role)

        VueCookieNext.setCookie('email', username, {
            expire: ACCESS_TOKEN_EXPIRE,
        });
        VueCookieNext.setCookie('role', role, {
            expire: ACCESS_TOKEN_EXPIRE,
        });
        VueCookieNext.setCookie('nickname', nickName, {
            expire: ACCESS_TOKEN_EXPIRE,
        });

        resolve();
    });
}

export {
    ParsingInfo,
}
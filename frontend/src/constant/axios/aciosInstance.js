import axios from 'axios';
import { VueCookieNext } from 'vue-cookie-next';
import { API_BASE_URL } from "@/constant/ApiUrl/ApiUrl";

const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
});

axiosInstance.interceptors.request.use(config => {
    const token = VueCookieNext.getCookie('accessToken');
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export default axiosInstance;
'use strict';

import axios from "axios";
import { viewLoading } from '@/stores/reactive'

import { authStoe } from '@/stores'
// import { useRouter } from "vue-router";

// const router = useRouter()

//自定义 axios 实例
const ajax=axios.create({
    baseURL:'http://172.20.10.3:30000',
    timeout:5000,
})

// request interceptor
ajax.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么

    // 展示加载中的弹窗
    if (config.viewLoading !== false && viewLoading.status === false) {
        viewLoading.status = true
    }

    const permission = authStoe()

    if(permission.token) config.headers.Authorization ='Bearer ' + permission.token // 在请求头中设置个 token
    
    return config;

}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// response interceptor
ajax.interceptors.response.use(
    response => {
        if (viewLoading.status === true) {
            viewLoading.status = false
        }
        return response.data
    }
)

export default ajax;

import axios from "axios";
import useUserStore from "../store/store.user"
import { ElMessage } from "element-plus";

const userStore=useUserStore()

//自定义 axios 实例
const ajax=axios.create({
    baseURL:'http://172.20.10.3:30000/',
    timeout:8000,
})

// 不需要 带 token 的 url
const excludedUrls = [
    '/user/user/login_by_password',
    '/user/user/login_by_verification_code',
    '/user/user/verification_code',
    '/user/student/register',
    '/user/teacher/register',
    '/course/info/all',
    '/course/info/banner',
    // '/index', 不是路由路径，而是请求后端接口的路径
    // '/course/detail',
]

// request interceptor
ajax.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    
    let flag = true

    // console.log(config.url)
    excludedUrls.forEach(e => {
        if(config.url == e){
            flag = false
            return ;
        }
    });

    if(flag && userStore.token !== null && userStore.token !== "" && userStore.token !== undefined) config.headers.Authorization ='Bearer ' + userStore.token // 在请求头中设置个 token
    
    return config;

}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

//response interceptor
// ajax.interceptors.response.use((config)=>{
//     console.log(config)
    // if(config.data.code == 200){
    //     console.log(config.data.message)
    //     ElMessage.success(config.data.message)
    // }
    // return config; // 别忘了返回这个config，否则那边接收不到响应
// })

export default ajax
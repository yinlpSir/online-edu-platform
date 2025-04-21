<script setup>
    import { reactive, ref ,defineComponent, toRaw, onMounted} from "vue"
    import useUserStore from "@/store/store.user"
    import {Avatar,Right,Notification,ChatDotRound,DArrowRight,Search} from "@element-plus/icons-vue"
    import { useRoute, useRouter } from "vue-router"
    import {logout} from "@/api/api.user.js"
    import { searchCourse } from "@/utils/common-utils"

    onMounted(()=>{
        if(window.location.href.startsWith('http://hxeihj.natappfree.cc/')){
            window.location.href = 'http://127.0.0.1:5173/user/center'
        }
    })

    const router=useRouter()
    
    const route = useRoute()

    const userStore=useUserStore()

    const avatarShow=ref(false)

    const avatarSize=ref(45)

    const avatarTransition=reactive({
        transition:'',
        transform:''
    })
    
    // 鼠标移入头像 显示 avatar-info
    const handleAvatarOver=()=>{
        // console.log('over')
        // avatarTransition.transition='all 1s ease'
        // avatarTransition.transform='scale(1.2,1.2) translateY(8px)'

        // Object.assign(avatarTransition,{
        //     transition:'all 1s ease',
        //     transform:'scale(1.2,1.2) translateY(8px)'
        // })
        avatarShow.value=true
        // console.log(avatarTransition.transition)

        // avatarSize.value=52
    }

    // 鼠标移出头像 不显示 avatar-info
    const handleAvatarLeave=()=>{
        // console.log('leave')
        avatarShow.value=false
        // avatarTransition.transition=''
        // avatarTransition.transform=''

        // Object.assign(avatarTransition,null)
        // console.log(avatarTransition)
        // avatarSize.value=45

        // console.log(userStore.isLogged)
    }

    const getCurrentComPath=()=>{
        const path=import.meta.url
        return path.substring(0,path.indexOf("components"))+'assets/'
    }

    // 显示项目logo
    const logoPath=ref(getCurrentComPath()+'images/logo-no-bgimg.png')

    const handleSearch = () => {
        if(route.path === '/course/search') {
            // if(userStore.searchCondition.searchType == '1'){
            //     userStore.searchCondition.courseName = userStore.searchContent
            // }else  userStore.searchCondition.teacherName = userStore.searchContent
            searchCourse(toRaw(userStore.searchCondition))
        }
        router.replace('/course/search')
    }

    const toHomePage= () => router.push('/')

    const toUserCenter= (id) => router.push({path:'/user/center',query:{id:id}})

    const toTeacherCenter = (id) => router.push({path:'/teacher',query:{id:id}})

    const toUserCourse= (id) => router.push({path:'/user/center/course',query:{id:id}})

    const toUserMsg= (id) => router.push({path:'/user/center/message',query:{id:id}})

    // quit login
    const quit=()=>{
        logout().then(response => {
            ElMessage.success(response.data.message) 
            userStore.setToken(null)
            userStore.setUserPartlyInfo(null)
            localStorage.clear()
            toHomePage()
        }, error => console.log("logout error!"))
    }
</script>

<template>
    <div class="header-box">
        <div class="header-wrapper">
            <div class="logo" @click="toHomePage">
                <el-image :src="logoPath" fit="fill" style="width: 60px;height: 50px; ">
                    <template #error>
                    <div class="image-slot">
                        <el-icon><icon-picture /></el-icon>
                    </div>
                    </template>
                </el-image>
                <div class="title">在 线 教 育 平 台</div>
            </div>
            <div class="right-box">
                <div style=" height: 45px;display: flex; align-items: center;margin-right: 20px;">
                    <el-input
                        v-model.trim="userStore.searchContent"
                        :placeholder="userStore.searchCondition.searchType == '1'? '请输入课程名':'请输入教师名'"
                        @keyup.enter="handleSearch"
                    >
                        <template #prefix>
                            <el-select v-model="userStore.searchCondition.searchType">
                                <el-option label="课程名"  value="1" />
                                <el-option label="教师名" value="2" />
                            </el-select>
                        </template>
                        <template #suffix>
                            <el-icon class="el-input__icon" style="cursor: pointer;" @click="handleSearch"><search /></el-icon>
                        </template>
                    </el-input>
                </div>
                <div class="avatar-container" @mouseover="handleAvatarOver" @mouseout="handleAvatarLeave" >
                    <!-- <transition name="avatar-slide-out"> -->
                        <el-avatar 
                            class="avatar"
                            :size="avatarSize" 
                            :src="userStore.userPartlyInfo?userStore.userPartlyInfo.headPortrait:undefined"
                        >
                            未登录
                        </el-avatar>
                    <!-- </transition> -->
                    <transition name="slide">
                        <div class="avatar-info" v-show="avatarShow">
                            <div v-show="!userStore.isLogged">
                                未登录！
                                <div>
                                    <el-button type="primary" round @click="userStore.setShowLoginCompo(true)">登录</el-button>
                                    <el-button type="primary" round @click="router.push('/user/register')">注册</el-button>
                                </div>
                            </div>

                            <div v-if="userStore.isLogged && userStore.userPartlyInfo.role == 3" class="userInfo">
                                <span style="display: block;text-align:center;margin-bottom: 8px;margin-top: 20px;"><b>{{userStore.userPartlyInfo.username}}</b></span>
                                <span style="width:100%;display: block;margin-left: 5px; font-size: 10px; color:skyblue;text-indent: 2em;text-decoration: underline;">{{ userStore.userPartlyInfo.introduction }}</span>
                                <div style="margin-top: 12px;">
                                    <div class="user-item" @click="toUserCenter(userStore.userPartlyInfo.id)">
                                        <div style="display: flex; align-items: center;margin-left: 10px;">
                                            <el-icon :size="18" :color="'#61666D'"><Avatar /></el-icon>
                                            <span style="margin-left:6px ; font-size: 14px;color: #61666D;">个人中心</span>
                                        </div>
                                        <el-icon style="margin-right: 6px; :color: '#61666D';"><Right /></el-icon>
                                    </div>
                                    <div class="user-item" @click="toUserCourse(userStore.userPartlyInfo.id)">
                                        <div style="display: flex; align-items: center;margin-left: 10px;">
                                            <el-icon :size="18" :color="'#61666D'"><Notification /></el-icon>
                                            <span style="margin-left:6px ; font-size: 14px;color: #61666D;">我的课程</span>
                                        </div>
                                        <el-icon style="margin-right: 6px; :color: '#61666D';"><Right /></el-icon>
                                    </div>
                                    <div class="user-item" @click="toUserMsg(userStore.userPartlyInfo.id)">
                                        <div style="display: flex; align-items: center;margin-left: 10px;">
                                            <el-icon :size="18" :color="'#61666D'"><ChatDotRound /></el-icon>
                                            <span style="margin-left:6px ; font-size: 14px;color: #61666D;">我的消息</span>
                                        </div>
                                        <el-icon style="margin-right: 6px; :color: '#61666D';"><Right /></el-icon>
                                    </div>
                                    <hr style="margin-left: 9px;" />
                                    <div class="user-item" style="margin-top: 4px;">
                                        <div style="display: flex; align-items: center;margin-left: 10px;" @click="quit">
                                            <el-icon :size="18" :color="'#61666D'"><DArrowRight /></el-icon>
                                            <span style="margin-left:6px ; font-size: 14px;color: #61666D;">退出</span>
                                        </div>
                                        <el-icon style="margin-right: 6px; :color: '#61666D';"><Right /></el-icon>
                                    </div>
                                </div>
                            </div>

                            <div v-if="userStore.isLogged && userStore.userPartlyInfo.role == 2" class="userInfo">
                                <span style="display: block;text-align:center;margin-bottom: 8px;margin-top: 20px;"><b>{{userStore.userPartlyInfo.username}}</b></span>
                                <span style="width:100%;display: block;margin-left: 5px; font-size: 10px; color:skyblue;text-indent: 2em;text-decoration: underline;">{{ userStore.userPartlyInfo.introduction }}</span>
                                <div style="margin-top: 12px;">
                                    <div class="user-item" @click="toTeacherCenter(userStore.userPartlyInfo.id)">
                                        <div style="display: flex; align-items: center;margin-left: 10px;">
                                            <el-icon :size="18" :color="'#61666D'"><Avatar /></el-icon>
                                            <span style="margin-left:6px ; font-size: 14px;color: #61666D;">个人中心</span>
                                        </div>
                                        <el-icon style="margin-right: 6px; :color: '#61666D';"><Right /></el-icon>
                                    </div>
                                    <hr style="margin-left: 9px;" />
                                    <div class="user-item" style="margin-top: 4px;">
                                        <div style="display: flex; align-items: center;margin-left: 10px;" @click="quit">
                                            <el-icon :size="18" :color="'#61666D'"><DArrowRight /></el-icon>
                                            <span style="margin-left:6px ; font-size: 14px;color: #61666D;">退出</span>
                                        </div>
                                        <el-icon style="margin-right: 6px; :color: '#61666D';"><Right /></el-icon>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </transition>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .header-box {
        min-width: 1300px;
        width: 100vw;
        height: 100%;
        display:flex;
        justify-content: center;
        cursor: default;
        background-color: rgba(214, 226, 252, 0.5);
        /* border: 1px solid black; */
    }
    .header-wrapper{
        min-width: 1300px;
        width: 90%;
        max-width: 1300px;
        /* border: 1px solid green; */
        display: flex;
        justify-content: space-between;
        align-items: center;
        
    }
    .header-box .header-wrapper .logo{
        display: inline-block;
        display: flex;
        justify-content: center;
        align-items: flex-end;
        margin-left: 15px;
        width: auto;
        /* border: 1px solid black; */
        height: 100%;
        text-align: center;
        line-height: 60px;
        cursor: pointer;
    }
    .header-box .header-wrapper .title{
        display: inline-block;
        /* border: 1px solid green; */
        /* width: 30px; */
        height: 50px;
        line-height: 50px;
        font-weight: bold;
        font-size: 20px;
    }
    /* .header-box .header-wrapper .logo .logo-img{
        width: 100%;
        height: 100%;
        border: 1px solid red;
        background-image: url('../assets/images/logo-no-bgimg.png');
        background-repeat: no-repeat;
        background-position: center center;
        background-size: 100%;
    } */
    .header-box .header-wrapper .right-box{
        display: flex;
        height: 100%;
        /* width: 100px; */
        /* line-height: 60px; */
        /* border: 1px solid black; */
        text-align:center;
        align-items: flex-end;
        /* position: relative; */
        /* margin-right: 10px; */
    }
    /* :deep(.el-input-group__prepend .el-input--suffix){
        border-top-left-radius: 20px;
        border-bottom-left-radius: 20px;
    } */
    /* :deep(.el-input-group__prepend .el-input--suffix .el-input__wrapper){
        border-top-left-radius: 20px;
        border-bottom-left-radius: 20px;
    } */
    :deep(.el-input__wrapper){
        border-radius: 20px;
        background-color: rgba(0, 0, 0, 0);
        border: 1px solid rgb(214,202,250);
        width: 290px;
        /* border-top-right-radius: 20px;
        border-bottom-right-radius: 20px; */
    }
    .header-box .header-wrapper .right-box .avatar-container{
        position: relative;
        display: inline-block;
        display: flex;
        align-items: flex-end;
        /* border: 1px solid green; */
        border-radius: 50% 50%;
        /* margin-left: 50px; */
        margin-bottom: 1px;
        cursor: pointer;
        margin-right: 15px;
    }

    .avatar-info {
        display: block;
        position: absolute;
        top: 58px;
        left: -180%;
        width: 200px;
        /* height: 100px; */
        text-align: center;
        line-height: 50px;
        z-index: 10;
        background-color: rgba(214, 226, 252, 1);
        border-radius: 10px;
    }

    .userInfo{
        line-height: normal;
        text-align: left;
    }
    .userInfo .user-item{
        display: flex; 
        align-items: center;
        justify-content: space-between; 
        margin-bottom: 4px;
        padding: 6px 6px;
        margin-left: 9px;
        border-radius: 24px;
    }
    .userInfo .user-item:hover{
        background-color: rgb(212,206,255);/**#E3E5E7 */
    }

    .slide-enter-active,
    .slide-leave-active{
        transition: all 1s ease;
    }
    .slide-enter-from,
    .slide-leave-to {
        transform: translateY(20px);
        opacity: 0;
    }
    :deep(.el-select){
        width: 94px;
        border: none;
        --el-select-input-focus-border-color:none;
        --el-border-color:none; /** 去除 .el-select 的边框 */
        --el-select-border-color-hover:none;
    }
    :deep(.el-select .el-input__wrapper){
        border:none;
        border-radius: 6px;
        font-size: 16px;
        font-weight: bold;
    }
</style>
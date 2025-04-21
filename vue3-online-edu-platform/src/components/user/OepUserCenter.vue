<script setup>
    import {
        Notification,
        ChatDotRound,
        DArrowRight,
        User,
        Edit,
        Postcard,
    } from '@element-plus/icons-vue'

    import useUserStore from "../../store/store.user"

    const userStore=useUserStore()

</script>

<template>
    <div class="top-container">
        <div class="user-info-container">
            <el-image 
                style="
                    width: 100px;
                    height: 100px;
                    border-radius: 50%; 
                    display: block;
                    cursor: pointer;
                    margin-left: 68px;
                " 
                :src="userStore.userPartlyInfo.headPortrait" fit="cover" 
                :preview-src-list="[userStore.userPartlyInfo.headPortrait]"
            >
                <template #error>
                    <div class="image-slot">
                        <el-icon><icon-picture /></el-icon>
                    </div>
                </template>
            </el-image>
            <div class="user-info">
                <div style=" display: inline-block; font-weight: bold;">
                    <div style="padding: 8px 0px;font-size: 20px;">
                        <b>{{userStore.userPartlyInfo.username}}</b>
                        <el-tag style="width: 30px; height: 15px; margin-left: 4px; margin-top: 3px;" effect="dark">学生</el-tag>
                    </div>
                    <div>性别：{{userStore.userPartlyInfo.gender == true?'男':'女'}}</div>
                    <div style="margin-top: 6px;"><span>个性签名：</span><u style="color: skyblue;">{{userStore.userPartlyInfo.introduction}}</u></div>
                </div>
                <!-- <div class="operation-button">
                    <el-button type="primary" :icon="Edit" circle />
                </div> -->
            </div>
        </div>
    </div>
    <div class="main-container">
        <el-menu
            :default-active="$route.path"
            :router="true"
            :collapse="true"
            class="main-menu"
        >
            <el-menu-item index="/user/center/course">
                <el-icon><Notification /></el-icon>
                <template #title>我的课程</template>
            </el-menu-item>
            <el-menu-item index="/user/center/info">
                <el-icon><Postcard /></el-icon>
                <template #title>个人资料</template>
            </el-menu-item>
            <el-menu-item index="/user/center/account">
                <el-icon><User /></el-icon>
                <template #title>账号设置</template>
            </el-menu-item>
            <el-menu-item index="/user/center/message">
                <el-icon><ChatDotRound /></el-icon>
                <template #title>系统通知</template>
            </el-menu-item>
        </el-menu>
        <div class="show-content-area">
            <div>
                <router-view></router-view>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .top-container {
        background-image: url(../../assets/images/user-background-img.png);
        background-repeat: no-repeat;
        background-position: center center;
        background-size: cover;
        width: 100vw;    
        background-attachment: scroll;
    }
    .top-container .user-info-container{
        width: 1300px;
        height: 130px;
        margin: 0 auto;
        display: flex;
        align-items: center;
    }
    .top-container .user-info-container .user-info{
        margin-left: 18px;
        font-size: 13px;
        display: flex;
        align-items: center;
    }
    .top-container .user-info-container .user-info .operation-button{
        display: flex;
        min-width: 130px;
        max-width: 150px;
        flex-wrap: wrap;
        justify-content: left;
        align-items: flex-end;
        /* border: 1px solid black; */
        height: 100px;
        margin-left: 18px;
        
    }
    .main-container{
        width: 1242px;
        margin: 10px auto;
        /* border: 1px solid black; */
        display: flex;
    }
    .main-container .main-menu{
        background-color: rgba(0, 0, 0, 0);
        margin: 10px 0;
        margin-left: 34px;
        display: inline-block;
    }
    .main-container .show-content-area{
        width: 100%;
        min-height: 500px;
        /* border: 1px solid black; */
        margin-left: 12px;
    }

    :deep(.el-button+.el-button){
        margin-left: 0;
    }
    :deep(.el-button){
        color: black;
        background-color: rgba(0, 0, 0, 0);
        margin-top:4px ;
    }
</style>
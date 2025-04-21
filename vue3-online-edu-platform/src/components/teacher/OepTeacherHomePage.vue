<script setup>
    import {
        User,
        Edit,
    } from '@element-plus/icons-vue'

    import useUserStore from "@/store/store.user"

    const userStore = useUserStore()

</script>

<template>
    <div class="teacher-top-container">
        <div class="user-info-container">
            <el-image style="
                    width: 100px;
                    height: 100px;
                    border-radius: 50%; 
                    display: block;
                    cursor: pointer;
                    margin-left: 56px;
                "
                :src="userStore.userPartlyInfo?userStore.userPartlyInfo.headPortrait:undefined" fit="cover"
                :preview-src-list="[userStore.userPartlyInfo?userStore.userPartlyInfo.headPortrait:undefined]">
                <template #error><!--:src="userStore.userPartlyInfo.headPortrait" fit="cover"-->
                    <div class="image-slot">
                        <el-icon><icon-picture /></el-icon>
                    </div>
                </template>
            </el-image>
            <div class="user-info">
                <div style=" display: inline-block; font-weight: bold;">
                    <div style="padding: 8px 0px;font-size: 20px;">
                        <b>{{userStore.userPartlyInfo.username}}</b>
                        <el-tag style="width: 30px; height: 15px; margin-left: 4px; margin-top: 3px;" type="danger"
                            effect="dark">教师</el-tag>
                    </div>
                    <div>性别：{{userStore.userPartlyInfo.gender == true?'男':'女'}}</div>
                    <div style="margin-top: 6px;"><span>个性签名：</span><u style="color: skyblue;">{{userStore.userPartlyInfo.introduction}}</u></div>
                </div>
                <!-- <div class="operation-button">
                    <el-button type="primary" :icon="Edit" circle size="large" />
                </div> -->
            </div>
        </div>
    </div>
    <div class="teacher-main-container">
        <el-menu 
            active-text-color="skyblue" 
            background-color="rgba(214, 226, 252,1)" 
            text-color="#000" 
            router
            :default-active="$route.path"
            style="display: inline-block; width: 150px; margin-left: 75px;"
        >
            <el-sub-menu index="">
                <template #title>
                    <span>课程管理</span>
                </template>
                <!-- <el-menu-item index="/teacher/allCourse">所有课程</el-menu-item> -->
                <el-menu-item index="/teacher/allCourse">我的课程</el-menu-item>
                <!-- <el-menu-item index="/teacher/allCourse">录播课程</el-menu-item> -->
                <!-- <el-menu-item index="/teacher/draftBox">课程草稿箱</el-menu-item> -->
                <el-menu-item index="/teacher/publishCourse">发布课程</el-menu-item>
            </el-sub-menu>
            <el-menu-item index="/teacher/account">
                <span>账号管理</span>
            </el-menu-item>
            <el-menu-item index="/teacher/info">
                <span>个人信息</span>
            </el-menu-item>
        </el-menu>
        <div class="left-container">
            <router-view></router-view>
        </div>
    </div>
</template>

<style scoped>
.teacher-top-container {
    background-image: url(../../assets/images/user-background-img.png);
    background-repeat: no-repeat;
    background-position: center center;
    background-size: cover;
    width: 100vw;
    background-attachment: scroll;
    /* border: 3px solid green; */
}

.teacher-top-container .user-info-container {
    width: 1300px;
    height: 130px;
    margin: 0 auto;
    /* border: 1px solid black; */
    display: flex;
    align-items: center;
}

.teacher-top-container .user-info-container .user-info {
    margin-left: 18px;
    font-size: 13px;
    display: flex;
    align-items: center;
}

.teacher-top-container .user-info-container .user-info .operation-button {
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
</style>

<style scoped>
    .teacher-main-container {
        margin: 0 auto;
        min-width: 1300px;
        max-width: 1350px;
        /* border: 1px solid black; */
        padding: 1px 0;
        display: flex;
    }

    .teacher-main-container .left-container {
        box-shadow: var(--el-box-shadow-light);
        min-height: 500px;
        display: inline-block;
        margin-left: 4px;
        /* margin-top: 2px; */
        /* border: 1px solid black; */
        width: 100%;
        /* padding: 0 1px; */
        /* height: 300px; */
        padding: 4px 8px;
    }

</style>
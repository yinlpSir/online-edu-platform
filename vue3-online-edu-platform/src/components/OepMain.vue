<script setup>
    import { onMounted, ref } from 'vue';
    import OepCourse from './courses/OepCourse.vue';
    import { Plus } from '@element-plus/icons-vue';
    import { ElMessage } from 'element-plus';
    import useCourseStore from '../store/store.course'
    import {getAllCourse} from '../api/api.course'
    import { useRouter } from 'vue-router';

    const router = useRouter()

    const courseStore=useCourseStore()

    onMounted(()=>{
        getAllCourse({
            "currentPage":1,
            "courseType":2 // 平台推荐
        }).then(res=>{
            const {data} = res
            courseStore.recommendationCourses = data.data.records
        },err=>{

        })

        getAllCourse({
            "currentPage":1,
            "courseType":3 // 精选英语
        }).then(res=>{
            const {data} = res
            courseStore.commonCourses['精选英语'] = data.data.records
        },err=>{

        })

        // 其他课程
        getAllCourse({
            "currentPage":1,
            "courseType":4
        }).then(res=>{
            const {data} = res
            courseStore.commonCourses['其他课程'] = data.data.records
            // console.log(courseStore.commonCourses)
        },err=>{

        })

    })

    const getMore=()=>{
        console.log('get more')
        router.push('/course/search')
    }
    
</script>

<template>
    <div class="oep-main">
        <!-- <div class="main-recommendation-module">
            <h4 class="recommendation-module-title">平台推荐</h4>
            <div class="recommendation-module-body">
                <span class="alike left">&lt;</span>
                <div class="content">
                    <oep-course v-for="(course,index) in courseStore.recommendationCourses.slice(0,4)" :key="index" :course="course" />
                </div>
                <span class="alike right">&gt;</span>
            </div>
        </div> -->
        <div class="main-common-module">
            <div class="common-module-head">
                <h4 class="common-module-title" >平台推荐</h4>
                <span class="more" @click="getMore"><b>更多-></b></span>
            </div>
            <div class="common-module-body">
                <oep-course v-for="(course,index) in courseStore.recommendationCourses.slice(0,4)" :key="index" :course="course" />
            </div>
        </div>
        <div class="main-common-module" v-for="(value,key) in courseStore.commonCourses" :key="key">
            <div class="common-module-head">
                <h4 class="common-module-title" >{{key}}</h4>
                <span class="more" @click="getMore"><b>更多-></b></span>
            </div>
            <div class="common-module-body">
                <oep-course v-for="course in value.slice(0,4)" :key="course.id" :course="course" />
            </div>
        </div>
    </div>
</template>

<style scoped>
    .oep-main{
        width: 1300px;
        /* border: 1px solid black; */
        margin: 0 auto;
        margin-top: 10px;
    }

    .oep-main .main-recommendation-module{
        width: 100%;
        /* height: 100px; */
        /* border: 1px solid black; */
    }

    .oep-main .main-recommendation-module .recommendation-module-title{
        font-size: 20px;
        margin-top: 20px;
        margin-bottom: 8px;
        margin-left: 25px;
    }

    .oep-main .main-recommendation-module .recommendation-module-body{
        display: flex;
        justify-content: space-between;
        /** align-items 设置侧轴上子元素的排列方式 */
        align-items: center; 
    }

    .oep-main .main-recommendation-module .recommendation-module-body .alike{
        display: inline-block;
        height: auto;
        width: 20px;
        text-align: center;
        line-height: 80px;
        font-size: 30px;
        cursor: pointer;
    }
    .oep-main .main-recommendation-module .recommendation-module-body .alike:hover{
        color:rgb(127,166,248);
    }

    .oep-main .main-recommendation-module .recommendation-module-body .content{
        display: flex;
        width: 100%;
        /* height: 79px; */
        border: 1px solid green;
        justify-content: space-around;
    }
    
    .oep-main .main-common-module{
        /* height: 100px; */
        /* border: 1px solid black; */
    }

    .oep-main .main-common-module .common-module-head{
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .oep-main .main-common-module .common-module-head .common-module-title{
        font-size: 20px;
        margin-top: 20px;
        margin-bottom: 8px;
        margin-left: 25px;
        /* font-weight: bold; */
        display: inline-block;
    }

    .oep-main .main-common-module .common-module-head .more{
        display: inline-block;
        margin-right: 28px;
        margin-top: 20px;
        cursor: pointer;
    }
    .oep-main .main-common-module .common-module-head .more:hover{
        color:rgb(127,166,248);
    }
    
    .oep-main .main-common-module .common-module-body{
        display: flex;
        justify-content: space-around;
        /* border: 1px solid green; */
    }
    
    /** avatar */
    .avatar-uploader .avatar {
        width: 178px;
        height: 178px;
        display: block;
    }
    
</style>

<style>
    /** avatar */
    .avatar-uploader .el-upload {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
    }

    .avatar-uploader .el-upload:hover {
        border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        text-align: center;
    }
</style>
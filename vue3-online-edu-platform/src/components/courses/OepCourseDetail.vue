<script setup>
    import { Picture as IconPicture } from '@element-plus/icons-vue';
    import { onMounted, reactive, ref } from 'vue';
    import { useRoute, useRouter } from "vue-router";
    // import useCourseStore from "../../store/store.course";
    import {getGradeRange} from "../../utils/common-utils"
    import {getCourseDetail,buyCourse} from '@/api/api.course.js'

    const router=useRouter()

    const route=useRoute()

    const courseDetail = reactive({
        "id":0,
        "courseName": "",
        "courseCover": "",
        "description": "",
        "price": 0,
        "grade": "",
        "number": 0,
        "subject": "",
        "purchaseQuantity": 0,
        "isVideoDraggable": true, // 视频是否可拖拽
        "teacherInfo": {
            "id": 0,
            "realName": "",
            "headPortrait": "",
            "gender": false,
            "introduction": "",
            "isAuthenticated": 1
        },
        "classList": [
            {
                "className": "",
                "number": 0,
                "startTime": "2023-11-10T19:00:00",
                "endTime": "2023-11-10T20:00:00",
                "videoPath": "",
                "classProcess": null
            },
        ],
        "courseComment": [
            {
                "content": "",
                "grade": 1,
                "userId": null,
                "username": "",
                "headPortrait": "",
                "createTime":'',
            },
        ]
    })

    const loading = ref(false)

    onMounted(()=>{
        loading.value = true
        getCourseDetail(route.query.courseId).then(res => {
            const {data} = res
            Object.assign(courseDetail,data.data)
            loading.value = false
        },err => {
            ElMessage({
                message:err.response.data.message,
                grouping:true,
                type:'error'
            })
        })
    })

    const getGradeRangeRef= (range) => getGradeRange(range)

    const handlePurchaseCourse=(courseId)=>{
        loading.value = true
        router.push({
            path:'/course/buy',
            query:{
                courseId:courseId
            }
        })
    }
</script>

<template>
    <div class="course-detail-container" v-loading.fullscreen.lock="loading">
        <div class="course-detail-container-top">
            <div class="left-box">
                <div class="course-cover">
                    <el-image :src="courseDetail.courseCover">
                        <template #error><!-- slot = error -> Customize the content that failed to load-->
                            <div class="image-slot">
                                <el-icon><icon-picture /></el-icon>
                            </div>
                        </template>
                    </el-image>
                </div>
            </div>
            <div class="right-box"> 
                <h2 style=" margin-top: 10px;margin-bottom: 14px;">{{courseDetail.courseName.trim()}}</h2>
                <p>教师：{{courseDetail.teacherInfo.realName}}</p>
                <p>科目：{{courseDetail.subject}}</p><!--{{course.startTime?course.startTime.replace('T',' '):'录播课'}}-->
                <p>开课时间：{{courseDetail.classList[0].startTime?courseDetail.classList[0].startTime.replace("T"," "):'录播课'}}</p>
                <p>课时数量：{{courseDetail.number}}</p>
                <p>适用年级：{{getGradeRangeRef(courseDetail.grade.split(','))}}</p>
                <p>报名人数：{{courseDetail.purchaseQuantity}}</p>
                <div style="display: flex; align-items: center; margin-top: 10px;">
                    <span class="price">￥{{courseDetail.price.toFixed(2)}}</span>
                    <el-button 
                        color="rgb(214,202,250)" 
                        type="primary" 
                        style="
                            width: 120px; 
                            color: black; 
                            font-size: 18px; 
                            margin-left: 80px; " 
                        size="large" 
                        round 
                        @click="handlePurchaseCourse(courseDetail.id)"
                    >购 买</el-button>
                </div>
            </div>
        </div>
        <!-- detail comtents comment-->
        <div class="course-detail-container-bottom">
            <el-tabs class="tabs" model-value="first" style="margin: 0 30px;" >
                <el-tab-pane label="课程详情" name="first">
                    <div v-html="courseDetail.description"></div>
                </el-tab-pane>
                <el-tab-pane label="课程目录" name="second">
                    <div>
                        <div class="lesson" v-for="lesson in courseDetail.classList" :key="lesson.className">
                            <div>
                                <div class="lesson-order">{{lesson.number}}</div>
                                <span style="margin-left: 14px;">{{lesson.className}}</span>
                            </div>
                            <div style="height:100%; width: 350px;display: flex; justify-content: right;align-items: center;">
                                <!-- <span style="margin-right: 14px; font-size: 13px;color: #818a92; display: inline-block; ">{{ lesson.startTime?`开课时间:${lesson.startTime.replace("T"," ")}`:`视频时长:${lesson.lastTime}`}}</span> -->
                                <button class="lesson-btn">购买后查看</button>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
                <!--评论-->
                <el-tab-pane :label="'用户评论 ('+courseDetail.courseComment.length+')'" name="third">
                    <el-empty :image-size="200" description="空空如也" v-if="courseDetail.courseComment.length == 0"/>
                    <div class="comment" v-else v-for="com in courseDetail.courseComment" :key="com.username">
                        <div class="headPortrait">
                            <el-image 
                                style="width: 45px; height: 45px; border-radius: 50%;" 
                                :src="com.headPortrait" 
                                :fit="cover" 
                            />
                        </div>
                        <div class="comment-body">
                            <div style="font-size: 16px;">{{com.username}}</div>
                            <div style="display: flex; align-items: center;">
                                <div class="time">
                                    {{com.createTime.replace('T',' ')}} <span style="margin-left: 6px;">|</span>
                                </div>
                                <div style="margin-left: 6px;">
                                    <el-rate v-model="com.grade" disabled size="small" />
                                </div>
                            </div>
                            <div style="font-size: 16px;margin-top: 4px;">
                                {{com.content}}
                            </div>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<style scoped>
    .course-detail-container{
        max-width: 1300px;
        width: 1300px;
        margin: 20px auto;
        /* border: 1px solid black; */
        
    }
    .course-detail-container .course-detail-container-top{
        margin: 5px 0;
        /* border: 1px solid black; */
        display: flex;
        /* justify-content: center; */
    }
    .course-detail-container .course-detail-container-top .left-box{
        width: 500px;
        /* border:1px solid skyblue; */
        display: inline-block;
        border-radius: 20px;
        /* margin-left:115px ; */
        margin-left: 49px;
    } 
    .course-detail-container .course-detail-container-top .left-box .course-cover .el-image{
        border-radius: 20px;
        width: 500px;
        height: 240px;
        border: 1px solid rgb(214,202,250);
    }
    .course-detail-container .course-detail-container-top .right-box{
        /* width: 400px; */
        max-width: 620px;
        min-width: 520px;
        /* border:1px solid green; */
        display: inline-block;
        margin-left: 40px;
        /* margin: 5px 5px; */
    }
    .course-detail-container .course-detail-container-top .right-box p{
        padding: 0;
        margin: 0;
        /* margin-left: 26px; */
        font-size: 15px;
        margin-top: 6px;
    }
    .course-detail-container .course-detail-container-top .right-box .price{
        display: inline-block;
        font-size: 22px;
        /* padding: 20px 40px; */
        margin-top: 2px;
        /* margin-left: 40px;
        margin-top: 25px; */
        color: #EB512E;
    }
    .course-detail-container .course-detail-container-bottom{
        width: 1200px;
        /* height: 100px; */
        border: 1px solid #ccc;
        margin: 0 auto;
        border-radius: 20px;
        min-height: 400px;
    }
    .course-detail-container .course-detail-container-bottom .cdcb-navigation div{
        display: inline-block;
        width: 100px;
        height: 26px;
        /* border: 1px solid black; */
        font-size: 18px;
        text-align: center;
        margin-left: 18px;
        margin-top: 5px;
        cursor: pointer;
        /* border-radius: 14px; */
    }
    .course-detail-container .course-detail-container-bottom .cdcb-navigation div:hover{
        color: rgb(127,166,248);
    }
    .course-detail-container .course-detail-container-bottom .cdcb-main{
        width: 95%;
        border: 1px solid blue;
        margin: 0 auto;
    }
    .lesson{
        display: flex;
        align-items: center;
        /* width: 1100px; */
        height: 30px;
        /* border: 1px solid #ccc; */
        border-radius: 20px;
        justify-content: space-between;
        /* margin-right: 20px; */
        margin-bottom: 10px;
    }
    .lesson:hover{
        background-color: rgba(214,202,250,0.3);
        cursor: default;
    }
    .lesson:hover .lesson-btn{
        color: skyblue;
        border: 1px solid skyblue;
    }
    .lesson .lesson-order{
        width: 25px;
        height: 25px; 
        border: 1px solid skyblue; 
        border-radius: 50%; 
        /* margin-left: 30px; */
        text-align: center;
        line-height: 25px;
        display: inline-block;
    }
    .lesson .lesson-btn{
        border: 0px;
        background-color: rgba(0, 0, 0, 0); /** 背景透明 */
        margin-right: 30px;
        border-radius: 8px;
        cursor: pointer;
        padding: 1px 4px;
    }

    .comment{
        display: flex;
        margin-bottom: 20px;
    }
    .comment .headPortrait{
        width: 45px;
        height: 45px;
        /* border: 1px solid skyblue; */
        margin-right: 20px;
    }
    .comment .comment-body{
        width: 100%;
        border-bottom: 1px solid #edeff2;
        padding-bottom: 15px;
        /* border: 1px solid skyblue; */
    }
    .comment .comment-body .time{
        font-size: 12px;
        color: #586470;
        
    }
</style>
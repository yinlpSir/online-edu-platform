<script setup>
    import { onMounted, ref ,reactive, toRaw} from 'vue';
    // import useCourseStore from "@/store/store.course";
    import useVideoStore from "@/store/store.video";
    import {getGradeRange} from "@/utils/common-utils";
    import {getCourseDetail} from '@/api/api.course.js'
    import {publishComment} from '@/api/api.comment.js'
    import { useRoute, useRouter } from 'vue-router';

    const router = useRouter()
    const route = useRoute()

    const videoStore = useVideoStore()

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
        ],
        "coursePlastic": [
            {
                "plasticName": "",
                "plasticPath": "",
                "plasticSize": 0
            },
        ],
    })

    const loading = ref(false)

    const getCourseData = ()=>{
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
    }

    onMounted(()=>{
        getCourseData()
    })

    const getGradeRangeRef = (range) => getGradeRange(range)

    const getHour = (startTime,endTime)=>{
        const st = new Date(startTime);
        const et = new Date(endTime);

        const timeDiff = Math.abs(et - st); // 获取时间差的毫秒数
        const hours = Math.floor(timeDiff / (1000 * 60 * 60)); // 将毫秒数转换为小时数
        return hours;
    }

    const handleFileDownload = (url,fileName) => {
        window.open(url)
    }

    const handleWatchVideo = ()=>{
        videoStore.setVideo(courseDetail.classList)
        router.push('/course/video')
    }

    /**
     * 观看直播
     * @param {*} url 
     */
    const watchLive = (url)=>{
        window.open(url)
    }

    //#region el-tabs
    const editableTabsValue = ref('1')
    //#endregion

    // #region 评论
    const comment = reactive({
        content:"",
        grade:0,
        courseId:''
    })
    
    const handlePublishComment = () => {
        comment.courseId = route.query.courseId
        publishComment(toRaw(comment)).then(
            response => {
                const {data} = response
                getCourseData()
                ElMessage.success("评论成功!")
                comment.content = ""
                comment.grade = 0
                comment.courseId = ""
            },
            error => {
                ElMessage({
                    message:error.response.data.message,
                    grouping:true,
                    type:'error'
                })
            }
        )
    }
    // #endregion

</script>

<template>
    <div class="course-purchased-container" v-loading.fullscreen.lock="loading">
        <div style="width: 1300px; min-width: 1300px; margin: 0 auto; display: flex; justify-content: center;">
            <div class="course-purchased-left-container">
                <div class="course-cover">
                    <el-image :src="courseDetail.courseCover">
                        <template #error><!--slot = error -> Customize the content that failed to load-->
                            <div class="image-slot">
                                <el-icon><icon-picture /></el-icon>
                            </div>
                        </template>
                    </el-image>
                </div>
                <div class="course-partly-info">
                    <h3 style="margin-left: 6px;">{{ courseDetail.courseName }}</h3>
                    <p>教师：{{ courseDetail.teacherInfo.realName }}</p>
                    <p>科目：{{ courseDetail.subject }}</p>
                    <p>课时数：{{ courseDetail.number }}</p>
                    <p>适用年级：{{ getGradeRangeRef(courseDetail.grade.split(",")) }}</p>
                    <!-- <el-button style="margin-left: 6px;margin-top: 4px;" type="primary" size="small">返回课程详情页</el-button> -->
                </div>
            </div>
            <div class="course-purchased-right-container">
                <el-tabs class="tabs" v-model="editableTabsValue" style="margin: 0 10px;">

                    <el-tab-pane v-if="courseDetail.classList[0].videoPath == null" label="直播目录" name="1" >
                        <el-empty :image-size="200" description="空空如也" v-if="courseDetail.classList.length == 0"/>
                        <div v-else>
                            <div class="lesson" v-for="lesson in courseDetail.classList" :key="lesson.id">
                                <div>
                                    <div class="lesson-order">{{lesson.number}}</div>
                                    <span style="margin-left: 14px;">{{lesson.className}}</span>
                                </div>
                                <div style="height:100%; width: 300px;display: flex; justify-content: space-between;align-items: center;">
                                    <span style=" font-size: 13px;color: #818a92; display: inline-block; ">{{ lesson.startTime.replace('T',' ') }}</span>
                                    <span style=" font-size: 13px;color: #818a92; display: inline-block; ">{{ lesson.endTime.replace('T',' ') }}</span>
                                    <el-button link type="primary" disabled v-if="lesson.liveInfo.liveStatus==1">直播未开始</el-button>
                                    <el-button link type="primary" v-else-if="lesson.liveInfo.liveStatus==2" @click="watchLive(lesson.liveInfo.watchUrl)" style="color:red;">正在直播</el-button>
                                    <el-button link type="primary" v-else-if="lesson.liveInfo.playbackUrl != null && lesson.liveInfo.playbackUrl != '' " @click="watchLive(lesson.liveInfo.playbackUrl)">课程回放</el-button>
                                    <el-button link type="primary" v-else disabled>生成回放中</el-button>
                                </div>
                            </div>
                        </div>
                    </el-tab-pane>

                    <el-tab-pane v-else label="视频目录" name="1">
                        <el-empty :image-size="200" description="空空如也" v-if="courseDetail.classList.length == 0"/>
                        <div v-else>
                            <div class="lesson" v-for="lesson in courseDetail.classList" :key="lesson.id">
                                <div>
                                    <div class="lesson-order">{{lesson.number}}</div>
                                    <span style="margin-left: 14px;">{{lesson.className}}</span>
                                </div>
                                <div style="height:100%; width: 300px;display: flex; justify-content: space-between;align-items: center;">
                                    <span 
                                        style=" font-size: 13px;color: #818a92; display: inline-block; "
                                    >
                                        <!--视频时长：{{getHour(lesson.startTime.replace('T',' '),lesson.endTime.replace('T',' '))}}小时-->
                                        视频时长：{{lesson.lastTime}}
                                    </span>
                                    <el-button type="primary" link @click="handleWatchVideo">点击观看</el-button>
                                </div>
                            </div>
                        </div>
                    </el-tab-pane>

                    <el-tab-pane label="资料下载" name="3">
                        <el-empty :image-size="200" description="空空如也" v-if="courseDetail.coursePlastic.length == 0"/>
                        <div class="lesson" v-else v-for="plastic in courseDetail.coursePlastic" :key="plastic.plasticName">
                                <div>
                                    <span style="margin-left: 14px;">
                                        {{plastic.plasticName+plastic.plasticPath.substring(plastic.plasticPath.lastIndexOf('.'))}}</span>
                                </div>
                                <div style="height:100%; width: 300px;display: flex; justify-content: space-between;align-items: center;">
                                    <span 
                                        style=" font-size: 13px;color: #818a92; display: inline-block; "
                                    >
                                        大小：{{(plastic.plasticSize/(1024*1024)).toFixed(2)}}MB
                                    </span>
                                    <el-button type="primary" link @click="handleFileDownload(plastic.plasticPath,plastic.plasticName)" >在线查看</el-button>
                                </div>
                            </div>
                    </el-tab-pane>

                    <el-tab-pane label="评论列表" name="4">
                        <div style="margin-bottom: 20px;">
                            <div style="display: flex; align-items: flex-end;">
                                <div style="width: 82%;">
                                    <div style="display: flex; align-items: center;">
                                        <span>评分：</span>
                                        <el-rate v-model="comment.grade" clearable />
                                    </div>
                                    <div style="display: flex; align-items: center;">
                                        <span style="width: 10%;">评论：</span>
                                        <el-input
                                            v-model="comment.content"
                                            :rows="2"
                                            type="textarea"
                                            placeholder="分享一下你的感受吧~"
                                            maxlength="60"
                                            show-word-limit
                                        />
                                    </div>
                                </div>
                                <el-button type="primary" style="margin-left: 10px;" @click="handlePublishComment">发布评论</el-button>  
                            </div>
                        </div>
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
    </div>
</template>

<style scoped>
    .course-purchased-container{
        min-width: 1300px;
        margin: 10px auto;
        /* border: 1px solid black; */
    }
    .course-purchased-container .course-purchased-left-container{
        /* margin: 0 auto; */
        width: 400px;
        min-height: 580px;
        padding: 2px;
        margin-right: 15px;
        /* border: 1px solid black; */
    }
    .course-purchased-container .course-purchased-left-container .course-cover .el-image{
        border-radius: 10px;
        width: 400px;
        height: 190px;
        
        border: 1px solid rgb(214,202,250);
    }
    .course-purchased-container .course-purchased-left-container .course-partly-info{
        margin-top: 8px;
    }
    .course-purchased-container .course-purchased-left-container .course-partly-info p{
        margin: 2px 0px;
        margin-left: 6px;
        font-size: 14px;
        
    }
    .course-purchased-container .course-purchased-right-container{
        /* margin: 0 auto; */
        width: 700px;
        margin-left: 15px;
        /* border: 1px solid green; */
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
    /* .lesson:hover{
        background-color: rgba(214,202,250,0.3);
        cursor: default;
    } */
    /* .lesson:hover .lesson-btn{
        color: skyblue;
        border: 1px solid skyblue;
    } */
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
<script setup>
    import { reactive, ref, toRaw } from "vue"
    import { useRouter,useRoute} from "vue-router"
    import {addCourse,addClass} from '@/api/api.course'
    import useCourseStore from "@/store/store.course"
    import { ElLoading } from 'element-plus'

    const courseStore = useCourseStore()

    const router = useRouter()

    const loading = ref(false)

    const {courseBasicInfo,lessons,courseForm} = courseStore
    
    const step = ref(0)

    const nextStep = () => {
        ++step.value
        if(step.value > 2) step.value = 2
        if(step.value === 1) router.push("description")
        else if(step.value === 2) router.push("outline")
    }

    const lastStep = () => {
        --step.value
        if(step.value < 0) step.value = 0
        if(step.value === 0) router.push("basicInfo")
        else if(step.value === 1) router.push("description")
    }

    const submitCourse = () => {

        loading.value = true

        let courseInfo = {
            ...toRaw(courseBasicInfo),
        }

        let formData = new FormData()
        Object.keys(courseInfo).forEach(key => {
            formData.append(key,courseInfo[key])
        })
        
        addCourseFun(formData) // 添加课程
        
    }

    const addCourseFun = async (data)=>{
        await addCourse(data).then(response => {
            const loadingInstance = ElLoading.service({ fullscreen: true })

            const {data} = response
            let courseId = data.data.courseId

            addLessons(courseId)

            loadingInstance.close()

            ElMessage({
                message:data.message,
                grouping:true,
                type:'success'
            })

            courseStore.clearAddCourseInfo()

            router.push('/teacher/publishCourse')
            
        },error =>{
            // console.log(error)
            ElMessage({
                message:error.response.data.message,
                grouping:true,
                type:'error'
            })
        })
    }

    const addLessons = async (courseId)=>{
        const judgeDigit=(num)=>{
            let count=1;
            while(Math.floor(num/10)){
                count++;
                num=Math.floor(num/10);
            }
            return count;
        }
            await toRaw(lessons).forEach(lesson => {
                delete lesson.isShow
                if(lesson.video == null){
                    delete lesson.video
                }

                lesson['courseId'] = courseId

                if(courseForm != 'record'){ // 添加录播课
                    // 开始时间转换
                    let startTime=new Date(lesson.startTime)
                    let strStartTime=`${startTime.getFullYear()}-${judgeDigit(startTime.getMonth()+1)===1?'0'+(startTime.getMonth()+1):startTime.getMonth()+1}-${judgeDigit(startTime.getDate())===1?'0'+startTime.getDate():startTime.getDate()} ${startTime.toLocaleTimeString()}`
                    lesson.startTime = strStartTime

                    // 结束时间转换
                    let endTime=new Date(lesson.endTime)
                    let strEndTime=`${endTime.getFullYear()}-${judgeDigit(endTime.getMonth()+1)===1?'0'+(endTime.getMonth()+1):endTime.getMonth()+1}-${judgeDigit(endTime.getDate())===1?'0'+endTime.getDate():endTime.getDate()} ${endTime.toLocaleTimeString()}` //${endTime.getHours()}-${endTime.getMinutes()}-${endTime.getSeconds()}
                    lesson.endTime = strEndTime
                }else {
                    delete lesson.startTime
                    delete lesson.endTime
                }

                let fd = new FormData()
                Object.keys(lesson).forEach(key => {
                    fd.append(key,lesson[key])
                })

                addClass(fd).then(res =>{
                    const {data} = response
                    // console.log(data)
                },err => {
                    console.log(err)
                })
            })
        loading.value = false
    }

</script>

<template>
    <div v-loading.fullscreen.lock="loading">
        <el-steps 
            :active="step" 
            finish-status="success" 
            simple 
            class="step"
        >
            <el-step title="基本信息" />
            <el-step title="课程介绍" />
            <el-step title="课程大纲" />
        </el-steps>
        
        <div style="margin-top: 30px;">
            <router-view></router-view>
        </div>
        <div class="operation">
            <el-button type="primary" @click="lastStep" v-show="step">上一步</el-button>
            <el-button type="primary" @click="nextStep" v-show="step !== 2" style="width: 120px;">下一步</el-button>
            <el-button type="primary" v-show="step === 2" style="width: 120px;" @click="submitCourse">提交</el-button>
        </div>
    </div>
</template>

<style scoped>
    .step{
        height: 12px;
        margin: 0 10px;
        margin-top: 10px;
    }
    .operation{
        margin-top: 40px;
        margin-bottom: 10px;
        margin-right: 20px;
        display: flex; 
        justify-content: right;
    }
    
</style>


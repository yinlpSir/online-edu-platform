<script setup>
    import { ref } from "vue"
    import { useRouter } from "vue-router"
    import useCourseStore from "../../../store/store.course"

    const courseStore = useCourseStore()

    const router = useRouter()

    const changeCourseForm = (val) => courseStore.courseForm = val

    const handleCourseFormNext = () => {
        if(courseStore.courseForm) router.push({
            path:"publishCourse/addCourse",
        })
    }
</script>

<template>
    <div>
        <div class="course-form-container">
            <div 
                class="form" 
                :style="{border:courseStore.courseForm === 'live'?'1px solid skyblue':''}" 
                @click="changeCourseForm('live')"
            >
                <img style="margin-top:34px;" src="../../../assets/images/course.form/live-img.png" alt="live image">
                <p>直播课程</p>
            </div>
            <div style="width: 60px;"></div>
            <div 
                class="form" 
                :style="{border:courseStore.courseForm === 'record'?'1px solid skyblue':''}" 
                @click="changeCourseForm('record')"
            >
                <img style="margin-top:34px;" src="../../../assets/images/course.form/record-img.png" alt="record image">
                <p>录播课程</p>
            </div>
        </div>
        <div style="display: flex; justify-content: center; margin-top: 50px;">
            <el-button style="width: 160px;" type="primary" @click="handleCourseFormNext">下一步</el-button>
        </div>
    </div>
</template>

<style scoped>
    .course-form-container{
        /* border: 1px solid black; */
        display: flex;
        justify-content: center;
        margin-top: 60px;
    }
    .course-form-container .form{  
        text-align: center;
        width: 160px;
        height: 196px;
        /* border: 1px solid black; */
        box-shadow: var(--el-box-shadow-light);
        border-radius: 8px;
        cursor: pointer;
    }
</style>
<script setup>
    import { onBeforeMount, onMounted, ref } from "vue";
    import OepCourse from "@/components/courses/OepCourse.vue"
    import {getAllBoughtCourse} from "@/api/api.course.js"
    
    const userCourses = ref([])

    /**
     * | emsp;&emsp;currentPage  |      |      | true  | integer(int64) |      |
        | ----------------------- | ---- | ---- | ----- | -------------- | ---- |
        | &emsp;&emsp;pageSize    |      |      | false | integer(int64) |      |
        | &emsp;&emsp;courseName  |      |      | false | string         |      |
        | &emsp;&emsp;subject     |      |      | false | string         |      |
        | &emsp;&emsp;grade       |      |      | false | string         |      |
        | &emsp;&emsp;teacherName |      |      | false | string         |      |
        | &emsp;&emsp;courseType  |      |      | false | integer(int32) |      |
        | &emsp;&emsp;isPassed    |      |      |       |                |      |
     */

    const currentPage = ref(1)

    const pageSize = ref(8)

    const total=ref() // 总条目数

    const sendAjax = (params) => {
        getAllBoughtCourse(params).then(response => {
            const {data} = response
            console.log(data)
            userCourses.value = data.data.records
        },error => console.log(error) )
    }

    onBeforeMount(()=>{
        sendAjax({
            currentPage:currentPage.value,
            pageSize:pageSize.value
        })
    })

    /**
     * 分页
     */
    const handleSizeChange = (val) => {
        // page-size 改变时触发
        console.log(`${val} items per page`)
        
        // {
        //     currentPage:currentPage.value,
        //     pageSize:pageSize.value
        // }
    }

    const handleCurrentChange = (val) => {
        // current-page 改变时触发
        console.log(`current page: ${val}`)

    }

</script>

<template>
    <div class="user-course-container">
        <div class="no-data">
            <el-empty v-show="!userCourses.length" :image-size="260" description="空空如也！快去主页看看吧！" style=" font-weight: bold;" />
        </div>
        <div class="course-display-container">
            <div class="course-container">
                <oep-course 
                    style="margin: 8px 14px;"
                    v-for="course in userCourses" 
                    :key="course.id" 
                    :course="course" >
                </oep-course>
            </div>
            <div class="pagination">
                <el-pagination
                    v-model:current-page="currentPage"
                    :page-size="pageSize"
                    :page-sizes="[12,16]"
                    :default-page-size="pageSize"
                    :background="true"
                    :hide-on-single-page="true"
                    layout="prev, pager, next, total , sizes"
                    :total="total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </div>
    </div>
</template>

<style scoped>

    .user-course-container {
        
    }
    .user-course-container .no-data {
        display: flex;
        justify-content: center;
        align-items:center;
    }   
    .user-course-container .course-display-container .course-container{
        width: 100%;
        /* display: flex; */
        /* flex-wrap: wrap; */
        /* justify-content: space-around; */
        
    }
    .user-course-container .course-display-container .pagination{
        display: flex;
        justify-content: right;
        margin-top: 18px;
        margin-bottom: 10px;
        margin-right: 2px;
    }

</style>
<script setup>
    import { ref ,onMounted,getCurrentInstance, onBeforeUnmount, toRaw} from "vue"
    import useUserStore from "@/store/store.user"
    import {getAllGrade,getAllSubject,searchCourse} from "@/utils/common-utils"
    import OepCourse from "../courses/OepCourse.vue"
    import {getAllCourse} from '@/api/api.course.js'

    const userStore = useUserStore()
    
    onMounted(()=>{
        searchCourse(toRaw(userStore.searchCondition))
        // sessionStorage.setItem('searchCondition',JSON.stringify(userStore.searchCondition))
    })

    /**
     * 筛选
     */
    const selectChange = () => {
        console.log(' select change ')
        searchCourse(toRaw(userStore.searchCondition))
        // sessionStorage.setItem('searchCondition',JSON.stringify(userStore.searchCondition))
    }
    
    // @clear 会引起 @change 事件的调用。先调用 @change 再调用 @clear
    const selectClear = () => {
        console.log(' select clear ')
    }

    /**
     * 分页
     */
    const handleSizeChange = (val) => {
        // page-size 改变时触发
        console.log(`${val} items per page`)
        userStore.searchCondition.currentPage = 1
        userStore.searchCondition.pageSize = val
        searchCourse(toRaw(userStore.searchCondition))
        // sessionStorage.setItem('searchCondition',JSON.stringify(userStore.searchCondition))
    }

    const handleCurrentChange = (val) => {
        // current-page 改变时触发
        console.log(`current page: ${val}`)
        userStore.searchCondition.currentPage = val
        searchCourse(toRaw(userStore.searchCondition))
        // sessionStorage.setItem('searchCondition',JSON.stringify(userStore.searchCondition))
    }

</script>

<template>
    <div class="search-content-container">
        <div class="filter-box">
            <div>
                <span>年级筛选: </span>
                <el-select v-model="userStore.searchCondition.grade" class="grade" clearable placeholder="请选择" @change="selectChange" @clear="selectClear">
                    <el-option-group v-for="group in getAllGrade()" :key="group.label" :label="group.label">
                        <el-option v-for=" (item) in group.options " :key="item" :label="item" :value="item" />
                    </el-option-group>
                </el-select>
            </div>
            <div style="margin-left: 16px;">
                <span>科目筛选: </span>
                <el-select v-model="userStore.searchCondition.subject" class="subject" clearable placeholder="请选择" @change="selectChange" @clear="selectClear">
                    <el-option v-for="item in getAllSubject()" :key="item" :label="item" :value="item" />
                </el-select>
            </div>
        </div>
        <div class="course-display-box">
            <div class="no-data" v-show="userStore.searchCondition.total == 0">
                <el-empty :image-size="280" description="无数据" style=" font-weight: bold;" />
            </div>
            <div class="course-display" v-show="userStore.searchCondition.total !== 0">
                <oep-course class="course-card" v-for="course in userStore.searchResult" :key="course.id" :course="course" ></oep-course>
            </div>
            <!-- 
                total 总条目数
                current-page / v-model:current-page 当前页数
                page-size 每一页显示的条目个数
                hide-on-single-page  只有一页时是否隐 。默认值为 false
            -->
            <div style="width: 100%; display: flex; justify-content: center; margin: 10px 0px;">
                <el-pagination
                    v-model:current-page="userStore.searchCondition.currentPage"
                    :page-size="8"
                    :page-sizes="[8,12,16]"
                    :default-page-size="8"
                    :background="true"
                    :hide-on-single-page="false"
                    layout="prev, pager, next , sizes"
                    :page-count="userStore.pages"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </div>
    </div>
</template>

<style scoped>
    .search-content-container{
        min-width: 1250px;
        max-width: 75vw;
        /* border: 1px solid black; */
        margin: 0 auto;
    }
    .search-content-container .filter-box{
        /* border: 1px solid red; */
        min-width: 1150px;
        max-width: 80%;
        margin: 0 auto;
        margin-top: 18px;
        display: flex;
        justify-content: left;
        
    }
    .grade{
        width: 100px;
    }
    .subject{
        width: 90px;
    }
    :deep(.el-select .el-input__wrapper){
        border-radius: 20px;
    }
    .search-content-container .course-display-box{
        min-width: 1250px;
        max-width: 1250px;
        min-height: 500px;
        /* border: 1px dashed skyblue; */
        border-radius: 20px;
        margin: 0 auto;
        margin-top: 14px;
        /* display: flex; */
        /* flex-wrap: wrap; */
        /* justify-content: space-between;  */
        /* justify-content: center; */
        /* align-items: center; */
    }
    .search-content-container .course-display-box .no-data{
        min-height: 500px;
        display: flex;
        justify-content: center;
        align-items:center;
    }
    .search-content-container .course-display-box .course-display{
        width: 100%;
        min-height: 500px;
        display: flex;
        /* justify-content: space-between; */
        flex-wrap: wrap;
        margin: 6px 0px;
    }
    .course-card {
        margin: 0 20px;
    }
</style>
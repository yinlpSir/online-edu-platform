<script setup>
import { onMounted, ref, toRaw } from 'vue'
import {getAllCourse,courseAuthentication} from '@/api/api.course.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const table = ref({
    total: 0, // 总共多少条数据
    query: { // 查询参数
        pageSize: 10, // 每页显示多少条数据
        currentPage: 1, // 当前页
    },
    data: []
})

// #region 分页相关函数
const handleChangePage = (value) => {
    table.value.query.currentPage = value
    handleTableData()
}
const handleChangePageSize = (value) => {
    table.value.query.pageSize = value
    handleTableData()
}
// #endregion

// 获得数据
const handleTableData = () => {
    getAllCourse(toRaw(table.value.query)).then((response) => {
        const {data} = response
        table.value.data = data.records
        table.value.total = data.total
    },error => ElMessage.error(error.response.data.msg))
}

// #region 课程介绍 相关dialog 操作
const courseDescriptionDialogVisible = ref(false)

const courseDescription = ref(null)

const handleShowCourseDescription = (description)=>{
    courseDescriptionDialogVisible.value = true
    courseDescription.value = description
}
// #endregion

// #region 审核课程相关
const checkDialogVisible = ref(false)

const isChecked = ref(0)

const courseId = ref(null)

const handleCourseAuthentication = (id)=>{
    checkDialogVisible.value=true
    courseId.value = id
}

const handleSubmitCheck = () => {
    // 提交数据
    if(isChecked.value == 0) {
        ElMessage.warning({
            message:'请选择审核结果！',
            grouping:true,
        })
        return ;
    }
    // 审核处理
    let authenticationValue = isChecked.value == 1 ? true:false;
    // 认证
    courseAuthentication(courseId.value,authenticationValue).then(response => {
        ElMessage.success('操作成功！')
        handleTableData()
        handleCheckDialogClose()
    },error => {
        console.log(error)
    })
}

const handleCheckDialogClose = () => {
    // 关闭前的回调
    isChecked.value = 0
    checkDialogVisible.value = false
}
// #endregion

// #region 观看课程视频 相关dialog操作

const videoDialogVisible = ref(false)

const currentVideoPath = ref(null)

const handleShowVideo = (videoPath)=>{
    videoDialogVisible.value = true
    
    if(videoPath == null) return ;

    currentVideoPath.value = videoPath
}

const handleVideoClose = ()=> { 
    currentVideoPath.value = null
}

// #endregion

/**
 * 查看资料
 */
const handleShowPlastic = (plasticPath) => {
    window.open(plasticPath)
}

handleTableData()

// const 

</script>

<template>

    <el-main class="ea-table">
        <el-table :data="table.data" style=" width: 100%" empty-text="暂无课程">
            <el-table-column type="expand">
                <template #default="props">
                    <div m="4" style="margin-left: 20px; padding-right: 40px;">
                        <h3>课时列表:</h3>
                        <el-table :data="props.row.classList" :border="false" style="margin-bottom: 20px; margin-left: 10px;" empty-text="暂无课程">
                            <el-table-column label="课时名" prop="className" />
                            <el-table-column label="课时数" prop="number" />
                            <el-table-column label="开始时间" prop="startTime">
                                <template #default="scope">
                                        <span>{{scope.row.startTime?scope.row.startTime.replace('T',' '):' '}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column label="结束时间" prop="endTime">
                                <template #default="scope">
                                        <span>{{scope.row.endTime?scope.row.endTime.replace('T',' '):' '}}</span>
                                </template>
                            </el-table-column>
                            <el-table-column label="视频时长" prop="lastTime" />
                            <el-table-column fixed="right" label="操作" width="100">
                                <template #default="scope">
                                    <el-button link type="primary" @click="handleShowVideo(scope.row.videoPath)">
                                        查看内容
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>

                        <h3>课程资料列表:</h3>
                        <el-table :data="props.row.classPlastic" :border="false" style="margin-bottom: 20px; margin-left: 10px;" empty-text="暂无资料">
                            <el-table-column label="资料名" prop="plasticName" />
                            <el-table-column label="大小" prop="plasticSize">
                                <template #default="scope">
                                        <span>{{((scope.row.plasticSize)/(1024*1024)).toFixed(2)}}MB</span>
                                </template>
                            </el-table-column>
                            <el-table-column fixed="right" label="操作" width="100">
                                <template #default="scope">
                                    <el-button link type="primary" @click="handleShowPlastic(scope.row.plasticPath)">
                                        查看资料
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="封面" width="220">
                <template #default="scope">
                    <div style="display: flex; align-items: center">
                        <el-image style="width: 220px; height: 120px" :src="scope.row.courseCover" :fit="fit" />
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程名" />
            <el-table-column prop="teacherName" label="所属教师" />
            <el-table-column prop="subject" label="科目" />
            <el-table-column prop="number" label="课时数" />
            <el-table-column label="价格">
                <template #default="scope">
                        <span>{{scope.row.price.toFixed(2)}}</span>
                </template>
            </el-table-column>
            <el-table-column label="审核状态">
                <template #default="scope">
                    <el-tag v-if="scope.row.isPassed == '1'" type="success">已审核</el-tag>
                    <el-tag v-else-if="scope.row.isPassed == '0'" type="warning">待审核</el-tag>
                    <el-tag v-else type="danger">未通过审核</el-tag>
                </template>
            </el-table-column>
            
            <el-table-column fixed="right" label="操作" width="120">
                <template #default="scope">
                    <el-button link type="primary" @click="handleShowCourseDescription(scope.row.description)">
                        查看课程介绍
                    </el-button>
                    <el-button link type="warning" v-show="scope.row.isPassed == '0'" @click="handleCourseAuthentication(scope.row.id)">
                        审核课程
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <br>
        <el-pagination background layout="total, sizes, prev, pager, next" :total="table.total" :page-sizes="[10, 15, 20]"
            :default-page-size="table.query.pageSize" @current-change="handleChangePage" @size-change="handleChangePageSize" />
    </el-main>
    
    <!--课程介绍 dialog-->
    <el-dialog v-model="courseDescriptionDialogVisible" width="75%" title="课程介绍" center >
        <div style=" text-align: center;">
            <span v-html="courseDescription"></span>
        </div>
    </el-dialog>

    <!--观看课程内容的 dialog-->
    <el-dialog v-model="videoDialogVisible" title="课程内容" width="50%" center @close="handleVideoClose">
        <video 
            v-if="currentVideoPath != null" 
            :src="currentVideoPath" 
            controls
            height="400"
            style="width: 100%;"
        ></video>
        <el-empty v-else description="暂无内容" />
    </el-dialog>

    <!--审核课程 dialog-->
    <el-dialog v-model="checkDialogVisible" title="课程审核" :close-on-click-modal="false" destroy-on-close
        close-on-press-escape center width="500px" @close="handleCheckDialogClose">
        <div style="display:flex; justify-content:center;">
            <el-radio-group v-model="isChecked">
                <el-radio :label="1" size="default"><b>通过审核</b></el-radio>
                <el-radio :label="-1" size="default"><b>审核失败</b></el-radio>
            </el-radio-group>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="checkDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmitCheck">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>



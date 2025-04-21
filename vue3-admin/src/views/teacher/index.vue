<script setup>
import { ref, toRaw } from 'vue'
import {getTeacher,delUser,teacherAuthentication} from '@/api/api.user.js'
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
    getTeacher(toRaw(table.value.query)).then((response) => {
        const {data} = response
        table.value.data = data.records
        table.value.total = data.total
    },error => ElMessage.error(error.response.data.msg))
}

// #region 教师资格证查看
const certificateDialogVisible = ref(false)

const teacherCertificate = ref(null)

const handleShowCertificate = (teacherCertificateImg)=>{
    certificateDialogVisible.value = true
    teacherCertificate.value = teacherCertificateImg
}
// #endregion

// #region 认证相关
const authenticationDialogVisible = ref(false)

const isAuthenticated = ref(0)

const teacherId = ref(null)

const handleTeacherAuthentication =(id)=>{
    authenticationDialogVisible.value=true
    teacherId.value = id
}

const handleSubmitAuthentication = () => {
    // 提交数据
    if(isAuthenticated.value == 0) {
        ElMessage.warning({
            message:'请选择认证成功或失败!',
            grouping:true,
        })
        return ;
    }
    let authenticationValue = isAuthenticated.value == 1 ? true:false;
    teacherAuthentication(teacherId.value,authenticationValue).then(response => {
        ElMessage.success('操作成功！')
        handleTableData()
        handleAuthenticationDialogClose()
    },error => {
        console.log(error)
    })
}

const handleAuthenticationDialogClose = () => {
    // 关闭前的回调
    authenticationDialogVisible.value = false
    isAuthenticated.value = 0
}
// #endregion

const handleDialogDelete = (teacherId) => {
    ElMessageBox.confirm('请仔细确认是否删除?', '警告', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        delUser(teacherId).then((response) => {
            ElMessage.warning(response.message)
            handleTableData()
        },error => {
            ElMessage.error(error.response.data.msg)
        })
    })
}

handleTableData()

</script>

<template>

    <el-main class="ea-table">
        <el-table :data="table.data" style=" width: 100%">
            <el-table-column label="头像" width="100">
                <template #default="scope">
                    <div style="display: flex; align-items: center">
                        <el-avatar
                            :src="scope.row.headPortrait"
                            :size="70"
                        />
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="realName" label="真实姓名" />
            <el-table-column prop="phoneNumber" label="手机号" />
            <el-table-column label="性别">
                <template #default="scope">
                    <span>{{scope.row.gender ? '男':'女'}}</span>
                </template>
            </el-table-column>
            <el-table-column label="是否认证">
                <template #default="scope">
                    <el-tag v-if="scope.row.isAuthenticated == '1'" type="success">已认证</el-tag>
                    <el-tag v-else-if="scope.row.isAuthenticated == '0'" type="warning">待认证</el-tag>
                    <el-tag v-else type="danger">认证失败</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="introduction" label="个性签名" />
            <el-table-column fixed="right" label="操作" width="120">
                <template #default="scope">
                    <el-button link type="primary" @click="handleShowCertificate(scope.row.teacherCertificateImg)">
                        查看资格
                    </el-button>
                    <el-button link type="warning" v-show="scope.row.isAuthenticated == '0'" @click="handleTeacherAuthentication(scope.row.id)">
                        点击认证
                    </el-button>
                    <el-button link type="danger" @click="handleDialogDelete(scope.row.id)">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <br>
        <el-pagination background layout="total, sizes, prev, pager, next" :total="table.total" :page-sizes="[10, 15, 20]"
            :default-page-size="table.query.pageSize" @current-change="handleChangePage" @size-change="handleChangePageSize" />
    </el-main>
    
    <!--教师资格证查看 dialog-->
    <el-dialog v-model="certificateDialogVisible" title="教师资格证明" width="30%" center>
        <el-image style="width: 100%; height: 100%" :src="teacherCertificate" :fit="fit" />
    </el-dialog>

    <!--认证 dialog-->
    <el-dialog v-model="authenticationDialogVisible" title="教师认证" :close-on-click-modal="false" destroy-on-close
        close-on-press-escape center width="500px" @close="handleAuthenticationDialogClose">
        <div style="display:flex; justify-content:center;">
            <el-radio-group v-model="isAuthenticated">
                <el-radio :label="1" size="default"><b>认证成功</b></el-radio>
                <el-radio :label="-1" size="default"><b>认证失败</b></el-radio>
            </el-radio-group>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="authenticationDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmitAuthentication">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>



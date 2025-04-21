<script setup>
import { ref, toRaw } from 'vue'
import {getStudent,delUser} from '@/api/api.user.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import {conversionGrade} from '@/utils/common.util'

const table = ref({
    total: 0, // 总共多少条数据
    query: { // 查询参数
        pageSize: 10, // 每页显示多少条数据
        currentPage: 1, // 当前页
        phoneNumber: ""
    },
    data: []
})
const handleQueryRefresh = () => {
    table.value.query.phoneNumber = ""
    handleTableData()
}
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
    getStudent(toRaw(table.value.query)).then((response) => {
        const {data} = response
        table.value.data = data.records
        table.value.total = data.total
    },error => ElMessage.error(error.response.data.msg))
}

const handleDialogDelete = (studentId) => {
    ElMessageBox.confirm('请仔细确认是否删除?', '警告', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        delUser(studentId).then((response) => {
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
    <el-form :inline="true">
        <el-form-item label="">
            <el-input v-model="table.query.phoneNumber" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="search" @click="handleTableData">
                查询
            </el-button>
            <el-button type="info" icon="Refresh" @click="handleQueryRefresh">
                重置
            </el-button>
        </el-form-item>
    </el-form>

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
            <el-table-column prop="phoneNumber" label="手机号" />
            <el-table-column label="性别">
                <template #default="scope">
                    <span>{{scope.row.gender ? '男':'女'}}</span>
                </template>
            </el-table-column>
            <el-table-column label="年级">
                <template #default="scope">
                    <span>{{conversionGrade(scope.row.grade)}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="introduction" label="个性签名" />
            <el-table-column fixed="right" label="操作" width="120">
                <template #default="scope">
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

</template>



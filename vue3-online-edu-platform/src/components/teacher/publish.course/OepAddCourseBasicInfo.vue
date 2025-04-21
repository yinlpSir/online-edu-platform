<script setup>
    import { reactive, ref } from "vue"
    import {Plus} from "@element-plus/icons-vue"
    import {getAllSubject,grades} from "../../../utils/common-utils"
    import useCourseStore from "../../../store/store.course"

    const courseStore = useCourseStore()
    
    const {courseBasicInfo} = courseStore

    const isFree = ref(true) // 设置课程是否免费

    // #region 课程封面相关

    // 文件列表移除文件时
    const handleAvatarRemove = (uploadFile) => {
        
        console.log('avatar remove')
        console.log(uploadFile)
    }

    // 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用
    const handleAvatarChange = (uploadFile) => {
        
        console.log('avatar change')
        
        courseBasicInfo.courseCover = uploadFile.raw
        courseStore.courseCoverUrl = URL.createObjectURL(courseBasicInfo.courseCover) // 显示图片
        console.log(courseBasicInfo.courseCover)
    }

    const beforeAvatarUpload = (rawFile) => {
        console.log('beforeAvatarUpload')
        if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/jpg' && rawFile.type !== 'image/png' ) {
            ElMessage.error('请上传正确的格式!')
            return false
        } else if (rawFile.size / 1024 / 1024 > 20) {
            ElMessage.error('课程封面不能超过20MB!')
            return false
        }
        return true
    }

    // #endregion

    // #region 表单验证相关
    
    // #endregion

</script>

<template>
    <div>
        <el-form label-position="right" label-width="100px" :model="courseBasicInfo" class="course-info-form">
            <el-form-item label="课程封面：">
                <!-- 
                        注意：
                            1、auto-upload 为 false 时，before-upload 不生效 
                -->
                <el-upload 
                    class="avatar-uploader" 
                    :auto-upload="false" 
                    :show-file-list="false"
                    accept="image/png,image/jpeg,image/jpg" 
                    :on-remove="handleAvatarRemove" 
                    :on-change="handleAvatarChange"
                    :before-upload="beforeAvatarUpload"
                >
                    <img v-if="courseBasicInfo.courseCover" :src="courseStore.courseCoverUrl" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
                <p style="color:var(--el-text-color-regular); font-size: 12px;">格式：仅支持JPG、PNG、JPEG</p>
                <p style="margin-left: 25px;color:var(--el-text-color-regular);font-size: 12px;">大小：20M以内</p>
                <p style="margin-left: 25px;color:var(--el-text-color-regular);font-size: 12px;">注意：建议上传550*250的图片</p>
            </el-form-item>
            <el-form-item label="课程名称：">
                <el-input v-model="courseBasicInfo.courseName" />
            </el-form-item>
            <el-form-item label="科目：">
                <el-select v-model="courseBasicInfo.subject" placeholder="请选择科目">
                    <el-option v-for="sub in getAllSubject()" :label="sub" :value="sub" :key="sub" />
                </el-select>
            </el-form-item>
            <el-form-item label="适用年级：">
                <!-- <el-input v-model="courseBasicInfo.grade" /> -->
                <el-select v-model="courseBasicInfo.grade" multiple filterable allow-create default-first-option
                    :reserve-keyword="false" placeholder="适用年级">
                    <el-option v-for="grade in grades" :key="grade.value" :label="grade.label" :value="grade.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="是否免费：">
                <el-radio-group v-model="isFree">
                    <el-radio :label="true">是</el-radio>
                    <el-radio :label="false">否</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="价格：" v-show="!isFree">
                <el-input-number v-model="courseBasicInfo.price" :min="0.0" />
            </el-form-item>
        </el-form>
        <!-- <button @click="console.log(courseBasicInfo.courseCover)">getCover</button> -->
    </div>
</template>

<style scoped>
    .course-info-form{
        min-width: 450px;
        max-width: 600px;
        margin: 0 auto;
    }
    :deep(.el-form-item__label){
        color: #000;
    }
</style>

<!-- 课程封面相关 -->
<style scoped>
    .avatar-uploader {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
        width: 500px;
        height: 240px;
    }
    .avatar-uploader:hover {
        border-color: var(--el-color-primary);
    }
    .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 500px;
        height: 240px;
        text-align: center;
    }
    .avatar{
        width: 500px;
        height: 240px;
    }
</style>
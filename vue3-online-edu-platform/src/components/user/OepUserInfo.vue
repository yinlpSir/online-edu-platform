<script setup>
    import { Plus } from '@element-plus/icons-vue'
    import { onBeforeMount, reactive, ref, toRaw } from 'vue'
    import {updateStudent,getCurrentStudent,} from '@/api/api.user.js'
    import useUserStore from "@/store/store.user"
    import { ElMessage } from 'element-plus'

    const loading = ref(false)

    const userStore=useUserStore()

    const formRef = ref()

    const userBasicInfo = reactive({
        headPortrait:'',
        username:'',
        gender:'',
        introduction:'',
        role:'',
    })

    const formRules = reactive({
        username:[
            {required:true,message:'请输入您的用户名!',trigger:'blur'},
            {
                validator:(rule,value,callback)=>{
                    if(value.trim() === '') callback(new Error('用户名不得全是空格!')) 
                    else callback()
                },
                trigger:'blur'
            }
        ],
        gender:[
            {required:true,message:'请选择您的性别!',trigger:'change'},
        ],
        // grade:[
        //     {required:true,message:'请选择您的年级!',trigger:'change'},
        // ],
    })

    const getUserInfo =()=> {
        Object.assign(userBasicInfo,userStore.userPartlyInfo)
    }

    onBeforeMount(()=>{
        getUserInfo()
    })

    const submitForm = async (formEl) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                loading.value = true
                const requestData = {...userBasicInfo}
                requestData.headPortrait = updateAvatar.value
                updateStudent(requestData).then(response => {
                    const {data} = response
                    userStore.setUserPartlyInfo(data.data)
                    getUserInfo()
                    ElMessage.success(data.message)
                    loading.value = false
                    setTimeout(() => {
                        location.reload()
                    }, 1000);
                },error => console.log(error))
            } else {
                console.log('error submit!', fields)
            }
        })
    }

    // #region 头像上传

    const updateAvatar = ref(null)

    const avatarRef = ref(null)

    const handleAvatarChange = (uploadFile,uploadFiles)=>{
        if (uploadFile.raw.type !== 'image/jpeg' && uploadFile.raw.type !== 'image/jpg' && uploadFile.raw.type !== 'image/png' ) {
            ElMessage.error('请上传正确的格式!')
            avatarRef.value.handleRemove(uploadFile.raw)
            return false
        } else if (uploadFile.raw.size / 1024 / 1024 > 5) {
            ElMessage.error('头像大小不能超过5MB!')
            avatarRef.value.handleRemove(uploadFile.raw)
            return false
        }
        updateAvatar.value = uploadFile.raw
        userBasicInfo.headPortrait = URL.createObjectURL(uploadFile.raw)
        console.log(updateAvatar.value == uploadFile.raw)
    }

    //#endregion 

</script>

<template>
    <div class="user-info-container" v-loading.fullscreen.lock="loading">
        <div class="avatar-box">
            <el-upload 
                ref="avatarRef"
                class="avatar-uploader" 
                :show-file-list="false"
                accept="image/png,image/jpeg,image/jpg"
                :on-change="handleAvatarChange"
                :auto-upload="false"
            >
                <img v-if="userBasicInfo.headPortrait" :src="userBasicInfo.headPortrait" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                    <Plus />
                </el-icon>
            </el-upload>
            <p><b>点击上传头像</b></p>
            <p>格式：支持JPG、PNG、JPEG</p>
            <p>大小：5M以内</p>
        </div>
        <div class="basic-info-box">
            <div style="width: 85%;">
                <h3 style="width:100% ; margin-bottom: 35px ;">基本信息</h3>
                <el-form ref="formRef" :model="userBasicInfo" :rules="formRules" label-width="96px" status-icon >
                    <el-form-item label="昵称：" prop="username">
                        <el-input v-model="userBasicInfo.username" />
                    </el-form-item>
                    <el-form-item label="性别：" prop="gender">
                        <el-radio-group v-model="userBasicInfo.gender">
                            <el-radio :label="true" >男</el-radio>
                            <el-radio :label="false" >女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <!-- <el-form-item label="年级：" prop="grade">
                        <el-select v-model="userBasicInfo.grade" placeholder="请选择年级">
                            <el-option label="Zone one" value="shanghai" />
                            <el-option label="Zone two" value="beijing" />
                        </el-select>
                    </el-form-item> -->
                    <el-form-item label="角色：">
                        <el-tag>学生</el-tag>
                    </el-form-item>
                    <el-form-item label="个性签名：">
                        <el-input v-model="userBasicInfo.introduction" type="textarea" show-word-limit maxlength="26" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="submitForm(formRef)">
                            提交修改
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .user-info-container {
        box-shadow: var(--el-box-shadow-light);
        /* height: 100%; */
        min-height: 500px;
        display: flex;
        justify-content: center;
        align-items: stretch;
        /* align-content: stretch; */
    }

    .user-info-container .avatar-box {
        /* border: 1px solid black; */
        max-width: 220px;
        flex-wrap: wrap;
        display: flex;
        justify-content: center;
        align-content: center;
        margin-right: 20px;
    }

    .user-info-container .avatar-box p {
        width: 78%;
        font-size: 12px;
    }

    .user-info-container .avatar-box p:first-of-type {
        text-align: center;
        margin: 10px 0px;
        font-size: 16px;
    }

    .user-info-container .basic-info-box {
        /* border: 1px solid black; */
        margin-left: 20px;
        width: 50%;
        display: flex;
        align-items: center;
        /* height: 100%; */
        /* flex-wrap: wrap; */
    }

    .user-info-container .avatar-uploader .avatar {
        width: 178px;
        height: 178px;
        display: block;
    }

    .user-info-container .avatar-uploader {
        border: 1px dashed var(--el-border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
        width: 178px;
        height: 178px;
    }

    .user-info-container .avatar-uploader:hover {
        border-color: var(--el-color-primary);
    }

    .user-info-container .el-icon.avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 178px;
        height: 178px;
        text-align: center;
    }

    :deep(.el-tag){
        font-size: 14px;
    }
</style>
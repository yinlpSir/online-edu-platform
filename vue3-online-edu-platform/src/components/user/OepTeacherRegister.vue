<script setup>
    import { reactive,ref,toRaw } from "vue";
    import {ArrowRight,Search,ArrowLeft} from "@element-plus/icons-vue"
    import {teacherRegister,getVerificationCode as getRealVerificationCode} from '../../api/api.user.js'

    const stepActive = ref(0)

    const oneFormRef = ref()

    const twoFormRef = ref()

    const registerDTO=reactive( sessionStorage.getItem('registerInfo') ? JSON.parse(sessionStorage.getItem('registerInfo')) : {
        username:'',
        password:'',
        gender:'',
        introduction:'',
        phoneNumber:'',
        verificationCode:'',
        realName: '',
        teacherCertificateFileList: [], // teacherCertificateImg
    } )

    const register = async (twoFormEl) => {
        if (!twoFormEl) return
        await twoFormEl.validate((valid, fields) => {
            if (valid) {
                console.log('register submit')
                
                const result = toRaw(registerDTO)
                result['teacherCertificateImg'] = result.teacherCertificateFileList[0].raw
                delete result.teacherCertificateFileList
                teacherRegister(result).then(res=>{
                    const {data} = res
                    ElMessage({
                        message:data.message,
                        grouping:true,
                        type:'success',
                    })
                },err=>{
                    console.log(err)
                })
            }
        })
    }

    const save = () => {
        // 缓存一下 registerDTO
        let o = Object.assign({},toRaw(registerDTO))
        o.teacherCertificateFileList=[]
        sessionStorage.setItem('registerInfo',JSON.stringify(o))
    }

    const nextStep= async (oneFormEl) => {
        if (!oneFormEl) return
        await oneFormEl.validate((valid, fields) => {
            if (valid) {
                stepActive.value++
            }
        })
    }

    const lastStep = () => {
        stepActive.value--
    }

    const handleChange = (uploadFile,uploadFiles) => {
        console.log('on change')
        if(uploadFile.size / 1024 / 1024 > 10){
            ElMessage.error('单个文件的大小不能超过10MB!')
        }
        registerDTO.teacherCertificateFileList=uploadFiles
        // console.log(registerDTO.teacherCertificateFileList)
    }

    const handleExceed = (files,uploadFiles) => {
        ElMessage.warning(
            '提示：至多只能上传 1 个文件哦！'
        )
    }

    const beforeAvatarRemove = (delFile,uploadFiles) => {
        // delFile is the file that will be deleted
        console.log('delete '+delFile.name)
        registerDTO.teacherCertificateFileList=toRaw(uploadFiles)
        // console.log(registerDTO.teacherCertificateFileList)
    }

    // const getVerificationCode = () => {
    //     console.log('Get Verification Code')
    //     isShowSendText.value = false
    //     getSetInterval()
    //     getRealVerificationCode({
    //         phoneNumber:registerDTO.phoneNumber
    //     }).then(response =>{
    //         console.log(response)
    //         isShowSendText.value = false
    //         getSetInterval()
    //     },error=>{
    //         console.log(error)
    //     })
    // }

    // #region 获取验证码相关

    // 验证码计时器
    const sms = reactive({
        disabled: false,
        total: 60,
        count: 0
    })

    // 计时器处理器
    const timerHandler = () => {
        sms.count = sms.total
        sms.disabled = true
        let timer = setInterval(() => {
            if (sms.count > 1 && sms.count <= sms.total) {
                sms.count--
            } else {
                sms.disabled = false
                clearInterval(timer)
            }
        }, 1000)
    }

    // 发送验证码
    const sendVerificationCode = (formEl)=> {
        formEl.validateField("phoneNumber").then(success => {
            timerHandler()
            getRealVerificationCode({
                phoneNumber:registerDTO.phoneNumber
            }).then(response =>{
                    
                },error=>{
                console.log(error)
            })
        }).catch(fail => {})
    }
    // #endregion

    //#region 表单验证相关
    const rules = reactive({
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
        password:[
            {
                required:true,
                validator:(rule,value,callback)=>{
                    let reg=/^(?=.*[a-zA-Z])(?=.*\d).{6,}$/;
                    if(value === '') callback(new Error('请输入您的密码!'))
                    else if(!reg.test(value)) callback(new Error('密码得包含大写字母或小写字母和数字且长度大于等于6'))
                    else callback()
                },
                trigger:'blur'
            }
        ],
        gender:[
            {required:true,message:'请选择您的性别!',trigger:'change'},
        ],
        introduction:[],
        phoneNumber:[
            {
                required:true,
                validator:(rule,value,callback)=>{
                    let reg=/^1[3456789]\d{9}$/
                    if(value === '') {
                        callback(new Error('请输入您的手机号!'))
                    }else if(!reg.test(value)) {
                        callback(new Error('请输入正确的手机号'))
                    }else {
                        callback()
                    }
                },
                trigger:'blur'
            }
        ],
        verificationCode:[
            {
                required:true,
                validator:(rule,value,callback)=>{
                    let reg=/^[0-9]{6}$/;
                    if(value === '') callback(new Error('请输入验证码!'))
                    else if(!reg.test(value)) callback(new Error('验证码由六位数字组成'))
                    else callback()
                },
                trigger:'blur'
            }
        ],
        realName:[
            {required:true,message:'请输入您的真实姓名！',trigger:'blur'},
            {
                validator:(rule,value,callback)=>{
                    if(value.trim() === '') callback(new Error('错误的名字!')) 
                    else callback()
                },
                trigger:'blur'
            }
        ],
        teacherCertificate:[
            {
                required:true,
                validator:(rule,value,callback)=>{
                    if(registerDTO.teacherCertificateFileList.length <= 0) callback(new Error('请上传教资相关信息！'))

                    registerDTO.teacherCertificateFileList.forEach(file => {
                        if(file.size / 1024 / 1024 > 10) {
                            callback(new Error('单个文件的大小不能超过10MB!'))
                        }
                    })

                    callback()
                },
                trigger:'change'
            }
        ]
    })
    //#endregion

</script>

<template>
    <div class="teacher-register-container">
        <div style=" width: 100px; min-width: 100px;">
            <el-steps :active="stepActive" align-center direction="vertical" process-status="process"  space="200px" finish-status="success" >
                <el-step title="步骤一" description="基本信息填写" />
                <el-step title="步骤二" description="教师信息填写" />
            </el-steps>
        </div>
        <!-- Step one -->
        <el-form 
            v-show="stepActive === 0"
            ref="oneFormRef"
            label-position="top" 
            label-width="100px" 
            :model="registerDTO" 
            :rules="rules"
            :hide-required-asterisk="false"
            style="width: 400px; min-width: 250px; max-width: 460px; margin-left: 6px ;"
        >
            <el-form-item label="用户名" prop="username">
                <el-input v-model="registerDTO.username" placeholder="username" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-model="registerDTO.password" placeholder="password" show-password clearable />
            </el-form-item>
            <el-form-item label="性别" size="small" prop="gender" style=":deep(.el-form-item--small){ font-size: 14px; }">
                <el-radio-group v-model="registerDTO.gender">
                    <el-radio border label="1" >男</el-radio>
                    <el-radio border label="0" >女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="个性签名" prop="introduction">
                <el-input v-model="registerDTO.introduction" type="textarea" placeholder="个性签名" show-word-limit maxlength="26"  />
            </el-form-item>
            <el-form-item size="small" style=" margin-top: 8px; float: right;">
                <el-button type="success" plain round @click="save">保存</el-button>
                <el-button type="primary" round @click="nextStep(oneFormRef)">
                    下一步<el-icon class="el-icon--right"><ArrowRight /></el-icon>
                </el-button>
            </el-form-item>
        </el-form>

        <!-- Step two -->
        <el-form 
            v-show="stepActive === 1"
            ref="twoFormRef"
            label-position="top" 
            label-width="100px" 
            :model="registerDTO" 
            :rules="rules"
            style="width: 400px; min-width: 250px; max-width: 460px; margin-left: 6px ;"
        >
            <el-form-item label="手机号" prop="phoneNumber">
                <el-input v-model="registerDTO.phoneNumber" :autofocus="true" placeholder="phone number" clearable />
            </el-form-item>
            <el-form-item label="验证码（验证码在5分钟内有效）" prop="verificationCode">
                <el-input
                    v-model="registerDTO.verificationCode"
                    style="width: 240px;"
                    placeholder="verification code"
                >
                    <template #suffix>
                        <div>
                            <el-button v-if="!sms.disabled" link type="default" @click="sendVerificationCode(twoFormRef)">
                                <span style="color:#eed2b0;">发送验证码</span>
                            </el-button>
                            <el-button v-else link type="default" color="#f38301" disabled>
                                <span style="color:#eed2b0;">{{ sms.count }} 秒后重新发送</span>
                            </el-button>
                        </div>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="registerDTO.realName" placeholder="real name" clearable />
            </el-form-item>
            <el-form-item label="上传教资相关信息" prop="teacherCertificate">
                <!--
                    要求：
                        1、最多上传一个文件
                        2、大小不能超过 10MB
                    
                    注意：
                        1、auto-upload 为 false 时，before-upload 不生效
                -->
                <el-upload
                    ref="teacherCertificateRef"
                    :limit="1"
                    :on-change="handleChange"
                    :on-exceed="handleExceed"
                    :before-remove="beforeAvatarRemove"
                    :auto-upload="false"
                >
                    <template #trigger>
                        <el-button type="primary" >Please select file.</el-button>
                    </template>
                    <template #tip>
                        <div class="el-upload__tip" style="margin-top: 0px;font-size: 10px;">
                            (至多上传1个文件，每个文件的大小不能超过10MB)
                        </div>
                    </template>
                </el-upload>
            </el-form-item>
            <el-form-item size="small" style=" margin-top: 8px; float: right;">
                <el-button type="primary" :icon="ArrowLeft" round @click="lastStep">上一步</el-button>
                <el-button type="success" plain round @click="save">保存</el-button>
                <el-button color="rgb(214,202,250)" size="default" round @click="register(twoFormRef)">
                    点击注册
                </el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<style scoped>
    .teacher-register-container {
        width: 70%;
        margin: 20px auto;
        /* border: 1px solid black; */
        display: flex;
        justify-content: space-evenly;
    }

    .vc-text:hover{
        color: skyblue;
    }

    /** input */
    :deep(.el-input__wrapper){
        border-radius: 20px;
        height: 30px;
        background-color: rgba(0, 0, 0, 0);
        border-color:#000;
        font-size: 12px;
    }

    /** textarea */
    :deep(.el-textarea__inner){
        background-color: rgba(0, 0, 0, 0);
        font-size: 12px;
    }
    :deep(.el-textarea .el-input__count){
        background-color: rgba(0, 0, 0, 0);
    }

    :deep(.el-form--default.el-form--label-top .el-form-item .el-form-item__label){
        margin-bottom: 3px;
    }
    
    :deep(.el-form-item){
        margin-bottom: 18px;
    }

    :deep(.el-form-item__label){
        color: black;
    }

</style>
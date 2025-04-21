<script setup>
    import {reactive,ref,toRaw} from 'vue'
    import useUserStore from "../store/store.user"
    import {CloseBold} from '@element-plus/icons-vue'
    import {loginByPassword,loginByVerificationCode} from '../api/api.user.js'

    const userStore=useUserStore()

    const passwordFormRef=ref();

    const smsFormRef=ref();

    const isPwdLogin=ref(true)

    const passwordLoginDTO=reactive({
        phoneNumber:'',
        password:''
    })

    // 短信登录
    const smsLoginDTO=reactive({
        phoneNumber:'',
        verificationCode:''
    })

    const getCurrentComPath=()=>{
        const path=import.meta.url
        return path.substring(0,path.indexOf("components"))+'assets/'
    }

    // 项目 logo
    const logoPath=ref(getCurrentComPath()+'images/logo-no-bgimg.png')

    const passwordLogin=(passwordFormEl)=>{
        if (!passwordFormEl) return
        passwordFormEl.validate((valid) => {
            if (valid) {
                console.log('password login submit!')
                loginByPassword(toRaw(passwordLoginDTO)).then(response =>{
                    const {data} = response
                    userStore.setShowLoginCompo(false)
                    userStore.setToken(data.data.token)
                    userStore.setIsLogged(true)
                    userStore.setUserPartlyInfo({
                        headPortrait:data.data.headPortrait,
                        id:data.data.id,
                        username:data.data.username,
                        role:data.data.role,
                        gender:data.data.gender,
                        introduction:data.data.introduction,
                    })
                    ElMessage({
                        message:data.message,
                        grouping:true,
                        type:'success',
                    })
                    // clear form data
                    passwordLoginDTO.phoneNumber = ''
                    passwordLoginDTO.password = ''
                },error=>{
                    ElMessage({
                        message:error.response.data.message,
                        grouping:true,
                        type:'error'
                    })
                })
            } else {
                return false
            }
        })
    }

    const smsLogin = (smsFormEl) => {
        if (!smsFormEl) return
        smsFormEl.validate((valid) => {
            if (valid) {
                console.log('sms login submit!')

                // loginByVerificationCode()
            } else {
                console.log('sms login error!')
                return false
            }
        })
    }

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
            // getRealVerificationCode({
            //     phoneNumber:registerDTO.phoneNumber
            // }).then(response =>{
                    // 获取成功
                // },error=>{
            //     console.log(error)
            // })
        }).catch(fail => {})
        
    }
    // #endregion

    const rules = reactive({
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
        ]
    })
</script>

<template>
    <div>
        <el-dialog 
            v-model="userStore.showLoginCompo"
            :show-close="false"
            :center="true"
            :align-center="true"
            :lock-scroll="false"
            :close-on-click-modal="false"
            class="login-box"
        >
            <template #header="{close}">
                <div>
                    <el-icon style="float: right; cursor: pointer;" color="green" :size="23" @click="close"><CloseBold /></el-icon>
                    <div style="display: flex; align-items: center;justify-content: center;">
                        <el-image :src="logoPath" fit="fill" style="width: 40px;height: 30px; ">
                            <template #error>
                            <div class="image-slot">
                                <el-icon><icon-picture /></el-icon>
                            </div>
                            </template>
                        </el-image>
                        <h2 style="display: inline-block; margin-left: 6px;"> 登 录</h2>
                    </div>
                </div>
            </template>
            <div class="dialog-body">
                <div class="option" v-show="!isRegister">
                    <span class="option-text" @click="isPwdLogin=true" :class="isPwdLogin?'active':''">密码登录</span>
                    <span style="display: inline-block; margin:0 30px;color: black;">|</span>
                    <span class="option-text" @click="isPwdLogin=false" :class="isPwdLogin?'':'active'">短信登录</span>
                </div>
                <el-form
                    ref="passwordFormRef"
                    :label-position="labelPosition"
                    label-width="100px"
                    :model="passwordLoginDTO"
                    v-show="isPwdLogin"
                    :rules="rules"
                >
                    <el-form-item label-width="0" prop="phoneNumber">
                        <el-input v-model="passwordLoginDTO.phoneNumber" :autofocus="true" clearable placeholder=" 请输入手机号"/>
                    </el-form-item>
                    <el-form-item label-width="0">
                        <el-input v-model="passwordLoginDTO.password" style=" min-width: 100px; max-width: 360px;" show-password placeholder=" 请输入密码" />
                        <span class="text">忘记密码?</span>
                    </el-form-item>
                    <div style="display: flex; justify-content: center; margin-bottom: 18px;">
                        <el-button color="rgb(214,202,250)" size="default" style=" height: 35px; min-width: 120px; width: 100%; " round @click="passwordLogin(passwordFormRef)">
                            登录
                        </el-button>
                    </div>
                </el-form>
                <el-form
                    ref="smsFormRef"
                    :label-position="labelPosition"
                    label-width="100px"
                    :model="smsLoginDTO"
                    :rules="rules"
                    v-show="!isPwdLogin"
                >
                    <el-form-item label-width="0" prop="phoneNumber">
                        <el-input v-model="smsLoginDTO.phoneNumber" :autofocus="true" clearable placeholder=" 请输入手机号"/>
                    </el-form-item>
                    <el-form-item label-width="0" prop="verificationCode">
                        <el-input
                            v-model="smsLoginDTO.verificationCode"
                            placeholder="验证码"
                        >
                            <template #suffix>
                                <div>
                                    <el-button v-if="!sms.disabled" link type="default" @click="sendVerificationCode(smsFormRef)">
                                        <span style="color:#eed2b0;">发送验证码</span>
                                    </el-button>
                                    <el-button v-else link type="default" color="#f38301" disabled>
                                        <span style="color:#eed2b0;">{{ sms.count }} 秒后重新发送</span>
                                    </el-button>
                                </div>
                            </template>
                        </el-input>
                    </el-form-item>
                    <div style="display: flex; justify-content: center; margin-bottom: 18px;">
                        <el-button color="rgb(214,202,250)" size="default" style=" height: 35px; min-width: 100px; width: 100%;" round @click="smsLogin(smsFormRef)">
                            登录
                        </el-button>
                    </div>
                </el-form>
            </div>
        </el-dialog>
    </div>
</template>

<style scoped>
    .login-box{
        min-width: 320px;
    }
    .login-box .dialog-body .el-form{
        max-width: 480px;
        /* border: 1px solid black; */
        margin: 10px auto;
    }
    .login-box .dialog-body .el-form .el-form-item{
        margin-bottom: 18px;
        min-width: 300px;
    }
    .login-box .dialog-body .option{
        display: flex;
        justify-content: center;
        color: black;
        font-size: 20px;
        margin-bottom: 18px;
    }
    .login-box .dialog-body .option .option-text{
        cursor: pointer;
    }
    .active{
        color: rgb(214,202,250);
    }
    .vc-text:hover{
        color: skyblue;
    }
    .text{
        display: inline-block;
        width: 100px; 
        text-align: center;
        font-size: 16px;
        cursor: pointer;
    }
    .text:hover{
        color: rgb(127,166,248);
    }
    :deep(.el-dialog){
        border-radius: 20px;
        min-width: 350px;
        max-width: 700px;
    }
    :deep(.el-input__wrapper){
        border-radius: 20px;
        height: 35px;
    }
    :deep(.el-form-item__error){
        margin-left: 15px;
    }
    :deep(.el-dialog--center .el-dialog__body){
        padding-top: 6px;
    }
</style>
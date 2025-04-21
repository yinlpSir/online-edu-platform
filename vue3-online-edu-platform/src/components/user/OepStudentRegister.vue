<script setup>
    import {reactive,ref,toRaw} from 'vue'
    import {studentRegister,getVerificationCode as getRealVerificationCode} from '@/api/api.user.js'
import { ElMessage } from 'element-plus'

    const loading = ref(false)

    const grades=[
        {label:'一年级',value:1},
        {label:'二年级',value:2},
        {label:'三年级',value:3},
        {label:'四年级',value:4},
        {label:'五年级',value:5},
        {label:'六年级',value:6},
        {label:'初一',value:7},
        {label:'初二',value:8},
        {label:'初三',value:9},
        {label:'高一',value:10},
        {label:'高二',value:11},
        {label:'高三',value:12}
    ]

    const formRef=ref()

    const registerDTO=reactive({
        username:'',
        password:'',
        gender:'',
        introduction:'',
        grade:'',
        phoneNumber:'',
        verificationCode:'',
    })

    /**
     * form validation rule
     */
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
        grade:[
            {required:true,message:'请选择您的年级!',trigger:'change'},
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
    })

    const submitForm = async (formEl) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                loading.value = true
                studentRegister(toRaw(registerDTO)).then(response =>{
                    const {data} = response
                    ElMessage.success(data.message)
                    loading.value = false
                    resetForm(formEl)
                },error=>{
                    console.log(error)
                })
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
            getRealVerificationCode({
                phoneNumber:registerDTO.phoneNumber
            }).then(response =>{
                    
                },error=>{
                console.log(error)
            })
        }).catch(fail => {})
        
    }
    // #endregion

    const resetForm = (formEl) => {
        if (!formEl) return
        formEl.resetFields()
    }


</script>

<template>
    <div class="student-register-container" v-loading.fullscreen.lock="loading">
        <el-form 
            label-width="80px" 
            :model="registerDTO" 
            ref="formRef"
            :rules="rules"
            :status-icon="true"
            size="small"
            label-position="right" 
            @validate="validate"
            style="min-width: 310px; width: 80%; max-width: 500px; margin: 10px auto;"
        >
            <el-form-item label="用户名：" prop="username">
                <el-input v-model="registerDTO.username" :autofocus="true" placeholder=" 请输入用户名" />
            </el-form-item>
            <el-form-item label="密码：" prop="password">
                <el-input type="password" v-model="registerDTO.password" :autofocus="true" placeholder=" 请输入密码" show-password />
            </el-form-item>
            <el-form-item label="性别：" prop="gender">
                <el-radio-group v-model="registerDTO.gender">
                    <el-radio border label="1" >男</el-radio>
                    <el-radio border label="0" >女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="年级：" prop="grade">
                <el-select v-model="registerDTO.grade" placeholder="请选择你的年级">
                    <el-option v-for="o in grades" :key="o.value" :label="o.label" :value="o.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="个性签名：" prop="introduction">
                <el-input v-model="registerDTO.introduction" type="textarea" placeholder="个性签名" show-word-limit maxlength="26" />
            </el-form-item>
            <el-form-item label="手机号：" prop="phoneNumber">
                <el-input v-model="registerDTO.phoneNumber" :autofocus="true" placeholder=" 请输入手机号" />
            </el-form-item>
            <el-form-item label="验证码：" prop="verificationCode">
                <el-input
                    v-model="registerDTO.verificationCode"
                    style="width: 230px;"
                    placeholder="verification code"
                    :clearable="false"
                >
                    <template #suffix>
                        <div>
                            <el-button v-if="!sms.disabled" link type="default" @click="sendVerificationCode(formRef)">
                                <span style="color:#eed2b0;">发送验证码</span>
                            </el-button>
                            <el-button v-else link type="default" color="#f38301" disabled>
                                <span style="color:#eed2b0;">{{ sms.count }} 秒后重新发送</span>
                            </el-button>
                        </div>
                    </template>
                </el-input>
            </el-form-item>
            <div style="display: flex; justify-content: space-evenly; margin-bottom: 18px;">
                <el-button 
                    color="rgb(214,202,250)" 
                    round 
                    size="default" 
                    style="min-width: 100px; width: 30%; "
                    @click="submitForm(formRef)"
                >注册</el-button>
                <el-button 
                    color="rgb(214,202,250)" 
                    round 
                    size="default" 
                    style="min-width: 100px; width: 30%;"
                    @click="resetForm(formRef)"
                >重置</el-button>
            </div>
        </el-form>
    </div>
</template>

<style scoped>
    .student-register-container{
        width: 48%;
        min-width: 320px;
        margin: 20px auto;
        padding-right: 6px;
        padding-top: 6px;
        /* border: 1px solid skyblue; */
        border-radius: 20px;
        /* background-color: rgb(214,226,252); */
    }
    .vc-text:hover{
        color: skyblue;
    }
    :deep(.el-input__wrapper){
        border-radius: 20px;
        height: 30px;
        background-color: rgba(0, 0, 0, 0);
    }
    :deep(.el-form-item__label){
        color: black;
    }
    :deep(.el-textarea__inner){
        background-color: rgba(0, 0, 0, 0);
    }
    :deep(.el-form-item__error){
        left: 15px;
    }
</style>
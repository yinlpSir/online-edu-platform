<script lang="js" setup>
import { reactive, ref,toRaw } from "vue"
import { useRouter,useRoute } from "vue-router"
import {login} from "@/api/api.user"
import { authStoe } from '@/stores'

const permission = authStoe()

const router = useRouter()

const route = useRoute()

/** 登录表单元素的引用 */
const loginFormRef = ref(null)

/** 登录表单数据 */
const loginFormData = reactive({
    phoneNumber: route.query.phoneNumber,
    password: route.query.password,
})

/** 登录表单校验规则 */
const loginFormRules = {
    phoneNumber: [
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
    password: [
        { required: true, message: "请输入密码", trigger: "blur" },
    ],
}

/** 登录逻辑 */
const handleLogin = (loginFormRef) => {
    if (!loginFormRef) return
    loginFormRef.validate((valid) => {
        if (valid) {
            login(toRaw(loginFormData)).then(response =>{
                const {data} = response
                ElMessage.success(response.message)

                permission.token =data.token
                permission.role = data.role 
                router.push(route.query.redirect ?? '/')
            },error=>{
                const {data} = error.response
                ElMessage.error(data.message)
            })
        }
    })
}
</script>

<template>
    <div class="login-container">
        <ThemeSwitch class="theme-switch" />
        <div class="login-card">
            <div class="title">
                <img src="@/assets/logo.png" alt="" style="width: 50px; height: 50px; margin-right: 10px;">
                <h1><b>在线教育后台管理</b></h1>
            </div>
            <div class="content">
                <el-form ref="loginFormRef" :model="loginFormData" :rules="loginFormRules"
                    @keyup.enter="handleLogin(loginFormRef)">
                    <el-form-item prop="phoneNumber">
                        <el-input v-model.trim="loginFormData.phoneNumber" placeholder="手机号" type="text" tabindex="1"
                            :prefix-icon="User" size="large" />
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input v-model.trim="loginFormData.password" placeholder="密码" type="password" tabindex="2"
                            :prefix-icon="Lock" size="large" show-password />
                    </el-form-item>
                    <el-button type="primary" size="large" @click.prevent="handleLogin(loginFormRef)">点 击 登 录</el-button>
                </el-form>
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    min-height: 100%;
    background-image: url("@/assets/images/login/login_background.jpg");
    background-size: cover;
    width: 100%;
    height: 100%;
    margin-top: 100px;

    .theme-switch {
        position: fixed;
        top: 5%;
        right: 5%;
        cursor: pointer;
    }

    .login-card {
        width: 480px;
        border-radius: 20px;
        box-shadow: 0 0 10px #dcdfe6;
        background-color: #fff;
        overflow: hidden;

        .title {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 120px;
            font-weight: bold;

            img {
                height: 100%;
            }
        }

        .content {
            padding: 20px 50px 50px 50px;

            :deep(.el-input-group__append) {
                padding: 0;
                overflow: hidden;

                .el-image {
                    width: 100px;
                    height: 40px;
                    border-left: 0px;
                    user-select: none;
                    cursor: pointer;
                    text-align: center;
                }
            }

            .el-button {
                width: 100%;
                margin-top: 10px;
            }
        }
    }
}
</style>
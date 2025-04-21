
<script setup>

</script>

<template>
    <div class="oep-register-container">
        <div class="register-inner-container">
            <div class="inner-header">
                <span class="header-option">
                    <router-link :to="{path:'/user/register/'}" class="router-class" exact-active-class="active-class" >学生注册</router-link>
                </span>
                <span style="margin: 0 16px;"> | </span>
                <span class="header-option">
                    <router-link to="/user/register/teacher" class="router-class" exact-active-class="active-class">老师注册</router-link>
                </span>
            </div>
            <div class="inner-body">
                <router-view></router-view>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .oep-register-container{
        /* border: 1px solid #ccc; */
        max-width: 90vw;
        width: 90vw;
        min-width: 420px;
        
        margin: 0 auto;
        /* display: flex; */
        /* align-items: center; */
    }
    .oep-register-container .register-inner-container{
        border: 1px solid #ccc;
        width: 85%;
        min-width: 400px;
        margin: 0 auto;
        min-height: 570px;
        /* height: 450px; */
        box-shadow: var(--el-box-shadow-light);
        margin-top: 4px;
        border-radius: 15px;
    }
    .oep-register-container .register-inner-container .inner-header{
        font-size: 24px;
        text-align: center;
        margin-top: 36px;
    }
    .oep-register-container .register-inner-container .inner-header .header-option{
        cursor: pointer;
        font-weight: bold;
        font-style: normal;
    }
    .router-class{
        color: black;
        text-decoration: none;
    }
    .active-class{
        color: rgb(214,202,250);
    }
</style>
<script setup>

</script>

<template>
    <div class="setting-account-container">
        <div class="account-item">
            <div class="account-item-left">
                <span>手机号</span>
                <span>198***6844</span>
            </div>
            <div style="margin-right: 10px;">
                <el-button type="primary" link style="font-size: 18px;">修改</el-button>
            </div>
        </div>
        <div class="account-item">
            <div class="account-item-left">
                <span>密码</span>
                <span>********</span>
            </div>
            <div style="margin-right: 10px;">
                <el-button type="primary" link style="font-size: 18px;">修改</el-button>
            </div>
        </div>
        <div class="account-item">
            <div class="account-item-left">
                <span>账号注销</span>
            </div>
            <div style="margin-right: 10px;">
                <el-button type="primary" link style="font-size: 18px;">注销</el-button>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .setting-account-container{
        box-shadow: var(--el-box-shadow-light);
        min-height: 500px;
        padding: 4px 0;
    }
    .setting-account-container .account-item{
        display: flex;
        align-items: center;
        font-size: 18px;
        min-height: 50px;
        width: 90%;
        border-bottom: 1px solid #ccc;
        justify-content: space-between;
        margin: 10px auto;
    }
    .setting-account-container .account-item .account-item-left{
        display: flex;
        margin-left: 10px;
    }
    .setting-account-container .account-item .account-item-left span{
        width: 150px;
    }

</style>
<script setup>
    import { ref } from "vue";


    const msgs =ref([
        {
            msgName:'注册成功通知',
            msgDate:'2023年2月12日 6:00',
            msgBody:'亲爱的用户，恭喜称为在线教育平台的一员！'
        },
        {
            msgName:'在线教育平台隐私通知',
            msgDate:'2023年2月12日 6:00',
            msgBody:'亲爱的用户，根据业务开展的实际情况，在线教育平台近期更新了《在线教育平台隐私政策》中的相关内容。'
        },
        {
            msgName:'在线教育平台功能维护通知',
            msgDate:'2023年2月12日 6:00',
            msgBody:'亲爱的用户，根据业务开展的实际情况，在线教育平台近期更新了《在线教育平台隐私政策》中的相关内容.'
        },
        
        {
            msgName:'协议修订通知',
            msgDate:'2023年2月12日 6:00',
            msgBody:'亲爱的用户，根据业务开展的实际情况，在线教育平台近期更新了《在线教育平台隐私政策》中的相关内容。你可以前往在线教育平台【我的-设置-隐私政策-在线教育平台隐私政策全文】查看更新后的主要提示以及全部内容。'
        },
        {
            msgName:'在线教育平台重大活动通知',
            msgDate:'2023年2月12日 6:00',
            msgBody:'亲爱的用户，根据业务开展的实际情况，在线教育平台近期更新了《在线教育平台隐私政策》中的相关内容。你可以前往在线教育平台【我的-设置-隐私政策-在线教育平台隐私政策全文】查看更新后的主要提示以及全部内容。亲爱的用户，根据业务开展的实际情况，在线教育平台近期更新了《在线教育平台隐私政策》中的相关内容。你可以前往在线教育平台【我的-设置-隐私政策-在线教育平台隐私政策全文】查看更新后的主要提示以及全部内容。'
        },
    ])
</script>

<template>
    <div class="user-msg-container">
        <div class="user-msg" v-for=" msg in msgs" :key="msg.msgName">
            <h4 class="msg-title">
                &lt;{{msg.msgName}}&gt;
                <span style="
                    font-size: 14px; 
                    font-weight: normal;
                    color:#999999;
                    margin-left: 14px;
                ">
                    {{msg.msgDate}}
                </span>
            </h4>
            <p class="msg-body">{{ msg.msgBody }}</p>
        </div>
    </div>
</template>

<style scoped>
    .user-msg-container{
        
    }

    .user-msg-container .user-msg{
        border: 1px solid #ccc;
        box-shadow: var(--el-box-shadow-lighter);
        min-height: 80px;
        margin: 10px 6px ;
        border-radius: 8px;
        padding: 6px 0;
    }

    .user-msg-container .user-msg .msg-title{
        margin-left: 20px;
        padding: 10px 0;
    }

    .user-msg-container .user-msg .msg-body{
        color: #666666;
        margin-left: 40px;
        margin-right: 10px;
        line-height: 1.5em;
        text-indent: 1.5em;
        margin-bottom: 10px;
    }

</style>
<script setup>

    // import OepHeader from './OepHeader.vue';
    // import OepMain from './OepMain.vue';
    // import OepCarousel from './OepCarousel.vue';
    // import OepFooter from './OepFooter.vue';
    import OepLogin from './OepLogin.vue';
    // import MyHeader from './MyHeader.vue';

    import useUserStore from "../store/store.user"

    const userStore=useUserStore()

    
</script>

<template>
    <div style="min-width: 1300px; overflow: hidden;">
        <el-container style="margin: 0 0; min-width: 1300px;" >
            <el-header style="height: 60px;padding: 0; min-width: 1300px;">
                <oep-header></oep-header>
            </el-header>
            <el-divider style="margin: 0; height: 0.5px; margin-top: 1px;width: 100vw; min-width: 1300px; background-color: green; " />
            <el-main style="width: 100%; min-width: 1300px; overflow: hidden; min-height: 580px;">
                <router-view></router-view>
                <router-view name="main"></router-view>
            </el-main>
            <el-divider style="margin-bottom: 0; margin-top: 60px;width: 100vw; min-width: 1300px; background-color: green; overflow: visible;" />
            <el-footer style="margin: 0; padding: 0; height: 100%;  ">
                <oep-footer></oep-footer>
            </el-footer>
            <oep-login v-show="userStore.showLoginCompo"></oep-login>
        </el-container>
    </div>
</template>

<style scoped>
    :deep(.el-main){
        padding: 0 0;
    }
</style>
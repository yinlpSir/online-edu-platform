
<script setup>
    import { onMounted, ref } from "vue";
    import { useRoute, useRouter } from "vue-router";
    import {buyCourse} from '@/api/api.course.js';

    const router = useRouter()
    const route=useRoute()

    const str = ref('')

    const loading = ref(false)

    onMounted(()=>{
        loading.value = true
        buyCourse(route.query.courseId).then(res => {
            const {data} = res
            str.value = data.data
            localStorage.setItem('buyCourseId',route.query.courseId)
            setTimeout(() => {
                document.forms[0].submit();
            }, 1000);
        },err => {
            ElMessage.warning(err.response.data.message)
            router.back()
        })
    })
</script>

<template>
    <div v-html="str" v-loading.fullscreen.lock="loading"></div>
</template>

<style scoped>

</style>
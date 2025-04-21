<script setup>
    import { ref } from "vue"
    import { useRouter } from "vue-router"
    
    const router = useRouter()
    const courseId = localStorage.getItem("buyCourseId")

    const count = ref(5)

    function countdown(seconds) {
        if (seconds > 0) {
            count.value -=1
            setTimeout(function() {
                countdown(seconds - 1);
            }, 1000);
        } else {
            console.log(courseId)
            router.push({
                path:"/course/purchased",
                query:{
                    courseId:courseId,
                }
            })
        }
    }
    
    countdown(count.value)
    
</script>

<template>
    <div class="tip">
        <h1 style="text-align: center;background-color: #ccc;width: 100%;">
            购买成功！<br>
            <span style="font-size: 12px; text-align: center; width: 100%;">{{count}}s将跳转到已购页！</span>
        </h1>
    </div>
</template>

<style scoped>
    .tip{
        margin: 0 auto;
        width: 500px;
        /* padding: 1px; */
        height: 600px;
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        /* border: 1px solid #ccc; */
    }
</style>


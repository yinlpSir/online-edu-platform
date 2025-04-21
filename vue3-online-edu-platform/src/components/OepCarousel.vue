<script setup>
    import { onMounted, ref } from "vue"
    import {getCarousels} from '../api/api.course'
    import { useRouter } from 'vue-router'

    const router=useRouter()

    const carouselImgs=ref()

    onMounted(()=>{
        getCarousels().then(res=>{
            const {data} = res
            carouselImgs.value = data.data
        },err=>{

        })
    })

    const handleDoubleClick = (id) => {
        router.push({
            path:'/course/detail',
            query:{
                courseId: id
            }
        })
    }
</script>

<template>
    <div>
        <el-carousel 
            :interval="4000" 
            type="card" 
            height="350px" 
            style="min-width: 1400px; width: 1400px; overflow: visible;margin: 0 auto;margin-top: 16px;"
        >
            <el-carousel-item 
                v-for="img in carouselImgs" 
                :key="img.name" 
                :style="{ backgroundImage: `url(${img.url})` }"
                @dblclick="handleDoubleClick(img.id)"
            >
            </el-carousel-item>
        </el-carousel>
    </div>
</template>

<style scoped>
    /** 轮播图 */
    .el-carousel__item{
        border-radius: 15px;
        background-position: center;
        background-repeat: no-repeat;
        background-size: 100% 100%;
        text-align: center;
    }
    .el-carousel__item:nth-child(2n) {
        background-color: #99a9bf;
    }
    .el-carousel__item:nth-child(2n + 1) {
        background-color: #d3dce6;
    }
</style>
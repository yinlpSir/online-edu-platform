<script setup>
    import { Picture as IconPicture } from '@element-plus/icons-vue'
    import { useRouter } from 'vue-router'

    defineProps(['course'])

    const router=useRouter()

    const toCourseDetail=(courseId,isBought)=>{
        console.log(courseId)
        console.log(isBought)
        
        if(isBought){
            router.push({
                path:'/course/purchased',
                query:{
                    courseId:courseId
                }
            })
        }else {
            router.push({
                path:'/course/detail',
                query:{
                    courseId: courseId
                }
            })
        }
    }

</script>

<template>
    <div class="oep-course-card" @click="toCourseDetail(course.id,course.isBought)">
        <el-image class="oep-course-card-img" :src="course.courseCover">
            <template #error><!--slot = error -> Customize the content that failed to load-->
                <div class="image-slot">
                    <el-icon><icon-picture /></el-icon>
                </div>
            </template>
        </el-image>
        <div class="oep-course-card-content">
            <p class="oep-course-card-name">{{course.courseName}}</p>
            <div class="oep-course-card-content-foot">
                <p style="font-size: 12px; margin-top: 5px; margin-left: 4px;">{{course.startTime?course.startTime.replace('T',' '):'录播课'}} | {{course.number}}</p>
                <hr style="margin:2px 0px;" />
                <p style="font-size: 11px; margin-left: 4px;">{{course.purchaseQuantity}}人已购 <span style="font-size: 14px; display: inline-block; float: right; color: #EB512E; margin-right: 15px;">￥ {{course.price}}</span></p>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .oep-course-card{
        width: 256px;
        height: 230px;
        /* border: 1px solid black; */
        border-radius: 15px;
        display: inline-block;
        /* margin:0 5px; */
        cursor: pointer;
        /* box-shadow: var(--el-box-shadow-light); */
    }

    .oep-course-card .oep-course-card-img{
        width: 100%;
        height: 146px;
        /* border: 1px solid black; */
        border-radius: 15px;
        text-align: center;
        box-shadow: var(--el-box-shadow-light);
        border: 1px solid var(--el-card-border-color);
    }
    .oep-course-card .oep-course-card-img .image-slot{
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        background: var(--el-fill-color-light);
        color: var(--el-text-color-secondary);
        font-size: 30px;
    }

    .oep-course-card .oep-course-card-content:hover .oep-course-card-name{
        color:rgb(127,166,248) ;
    }

    .oep-course-card .oep-course-card-content .oep-course-card-name{
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
        word-break: keep-all;
        text-overflow: ellipsis;
        /* text-indent: 2em; */
        font-size: 14px;
        font-weight: bold;
        margin-left:4px;
        margin-top: 4px;
    }

    .oep-course-card .oep-course-card-content .oep-course-card-content-foot{
        color: #7FA6F8;
        /* margin: 0 5px; */
    }

    /* .oep-course-card .oep-course-card-img .el-image{
        padding: 0 5px;
  max-width: 300px;
  max-height: 200px;
  width: 100%;
  height: 200px;
    }    */

    /* .oep-course-card .oep-course-card-img{
        padding: 30px 0;
  text-align: center;
  border-right: solid 1px var(--el-border-color);
  display: inline-block;
  width: 49%;
  box-sizing: border-box;
  vertical-align: top;
    }

    .oep-course-card .oep-course-card-img .el-image{
        padding: 0 5px;
  max-width: 300px;
  max-height: 200px;
  width: 100%;
  height: 200px;
    }

    .oep-course-card .oep-course-card-img .image-slot{
        display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-secondary);
  font-size: 30px;
    }

    .oep-course-card .oep-course-card-img .image-slot .image-slot .el-icon {
  font-size: 30px;
} */
</style>
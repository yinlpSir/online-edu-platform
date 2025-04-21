<script setup>
    import { onBeforeUnmount, onMounted, ref, toRaw,reactive } from 'vue';
    import { useRouter } from 'vue-router';
    import useVideoStore from "@/store/store.video";
    import videojs from 'video.js';
    import {setVideoProcess} from '@/api/api.course.js';

    const videoStore = useVideoStore()

    const router = useRouter()

    const currentVideo = ref({
        classId:null,
        videoPath:null,
        process:null,
    })

    /**
     * 保存当前观看视频的视频进度
     * @param {*} classId 
     * @param {*} process 
     */
    const sendAjax = (classId,process)=> {
        // 存 当前视频的进度
        setVideoProcess(classId,process).then(res=>{
            // 更新 localStore 里的 video(课程列表) 中某节课的课程进度。这是把所有数据都返回来造成的后果
            const classList = videoStore.video;
            classList.forEach(e => {
                if(e.id == classId){
                    e.classProcess = process
                }
            })
            videoStore.setVideo(toRaw(classList))
        },err=>{console.log(err.response)})
    }

    const player = ref(null);

    const getPlayer = ()=> {
        return videojs("playerId",{
            fluid:true, // 播放器的的大小将不再是一个固定的值，它会根据宽高比（aspectRatio）动态计算出来，以适应其容器的大小。
            playbackRates: [0.5, 1, 1.5, 2], // 播放速度设置。由大于0的数字所组成的数组，1是正常播放，0.5是0.5倍播放，2是2倍播放，以此类推。video.js会在控制条上显示该选项，以供用户选择。按照从小到大的顺序，自底到顶显示。
        },function onPlayerReady(){
            videojs.log('player readied!')
            // initData() 比 onMounted() 先执行
            this.currentTime(currentVideo.value.process)
            
            this.on('ended',()=>{
                videojs.log("play ended!")
                sendAjax(currentVideo.value.classId,player.value.currentTime().toFixed(0))
            })
        })
    }

    const disposePlayer = (player)=>{
        if(!player.isDisposed()){
            player.dispose(); 
        }
    }

    onMounted(()=>{
        if(toRaw(videoStore.video) == null || toRaw(videoStore.video).length == 0) router.back()

        player.value = getPlayer()
    })

    onBeforeUnmount(()=>{
        sendAjax(currentVideo.value.classId,player.value.currentTime().toFixed(0))

        videoStore.clearVideo()

        disposePlayer(player.value)
    })

    const handleVideoChange=(lesson)=>{
        // 存 当前视频的进度
        sendAjax(currentVideo.value.classId,player.value.currentTime().toFixed(0))

        // 切换视频
        currentVideo.value.classId = lesson.id
        currentVideo.value.videoPath = lesson.videoPath
        currentVideo.value.process = lesson.classProcess
        // console.log(lesson.classProcess)

        player.value = getPlayer()

    }

    // init
    const initData = ()=> {
        if(videoStore.video.length !== 0) {
            currentVideo.value.classId = videoStore.video[0].id
            currentVideo.value.videoPath = videoStore.video[0].videoPath
            currentVideo.value.process = videoStore.video[0].classProcess
        }
    }

    initData()

</script>

<template>
    <div class="video-container">
        <div class="side-box">
            <el-menu
                :default-active="0"
                class="el-menu-vertical-demo"
                @open="handleOpen"
                @close="handleClose"
            >
                <el-menu-item 
                    :index="index" 
                    v-for="(lesson,index) in videoStore.video" 
                    :key="index"
                    @click="handleVideoChange(lesson)"
                >
                    <el-icon><icon-menu /></el-icon>
                    <span>{{ lesson.className }}</span>
                </el-menu-item>
            </el-menu>
        </div>
        <div class="watch">
            <!-- <video :src="currentVideo" controls style="width: 100%; height: 100%;"></video> -->
            <video 
                id="playerId" 
                class="video-js vjs-default-skin  vjs-big-play-centered" 
                playsinline
                controls 
                width="500" height="500"
                :src="currentVideo.videoPath"
            >
            </video>
        </div>
    </div>
</template>

<style scoped>
    .video-container{
        width: 1200px;
        margin: 0 auto;
        /* border: 1px solid black; */
        display: flex;
        justify-content: space-evenly;
        margin-top: 20px;
    }
    .side-box{
        width:20%;
        /* border: 1px solid black; */
        text-align: center;
        border-radius: 15px;
    }
    /* .side-item{
        padding:10px 0px ;
        cursor: pointer;
    }
    .side-item:first-of-type{
        border-top-left-radius: 15px;
        border-top-right-radius: 15px;
    }
    .side-item:last-of-type{
        border-bottom-left-radius: 15px;
        border-bottom-right-radius: 15px;
    }
    .side-item:hover{
        background-color: #ccc;
    } */
    :deep(.el-menu){
        border-right: none;
    }
    :deep(.el-menu-item){
        border-radius: 40px;
    }
    :deep(.el-menu-item.is-active){
        background-color: var(--el-menu-border-color);
        color: rgb(127,166,248);
        font-weight: bold;
    }
    .watch{
        /* border: 1px solid black; */
        width: 75%;
        /* height: 500px; */
    }
</style>


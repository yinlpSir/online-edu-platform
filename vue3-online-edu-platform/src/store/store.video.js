import { defineStore } from "pinia";
import { ref,reactive } from "vue";

// course store
export default defineStore('video',()=>{
    const video = ref(JSON.parse(localStorage.getItem('videos')))

    const setVideo = (data)=>{
        video.value = data
        localStorage.setItem('videos',JSON.stringify(data))
    }

    const getVideo = ()=>{
        video.value = JSON.parse(localStorage.getItem('videos'))
    }

    const clearVideo = ()=>{
        video.value = []
        localStorage.removeItem('videos')
    }

    return {
        video,
        setVideo,
        getVideo,
        clearVideo,
    }
})
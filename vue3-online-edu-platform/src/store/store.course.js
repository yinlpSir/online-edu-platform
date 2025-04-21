import { defineStore } from "pinia";
import { ref,reactive } from "vue";

// course store
export default defineStore('course',()=>{

    // #region 添加课程相关
    const courseForm=ref(undefined) // 课程形式 (record / live)

    const courseBasicInfo = reactive({
        courseName:"",
        description:"",
        courseCover:"",
        price: 0.0,
        grade: [],
        number: 0, // 课时数
        subject: "", // 科目
        isVideoDraggable:"", // 视频是否可拖动
    })

    const lessonOrder = ref(0) // 课的序号

    const lessons = ref([])

    const courseCoverUrl = ref(undefined)

    const clearAddCourseInfo = ()=>{
        courseForm.value = undefined
        Object.assign(courseBasicInfo,{
            courseName:"",
            description:"",
            courseCover:"",
            price: 0.0,
            grade: [],
            number: 0, // 课时数
            subject: "", // 科目
            isVideoDraggable:"", // 视频是否可拖动
        })
        lessonOrder.value = 0
        lessons.value = []
        courseCoverUrl.value = undefined
    }
    // #endregion

    // #region 主页数据
    const recommendationCourses=ref([]) // 推荐课程

    const choicenessEnglish=ref([]) // 精选英语

    const otherCourses = ref([]) // 其他课程
    const commonCourses = reactive({
        "精选英语":[],
        "其他课程":[]
    })
    // #endregion

    return{
        recommendationCourses,
        choicenessEnglish,
        otherCourses,
        commonCourses,
        courseForm,
        courseBasicInfo,
        lessons,
        lessonOrder,
        courseCoverUrl,
        clearAddCourseInfo,
    }
})
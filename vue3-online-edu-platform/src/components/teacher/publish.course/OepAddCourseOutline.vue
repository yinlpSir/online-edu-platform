<script setup>
    import useCourseStore from "../../../store/store.course"
    import {reactive, ref,toRaw,toRef} from "vue"
    import { ElMessage,genFileId } from "element-plus"

    const courseStore = useCourseStore()

    const {courseBasicInfo,lessons} = courseStore

    const classNameInputRef = ref()

    const dialogVisible = ref(false)

    // 模态框关闭的钩子
    const handleClose = (done) => {
        // ElMessageBox.confirm('Are you sure to close this dialog?')
        //     .then(() => {
        //     done()
        //     })
        //     .catch(() => {
        //     // catch error
        //     })
        done()
    }

    /**
     * 浏览器自带的更新提示：浏览器会在页面更新时自动弹出更新提示框，提示用户是否刷新页面。
     */
    window.addEventListener('beforeunload', function (e) {
        e.preventDefault();
        e.returnValue = '';
    });
    
    //#region 上传视频相关
    let currentUploadLesson = null // 当前要上传视频的课

    const uploadRef = ref()

    const fileList = ref([])

    /**
     *  点击上传视频执行的函数
     * @param {*} lesson 当前要上传视频的课
     */
    const uploadVideo = (lesson)=>{
        dialogVisible.value = true

        fileList.value.splice(0,fileList.value.length) // 清空文件列表

        if(lesson.video instanceof File){
            fileList.value.push(lesson.video)
        }

        currentUploadLesson = lesson // 引用
    }

    //当超出限制时，执行的钩子
    const handleExceed = (files,uploadFiles,ref)=>{
        const file = files[0]
        if(file.type !== 'video/mp4'){
            ElMessage.error('仅支持mp4格式!')
            return 
        }

        // 当文件超过limit时，覆盖前一个文件的操作。这里执行后会改变文件的状态，所以就会执行 handleUploadChange 这个函数
        ref.clearFiles() // 清空 el-upload 中的文件
        file.uid = genFileId()
        ref.handleStart(file)
    }

    // 文件状态改变时的钩子，添加文件、上传成功和上传失败时都会被调用。
    const handleUploadChange = (uploadFile,uploadFiles,ref) => {
        console.log("handleUploadChange")
        if(uploadFile.raw.type == 'video/mp4'){
            currentUploadLesson.video = uploadFile.raw
            return 
        }
        
        ElMessage.error('仅支持mp4格式!')
        ref.handleRemove(uploadFile.raw)
        ref.clearFiles()
    }

    /**
     * 删除文件之前的钩子，参数为上传的文件和文件列表， 若返回 false 或者返回 Promise 且被 reject，则停止删除。
     * @param {*} uploadFile 一个File对象，它是被删除的文件
     */
    const handleFileRemove =(uploadFile)=>{
        currentUploadLesson.video = null
    }

    //#endregion

    // #region 日期相关

    const handleStartTimeChange=()=>{
        if(Date.parse(currentUploadLesson.startTime) < Date.now()){
            ElMessage.error('开播时间不能是过去时间!')
            currentUploadLesson.startTime = null
        }
    }

    const handleEndTimeChange=()=>{
        if(Date.parse(currentUploadLesson.endTime) < Date.parse(currentUploadLesson.startTime)){
            ElMessage.error('关播时间不能小于开播时间!')
            currentUploadLesson.endTime = null
        }
    }

    // #endregion

    // #region lesson 相关
    const handleAddLesson = () =>{
        courseBasicInfo.number++
        courseStore.lessonOrder++
        lessons.push({
            className: "",
            number: 0, // 课程序号
            video: null, // 课程视频
            startTime: "",
            endTime: "",
            courseId: 0,
            isShow:true, // 发请求时将此 key 排除
        })
        lessons[courseStore.lessonOrder-1].number = courseStore.lessonOrder
    }

    const handleModifyClassName = (e,num) => {
        lessons[num].isShow = true

        // 得异步，不然会先获取 display=none 的 input框
        setTimeout(()=>{
            e[`${num}`].focus()
        },10) 
    }
    
    const handleInputBlur = (lesson) => {
        if (lesson.className != null && lesson.className != undefined && lesson.className.trim() != '') {
            lesson.isShow = false
            return ;
        }
        ElMessage({
            type:'warning',
            grouping:true,
            message:'名称不能为空！'
        })
    }

    const handleMoveLesson = (index) => {
        courseBasicInfo.number--
        courseStore.lessonOrder--
        lessons.splice(index,1)
        lessons.forEach( (lesson,index) => lesson.number = index+1)
    }
    // #endregion

</script>

<template>
    <div class="outline">
        <div>
            总课时数：{{courseBasicInfo.number}}
            <el-button style="margin-left: 10px;" type="primary" @click="handleAddLesson">添加课</el-button>
        </div>
        <div style="height: 300px; display: flex; align-items: center; justify-content: center;" v-show="courseBasicInfo.number > 0?false:true">
            <div style="color: #ccc;">暂无小课</div>
        </div>
        <div class="lesson-container" ref="lessonContainerRef" v-show="courseBasicInfo.number">
            <div class="lesson" v-for="(lesson,ind) in lessons" :key="ind">
                <div style="font-size: 16px;">
                    <div class="lesson-order">{{lesson.number}}</div>
                    <span v-show="!lesson.isShow" style="margin-left: 14px;">{{lesson.className}}</span>
                    <input 
                        ref="classNameInputRef"
                        id="class-name-input"
                        v-model="lesson.className" 
                        v-show="lesson.isShow" 
                        @blur="handleInputBlur(lesson)" 
                    />
                </div>
                <div>
                    <el-button type="primary" plain link @click="handleModifyClassName(classNameInputRef,ind)">修改名字</el-button>
                    <!-- <button @click="console.log(lesson);console.log(courseStore.courseForm)">查看</button> -->
                    <el-button type="primary" plain link @click="uploadVideo(lesson)" v-show="courseStore.courseForm === 'live'?true:false">开课日期</el-button>
                    <el-button type="primary" plain link @click="uploadVideo(lesson)" v-show="courseStore.courseForm == 'record'">上传视频</el-button>
                    <el-button type="primary" plain link @click="handleMoveLesson(ind)">删除</el-button>
                </div>
            </div>
        </div>
        <div>
            <el-dialog
                v-model="dialogVisible"
                style="min-width: 300px;"
                width="30%"
                :before-close="handleClose"
            >
                <div class="date" v-show="courseStore.courseForm === 'live'?true:false">
                    <div style=" margin-bottom: 6px; display: flex; flex-wrap: wrap;">
                        <p style="width: 100%; margin-bottom: 8px;">直播开始时间：</p>
                        <el-date-picker
                            style="min-width: 260px; margin: 0 auto;"
                            v-model="currentUploadLesson.startTime"
                            type="datetime"
                            placeholder="请选择开始时间"
                            @change="handleStartTimeChange"
                        />
                    </div>
                    <div style="display: flex; flex-wrap: wrap;">
                        <p style="width: 100%; margin-bottom: 8px;">直播结束时间：</p>
                        <el-date-picker
                            style="min-width: 260px; margin: 0 auto;"
                            v-model="currentUploadLesson.endTime"
                            type="datetime"
                            placeholder="请选择结束时间"
                            :disabled="currentUploadLesson.startTime === '' || currentUploadLesson.startTime === null ? true:false"
                            @change="handleEndTimeChange"
                        />
                    </div>
                </div>
                
                <div v-show="courseStore.courseForm == 'record'">
                    <p style="margin-bottom: 8px;">上传视频：</p>
                    <!-- 
                        注意：
                            1、auto-upload 为 false 时，before-upload 不生效 
                    -->
                    <el-upload
                        class="upload"
                        ref="uploadRef"
                        v-model:file-list="fileList"
                        :limit="1"
                        :on-exceed="(files,uploadFiles)=>handleExceed(files,uploadFiles,uploadRef)"
                        :auto-upload="false"
                        :before-remove="handleFileRemove"
                        :on-change="(uploadFile,uploadFiles)=>handleUploadChange(uploadFile,uploadFiles,uploadRef)"
                    >
                        <template #trigger>
                            <el-button type="primary" style="min-width: 260px;">点击上传视频</el-button>
                        </template>
                        <template #tip>
                            <div class="el-upload__tip">
                                注意:视频格式仅限 mp4 格式.
                            </div>
                        </template>
                    </el-upload>
                </div>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="dialogVisible = false">
                            确认
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<style scoped>
    .outline{
        /* border: 1px solid black; */
        margin: 0 10px;
    }
    .lesson-container{
        min-height: 300px;
    }
    .lesson{
        height: 30px;
        /* border: 1px solid black; */
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 6px 10px;
        margin: 8px 0;
        border-radius: 8px;
        background-color: #F1F1F1;
        
    }
    .lesson .lesson-order{
        width: 25px;
        height: 25px; 
        border: 1px solid skyblue; 
        border-radius: 50%; 
        /* margin-left: 30px; */
        text-align: center;
        line-height: 25px;
        display: inline-block;
    }
    #class-name-input{
        margin-left: 14px;
        height: 20px;
    }
    /** 上传视频相关 */
    .upload{
        min-width: 260px;
        margin: 0 auto;
        text-align: center;
    }
    :deep(.upload .el-button>span){
        width: auto;
    }
    /** 选择直播的日期相关 */
    .date{
        min-width: 260px;
        margin: 0 auto;
        margin-bottom: 8px;
    }
</style>
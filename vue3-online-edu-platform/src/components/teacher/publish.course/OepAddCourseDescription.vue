<script setup>
    import useCourseStore from "../../../store/store.course"
    import Editor from '@tinymce/tinymce-vue' // npm install --save "@tinymce/tinymce-vue@^5"

    const courseStore = useCourseStore()

    const tinymceApiKey = "nzn8ixegx380zflxo87z4a8tkqjatd46nti2mcb916datx2g"
    
    const tinymcePlugins = 'codesample link image lists advlist help emoticons table preview fullscreen' // powerpaste

    const tinymceToolbar = [
        `undo redo | styles formatselect fontselect fontsizeselect | bold italic underline strikethrough subscript superscript removeformat blockquote | outdent indent emoticons`,
        `codesample link image preview fullscreen| alignleft aligncenter alignright alignjustify | bullist numlist table`,
    ]

    const tinyInit = {
        language_url:'/public/tinymce/langs/zh-CN.js', // 指定语言包文件。官网下的语言包：https://www.tiny.cloud/get-tiny/language-packages/
        language:'zh-Hans', //设置语种。 tinymce.addI18n("zh-Hans",{...})
        height:'600', // 设置整个Tinymce这个组件的高度
        width:'100%', // 设置整个Tinymce这个组件的宽度
        plugins:tinymcePlugins, // 插件设置
        menubar:'file edit view insert format tools table commonMenu help', // 菜单栏
        menu:{
            tools: { title: 'Tools', items: 'spellchecker spellcheckerlanguage | a11ycheck code wordcount' },
            commonMenu:{ // 自定义菜单栏
                title:'常用', // 菜单标题
                items:'selectall image link hr' // 自定义菜单项
            }
        },
        toolbar:tinymceToolbar, // 工具栏
        automatic_uploads:false,
        paste_data_images:true, // 开启 粘贴图像，并会自动调用 images_upload_handler 钩子。可实现粘贴图片后自动上传图片操作
        images_upload_handler:function(blobInfo,successCallback,failureCallback){// 自定义函数代替TinyMCE来处理上传图片操作.(只要涉及上传图片就会触发方法)
            const file = blobInfo.blob() // 上传的文件对象
            console.log(file)
        },
        images_reuse_filename:true,// TinyMCE默认会给每个上传的文件生成唯一的文件名。将该配置设为true则可以避免此问题，它将告诉TinyMCE使用图片文件实际的文件名，而不是每次随即生成一个新的。
        file_picker_types: 'image', // 设置 文件选择器 的文件上传类型。允许的值包括：file,image和media
        browser_spellcheck: true, // 拼写检查
        branding: false, // 去水印
        elementpath: false, //禁用编辑器底部的状态栏
        smart_paste:true, // 开启智能粘贴
    }

</script>

<template>
    <div style="margin: 0 10px;">
        <Editor  
            v-model="courseStore.courseBasicInfo.description"
            :api-key="tinymceApiKey"
            :init="tinyInit"
        />
    </div>
</template>

<style>
    /**Editor相关的 */
    .tox-promotion{
        display: none;
    }
</style>

<style scoped>

</style>
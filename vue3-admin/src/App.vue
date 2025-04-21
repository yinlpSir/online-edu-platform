<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style scoped>
  .app{
    margin: 0 auto; 
    width: 100vw; 
    height: 100vh; 
  }
</style>

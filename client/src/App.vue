<script setup lang="ts">
import GlobalLoading from '@/shared/components/GlobalLoading.vue';
import ToastContainer from '@/shared/components/ToastContainer.vue';
</script>

<template>
    <GlobalLoading />
    <ToastContainer />
    <router-view />
</template>

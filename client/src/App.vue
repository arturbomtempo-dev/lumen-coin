<script setup lang="ts">
import { Toaster } from 'vue-sonner';
import GlobalLoading from '@/shared/components/GlobalLoading.vue';
</script>

<template>
    <GlobalLoading />
    <Toaster position="top-right" rich-colors />
    <router-view />
</template>

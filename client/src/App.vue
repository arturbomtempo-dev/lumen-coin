<script setup lang="ts">
import GlobalLoading from '@/shared/components/GlobalLoading.vue';
import { useThemeStore } from '@/shared/stores/theme.store';
import { Toaster } from 'vue-sonner';
import 'vue-sonner/style.css';

const themeStore = useThemeStore();
</script>

<template>
    <GlobalLoading />
    <Toaster
        position="top-center"
        rich-colors
        :theme="themeStore.theme === 'night' ? 'dark' : 'light'"
    />
    <router-view />
</template>

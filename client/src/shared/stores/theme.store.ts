import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

type Theme = 'day' | 'night';

export const useThemeStore = defineStore('theme', () => {
    const theme = ref<Theme>((localStorage.getItem('lumen-theme') as Theme) || 'day');

    watch(
        theme,
        (value) => {
            const root = document.documentElement;
            root.classList.add('theme-transition');
            root.classList.toggle('dark', value === 'night');
            localStorage.setItem('lumen-theme', value);
            setTimeout(() => root.classList.remove('theme-transition'), 260);
        },
        { immediate: true }
    );

    function toggle() {
        theme.value = theme.value === 'day' ? 'night' : 'day';
    }

    return { theme, toggle };
});

<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    hair: '#F0C040',
    skin: '#FBD9B5',
    shirt: '#FF8AC2',
    shoe: '#C03070',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="peach"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="4" y="0" width="1" height="1" fill="#F8D000" />
        <rect x="6" y="0" width="1" height="1" fill="#F8D000" />
        <rect x="8" y="0" width="1" height="1" fill="#F8D000" />
        <rect x="4" y="1" width="5" height="1" fill="#F8D000" />
        <rect x="3" y="2" width="6" height="1" :fill="palette.hair" />
        <rect x="2" y="3" width="2" height="4" :fill="palette.hair" />
        <rect x="8" y="3" width="2" height="4" :fill="palette.hair" />
        <rect x="2" y="7" width="1" height="1" :fill="palette.hair" />
        <rect x="9" y="7" width="1" height="1" :fill="palette.hair" />
        <rect x="4" y="3" width="4" height="3" :fill="palette.skin" />
        <rect x="3" y="4" width="1" height="2" :fill="palette.skin" />
        <rect x="8" y="4" width="1" height="2" :fill="palette.skin" />
        <rect x="4" y="4" width="1" height="2" fill="hsl(var(--border))" />
        <rect x="7" y="4" width="1" height="2" fill="hsl(var(--border))" />
        <rect x="5" y="6" width="2" height="1" fill="#C03070" />
        <rect x="2" y="5" width="1" height="1" fill="#F8D000" />
        <rect x="9" y="5" width="1" height="1" fill="#F8D000" />
        <rect x="3" y="7" width="6" height="1" :fill="palette.shirt" />
        <rect x="2" y="8" width="1" height="1" :fill="palette.shirt" />
        <rect x="9" y="8" width="1" height="1" :fill="palette.shirt" />
        <rect x="3" y="8" width="6" height="1" :fill="palette.shirt" />
        <rect x="2" y="9" width="8" height="1" :fill="palette.shirt" />
        <rect x="1" y="10" width="10" height="1" :fill="palette.shirt" />
        <rect x="5" y="7" width="2" height="1" fill="#1F4FD8" />
        <rect x="3" y="11" width="2" height="1" :fill="palette.shoe" />
        <rect x="7" y="11" width="2" height="1" :fill="palette.shoe" />
    </svg>
</template>

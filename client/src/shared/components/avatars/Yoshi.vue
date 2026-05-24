<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    hat: '#39B54A',
    skin: '#FBD9B5',
    overall: '#FFFFFF',
    shoe: '#E2622E',
    spot: '#1F4FD8',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="yoshi"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="2" y="1" width="8" height="4" :fill="palette.hat" />
        <rect x="3" y="0" width="2" height="1" :fill="palette.hat" />
        <rect x="4" y="2" width="2" height="2" fill="#FFFFFF" />
        <rect x="7" y="2" width="2" height="2" fill="#FFFFFF" />
        <rect x="5" y="3" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="8" y="3" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="2" y="4" width="3" height="2" :fill="palette.skin" />
        <rect x="3" y="6" width="2" height="1" :fill="palette.skin" />
        <rect x="3" y="6" width="6" height="3" :fill="palette.hat" />
        <rect x="4" y="7" width="4" height="2" :fill="palette.overall" />
        <rect x="3" y="9" width="3" height="2" :fill="palette.shoe" />
        <rect x="6" y="9" width="3" height="2" :fill="palette.shoe" />
        <rect x="9" y="5" width="1" height="1" :fill="palette.spot" />
        <rect x="10" y="6" width="1" height="1" :fill="palette.spot" />
    </svg>
</template>

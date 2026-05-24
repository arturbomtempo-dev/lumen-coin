<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    hat: '#E52521',
    shirt: '#E52521',
    overall: '#1F4FD8',
    skin: '#FBC8A0',
    hair: '#3A1F0B',
    shoe: '#5B2B0E',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="mario"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="3" y="0" width="6" height="1" :fill="palette.hat" />
        <rect x="2" y="1" width="2" height="1" :fill="palette.hat" />
        <rect x="4" y="1" width="5" height="1" :fill="palette.hat" />
        <rect x="5" y="2" width="2" height="1" fill="#FFFFFF" />
        <rect x="2" y="2" width="2" height="2" :fill="palette.hair" />
        <rect x="8" y="2" width="2" height="2" :fill="palette.hair" />
        <rect x="4" y="2" width="1" height="1" :fill="palette.skin" />
        <rect x="7" y="2" width="1" height="1" :fill="palette.skin" />
        <rect x="3" y="3" width="6" height="3" :fill="palette.skin" />
        <rect x="4" y="4" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="7" y="4" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="3" y="5" width="6" height="1" :fill="palette.hair" />
        <rect x="5" y="5" width="2" height="1" fill="#D06060" />
        <rect x="2" y="6" width="8" height="2" :fill="palette.shirt" />
        <rect x="3" y="8" width="6" height="3" :fill="palette.overall" />
        <rect x="5" y="8" width="2" height="1" :fill="palette.shirt" />
        <rect x="4" y="9" width="1" height="1" fill="#F8D000" />
        <rect x="7" y="9" width="1" height="1" fill="#F8D000" />
        <rect x="2" y="11" width="3" height="1" :fill="palette.shoe" />
        <rect x="7" y="11" width="3" height="1" :fill="palette.shoe" />
    </svg>
</template>

<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    hat: '#39B54A',
    shirt: '#F2A93B',
    skin: '#F2A93B',
    hair: '#E52521',
    shoe: '#1F1F1F',
    spot: '#FFFFFF',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="bowser"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="2" y="0" width="2" height="2" :fill="palette.spot" />
        <rect x="8" y="0" width="2" height="2" :fill="palette.spot" />
        <rect x="4" y="0" width="4" height="1" :fill="palette.hair" />
        <rect x="3" y="1" width="6" height="4" :fill="palette.hat" />
        <rect x="4" y="3" width="4" height="2" :fill="palette.skin" />
        <rect x="4" y="2" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="7" y="2" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="2" y="5" width="8" height="5" :fill="palette.shirt" />
        <rect x="3" y="6" width="6" height="3" :fill="palette.hat" />
        <rect x="5" y="7" width="2" height="1" :fill="palette.spot" />
        <rect x="2" y="10" width="3" height="2" :fill="palette.shoe" />
        <rect x="7" y="10" width="3" height="2" :fill="palette.shoe" />
    </svg>
</template>

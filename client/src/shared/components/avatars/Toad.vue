<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    hat: '#FFFFFF',
    skin: '#FBE2C0',
    shirt: '#FFFFFF',
    overall: '#1F4FD8',
    shoe: '#5B2B0E',
    spot: '#E52521',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="toad"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="2" y="0" width="8" height="3" :fill="palette.hat" />
        <rect x="1" y="1" width="1" height="2" :fill="palette.hat" />
        <rect x="10" y="1" width="1" height="2" :fill="palette.hat" />
        <rect x="3" y="1" width="2" height="1" :fill="palette.spot" />
        <rect x="7" y="1" width="2" height="1" :fill="palette.spot" />
        <rect x="5" y="2" width="2" height="1" :fill="palette.spot" />
        <rect x="3" y="3" width="6" height="3" :fill="palette.skin" />
        <rect x="4" y="4" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="7" y="4" width="1" height="1" fill="hsl(var(--border))" />
        <rect x="5" y="5" width="2" height="1" fill="#D04060" />
        <rect x="3" y="6" width="6" height="3" :fill="palette.shirt" />
        <rect x="4" y="7" width="4" height="1" :fill="palette.overall" />
        <rect x="3" y="9" width="3" height="2" :fill="palette.overall" />
        <rect x="6" y="9" width="3" height="2" :fill="palette.overall" />
        <rect x="3" y="11" width="3" height="1" :fill="palette.shoe" />
        <rect x="6" y="11" width="3" height="1" :fill="palette.shoe" />
    </svg>
</template>

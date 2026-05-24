<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    roof: '#2563EB',
    wall: '#1D4ED8',
    floor: '#E5E7EB',
    door: '#111827',
    window: '#F8FAFC',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="company"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="2" y="0" width="8" height="2" :fill="palette.roof" />
        <rect x="2" y="2" width="8" height="8" :fill="palette.wall" />
        <rect x="5" y="7" width="2" height="3" :fill="palette.door" />
        <rect x="3" y="3" width="1" height="1" :fill="palette.window" />
        <rect x="5" y="3" width="1" height="1" :fill="palette.window" />
        <rect x="7" y="3" width="1" height="1" :fill="palette.window" />
        <rect x="3" y="5" width="1" height="1" :fill="palette.window" />
        <rect x="5" y="5" width="1" height="1" :fill="palette.window" />
        <rect x="7" y="5" width="1" height="1" :fill="palette.window" />
        <rect x="1" y="10" width="10" height="1" :fill="palette.floor" />
        <rect x="0" y="11" width="12" height="1" :fill="palette.door" />
    </svg>
</template>

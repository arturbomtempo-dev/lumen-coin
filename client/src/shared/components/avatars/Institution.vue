<script setup lang="ts">
withDefaults(defineProps<{ size?: number }>(), { size: 64 });

const palette = {
    frame: '#6B3F1F',
    building: '#1E4D32',
    base: '#B68252',
};
</script>

<template>
    <svg
        :width="size"
        :height="size"
        viewBox="0 0 12 12"
        shape-rendering="crispEdges"
        :style="{ imageRendering: 'pixelated' }"
        aria-label="institution"
    >
        <rect x="0" y="0" width="12" height="12" fill="transparent" />
        <rect x="1" y="1" width="10" height="9" :fill="palette.frame" />
        <rect x="2" y="2" width="8" height="7" :fill="palette.building" />
        <rect x="2" y="9" width="8" height="1" :fill="palette.base" />
        <rect x="0" y="11" width="12" height="1" :fill="palette.base" />
        <text
            x="6"
            y="6"
            text-anchor="middle"
            dominant-baseline="middle"
            font-size="3"
            font-weight="bold"
            fill="#FFFFFF"
            style="font-family: monospace"
        >
            A+
        </text>
    </svg>
</template>

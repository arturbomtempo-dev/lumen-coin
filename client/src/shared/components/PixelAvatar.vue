<script setup lang="ts">
withDefaults(defineProps<{
  size?: number
}>(), {
  size: 48,
})
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 10 10"
    shape-rendering="crispEdges"
    aria-hidden="true"
  >
    <rect x="2" y="0" width="6" height="1" fill="hsl(var(--secondary))" />
    <rect x="1" y="1" width="8" height="1" fill="hsl(var(--secondary))" />
    <rect x="2" y="2" width="6" height="4" fill="hsl(35 80% 75%)" />
    <rect x="3" y="3" width="1" height="1" fill="hsl(var(--border))" />
    <rect x="6" y="3" width="1" height="1" fill="hsl(var(--border))" />
    <rect x="3" y="5" width="4" height="1" fill="hsl(var(--border))" />
    <rect x="2" y="6" width="6" height="3" fill="hsl(var(--info))" />
    <rect x="4" y="7" width="2" height="1" fill="hsl(var(--primary))" />
    <rect x="1" y="0" width="1" height="2" fill="hsl(var(--border))" />
    <rect x="8" y="0" width="1" height="2" fill="hsl(var(--border))" />
    <rect x="0" y="1" width="1" height="8" fill="hsl(var(--border))" />
    <rect x="9" y="1" width="1" height="8" fill="hsl(var(--border))" />
    <rect x="1" y="9" width="8" height="1" fill="hsl(var(--border))" />
  </svg>
</template>

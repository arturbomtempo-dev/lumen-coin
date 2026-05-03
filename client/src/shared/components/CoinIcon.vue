<script setup lang="ts">
withDefaults(defineProps<{
  size?: number
  class?: string
}>(), {
  size: 16,
})
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 8 8"
    shape-rendering="crispEdges"
    :class="['inline-block align-[-2px]', $attrs.class]"
    aria-hidden="true"
    v-bind="{ ...$attrs, class: undefined }"
  >
    <rect x="2" y="0" width="4" height="1" fill="hsl(var(--border))" />
    <rect x="1" y="1" width="1" height="1" fill="hsl(var(--border))" />
    <rect x="6" y="1" width="1" height="1" fill="hsl(var(--border))" />
    <rect x="0" y="2" width="1" height="4" fill="hsl(var(--border))" />
    <rect x="7" y="2" width="1" height="4" fill="hsl(var(--border))" />
    <rect x="1" y="6" width="1" height="1" fill="hsl(var(--border))" />
    <rect x="6" y="6" width="1" height="1" fill="hsl(var(--border))" />
    <rect x="2" y="7" width="4" height="1" fill="hsl(var(--border))" />
    <rect x="2" y="1" width="4" height="6" fill="hsl(var(--primary))" />
    <rect x="1" y="2" width="6" height="4" fill="hsl(var(--primary))" />
    <rect x="2" y="2" width="1" height="3" fill="hsl(50 100% 80%)" />
    <rect x="4" y="2" width="1" height="3" fill="hsl(var(--primary-shadow))" />
    <rect x="4" y="4" width="2" height="1" fill="hsl(var(--primary-shadow))" />
  </svg>
</template>

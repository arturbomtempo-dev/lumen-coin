<script setup lang="ts">
withDefaults(defineProps<{
  value: number
  label?: string
}>(), {
  label: undefined,
})
</script>

<template>
  <div>
    <div v-if="label" class="flex items-center justify-between font-pixel text-[9px] mb-1">
      <span>{{ label }}</span>
      <span>{{ value }}/100 XP</span>
    </div>
    <div class="relative h-4 border-2 border-border bg-card overflow-hidden">
      <div
        class="h-full bg-success"
        :style="{
          width: `${value}%`,
          backgroundImage: 'repeating-linear-gradient(90deg, hsl(var(--success)) 0, hsl(var(--success)) 6px, hsl(var(--success-shadow)) 6px, hsl(var(--success-shadow)) 8px)',
        }"
      />
    </div>
  </div>
</template>

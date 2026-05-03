<script setup lang="ts">
type Tone = 'gold' | 'red' | 'green' | 'blue' | 'purple' | 'teal';

withDefaults(
    defineProps<{
        tone?: Tone;
    }>(),
    {
        tone: 'gold',
    }
);

const toneClass: Record<Tone, string> = {
    gold: 'bg-primary text-primary-foreground',
    red: 'bg-secondary text-secondary-foreground',
    green: 'bg-success text-success-foreground',
    blue: 'bg-info text-info-foreground',
    purple: 'bg-accent text-accent-foreground',
    teal: 'bg-accent text-accent-foreground',
};
</script>

<template>
    <span
        :class="[
            'inline-flex items-center gap-1 px-2 py-1 font-pixel text-[9px] uppercase border-2 border-border',
            'shadow-[2px_2px_0_0_hsl(var(--border))]',
            toneClass[tone],
        ]"
    >
        <slot />
    </span>
</template>

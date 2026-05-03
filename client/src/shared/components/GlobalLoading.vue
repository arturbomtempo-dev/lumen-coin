<script setup lang="ts">
import { useUIStore } from '@/shared/stores/ui.store';

const ui = useUIStore();
</script>

<template>
    <div
        v-if="ui.isLoading"
        class="fixed top-0 left-0 right-0 h-0.5 z-50 overflow-hidden"
        style="background-color: var(--color-surface)"
    >
        <div
            class="h-full w-1/3 animate-pulse rounded-full"
            style="background-color: var(--color-primary)"
        />
    </div>
</template>

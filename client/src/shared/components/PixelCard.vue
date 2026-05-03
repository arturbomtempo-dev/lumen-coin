<script setup lang="ts">
defineProps<{
    class?: string;
}>();
</script>

<template>
    <div
        :class="[
            'relative bg-card text-card-foreground border-2 border-border pixel-shadow',
            $attrs.class,
        ]"
        v-bind="$attrs"
    >
        <slot />
    </div>
</template>

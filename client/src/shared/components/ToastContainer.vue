<script setup lang="ts">
import { useToastStore } from '@/shared/stores/toast.store';

const toast = useToastStore();

const colorMap = {
    success: 'var(--color-success)',
    error: 'var(--color-danger)',
    warning: 'var(--color-warning)',
    info: 'var(--color-info)',
};
</script>

<template>
    <div class="fixed bottom-4 right-4 z-50 flex flex-col gap-2 min-w-72">
        <transition-group name="toast">
            <div
                v-for="t in toast.toasts"
                :key="t.id"
                class="flex items-start gap-3 px-4 py-3 text-sm font-medium text-white"
                :style="{
                    backgroundColor: colorMap[t.type],
                    borderRadius: 'var(--radius)',
                    boxShadow: 'var(--shadow-lg)',
                }"
            >
                <span class="flex-1">{{ t.message }}</span>
                <button
                    class="text-white/70 hover:text-white shrink-0"
                    @click="toast.removeToast(t.id)"
                >
                    ✕
                </button>
            </div>
        </transition-group>
    </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
    transition: all 0.3s ease;
}

.toast-enter-from {
    opacity: 0;
    transform: translateY(12px);
}

.toast-leave-to {
    opacity: 0;
    transform: translateX(100%);
}
</style>

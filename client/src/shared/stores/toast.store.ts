import { defineStore } from 'pinia';
import { ref } from 'vue';

type ToastType = 'success' | 'error' | 'info' | 'warning';

type Toast = {
    id: number;
    message: string;
    type: ToastType;
};

export const useToastStore = defineStore('toast', () => {
    const toasts = ref<Toast[]>([]);

    function addToast(message: string, type: ToastType = 'error') {
        const id = Date.now();

        toasts.value.push({ id, message, type });

        setTimeout(() => {
            removeToast(id);
        }, 3000);
    }

    function removeToast(id: number) {
        toasts.value = toasts.value.filter((t) => t.id !== id);
    }

    return { toasts, addToast, removeToast };
});

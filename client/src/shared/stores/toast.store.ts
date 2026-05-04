import { defineStore } from 'pinia';
import { toast } from 'vue-sonner';

export const useToastStore = defineStore('toast', () => {
    function success(message: string, description?: string) {
        toast.success(message, description ? { description } : undefined);
    }

    function error(message: string) {
        toast.error(message);
    }

    return { success, error };
});

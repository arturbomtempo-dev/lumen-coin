import router from '@/app/router';
import { useToastStore } from '@/shared/stores/toast.store';
import { useUIStore } from '@/shared/stores/ui.store';
import axios from 'axios';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials: true,
});

api.interceptors.request.use((config) => {
    const ui = useUIStore();
    ui.startLoading();
    return config;
});

api.interceptors.response.use(
    (response) => {
        const ui = useUIStore();
        ui.stopLoading();
        return response;
    },
    (error) => {
        const ui = useUIStore();
        const toast = useToastStore();

        ui.stopLoading();

        const message = error.response?.data?.message || 'Unexpected error occurred';
        toast.addToast(message, 'error');

        if (error.response?.status === 401) {
            router.push('/login');
        }

        return Promise.reject(error);
    }
);

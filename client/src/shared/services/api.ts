import router from '@/app/router';
import { useUIStore } from '@/shared/stores/ui.store';
import axios from 'axios';
import { toast } from 'vue-sonner';

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

        ui.stopLoading();

        const message = error.response?.data?.message || 'Unexpected error occurred';
        toast.error(message);

        if (error.response?.status === 401) {
            router.push('/login');
        }

        return Promise.reject(error);
    }
);

import router from '@/app/router';
import { useUIStore } from '@/shared/stores/ui.store';
import type { AxiosError } from 'axios';
import axios from 'axios';
import { toast } from 'vue-sonner';

declare module 'axios' {
    interface AxiosRequestConfig {
        skipGlobalErrorToast?: boolean;
    }
}

function getErrorMessageFromPayload(payload: unknown): string | null {
    if (typeof payload === 'string') {
        const message = payload.trim();
        return message.length > 0 && message !== '[object Object]' ? message : null;
    }

    if (payload == null || typeof payload !== 'object') {
        return null;
    }

    const data = payload as Record<string, unknown>;
    const priorityKeys = ['message', 'error', 'detail', 'title'];

    for (const key of priorityKeys) {
        const value = data[key];
        if (typeof value === 'string' && value.trim().length > 0) {
            return value.trim();
        }
    }

    const errors = data.errors;
    if (Array.isArray(errors)) {
        for (const item of errors) {
            const message = getErrorMessageFromPayload(item);
            if (message) return message;
        }
    }

    return null;
}

function getApiErrorMessage(error: AxiosError<unknown>): string {
    const payloadMessage = getErrorMessageFromPayload(error.response?.data);
    if (payloadMessage) return payloadMessage;

    const status = error.response?.status;
    const requestUrl = error.config?.url ?? '';

    if (status === 401 && requestUrl.includes('/auth/login')) {
        return 'Não foi possível fazer login. Verifique suas credenciais.';
    }
    if (status === 401) {
        return 'Sua sessão expirou. Faça login novamente.';
    }
    if (status === 403) {
        return 'Você não tem permissão para executar esta ação.';
    }
    if (status === 404) {
        return 'Recurso não encontrado.';
    }
    if (status != null && status >= 500) {
        return 'Erro interno no servidor. Tente novamente em instantes.';
    }

    return 'Não foi possível concluir a operação. Tente novamente.';
}

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

        const axiosError = error as AxiosError<unknown>;

        if (!axiosError.config?.skipGlobalErrorToast) {
            toast.error(getApiErrorMessage(axiosError));
        }

        if (error.response?.status === 401) {
            router.push('/login');
        }

        return Promise.reject(error);
    }
);

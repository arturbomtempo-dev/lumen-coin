import { useAuthStore } from '@/modules/auth/stores/auth.store';

export async function initializeApp() {
    const authStore = useAuthStore();
    await authStore.checkAuth();
}

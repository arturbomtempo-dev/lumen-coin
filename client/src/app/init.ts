import { useAuthStore } from '@/modules/auth/stores/auth.store';

export async function initializeApp() {
    const auth = useAuthStore();
    await auth.checkAuth();
}

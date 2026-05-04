import { createRouter, createWebHistory } from 'vue-router';
import { routes } from './routes';

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior(_to, _from, savedPosition) {
        if (savedPosition) return savedPosition;
        return { top: 0 };
    },
});

router.beforeEach(async (to) => {
    if (to.meta.requiresAuth) {
        const { useAuthStore } = await import('@/modules/auth/stores/auth.store');
        const authStore = useAuthStore();
        if (!authStore.isAuthenticated) {
            return { name: 'login' };
        }
    }
});

export default router;

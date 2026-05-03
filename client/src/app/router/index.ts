import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { createRouter, createWebHistory } from 'vue-router';
import { routes } from './routes';

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to) => {
    const auth = useAuthStore();

    if (to.meta.requiresAuth && !auth.isAuthenticated) {
        return '/login';
    }

    const publicPaths = ['/login', '/register', '/'];
    if (publicPaths.includes(to.path) && auth.isAuthenticated) {
        return `/${auth.user?.role}/dashboard`;
    }
});

export default router;

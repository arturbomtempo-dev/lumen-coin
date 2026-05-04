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
    const { useAuthStore } = await import('@/modules/auth/stores/auth.store');
    const authStore = useAuthStore();

    if (authStore.isLoading) {
        await authStore.checkAuth();
    }

    if (authStore.isAuthenticated && authStore.user) {
        const dashboardByRole = {
            student: 'student-dashboard',
            teacher: 'teacher-dashboard',
            institution: 'institution-dashboard',
            company: 'company-dashboard',
        } as const;

        const guestOnlyRouteNames = new Set([
            'login',
            'register',
            'register-institution',
            'register-student',
            'register-company',
        ]);

        if (typeof to.name === 'string' && guestOnlyRouteNames.has(to.name)) {
            return { name: dashboardByRole[authStore.user.role] };
        }
    }

    if (to.meta.requiresAuth) {
        if (!authStore.isAuthenticated) {
            return { name: 'login' };
        }
    }
});

export default router;

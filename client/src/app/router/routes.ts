import type { RouteRecordRaw } from 'vue-router';

export const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'home',
        component: () => import('@/modules/home/pages/HomePage.vue'),
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/modules/auth/pages/LoginPage.vue'),
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('@/modules/auth/pages/RegisterPage.vue'),
    },
    {
        path: '/student',
        component: () => import('@/shared/layouts/AppLayout.vue'),
        children: [
            {
                path: 'dashboard',
                name: 'student-dashboard',
                component: () => import('@/modules/student/pages/StudentDashboardPage.vue'),
                meta: { requiresAuth: true, role: 'student' },
            },
            {
                path: 'statement',
                name: 'student-statement',
                component: () => import('@/modules/student/pages/StudentStatementPage.vue'),
                meta: { requiresAuth: true, role: 'student' },
            },
            {
                path: 'advantages',
                name: 'student-advantages',
                component: () => import('@/modules/student/pages/StudentAdvantagesPage.vue'),
                meta: { requiresAuth: true, role: 'student' },
            },
        ],
    },
    {
        path: '/teacher',
        component: () => import('@/shared/layouts/AppLayout.vue'),
        children: [
            {
                path: 'dashboard',
                name: 'teacher-dashboard',
                component: () => import('@/modules/teacher/pages/TeacherDashboardPage.vue'),
                meta: { requiresAuth: true, role: 'teacher' },
            },
            {
                path: 'statement',
                name: 'teacher-statement',
                component: () => import('@/modules/teacher/pages/TeacherStatementPage.vue'),
                meta: { requiresAuth: true, role: 'teacher' },
            },
            {
                path: 'send-coins',
                name: 'teacher-send-coins',
                component: () => import('@/modules/teacher/pages/TeacherSendCoinsPage.vue'),
                meta: { requiresAuth: true, role: 'teacher' },
            },
        ],
    },
    {
        path: '/company',
        component: () => import('@/shared/layouts/AppLayout.vue'),
        children: [
            {
                path: 'dashboard',
                name: 'company-dashboard',
                component: () => import('@/modules/company/pages/CompanyDashboardPage.vue'),
                meta: { requiresAuth: true, role: 'company' },
            },
            {
                path: 'advantages',
                name: 'company-advantages',
                component: () => import('@/modules/company/pages/CompanyAdvantagesPage.vue'),
                meta: { requiresAuth: true, role: 'company' },
            },
        ],
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'not-found',
        component: () => import('@/modules/home/pages/NotFoundPage.vue'),
    },
];

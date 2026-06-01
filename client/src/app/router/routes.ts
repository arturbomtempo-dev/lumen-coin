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
        path: '/signup',
        name: 'register',
        component: () => import('@/modules/auth/pages/RegisterPage.vue'),
    },
    {
        path: '/signup/instituicao',
        name: 'register-institution',
        component: () => import('@/modules/auth/pages/RegisterInstitutionPage.vue'),
    },
    {
        path: '/signup/student',
        name: 'register-student',
        component: () => import('@/modules/auth/pages/RegisterStudentPage.vue'),
    },
    {
        path: '/signup/empresa',
        name: 'register-company',
        component: () => import('@/modules/auth/pages/RegisterCompanyPage.vue'),
    },
    {
        path: '/forgot-password',
        name: 'forgot-password',
        component: () => import('@/modules/auth/pages/ForgotPasswordPage.vue'),
    },
    {
        path: '/reset-password',
        name: 'reset-password',
        component: () => import('@/modules/auth/pages/ResetPasswordPage.vue'),
    },
    {
        path: '/design-system',
        name: 'design-system',
        component: () => import('@/modules/home/pages/DesignSystemPage.vue'),
    },
    {
        path: '/app/aluno',
        component: () => import('@/shared/layouts/StudentLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                name: 'student-dashboard',
                component: () => import('@/modules/student/pages/StudentDashboardPage.vue'),
            },
            {
                path: 'extrato',
                name: 'student-statement',
                component: () => import('@/modules/student/pages/StudentStatementPage.vue'),
            },
            {
                path: 'perfil',
                name: 'student-profile',
                component: () => import('@/modules/student/pages/StudentProfilePage.vue'),
            },
            {
                path: 'vantagens',
                name: 'student-advantages',
                component: () => import('@/modules/student/pages/StudentAdvantagesPage.vue'),
            },
        ],
    },
    {
        path: '/app/professor',
        component: () => import('@/shared/layouts/TeacherLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',
                name: 'teacher-dashboard',
                component: () => import('@/modules/teacher/pages/TeacherDashboardPage.vue'),
            },
            {
                path: 'perfil',
                name: 'teacher-profile',
                component: () => import('@/modules/teacher/pages/TeacherProfilePage.vue'),
            },
            {
                path: 'enviar-moedas',
                name: 'teacher-send-coins',
                component: () => import('@/modules/teacher/pages/TeacherSendCoinsPage.vue'),
            },
            {
                path: 'extrato',
                name: 'teacher-statement',
                component: () => import('@/modules/teacher/pages/TeacherStatementPage.vue'),
            },
        ],
    },
    {
        path: '/app/empresa',
        name: 'company-dashboard',
        meta: { requiresAuth: true },
        component: () => import('@/modules/company/pages/CompanyDashboardPage.vue'),
    },
    {
        path: '/app/instituicao',
        name: 'institution-dashboard',
        meta: { requiresAuth: true },
        component: () => import('@/modules/institution/pages/InstitutionDashboardPage.vue'),
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'not-found',
        component: () => import('@/modules/home/pages/NotFoundPage.vue'),
    },
];

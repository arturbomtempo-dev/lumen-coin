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
        path: '/app/aluno',
        component: () => import('@/shared/layouts/StudentLayout.vue'),
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
                path: 'notificacoes',
                name: 'student-notifications',
                component: () => import('@/modules/student/pages/StudentNotificationsPage.vue'),
            },
        ],
    },
    {
        path: '/app/professor',
        name: 'teacher-dashboard',
        component: () => import('@/modules/teacher/pages/TeacherDashboardPage.vue'),
    },
    {
        path: '/app/empresa',
        name: 'company-dashboard',
        component: () => import('@/modules/company/pages/CompanyDashboardPage.vue'),
    },
    {
        path: '/app/instituicao',
        name: 'institution-dashboard',
        component: () => import('@/modules/institution/pages/InstitutionDashboardPage.vue'),
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'not-found',
        component: () => import('@/pages/NotFoundPage.vue'),
    },
];

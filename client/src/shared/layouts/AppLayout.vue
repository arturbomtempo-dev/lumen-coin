<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { useRoute, useRouter } from 'vue-router';

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

type NavItem = {
    to: string;
    label: string;
};

const studentNav: NavItem[] = [
    { to: '/student/dashboard', label: 'Dashboard' },
    { to: '/student/statement', label: 'Statement' },
    { to: '/student/advantages', label: 'Advantages' },
];

const teacherNav: NavItem[] = [
    { to: '/teacher/dashboard', label: 'Dashboard' },
    { to: '/teacher/send-coins', label: 'Send Coins' },
    { to: '/teacher/statement', label: 'Statement' },
];

const companyNav: NavItem[] = [
    { to: '/company/dashboard', label: 'Dashboard' },
    { to: '/company/advantages', label: 'Advantages' },
];

const navItems = computed<NavItem[]>(() => {
    const role = auth.user?.role;
    if (role === 'student') return studentNav;
    if (role === 'teacher') return teacherNav;
    if (role === 'company') return companyNav;
    return [];
});

function isActive(item: NavItem) {
    return route.path === item.to;
}

async function handleLogout() {
    await auth.logout();
    router.push('/login');
}
</script>

<template>
    <div class="flex min-h-screen" style="background-color: var(--color-bg)">
        <div
            v-if="sidebarOpen"
            class="fixed inset-0 z-20 bg-black/60 lg:hidden"
            @click="sidebarOpen = false"
        />

        <aside
            class="fixed inset-y-0 left-0 z-30 w-60 flex flex-col shrink-0 transition-transform duration-200 lg:static lg:translate-x-0"
            :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full'"
            style="background-color: var(--color-sidebar-bg)"
        >
            <div
                class="px-5 py-6 flex items-center justify-between"
                style="border-bottom: 1px solid var(--color-border)"
            >
                <span class="font-bold text-lg tracking-tight" style="color: var(--color-primary)">
                    Lumen Coin
                </span>
                <button
                    class="lg:hidden p-1 rounded text-gray-400 hover:text-white"
                    @click="sidebarOpen = false"
                >
                    ✕
                </button>
            </div>

            <nav class="flex-1 p-3 space-y-1">
                <router-link
                    v-for="item in navItems"
                    :key="item.to"
                    :to="item.to"
                    class="nav-link"
                    :class="{ 'nav-link--active': isActive(item) }"
                    @click="sidebarOpen = false"
                >
                    {{ item.label }}
                </router-link>
            </nav>

            <div class="p-4" style="border-top: 1px solid var(--color-border)">
                <p class="text-xs truncate mb-3 px-1 text-muted">
                    {{ auth.user?.email }}
                </p>
                <button class="nav-link w-full text-left" @click="handleLogout">Sair</button>
            </div>
        </aside>

        <div class="flex-1 flex flex-col min-w-0">
            <header
                class="px-4 sm:px-6 py-4 flex items-center gap-4 shrink-0"
                style="
                    background-color: var(--color-surface);
                    border-bottom: 1px solid var(--color-border);
                    box-shadow: var(--shadow);
                "
            >
                <button
                    class="lg:hidden p-2 rounded-md text-muted hover:text-body"
                    style="background-color: var(--color-surface-hover)"
                    @click="sidebarOpen = true"
                >
                    ☰
                </button>
                <span class="font-semibold capitalize">{{ auth.user?.role }} Area</span>
                <button class="nav-link ml-auto px-3 py-2" @click="handleLogout">Sair</button>
            </header>

            <main class="flex-1 p-4 sm:p-6">
                <router-view />
            </main>
        </div>
    </div>
</template>

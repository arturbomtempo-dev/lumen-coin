<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { useThemeStore } from '@/shared/stores/theme.store';
import { useRoute, useRouter } from 'vue-router';
import { PhGameController, PhMoon, PhSignOut, PhSun, PhList, PhX } from '@phosphor-icons/vue';

const auth = useAuthStore();
const themeStore = useThemeStore();
const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

type NavItem = {
    to: string;
    label: string;
};

const studentNav: NavItem[] = [
    { to: '/app/aluno', label: 'INÍCIO' },
    { to: '/app/aluno/extrato', label: 'EXTRATO' },
    { to: '/app/aluno/perfil', label: 'PERFIL' },
];

const teacherNav: NavItem[] = [{ to: '/app/professor', label: 'PAINEL' }];

const companyNav: NavItem[] = [{ to: '/app/empresa', label: 'PAINEL' }];

const institutionNav: NavItem[] = [{ to: '/app/instituicao', label: 'PAINEL' }];

const navItems = computed<NavItem[]>(() => {
    const role = auth.user?.role;
    if (role === 'student') return studentNav;
    if (role === 'teacher') return teacherNav;
    if (role === 'company') return companyNav;
    if (role === 'institution') return institutionNav;
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
    <div class="min-h-screen flex flex-col">
        <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-3">
                <router-link to="/" class="flex items-center gap-2 font-pixel text-[10px]">
                    <PhGameController weight="fill" class="pixel-icon text-primary" :size="18" />
                    LUMEN COIN
                </router-link>

                <nav class="hidden md:flex items-center gap-4 font-pixel text-[9px]">
                    <router-link
                        v-for="item in navItems"
                        :key="item.to"
                        :to="item.to"
                        class="hover:text-primary transition-colors"
                        :class="isActive(item) ? 'text-primary' : ''"
                    >
                        {{ item.label }}
                    </router-link>
                </nav>

                <div class="flex items-center gap-2">
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-0.5 active:translate-y-0.5 active:shadow-none"
                        aria-label="Alternar tema"
                        @click="themeStore.toggle"
                    >
                        <PhSun
                            v-if="themeStore.theme === 'night'"
                            weight="fill"
                            class="pixel-icon"
                        />
                        <PhMoon v-else weight="fill" class="pixel-icon" />
                        {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
                    </button>
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-0.5 active:translate-y-0.5 active:shadow-none"
                        aria-label="Sair"
                        @click="handleLogout"
                    >
                        <PhSignOut weight="bold" class="pixel-icon" />
                        SAIR
                    </button>
                    <button
                        class="md:hidden font-pixel text-[9px] flex items-center gap-1 border-2 border-border bg-card text-card-foreground px-2 py-1"
                        aria-label="Menu"
                        @click="sidebarOpen = true"
                    >
                        <PhList weight="bold" :size="16" />
                    </button>
                </div>
            </div>
        </header>

        <div
            v-if="sidebarOpen"
            class="fixed inset-0 z-20 bg-black/60 md:hidden"
            @click="sidebarOpen = false"
        />

        <aside
            class="fixed inset-y-0 left-0 z-30 w-56 flex flex-col md:hidden transition-transform duration-200 bg-hud text-hud-foreground border-r-4 border-border"
            :class="sidebarOpen ? 'translate-x-0' : '-translate-x-full'"
        >
            <div class="px-5 py-4 flex items-center justify-between border-b-2 border-border">
                <span class="font-pixel text-[10px] text-primary">LUMEN COIN</span>
                <button aria-label="Fechar menu" @click="sidebarOpen = false">
                    <PhX weight="bold" :size="16" />
                </button>
            </div>
            <nav class="flex-1 p-4 space-y-2">
                <router-link
                    v-for="item in navItems"
                    :key="item.to"
                    :to="item.to"
                    class="block font-pixel text-[9px] px-3 py-2 border-2 border-transparent hover:border-border hover:bg-card hover:text-card-foreground transition-colors"
                    :class="isActive(item) ? 'border-border bg-card text-primary' : ''"
                    @click="sidebarOpen = false"
                >
                    {{ item.label }}
                </router-link>
            </nav>
            <div class="p-4 border-t-2 border-border">
                <p class="font-pixel text-[8px] text-primary truncate mb-3">
                    {{ auth.user?.email }}
                </p>
                <button
                    class="w-full font-pixel text-[9px] flex items-center justify-center gap-2 border-2 border-border bg-card text-card-foreground px-3 py-2"
                    @click="handleLogout"
                >
                    <PhSignOut weight="bold" class="pixel-icon" /> SAIR
                </button>
            </div>
        </aside>

        <main class="flex-1 container py-6">
            <router-view />
        </main>
    </div>
</template>

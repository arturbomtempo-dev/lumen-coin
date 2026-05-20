<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { useTeacherStore } from '@/modules/teacher/stores/teacher.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import { useThemeStore } from '@/shared/stores/theme.store';
import {
    PhGameController,
    PhGraduationCap,
    PhMoon,
    PhPaperPlaneTilt,
    PhReceipt,
    PhSignOut,
    PhSun,
    PhUser,
} from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';

const themeStore = useThemeStore();
const teacherStore = useTeacherStore();
const authStore = useAuthStore();
const { name, balance } = storeToRefs(teacherStore);
const route = useRoute();
const router = useRouter();

const navItems = [
    { to: '/app/professor', icon: PhGraduationCap, label: 'PAINEL' },
    { to: '/app/professor/enviar-moedas', icon: PhPaperPlaneTilt, label: 'ENVIAR' },
    { to: '/app/professor/extrato', icon: PhReceipt, label: 'EXTRATO' },
    { to: '/app/professor/perfil', icon: PhUser, label: 'PERFIL' },
];

async function handleLogout() {
    await authStore.logout();
    router.push('/login');
}

onMounted(() => {
    if (!teacherStore.isLoaded) {
        teacherStore.loadProfile();
    }
});
</script>

<template>
    <div class="min-h-screen flex flex-col">
        <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-3">
                <RouterLink to="/app/professor" class="flex items-center gap-2 min-w-0">
                    <div
                        class="pixel-icon border-2 border-border bg-primary text-primary-foreground p-1"
                    >
                        <PhGameController :size="18" weight="fill" />
                    </div>
                    <div class="min-w-0">
                        <div class="font-pixel text-[10px] leading-tight">LUMEN COIN</div>
                        <div class="font-display text-xs opacity-70 -mt-0.5 truncate">
                            {{ name ? name.toUpperCase() : 'PROFESSOR' }}
                        </div>
                    </div>
                </RouterLink>

                <div class="hidden sm:flex items-center gap-4 font-pixel text-[10px]">
                    <span class="flex items-center gap-1">
                        <CoinIcon :size="14" />
                        {{ balance.toLocaleString('pt-BR') }}
                    </span>
                </div>

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
                </div>
            </div>
        </header>

        <main class="flex-1 container py-6">
            <router-view />
        </main>

        <nav class="sticky bottom-0 z-40 bg-hud text-hud-foreground border-t-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-2">
                <div class="hidden md:flex items-center gap-2 font-pixel text-[10px]">
                    <CoinIcon :size="14" /> {{ balance.toLocaleString('pt-BR') }} LUMEN
                </div>
                <ul
                    class="flex flex-1 md:flex-none items-center justify-around md:justify-end gap-1 md:gap-2"
                >
                    <li v-for="item in navItems" :key="item.to">
                        <RouterLink
                            :to="item.to"
                            class="relative flex flex-col items-center gap-0.5 px-3 py-1.5 border-2 border-border font-pixel text-[8px] transition-transform"
                            :class="
                                route.path === item.to
                                    ? 'bg-primary text-primary-foreground shadow-[2px_2px_0_0_hsl(var(--border))] animate-glow'
                                    : 'bg-card text-card-foreground hover:-translate-y-0.5'
                            "
                        >
                            <component
                                :is="item.icon"
                                :size="18"
                                weight="fill"
                                class="pixel-icon"
                            />
                            <span class="hidden sm:inline">{{ item.label }}</span>
                        </RouterLink>
                    </li>
                </ul>
            </div>
        </nav>
    </div>
</template>

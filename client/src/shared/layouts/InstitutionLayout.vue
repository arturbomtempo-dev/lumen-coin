<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { useThemeStore } from '@/shared/stores/theme.store';
import {
    PhBuildings,
    PhGameController,
    PhMoon,
    PhSignOut,
    PhStorefront,
    PhSun,
} from '@phosphor-icons/vue';
import { onMounted } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';

const themeStore = useThemeStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

const navItems = [
    { to: '/app/instituicao', icon: PhBuildings, label: 'PAINEL' },
    { to: '/app/instituicao/vantagens', icon: PhStorefront, label: 'VANTAGENS' },
];

async function handleLogout() {
    await authStore.logout();
    router.push('/login');
}

onMounted(() => {});
</script>

<template>
    <div class="min-h-screen flex flex-col">
        <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-3">
                <RouterLink to="/app/instituicao" class="flex items-center gap-2 min-w-0">
                    <div
                        class="pixel-icon border-2 border-border bg-primary text-primary-foreground p-1"
                    >
                        <PhGameController :size="18" weight="fill" />
                    </div>
                    <div class="min-w-0">
                        <div class="font-pixel text-[10px] leading-tight">LUMEN COIN</div>
                        <div class="font-display text-xs opacity-70 -mt-0.5 truncate">
                            {{ authStore.user?.name?.toUpperCase() ?? 'INSTITUIÇÃO' }}
                        </div>
                    </div>
                </RouterLink>

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
            <div class="container flex items-center justify-end py-2 gap-2">
                <ul class="flex items-center gap-1 md:gap-2">
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

<script setup lang="ts">
import { RouterLink, useRoute } from 'vue-router'
import { PhGameController, PhHouse, PhReceipt, PhUser, PhBell, PhSun, PhMoon } from '@phosphor-icons/vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import { useThemeStore } from '@/shared/stores/theme.store'
import { useStudentStore } from '@/modules/student/stores/student.store'
import { storeToRefs } from 'pinia'

const themeStore = useThemeStore()
const studentStore = useStudentStore()
const { saldo, nivel, nome, unreadCount } = storeToRefs(studentStore)
const route = useRoute()

const navItems = [
  { to: '/app/aluno', icon: PhHouse, label: 'INÍCIO', badge: 0 },
  { to: '/app/aluno/extrato', icon: PhReceipt, label: 'EXTRATO', badge: 0 },
  { to: '/app/aluno/perfil', icon: PhUser, label: 'PERFIL', badge: 0 },
  { to: '/app/aluno/notificacoes', icon: PhBell, label: 'AVISOS', badge: unreadCount },
]

</script>

<template>
  <div class="min-h-screen flex flex-col">
    <!-- HUD Top -->
    <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
      <div class="container flex items-center justify-between py-2 gap-3">
        <RouterLink to="/app/aluno" class="flex items-center gap-2 min-w-0">
          <div class="pixel-icon border-2 border-border bg-primary text-primary-foreground p-1">
            <PhGameController :size="18" weight="fill" />
          </div>
          <div class="min-w-0">
            <div class="font-pixel text-[10px] leading-tight">LUMEN COIN</div>
            <div class="font-display text-xs opacity-70 -mt-0.5 truncate">{{ nome.toUpperCase() }}</div>
          </div>
        </RouterLink>

        <div class="hidden sm:flex items-center gap-4 font-pixel text-[10px]">
          <span>LV {{ nivel }}</span>
          <span class="flex items-center gap-1">
            <CoinIcon :size="14" />
            {{ saldo.toLocaleString('pt-BR') }}
          </span>
        </div>

        <button
          class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-[2px] active:translate-y-[2px] active:shadow-none"
          aria-label="Alternar tema"
          @click="themeStore.toggle"
        >
          <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
          <PhMoon v-else weight="fill" class="pixel-icon" />
          {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
        </button>
      </div>
    </header>

    <!-- Main content -->
    <main class="flex-1 container py-6">
      <router-view />
    </main>

    <!-- HUD Bottom -->
    <nav class="sticky bottom-0 z-40 bg-hud text-hud-foreground border-t-4 border-border">
      <div class="container flex items-center justify-between py-2 gap-2">
        <div class="hidden md:flex items-center gap-2 font-pixel text-[10px]">
          <CoinIcon :size="14" /> {{ saldo.toLocaleString('pt-BR') }} LUMEN
        </div>
        <ul class="flex flex-1 md:flex-none items-center justify-around md:justify-end gap-1 md:gap-2">
          <li v-for="item in navItems" :key="item.to">
            <RouterLink
              :to="item.to"
              class="relative flex flex-col items-center gap-0.5 px-3 py-1.5 border-2 border-border font-pixel text-[8px] transition-transform"
              :class="route.path === item.to
                ? 'bg-primary text-primary-foreground shadow-[2px_2px_0_0_hsl(var(--border))] animate-glow'
                : 'bg-card text-card-foreground hover:-translate-y-0.5'"
            >
              <component :is="item.icon" :size="18" weight="fill" class="pixel-icon" />
              <span class="hidden sm:inline">{{ item.label }}</span>
              <span
                v-if="item.badge && (typeof item.badge === 'number' ? item.badge : item.badge.value) > 0"
                class="absolute -top-1 -right-1 h-4 min-w-4 px-0.5 flex items-center justify-center bg-secondary text-secondary-foreground border-2 border-border font-pixel text-[8px]"
              >
                {{ typeof item.badge === 'number' ? item.badge : item.badge.value }}
              </span>
            </RouterLink>
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

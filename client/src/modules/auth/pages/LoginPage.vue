<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import {
  PhArrowLeft, PhGameController, PhSun, PhMoon, PhSparkle,
  PhStudent, PhGraduationCap, PhBuildings, PhEye, PhEyeSlash,
} from '@phosphor-icons/vue'
import PixelButton from '@/shared/components/PixelButton.vue'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelInput from '@/shared/components/PixelInput.vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import MarioAvatar from '@/shared/components/MarioAvatar.vue'
import { useThemeStore } from '@/shared/stores/theme.store'

const themeStore = useThemeStore()
const router = useRouter()

type Role = 'aluno' | 'professor' | 'empresa' | 'instituicao'

const role = ref<Role>('aluno')
const email = ref('')
const senha = ref('')
const showPwd = ref(false)

const avatarChar = computed(() =>
  role.value === 'professor' ? 'luigi' : role.value === 'empresa' ? 'toad' : 'mario'
)

const roleItems = [
  { id: 'aluno' as Role, label: 'ALUNO', icon: PhStudent },
  { id: 'professor' as Role, label: 'PROFESSOR', icon: PhGraduationCap },
  { id: 'empresa' as Role, label: 'EMPRESA', icon: PhBuildings },
  { id: 'instituicao' as Role, label: 'INSTITUIÇÃO', icon: PhBuildings },
]

function submit(e: Event) {
  e.preventDefault()
  const route =
    role.value === 'aluno' ? '/app/aluno'
    : role.value === 'professor' ? '/app/professor'
    : role.value === 'empresa' ? '/app/empresa'
    : '/app/instituicao'
  router.push(route)
}
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-hud text-hud-foreground border-b-4 border-border">
      <div class="container flex items-center justify-between py-3">
        <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px]">
          <PhArrowLeft weight="bold" />
          <PhGameController weight="fill" class="pixel-icon text-primary" :size="18" />
          VOLTAR AO MUNDO 1-1
        </RouterLink>
        <button
          class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))]"
          @click="themeStore.toggle"
        >
          <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
          <PhMoon v-else weight="fill" class="pixel-icon" />
          {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
        </button>
      </div>
    </header>

    <main class="flex-1 relative overflow-hidden">
      <div class="absolute inset-0 bg-info/10" />
      <!-- Clouds -->
      <svg width="80" height="40" viewBox="0 0 16 8" class="absolute top-10 left-6 opacity-80" shape-rendering="crispEdges">
        <rect x="2" y="3" width="12" height="3" fill="#ffffff" />
        <rect x="3" y="2" width="3" height="1" fill="#ffffff" />
        <rect x="7" y="1" width="3" height="2" fill="#ffffff" />
        <rect x="11" y="2" width="3" height="1" fill="#ffffff" />
        <rect x="1" y="6" width="14" height="1" fill="hsl(var(--border))" />
      </svg>
      <svg width="80" height="40" viewBox="0 0 16 8" class="absolute top-20 right-12 opacity-70" shape-rendering="crispEdges">
        <rect x="2" y="3" width="12" height="3" fill="#ffffff" />
        <rect x="3" y="2" width="3" height="1" fill="#ffffff" />
        <rect x="7" y="1" width="3" height="2" fill="#ffffff" />
        <rect x="1" y="6" width="14" height="1" fill="hsl(var(--border))" />
      </svg>
      <!-- Brick floor -->
      <div
        class="absolute bottom-0 left-0 right-0 h-24 border-t-4 border-border bg-secondary"
        :style="{ backgroundImage: 'repeating-linear-gradient(0deg, hsl(var(--secondary-shadow)) 0 2px, transparent 2px 24px), repeating-linear-gradient(90deg, hsl(var(--secondary-shadow)) 0 2px, transparent 2px 48px)' }"
      />
      <!-- Question block -->
      <div class="absolute bottom-32 right-10 hidden md:block">
        <div class="w-16 h-16 bg-primary border-4 border-border shadow-[4px_4px_0_0_hsl(var(--border))] flex items-center justify-center font-pixel text-2xl text-primary-foreground animate-bob">?</div>
      </div>
      <!-- Mario -->
      <div class="absolute bottom-24 right-1/4 hidden lg:block animate-bob">
        <MarioAvatar character="mario" :size="72" />
      </div>

      <div class="relative container py-10 flex items-center justify-center min-h-full">
        <PixelCard class="w-full max-w-md p-6 md:p-8 crt-scanlines">
          <div class="flex items-center gap-3 mb-5">
            <div class="border-2 border-border bg-hud p-2">
              <MarioAvatar :character="avatarChar" :size="48" />
            </div>
            <div>
              <div class="font-pixel text-[10px] text-primary">▶ INSERT COIN</div>
              <h1 class="font-pixel text-lg">ENTRAR NO ARCADE</h1>
            </div>
            <div class="ml-auto"><CoinIcon :size="28" class="animate-bob" /></div>
          </div>

          <!-- Role selector -->
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-2 mb-5">
            <button
              v-for="r in roleItems"
              :key="r.id"
              type="button"
              class="border-2 border-border p-2 font-pixel text-[9px] flex flex-col items-center gap-1 transition-all"
              :class="role === r.id
                ? 'bg-primary text-primary-foreground shadow-[3px_3px_0_0_hsl(var(--border))]'
                : 'bg-card hover:-translate-y-0.5'"
              @click="role = r.id"
            >
              <component :is="r.icon" weight="fill" class="pixel-icon" :size="18" />
              {{ r.label }}
            </button>
          </div>

          <form class="space-y-4" @submit="submit">
            <div>
              <label class="font-pixel text-[10px] block mb-2">E-MAIL</label>
              <PixelInput v-model="email" type="email" placeholder="voce@email.com" required />
            </div>
            <div>
              <label class="font-pixel text-[10px] block mb-2">SENHA</label>
              <div class="relative">
                <PixelInput v-model="senha" :type="showPwd ? 'text' : 'password'" placeholder="••••••••" required />
                <button
                  type="button"
                  class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  aria-label="Mostrar senha"
                  @click="showPwd = !showPwd"
                >
                  <PhEyeSlash v-if="showPwd" weight="bold" />
                  <PhEye v-else weight="bold" />
                </button>
              </div>
            </div>

            <div class="flex items-center justify-between font-pixel text-[9px]">
              <label class="flex items-center gap-2">
                <input type="checkbox" class="accent-primary" /> LEMBRAR
              </label>
              <a href="#" class="hover:text-primary">ESQUECI A SENHA</a>
            </div>

            <PixelButton variant="primary" class="w-full" type="submit">
              <PhSparkle weight="fill" class="pixel-icon" /> PRESS START
            </PixelButton>
          </form>

          <div class="my-5 flex items-center gap-2 font-pixel text-[9px] text-muted-foreground">
            <div class="flex-1 h-px bg-border" /> OU <div class="flex-1 h-px bg-border" />
          </div>

          <RouterLink to="/signup">
            <PixelButton variant="success" class="w-full">CRIAR NOVO PERSONAGEM</PixelButton>
          </RouterLink>

          <p class="font-display text-base text-center mt-5 opacity-80">★ 1-UP · sua jornada acadêmica continua ★</p>
        </PixelCard>
      </div>
    </main>
  </div>
</template>

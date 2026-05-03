<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { PhUser, PhPalette } from '@phosphor-icons/vue'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelBadge from '@/shared/components/PixelBadge.vue'
import PixelButton from '@/shared/components/PixelButton.vue'
import MarioAvatar from '@/shared/components/MarioAvatar.vue'
import { MARIO_CHARACTERS } from '@/shared/data/characters'
import XPBar from '@/shared/components/XPBar.vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import { useStudentStore } from '@/modules/student/stores/student.store'
import { conquistas } from '@/shared/data/mockData'

const store = useStudentStore()
const { nome, nivel, xp, saldo, personagem } = storeToRefs(store)
</script>

<template>
  <div class="space-y-8">
    <div>
      <div class="font-pixel text-[10px] text-primary">▶ PERFIL</div>
      <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
        <PhUser weight="fill" class="pixel-icon" /> MEU PERSONAGEM
      </h1>
    </div>

    <PixelCard class="p-6 grid md:grid-cols-[auto_1fr] gap-6">
      <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
        <MarioAvatar :character="personagem" :size="112" />
      </div>
      <div>
        <div class="font-pixel text-2xl">{{ nome.toUpperCase() }}</div>
        <div class="font-sans text-sm text-muted-foreground mt-1">Eng. de Software · UNIFEI · Sala 204</div>
        <div class="mt-5 grid grid-cols-3 gap-3 max-w-md">
          <div class="border-2 border-border bg-card p-3">
            <div class="font-pixel text-[8px] text-muted-foreground">NÍVEL</div>
            <div class="font-pixel text-sm mt-1">LV {{ nivel }}</div>
          </div>
          <div class="border-2 border-border bg-card p-3">
            <div class="font-pixel text-[8px] text-muted-foreground">MOEDAS</div>
            <div class="font-pixel text-sm mt-1 flex items-center gap-1"><CoinIcon :size="12" /> {{ saldo.toLocaleString('pt-BR') }}</div>
          </div>
          <div class="border-2 border-border bg-card p-3">
            <div class="font-pixel text-[8px] text-muted-foreground">STREAK</div>
            <div class="font-pixel text-sm mt-1">12 DIAS</div>
          </div>
        </div>
        <div class="mt-5 max-w-md"><XPBar :value="xp" label="EXPERIÊNCIA" /></div>
        <div class="mt-5 flex flex-wrap gap-2">
          <PixelBadge tone="gold">LÍDER</PixelBadge>
          <PixelBadge tone="blue">ESTUDIOSO</PixelBadge>
          <PixelBadge tone="green">COLABORATIVO</PixelBadge>
        </div>
      </div>
    </PixelCard>

    <PixelCard class="p-6">
      <div class="flex items-center justify-between mb-4">
        <div class="font-pixel text-sm flex items-center gap-2">
          <PhPalette weight="fill" class="pixel-icon text-accent" /> TROCAR PERSONAGEM
        </div>
        <PixelBadge tone="teal">{{ MARIO_CHARACTERS.length }} HERÓIS</PixelBadge>
      </div>
      <div class="grid grid-cols-3 sm:grid-cols-6 gap-3">
        <button
          v-for="c in MARIO_CHARACTERS"
          :key="c.id"
          class="border-2 border-border p-2 transition-all"
          :class="personagem === c.id ? 'bg-primary text-primary-foreground -translate-y-0.5 shadow-[4px_4px_0_0_hsl(var(--border))]' : 'bg-card hover:-translate-y-0.5'"
          @click="store.setPersonagem(c.id)"
        >
          <div class="flex justify-center bg-hud border-2 border-border p-1">
            <MarioAvatar :character="c.id" :size="40" />
          </div>
          <div class="font-pixel text-[8px] mt-2">{{ c.nome.toUpperCase() }}</div>
        </button>
      </div>
    </PixelCard>

    <PixelCard class="p-6">
      <div class="font-pixel text-sm mb-4">CONQUISTAS</div>
      <div class="grid sm:grid-cols-2 gap-3">
        <div
          v-for="c in conquistas"
          :key="c.id"
          class="border-2 border-border p-3"
          :class="c.desbloqueado ? 'bg-card' : 'bg-muted opacity-60'"
        >
          <PixelBadge :tone="c.tone as any">{{ c.desbloqueado ? '✓' : '?' }} {{ c.nome.toUpperCase() }}</PixelBadge>
          <p class="font-sans text-xs text-foreground/75 mt-2">{{ c.desc }}</p>
        </div>
      </div>
    </PixelCard>

    <div>
      <PixelButton variant="ghost">SAIR DO ARCADE</PixelButton>
    </div>
  </div>
</template>

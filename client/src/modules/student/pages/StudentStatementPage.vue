<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { PhReceipt, PhTicket } from '@phosphor-icons/vue'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelBadge from '@/shared/components/PixelBadge.vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import { useStudentStore } from '@/modules/student/stores/student.store'

const store = useStudentStore()
const { transacoes, cupons } = storeToRefs(store)
</script>

<template>
  <div class="space-y-8">
    <div>
      <div class="font-pixel text-[10px] text-primary">▶ HISTÓRICO</div>
      <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
        <PhReceipt weight="fill" class="pixel-icon" /> EXTRATO DE MOEDAS
      </h1>
    </div>

    <PixelCard class="overflow-hidden">
      <div class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] grid grid-cols-[1fr_auto] sm:grid-cols-[1.5fr_2fr_auto_auto] gap-2">
        <span>PROFESSOR</span>
        <span class="hidden sm:block">MOTIVO</span>
        <span class="hidden sm:block">DATA</span>
        <span>VALOR</span>
      </div>
      <div v-if="transacoes.length === 0" class="p-6 font-sans text-sm text-muted-foreground text-center">
        Nenhuma transação registrada.
      </div>
      <div
        v-for="t in transacoes"
        :key="t.id"
        class="px-4 py-3 border-t-2 border-border grid grid-cols-[1fr_auto] sm:grid-cols-[1.5fr_2fr_auto_auto] gap-2 items-center"
      >
        <div>
          <div class="font-pixel text-[10px]">{{ t.professor }}</div>
          <div class="font-sans text-xs text-muted-foreground sm:hidden mt-1">{{ t.motivo }}</div>
          <div class="font-sans text-xs text-muted-foreground sm:hidden">{{ t.data }}</div>
        </div>
        <div class="hidden sm:block font-sans text-sm">{{ t.motivo }}</div>
        <div class="hidden sm:block font-pixel text-[9px] text-muted-foreground">{{ t.data }}</div>
        <div class="font-pixel text-sm flex items-center gap-1 text-success">
          +<CoinIcon :size="12" /> {{ t.valor }}
        </div>
      </div>
    </PixelCard>

    <div>
      <h2 class="font-pixel text-lg mb-4 flex items-center gap-2">
        <PhTicket weight="fill" class="pixel-icon text-secondary" /> MEUS CUPONS
      </h2>
      <PixelCard v-if="cupons.length === 0" class="p-6 text-center font-sans text-muted-foreground text-sm">
        Você ainda não resgatou nenhuma vantagem. Vá ao marketplace e desbloqueie recompensas!
      </PixelCard>
      <div v-else class="grid sm:grid-cols-2 gap-4">
        <PixelCard v-for="c in cupons" :key="c.id" class="p-4">
          <div class="flex items-center justify-between">
            <div class="font-pixel text-xs">{{ c.vantagem.toUpperCase() }}</div>
            <PixelBadge :tone="c.usado ? 'red' : 'green'">{{ c.usado ? 'USADO' : 'VÁLIDO' }}</PixelBadge>
          </div>
          <div class="mt-3 border-2 border-dashed border-border bg-hud text-hud-foreground p-3">
            <div class="font-pixel text-[9px] opacity-70">CÓDIGO</div>
            <div class="font-pixel text-sm break-all">{{ c.codigo }}</div>
          </div>
          <div class="font-sans text-xs text-muted-foreground mt-2">Gerado em {{ c.data }}</div>
        </PixelCard>
      </div>
    </div>
  </div>
</template>

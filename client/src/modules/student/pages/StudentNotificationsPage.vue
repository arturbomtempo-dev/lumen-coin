<script setup lang="ts">
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { PhBell } from '@phosphor-icons/vue'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelBadge from '@/shared/components/PixelBadge.vue'
import PixelButton from '@/shared/components/PixelButton.vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import { useStudentStore } from '@/modules/student/stores/student.store'

const store = useStudentStore()
const { notificacoes } = storeToRefs(store)

onMounted(() => {
  setTimeout(() => store.marcarLidas(), 800)
})
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between flex-wrap gap-3">
      <div>
        <div class="font-pixel text-[10px] text-primary">▶ CAIXA DE MISSÕES</div>
        <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
          <PhBell weight="fill" class="pixel-icon" /> NOTIFICAÇÕES
        </h1>
      </div>
      <PixelButton size="sm" variant="ghost" @click="store.marcarLidas()">MARCAR COMO LIDAS</PixelButton>
    </div>

    <PixelCard v-if="notificacoes.length === 0" class="p-6 text-center font-sans text-muted-foreground text-sm">
      Sem notificações. Continue jogando!
    </PixelCard>

    <div class="space-y-3">
      <PixelCard
        v-for="n in notificacoes"
        :key="n.id"
        class="p-4"
        :class="!n.lida ? 'ring-4 ring-primary/50' : ''"
      >
        <div class="flex items-start gap-4">
          <div class="shrink-0 border-2 border-border bg-hud text-hud-foreground w-12 h-12 flex items-center justify-center">
            <CoinIcon :size="24" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 flex-wrap">
              <div class="font-pixel text-xs">{{ n.titulo }}</div>
              <PixelBadge v-if="!n.lida" tone="red">NOVA</PixelBadge>
            </div>
            <p class="font-sans text-sm text-foreground/80 mt-1">{{ n.mensagem }}</p>
            <p v-if="n.professor" class="font-sans text-xs text-muted-foreground mt-1">Enviado por {{ n.professor }}</p>
            <div class="flex items-center justify-between mt-2">
              <span class="font-pixel text-[9px] text-muted-foreground">{{ n.data }}</span>
              <span v-if="n.valor !== undefined" class="font-pixel text-sm text-success flex items-center gap-1">
                +<CoinIcon :size="12" /> {{ n.valor }}
              </span>
            </div>
          </div>
        </div>
      </PixelCard>
    </div>
  </div>
</template>

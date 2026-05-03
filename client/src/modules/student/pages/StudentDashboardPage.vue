<script setup lang="ts">
import { ref, computed } from 'vue'
import { PhCrown, PhStorefront, PhLightning, PhTrophy, PhSparkle, PhMagnifyingGlass, PhX } from '@phosphor-icons/vue'
import { storeToRefs } from 'pinia'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelBadge from '@/shared/components/PixelBadge.vue'
import PixelButton from '@/shared/components/PixelButton.vue'
import PixelInput from '@/shared/components/PixelInput.vue'
import MarioAvatar from '@/shared/components/MarioAvatar.vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import XPBar from '@/shared/components/XPBar.vue'
import { useStudentStore } from '@/modules/student/stores/student.store'
import { vantagensIniciais, ranking, conquistas } from '@/shared/data/mockData'

const store = useStudentStore()
const { saldo, nivel, xp, nome, personagem } = storeToRefs(store)

const resgate = ref<{ nome: string; custo: number } | null>(null)
const cupom = ref<{ codigo: string; nome: string } | null>(null)
const busca = ref('')
const categoria = ref('todas')
const ordenar = ref<'recentes' | 'menor' | 'maior' | 'alfabetica'>('recentes')
const soResgataveis = ref(false)

const categorias = computed(() => Array.from(new Set(vantagensIniciais.map((v) => v.categoria))))

const vantagensFiltradas = computed(() => {
  let lista = [...vantagensIniciais]
  if (busca.value) {
    const q = busca.value.toLowerCase()
    lista = lista.filter((v) => v.nome.toLowerCase().includes(q) || v.descricao.toLowerCase().includes(q) || v.empresa.toLowerCase().includes(q))
  }
  if (categoria.value !== 'todas') lista = lista.filter((v) => v.categoria === categoria.value)
  if (soResgataveis.value) lista = lista.filter((v) => saldo.value >= v.custo)
  if (ordenar.value === 'menor') lista = lista.sort((a, b) => a.custo - b.custo)
  else if (ordenar.value === 'maior') lista = lista.sort((a, b) => b.custo - a.custo)
  else if (ordenar.value === 'alfabetica') lista = lista.sort((a, b) => a.nome.localeCompare(b.nome))
  return lista
})

function confirmar() {
  if (!resgate.value) return
  if (saldo.value < resgate.value.custo) { resgate.value = null; return }
  const codigo = store.gastar(resgate.value.custo, resgate.value.nome)
  cupom.value = { codigo, nome: resgate.value.nome }
  resgate.value = null
}

const medals: Array<'gold' | 'red' | 'blue' | 'teal'> = ['gold', 'red', 'blue', 'teal', 'teal']
</script>

<template>
  <div class="space-y-10">
    <!-- Wallet HUD -->
    <section class="grid lg:grid-cols-[1.1fr_1fr] gap-6 items-start">
      <PixelCard class="p-6 crt-scanlines">
        <div class="flex items-center justify-between">
          <div>
            <div class="font-pixel text-[10px] text-primary">▶ CARTEIRA DO JOGADOR</div>
            <div class="font-pixel text-xl mt-2">{{ nome.toUpperCase() }}</div>
            <div class="font-sans text-sm text-muted-foreground">Eng. de Software · UNIFEI</div>
          </div>
          <div class="border-2 border-border bg-hud p-1">
            <MarioAvatar :character="personagem" :size="64" />
          </div>
        </div>

        <div class="mt-6 border-2 border-border bg-hud text-hud-foreground p-5 text-center">
          <div class="font-pixel text-[10px] mb-2">SALDO ATUAL</div>
          <div class="flex items-center justify-center gap-3">
            <CoinIcon :size="36" class="animate-bob" />
            <span class="font-pixel text-4xl md:text-5xl animate-coin-flash">{{ saldo.toLocaleString('pt-BR') }}</span>
          </div>
          <div class="font-display text-sm mt-2 opacity-80 tracking-wider">LUMEN COINS</div>
        </div>

        <div class="mt-5 grid grid-cols-2 gap-4">
          <div class="border-2 border-border bg-card p-3">
            <div class="font-pixel text-[9px] text-muted-foreground">NÍVEL</div>
            <div class="font-pixel text-2xl mt-1 flex items-center gap-2">
              <PhTrophy weight="fill" class="pixel-icon text-primary" /> LV {{ nivel }}
            </div>
          </div>
          <div class="border-2 border-border bg-card p-3">
            <div class="font-pixel text-[9px] text-muted-foreground">PRÓXIMO NÍVEL</div>
            <div class="mt-2"><XPBar :value="xp" /></div>
          </div>
        </div>
      </PixelCard>

      <!-- Achievements -->
      <PixelCard class="p-6">
        <div class="font-pixel text-[10px] text-primary">▶ CONQUISTAS</div>
        <div class="font-pixel text-sm mt-1 mb-4">MEDALHAS</div>
        <div class="grid grid-cols-2 gap-3">
          <div
            v-for="c in conquistas"
            :key="c.id"
            class="border-2 border-border p-3"
            :class="c.desbloqueado ? 'bg-card' : 'bg-muted opacity-50'"
          >
            <PixelBadge :tone="c.tone as any">{{ c.desbloqueado ? '✓' : '?' }} {{ c.nome.toUpperCase() }}</PixelBadge>
            <p class="font-sans text-xs text-foreground/75 mt-2">{{ c.desc }}</p>
          </div>
        </div>
      </PixelCard>
    </section>

    <!-- Marketplace -->
    <section>
      <div class="flex items-end justify-between mb-4 flex-wrap gap-3">
        <div>
          <div class="font-pixel text-[10px] text-primary">▶ MARKETPLACE · WORLD 1-2</div>
          <h2 class="font-pixel text-xl mt-2 flex items-center gap-2">
            <PhStorefront weight="fill" class="pixel-icon" /> VANTAGENS
          </h2>
        </div>
        <div class="font-pixel text-[10px] text-muted-foreground">{{ vantagensFiltradas.length }} DE {{ vantagensIniciais.length }} VANTAGENS</div>
      </div>

      <!-- Filters -->
      <PixelCard class="p-4 mb-5">
        <div class="grid md:grid-cols-[1fr_auto_auto] gap-3 items-stretch">
          <div class="relative">
            <PhMagnifyingGlass weight="bold" :size="16" class="absolute left-2 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none" />
            <PixelInput v-model="busca" placeholder="Buscar vantagem, empresa, descrição..." class="pl-8 pr-8" />
            <button v-if="busca" type="button" class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground" @click="busca = ''">
              <PhX weight="bold" :size="14" />
            </button>
          </div>
          <select v-model="ordenar" class="bg-input border-2 border-border px-3 py-2 font-display text-base focus:outline-none">
            <option value="recentes">ORDENAR: PADRÃO</option>
            <option value="menor">MENOR PREÇO</option>
            <option value="maior">MAIOR PREÇO</option>
            <option value="alfabetica">A → Z</option>
          </select>
          <label class="flex items-center gap-2 font-pixel text-[9px] border-2 border-border bg-card px-3 py-2 cursor-pointer">
            <input v-model="soResgataveis" type="checkbox" class="accent-primary" /> SÓ POSSO RESGATAR
          </label>
        </div>
        <div class="mt-3 flex flex-wrap gap-2">
          <button
            class="border-2 border-border font-pixel text-[9px] px-2 py-1 transition-all"
            :class="categoria === 'todas' ? 'bg-primary text-primary-foreground' : 'bg-card hover:-translate-y-0.5'"
            @click="categoria = 'todas'"
          >TODAS</button>
          <button
            v-for="c in categorias"
            :key="c"
            class="border-2 border-border font-pixel text-[9px] px-2 py-1 transition-all"
            :class="categoria === c ? 'bg-primary text-primary-foreground' : 'bg-card hover:-translate-y-0.5'"
            @click="categoria = c"
          >{{ c.toUpperCase() }}</button>
        </div>
      </PixelCard>

      <PixelCard v-if="vantagensFiltradas.length === 0" class="p-8 text-center">
        <div class="font-pixel text-sm">GAME OVER · NENHUMA VANTAGEM</div>
        <p class="font-sans text-sm text-muted-foreground mt-2">Tente outra busca ou remova os filtros.</p>
        <div class="mt-4">
          <PixelButton variant="ghost" @click="busca = ''; categoria = 'todas'; soResgataveis = false">LIMPAR FILTROS</PixelButton>
        </div>
      </PixelCard>

      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <PixelCard v-for="v in vantagensFiltradas" :key="v.id" class="overflow-hidden flex flex-col">
          <div class="aspect-[5/3] flex items-center justify-center bg-hud text-hud-foreground crt-scanlines border-b-2 border-border text-7xl">
            <span class="animate-bob" style="image-rendering: pixelated">{{ v.emoji }}</span>
          </div>
          <div class="p-4 flex-1 flex flex-col">
            <div class="flex items-start justify-between gap-2">
              <div>
                <div class="font-pixel text-xs">{{ v.nome.toUpperCase() }}</div>
                <div class="font-sans text-xs text-muted-foreground mt-1">{{ v.empresa }}</div>
              </div>
              <PixelBadge tone="gold">{{ v.categoria.toUpperCase() }}</PixelBadge>
            </div>
            <p class="font-sans text-sm text-foreground/75 mt-2 flex-1">{{ v.descricao }}</p>
            <div class="mt-4 flex items-center justify-between">
              <div class="font-pixel text-sm flex items-center gap-1">
                <CoinIcon :size="14" /> {{ v.custo }}
              </div>
              <PixelButton
                size="sm"
                :variant="saldo >= v.custo ? 'success' : 'ghost'"
                :disabled="saldo < v.custo"
                @click="resgate = { nome: v.nome, custo: v.custo }"
              >
                <PhSparkle weight="fill" class="pixel-icon" />
                {{ saldo >= v.custo ? 'RESGATAR' : 'FALTAM MOEDAS' }}
              </PixelButton>
            </div>
          </div>
        </PixelCard>
      </div>
    </section>

    <!-- Ranking -->
    <section>
      <div class="font-pixel text-[10px] text-primary">▶ PLACAR</div>
      <h2 class="font-pixel text-xl mt-2 mb-4 flex items-center gap-2">
        <PhCrown weight="fill" class="pixel-icon text-primary" /> RANKING DA TURMA
      </h2>
      <PixelCard class="p-4">
        <ul class="space-y-2">
          <li
            v-for="(p, i) in ranking.slice(0, 5)"
            :key="p.id"
            class="flex items-center justify-between border-2 border-border bg-card px-3 py-2 gap-3"
          >
            <span class="flex items-center gap-3 min-w-0">
              <PixelBadge :tone="medals[i] || 'teal'">#{{ i + 1 }}</PixelBadge>
              <span class="font-sans text-sm truncate">{{ p.nome }}</span>
              <span class="hidden sm:inline font-pixel text-[9px] text-muted-foreground">LV{{ p.nivel }}</span>
            </span>
            <span class="font-pixel text-xs flex items-center gap-1 shrink-0">
              <CoinIcon :size="12" /> {{ p.moedas }}
            </span>
          </li>
        </ul>
      </PixelCard>
    </section>

    <!-- Confirm modal -->
    <div v-if="resgate" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/70" @click="resgate = null">
      <div class="w-full max-w-md bg-card text-card-foreground border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop" @click.stop>
        <div class="bg-secondary text-secondary-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs">⚠ CONFIRMAR RESGATE</div>
        <div class="p-5">
          <p class="font-display text-xl">Resgatar <span class="text-primary">{{ resgate.nome }}</span> por <span class="inline-flex items-center gap-1"><CoinIcon :size="16" /> {{ resgate.custo }}</span>?</p>
          <p class="font-sans text-sm text-muted-foreground mt-3">Um cupom será gerado no seu extrato. Saldo após resgate: <strong>{{ (saldo - resgate.custo).toLocaleString('pt-BR') }}</strong>.</p>
          <div class="mt-5 flex gap-3">
            <PixelButton variant="ghost" class="flex-1" @click="resgate = null">CANCELAR</PixelButton>
            <PixelButton variant="success" class="flex-1" @click="confirmar"><PhLightning weight="fill" class="pixel-icon" /> CONFIRMAR</PixelButton>
          </div>
        </div>
      </div>
    </div>

    <!-- Coupon modal -->
    <div v-if="cupom" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80" @click="cupom = null">
      <div class="w-full max-w-md bg-card border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop" @click.stop>
        <div class="bg-success text-success-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs">★ RECOMPENSA DESBLOQUEADA!</div>
        <div class="p-6 text-center">
          <div class="flex justify-center mb-3"><CoinIcon :size="48" class="animate-bob" /></div>
          <p class="font-display text-xl">Você resgatou <strong>{{ cupom.nome }}</strong>!</p>
          <div class="mt-5 border-2 border-dashed border-border bg-hud text-hud-foreground p-4">
            <div class="font-pixel text-[9px] mb-2">CUPOM GERADO</div>
            <div class="font-pixel text-lg md:text-xl break-all">{{ cupom.codigo }}</div>
          </div>
          <p class="font-sans text-xs text-muted-foreground mt-3">Apresente este código no estabelecimento parceiro.</p>
          <div class="mt-5">
            <PixelButton variant="primary" class="w-full" @click="cupom = null">FECHAR</PixelButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

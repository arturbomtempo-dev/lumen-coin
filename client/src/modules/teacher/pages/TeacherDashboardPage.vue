<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { PhPaperPlaneTilt, PhGameController, PhSun, PhMoon, PhReceipt, PhGraduationCap, PhArrowLeft, PhLightning, PhUsers, PhMagnifyingGlass } from '@phosphor-icons/vue'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelButton from '@/shared/components/PixelButton.vue'
import PixelInput from '@/shared/components/PixelInput.vue'
import PixelBadge from '@/shared/components/PixelBadge.vue'
import CoinIcon from '@/shared/components/CoinIcon.vue'
import MarioAvatar from '@/shared/components/MarioAvatar.vue'
import type { MarioCharacter } from '@/shared/data/characters'
import { alunos, transacoesIniciais, turmasProfessor, type Transacao } from '@/shared/data/mockData'
import { useThemeStore } from '@/shared/stores/theme.store'
import { toast } from 'vue-sonner'

const themeStore = useThemeStore()

const LIMITE = 1000
const transacoes = ref<Transacao[]>([...transacoesIniciais])
const alunoId = ref(alunos[0].id)
const valor = ref<number | ''>(50)
const motivo = ref('')
const err = ref('')
const turmaFiltro = ref('todas')
const busca = ref('')

const enviadas = computed(() => transacoes.value.filter((t) => t.professor === 'Prof. Carlos M.').reduce((s, t) => s + t.valor, 0))
const disponivel = computed(() => LIMITE - enviadas.value)

const alunosFiltrados = computed(() => alunos.filter((a) => {
  if (turmaFiltro.value !== 'todas' && a.turma !== turmaFiltro.value) return false
  if (busca.value && !a.nome.toLowerCase().includes(busca.value.toLowerCase())) return false
  return true
}))

const avatarFor = (id: string): MarioCharacter => {
  const map: Record<string, MarioCharacter> = { a1: 'peach', a2: 'luigi', a3: 'yoshi', a4: 'mario', a5: 'toad', a6: 'luigi', a7: 'peach', a8: 'bowser' }
  return map[id] ?? 'mario'
}

function submit(e: Event) {
  e.preventDefault()
  err.value = ''
  const v = Number(valor.value)
  if (!v || v <= 0) return void (err.value = 'Informe um valor válido.')
  if (v > disponivel.value) return void (err.value = `Você só tem ${disponivel.value} moedas disponíveis este semestre.`)
  if (!motivo.value.trim()) return void (err.value = 'O motivo é obrigatório.')
  const al = alunos.find((a) => a.id === alunoId.value)!
  const nova: Transacao = { id: 't' + Date.now(), aluno: al.nome, professor: 'Prof. Carlos M.', valor: v, motivo: motivo.value, data: new Date().toISOString().slice(0, 10) }
  transacoes.value.unshift(nova)
  toast.success(`+${v} moedas enviadas para ${al.nome}!`)
  motivo.value = ''
  valor.value = 50
}
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
      <div class="container flex items-center justify-between py-2 gap-3">
        <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px]">
          <PhArrowLeft weight="bold" /> <PhGameController weight="fill" class="pixel-icon" :size="18" />
          PROFESSOR · MESTRE DO ARCADE
        </RouterLink>
        <div class="hidden sm:flex items-center gap-4 font-pixel text-[10px]">
          <span>Prof. Carlos M.</span>
          <span class="flex items-center gap-1"><CoinIcon :size="14" /> {{ disponivel }}/{{ LIMITE }}</span>
        </div>
        <button class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))]" @click="themeStore.toggle()">
          <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
          <PhMoon v-else weight="fill" class="pixel-icon" />
          {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
        </button>
      </div>
    </header>

    <main class="flex-1 container py-6 space-y-8">
      <div>
        <div class="font-pixel text-[10px] text-primary">▶ MESA DE CONTROLE</div>
        <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
          <PhGraduationCap weight="fill" class="pixel-icon" /> PAINEL DO PROFESSOR
        </h1>
      </div>

      <div class="grid md:grid-cols-3 gap-4">
        <PixelCard class="p-5 md:col-span-2 crt-scanlines">
          <div class="font-pixel text-[10px] text-muted-foreground">MOEDAS DISPONÍVEIS · SEMESTRE</div>
          <div class="flex items-baseline gap-3 mt-2">
            <CoinIcon :size="36" class="animate-bob" />
            <span class="font-pixel text-4xl text-primary animate-coin-flash">{{ disponivel }}</span>
            <span class="font-pixel text-sm text-muted-foreground">/ {{ LIMITE }}</span>
          </div>
          <div class="mt-4 h-4 border-2 border-border bg-card overflow-hidden">
            <div class="h-full bg-info transition-all" :style="{ width: `${(disponivel / LIMITE) * 100}%`, backgroundImage: 'repeating-linear-gradient(90deg, hsl(var(--info)) 0, hsl(var(--info)) 6px, hsl(var(--info-shadow)) 6px, hsl(var(--info-shadow)) 8px)' }" />
          </div>
        </PixelCard>
        <PixelCard class="p-5">
          <div class="font-pixel text-[10px] text-muted-foreground">DISTRIBUÍDAS</div>
          <div class="font-pixel text-3xl mt-2 text-success">{{ enviadas }}</div>
          <div class="font-sans text-xs text-muted-foreground mt-1">moedas enviadas este semestre</div>
        </PixelCard>
      </div>

      <div class="grid lg:grid-cols-[1fr_1.3fr] gap-6 items-start">
        <PixelCard class="p-6">
          <div class="font-pixel text-sm mb-4">ENVIAR MOEDAS</div>
          <form class="space-y-4" @submit="submit">
            <div>
              <label class="font-pixel text-[10px] block mb-2">ALUNO</label>
              <select v-model="alunoId" class="w-full bg-input border-2 border-border px-3 py-2 font-display text-lg focus:outline-none">
                <option v-for="a in alunos" :key="a.id" :value="a.id">{{ a.nome }} — {{ a.curso }}</option>
              </select>
            </div>
            <div>
              <label class="font-pixel text-[10px] block mb-2">VALOR (MOEDAS)</label>
              <PixelInput v-model="valor" type="number" :min="1" :max="disponivel" />
            </div>
            <div>
              <label class="font-pixel text-[10px] block mb-2">MOTIVO <span class="text-secondary">*</span></label>
              <PixelInput v-model="motivo" placeholder="Ex: Excelência na apresentação do projeto" />
            </div>
            <div v-if="err" class="font-pixel text-[10px] text-destructive">⚠ {{ err }}</div>
            <PixelButton variant="success" class="w-full" type="submit">
              <PhPaperPlaneTilt weight="fill" class="pixel-icon" /> ENVIAR RECONHECIMENTO
            </PixelButton>
          </form>
        </PixelCard>

        <PixelCard class="overflow-hidden">
          <div class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] flex items-center gap-2">
            <PhReceipt weight="fill" /> HISTÓRICO DE ENVIOS
          </div>
          <div class="max-h-[500px] overflow-auto">
            <div v-for="t in transacoes" :key="t.id" class="px-4 py-3 border-t-2 border-border grid grid-cols-[1fr_auto] gap-2">
              <div>
                <div class="font-pixel text-[10px]">{{ t.aluno }}</div>
                <div class="font-sans text-sm text-foreground/80 mt-1">{{ t.motivo }}</div>
                <div class="font-pixel text-[9px] text-muted-foreground mt-1">{{ t.data }} · {{ t.professor }}</div>
              </div>
              <div class="font-pixel text-sm flex items-center gap-1 text-success self-start">
                +<CoinIcon :size="12" /> {{ t.valor }}
              </div>
            </div>
          </div>
        </PixelCard>
      </div>

      <div>
        <div class="font-pixel text-[10px] text-primary mb-3">▶ ATALHOS</div>
        <div class="flex flex-wrap gap-3">
          <PixelBadge tone="gold"><PhLightning weight="fill" class="pixel-icon" /> LIMITE 1000/SEM.</PixelBadge>
          <PixelBadge tone="blue">{{ alunos.length }} ALUNOS NAS MINHAS TURMAS</PixelBadge>
          <PixelBadge tone="green">100% APROVEITAMENTO</PixelBadge>
        </div>
      </div>

      <section>
        <div class="font-pixel text-[10px] text-primary mb-2">▶ MINHAS TURMAS</div>
        <h2 class="font-pixel text-lg flex items-center gap-2">
          <PhUsers weight="fill" class="pixel-icon" /> ALUNOS POR TURMA
        </h2>
        <div class="mt-4 grid md:grid-cols-[1fr_auto] gap-3 items-end">
          <div class="flex flex-wrap gap-2">
            <button
              class="border-2 border-border font-pixel text-[9px] px-2 py-1"
              :class="turmaFiltro === 'todas' ? 'bg-primary text-primary-foreground' : 'bg-card'"
              @click="turmaFiltro = 'todas'"
            >TODAS ({{ alunos.length }})</button>
            <button
              v-for="t in turmasProfessor"
              :key="t.id"
              class="border-2 border-border font-pixel text-[9px] px-2 py-1"
              :class="turmaFiltro === t.id ? 'bg-primary text-primary-foreground' : 'bg-card'"
              @click="turmaFiltro = t.id"
            >{{ t.id }} ({{ alunos.filter(a => a.turma === t.id).length }})</button>
          </div>
          <div class="relative w-full md:w-64">
            <PhMagnifyingGlass weight="bold" class="absolute left-2 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none" :size="16" />
            <PixelInput v-model="busca" placeholder="Buscar aluno..." class="pl-8" />
          </div>
        </div>

        <div class="mt-5 grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          <PixelCard
            v-for="t in turmasProfessor.filter(t => turmaFiltro === 'todas' || t.id === turmaFiltro)"
            :key="t.id"
            class="overflow-hidden"
          >
            <div class="bg-hud text-hud-foreground px-3 py-2 border-b-2 border-border">
              <div class="font-pixel text-[10px] text-primary">{{ t.id }} · {{ t.periodo }}</div>
              <div class="font-pixel text-xs mt-1">{{ t.disciplina.toUpperCase() }}</div>
              <div class="font-sans text-xs opacity-80 mt-0.5">{{ t.nome }}</div>
            </div>
            <ul class="divide-y-2 divide-border">
              <li v-if="alunosFiltrados.filter(a => a.turma === t.id).length === 0" class="p-3 font-sans text-sm text-muted-foreground text-center">
                Sem alunos nesse filtro.
              </li>
              <li v-for="a in alunosFiltrados.filter(a => a.turma === t.id)" :key="a.id" class="p-3 flex items-center gap-3">
                <div class="border-2 border-border bg-hud p-1 shrink-0">
                  <MarioAvatar :character="avatarFor(a.id)" :size="32" />
                </div>
                <div class="min-w-0 flex-1">
                  <div class="font-pixel text-[10px] truncate">{{ a.nome }}</div>
                  <div class="font-sans text-xs text-muted-foreground truncate">LV {{ a.nivel }} · {{ a.curso }}</div>
                </div>
                <div class="flex flex-col items-end gap-1">
                  <span class="font-pixel text-[10px] flex items-center gap-1"><CoinIcon :size="10" /> {{ a.moedas }}</span>
                  <button type="button" class="font-pixel text-[8px] text-primary hover:underline" @click="alunoId = a.id">SELECIONAR</button>
                </div>
              </li>
            </ul>
          </PixelCard>
        </div>
      </section>
    </main>
  </div>
</template>

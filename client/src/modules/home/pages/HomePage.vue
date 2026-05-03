<script setup lang="ts">
import { RouterLink } from 'vue-router'
import {
  PhGameController, PhSun, PhMoon, PhSparkle, PhTrophy, PhCoin, PhStorefront,
  PhGraduationCap, PhStudent, PhBuildings, PhLightning, PhShieldCheck,
  PhTicket, PhChartBar, PhHeart, PhCrown,
} from '@phosphor-icons/vue'
import PixelButton from '@/shared/components/PixelButton.vue'
import PixelCard from '@/shared/components/PixelCard.vue'
import PixelBadge from '@/shared/components/PixelBadge.vue'
import MarioAvatar from '@/shared/components/MarioAvatar.vue'
import { MARIO_CHARACTERS } from '@/shared/data/characters'
import { useThemeStore } from '@/shared/stores/theme.store'

const themeStore = useThemeStore()
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <!-- HUD Header -->
    <header class="bg-hud text-hud-foreground border-b-4 border-border">
      <div class="container flex items-center justify-between py-3 gap-3">
        <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px]">
          <PhGameController weight="fill" class="pixel-icon text-primary" :size="18" />
          LUMEN COIN · MUSHROOM ACADEMY
        </RouterLink>
        <nav class="hidden md:flex items-center gap-4 font-pixel text-[9px]">
          <a href="#regras" class="hover:text-primary">REGRAS</a>
          <a href="#personagens" class="hover:text-primary">PERSONAGENS</a>
          <a href="#perfis" class="hover:text-primary">PERFIS</a>
        </nav>
        <div class="flex items-center gap-2">
          <button
            class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-[2px] active:translate-y-[2px] active:shadow-none"
            @click="themeStore.toggle"
          >
            <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
            <PhMoon v-else weight="fill" class="pixel-icon" />
            {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
          </button>
          <RouterLink to="/login">
            <PixelButton size="sm" variant="primary">
              <PhSparkle weight="fill" class="pixel-icon" /> ENTRAR
            </PixelButton>
          </RouterLink>
        </div>
      </div>
    </header>

    <!-- Hero -->
    <section class="relative overflow-hidden border-b-4 border-border crt-scanlines">
      <div
        class="absolute inset-0 opacity-25 pointer-events-none"
        :style="{ backgroundImage: 'repeating-linear-gradient(0deg, hsl(var(--border)/0.15) 0 1px, transparent 1px 28px), repeating-linear-gradient(90deg, hsl(var(--border)/0.15) 0 1px, transparent 1px 28px)' }"
      />
      <div class="container py-14 md:py-20 grid lg:grid-cols-[1.2fr_1fr] gap-10 items-center relative">
        <div>
          <div class="font-pixel text-[10px] text-primary mb-3">▶ WORLD 1-1 · BEM-VINDO</div>
          <h1 class="font-pixel text-3xl md:text-5xl leading-tight">
            <span class="text-secondary">RECONHECIMENTO</span><br />
            <span class="text-primary">VIRA MOEDA</span>
          </h1>
          <p class="font-sans text-base md:text-lg mt-5 max-w-xl text-foreground/85">
            Lumen Coin é um sistema gamificado onde professores reconhecem alunos com moedas a cada conquista acadêmica.
            Os alunos trocam essas moedas por vantagens reais oferecidas por empresas parceiras —
            tudo em um arcade inspirado no Reino dos Cogumelos.
          </p>
          <div class="mt-6 flex flex-wrap gap-3">
            <RouterLink to="/login">
              <PixelButton variant="primary" size="lg"><PhSparkle weight="fill" class="pixel-icon" /> PRESS START</PixelButton>
            </RouterLink>
            <RouterLink to="/signup">
              <PixelButton variant="success" size="lg"><PhTrophy weight="fill" class="pixel-icon" /> NOVO PERSONAGEM</PixelButton>
            </RouterLink>
          </div>
          <div class="mt-6 flex flex-wrap gap-2">
            <PixelBadge tone="gold"><PhCoin weight="fill" class="pixel-icon" /> 1000 MOEDAS / SEMESTRE</PixelBadge>
            <PixelBadge tone="blue"><PhShieldCheck weight="fill" class="pixel-icon" /> CUPONS ÚNICOS</PixelBadge>
            <PixelBadge tone="green"><PhHeart weight="fill" class="pixel-icon" /> SEM TAXAS</PixelBadge>
          </div>
        </div>
        <div class="grid grid-cols-3 gap-3">
          <PixelCard v-for="c in MARIO_CHARACTERS" :key="c.id" class="p-3 text-center">
            <div class="border-2 border-border bg-hud p-2 flex items-center justify-center">
              <MarioAvatar :character="c.id" :size="64" />
            </div>
            <div class="font-pixel text-[9px] mt-2">{{ c.nome.toUpperCase() }}</div>
          </PixelCard>
        </div>
      </div>
    </section>

    <!-- Stats -->
    <section class="bg-hud text-hud-foreground border-b-4 border-border">
      <div class="container py-6 grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
        <div v-for="s in [{ v: '1000', l: 'MOEDAS / PROF / SEM' }, { v: '50+', l: 'VANTAGENS PARCEIRAS' }, { v: '8-BIT', l: 'EXPERIÊNCIA' }, { v: '∞', l: 'POSSIBILIDADES' }]" :key="s.l">
          <div class="font-pixel text-2xl text-primary">{{ s.v }}</div>
          <div class="font-pixel text-[9px] mt-1 opacity-80">{{ s.l }}</div>
        </div>
      </div>
    </section>

    <!-- Como funciona -->
    <section class="container py-14">
      <div class="font-pixel text-[10px] text-primary text-center">▶ COMO FUNCIONA</div>
      <h2 class="font-pixel text-2xl text-center mt-3">A AVENTURA EM 3 FASES</h2>
      <div class="grid md:grid-cols-3 gap-5 mt-8">
        <PixelCard v-for="step in [
          { icon: PhGraduationCap, t: '1. PROFESSOR ENVIA', d: 'Cada professor recebe 1000 moedas por semestre e distribui aos alunos com um motivo claro de reconhecimento.' },
          { icon: PhCoin, t: '2. ALUNO ACUMULA', d: 'O aluno recebe notificações, sobe de nível, ganha XP e acompanha tudo no extrato gamificado.' },
          { icon: PhStorefront, t: '3. EMPRESA RESGATA', d: 'Vantagens das empresas parceiras viram cupons únicos que o aluno apresenta no estabelecimento.' },
        ]" :key="step.t" class="p-6">
          <div class="border-2 border-border bg-hud text-hud-foreground inline-flex p-3">
            <component :is="step.icon" weight="fill" class="pixel-icon text-primary" :size="28" />
          </div>
          <div class="font-pixel text-sm mt-4">{{ step.t }}</div>
          <p class="font-sans text-sm text-foreground/80 mt-2">{{ step.d }}</p>
        </PixelCard>
      </div>
    </section>

    <!-- Perfis/Regras -->
    <section id="regras" class="border-t-4 border-border bg-card">
      <div class="container py-14">
        <div class="font-pixel text-[10px] text-primary text-center">▶ REGRAS DO ARCADE</div>
        <h2 class="font-pixel text-2xl text-center mt-3">REGRAS POR PERFIL</h2>
        <div id="perfis" class="grid md:grid-cols-2 lg:grid-cols-4 gap-5 mt-10">
          <PixelCard class="p-6">
            <div class="flex items-center gap-3 mb-4">
              <div class="border-2 border-border bg-primary text-primary-foreground p-2"><PhBuildings weight="fill" class="pixel-icon" :size="24" /></div>
              <div><div class="font-pixel text-[10px] text-primary">PLAYER 0</div><div class="font-pixel text-sm">INSTITUIÇÃO</div></div>
            </div>
            <p class="font-sans text-sm text-foreground/80">O castelo do reino. Cria cursos, cadastra professores e acompanha alunos matriculados.</p>
            <ul class="mt-4 space-y-2 font-sans text-sm">
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Cadastra e gerencia <strong>cursos</strong> oferecidos.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Cadastra <strong>professores</strong> na plataforma.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Vincula professores a um ou mais cursos.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Visualiza todos os alunos matriculados por curso.</li>
            </ul>
            <div class="mt-5"><RouterLink to="/app/instituicao"><PixelButton variant="primary" class="w-full"><PhBuildings weight="fill" class="pixel-icon" /> ENTRAR COMO INSTITUIÇÃO</PixelButton></RouterLink></div>
          </PixelCard>

          <PixelCard class="p-6">
            <div class="flex items-center gap-3 mb-4">
              <div class="border-2 border-border bg-info text-info-foreground p-2"><PhStudent weight="fill" class="pixel-icon" :size="24" /></div>
              <div><div class="font-pixel text-[10px] text-primary">PLAYER 1</div><div class="font-pixel text-sm">ALUNO</div></div>
            </div>
            <p class="font-sans text-sm text-foreground/80">O herói da jornada. Recebe moedas pelo bom desempenho e troca por recompensas no marketplace.</p>
            <ul class="mt-4 space-y-2 font-sans text-sm">
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Cadastro grátis com nome, e-mail, CPF, RG, endereço, instituição e curso.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Recebe moedas de qualquer professor da sua instituição.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Cupons gerados são únicos e devem ser apresentados na empresa parceira.</li>
            </ul>
            <div class="mt-5"><RouterLink to="/app/aluno"><PixelButton variant="info" class="w-full"><PhLightning weight="fill" class="pixel-icon" /> ENTRAR COMO ALUNO</PixelButton></RouterLink></div>
          </PixelCard>

          <PixelCard class="p-6">
            <div class="flex items-center gap-3 mb-4">
              <div class="border-2 border-border bg-secondary text-secondary-foreground p-2"><PhGraduationCap weight="fill" class="pixel-icon" :size="24" /></div>
              <div><div class="font-pixel text-[10px] text-primary">PLAYER 2</div><div class="font-pixel text-sm">PROFESSOR</div></div>
            </div>
            <p class="font-sans text-sm text-foreground/80">O mestre do arcade. Reconhece bons alunos com moedas e mantém o histórico transparente.</p>
            <ul class="mt-4 space-y-2 font-sans text-sm">
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Recebe <strong>1000 moedas por semestre</strong> para distribuir.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Cada envio exige um <strong>motivo obrigatório</strong> que o aluno verá.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Histórico completo de envios fica disponível no painel.</li>
            </ul>
            <div class="mt-5"><RouterLink to="/app/professor"><PixelButton variant="primary" class="w-full"><PhChartBar weight="fill" class="pixel-icon" /> ENTRAR COMO PROFESSOR</PixelButton></RouterLink></div>
          </PixelCard>

          <PixelCard class="p-6">
            <div class="flex items-center gap-3 mb-4">
              <div class="border-2 border-border bg-accent text-accent-foreground p-2"><PhBuildings weight="fill" class="pixel-icon" :size="24" /></div>
              <div><div class="font-pixel text-[10px] text-primary">PLAYER 3</div><div class="font-pixel text-sm">EMPRESA PARCEIRA</div></div>
            </div>
            <p class="font-sans text-sm text-foreground/80">A loja do reino. Cria vantagens e valida cupons gerados pelos alunos no resgate.</p>
            <ul class="mt-4 space-y-2 font-sans text-sm">
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Pode criar e ativar/desativar vantagens a qualquer momento.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Valida cupons únicos no portal antes de liberar a recompensa.</li>
              <li class="flex items-start gap-1"><span class="text-primary mt-0.5">▶</span> Cada cupom só pode ser usado uma vez.</li>
            </ul>
            <div class="mt-5"><RouterLink to="/app/empresa"><PixelButton variant="success" class="w-full"><PhTicket weight="fill" class="pixel-icon" /> ENTRAR COMO EMPRESA</PixelButton></RouterLink></div>
          </PixelCard>
        </div>
      </div>
    </section>

    <!-- Personagens -->
    <section id="personagens" class="container py-14">
      <div class="font-pixel text-[10px] text-primary text-center">▶ ROSTER</div>
      <h2 class="font-pixel text-2xl text-center mt-3 flex items-center justify-center gap-2">
        <PhCrown weight="fill" class="pixel-icon text-primary" /> ESCOLHA SEU AVATAR
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mt-8">
        <PixelCard v-for="c in MARIO_CHARACTERS" :key="c.id" class="p-4 text-center">
          <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
            <MarioAvatar :character="c.id" :size="72" />
          </div>
          <div class="font-pixel text-[10px] mt-3">{{ c.nome.toUpperCase() }}</div>
          <div class="font-sans text-xs text-muted-foreground mt-1">{{ c.classe }}</div>
        </PixelCard>
      </div>
    </section>

    <!-- CTA -->
    <section class="border-t-4 border-border bg-hud text-hud-foreground">
      <div class="container py-12 text-center">
        <h3 class="font-pixel text-2xl">PRONTO PARA INICIAR A AVENTURA?</h3>
        <p class="font-display text-xl mt-3 opacity-90">Insert coin to continue. Sua jornada acadêmica nunca foi tão divertida.</p>
        <div class="mt-6 flex flex-wrap justify-center gap-3">
          <RouterLink to="/signup"><PixelButton variant="primary" size="lg"><PhTrophy weight="fill" class="pixel-icon" /> CRIAR PERSONAGEM</PixelButton></RouterLink>
          <RouterLink to="/login"><PixelButton variant="success" size="lg"><PhSparkle weight="fill" class="pixel-icon" /> JÁ TENHO CONTA</PixelButton></RouterLink>
        </div>
      </div>
    </section>

    <footer class="border-t-4 border-border bg-card">
      <div class="container py-4 flex items-center justify-between flex-wrap gap-2 font-pixel text-[9px]">
        <span>© 2025 LUMEN COIN — INSERT COIN TO CONTINUE</span>
      </div>
    </footer>
  </div>
</template>

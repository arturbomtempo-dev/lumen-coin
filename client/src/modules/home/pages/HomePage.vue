<script setup lang="ts">
import { RouterLink } from 'vue-router';
import {
    PhSun,
    PhMoon,
    PhSparkle,
    PhTrophy,
    PhCoin,
    PhStorefront,
    PhBuildings,
    PhGraduationCap,
    PhStudent,
    PhLightning,
    PhTicket,
    PhChartBar,
    PhCrown,
    PhList,
    PhX,
} from '@phosphor-icons/vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import { computed, ref } from 'vue';
import HeroScenario from '@/shared/components/HeroScenario.vue';
import { MARIO_CHARACTERS } from '@/shared/data/characters';
import { useThemeStore } from '@/shared/stores/theme.store';

const themeStore = useThemeStore();
const VISIBLE_CHARACTERS = computed(() =>
    MARIO_CHARACTERS.filter((c) => c.id !== 'company' && c.id !== 'institution')
);

const mobileMenuOpen = ref(false);

const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
    }
};
</script>

<template>
    <div class="min-h-screen flex flex-col">
        <header class="bg-hud text-hud-foreground border-b-4 border-border sticky top-0 z-10">
            <div class="px-6 md:px-12 flex items-center justify-between py-3 gap-3">
                <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px]">
                    <PhCoin weight="fill" class="pixel-icon text-primary" :size="18" />
                    LUMEN COIN
                </RouterLink>
                <nav class="hidden md:flex items-center gap-4 font-pixel text-[9px]">
                    <a @click="scrollToSection('sobre')" class="hover:text-primary">SOBRE</a>
                    <a @click="scrollToSection('regras')" class="hover:text-primary">REGRAS</a>
                    <a @click="scrollToSection('personagens')" class="hover:text-primary"
                        >PERSONAGENS</a
                    >
                </nav>
                <div class="flex items-center gap-2">
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-0.5 active:translate-y-0.5 active:shadow-none"
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

                    <div class="hidden md:flex items-center gap-2">
                        <RouterLink to="/signup">
                            <PixelButton size="sm" variant="success">CRIAR CONTA</PixelButton>
                        </RouterLink>
                        <RouterLink to="/login">
                            <PixelButton size="sm" variant="primary">
                                <PhSparkle weight="fill" class="pixel-icon" /> ENTRAR
                            </PixelButton>
                        </RouterLink>
                    </div>

                    <button
                        class="md:hidden font-pixel flex items-center gap-1 border-2 border-border text-white px-2 py-1"
                        aria-label="Menu"
                        @click="mobileMenuOpen = true"
                    >
                        <PhList weight="bold" :size="22" />
                    </button>
                </div>
            </div>
        </header>

        <div
            v-if="mobileMenuOpen"
            class="fixed inset-0 z-20 bg-black/60 md:hidden"
            @click="mobileMenuOpen = false"
        />

        <aside
            class="fixed inset-y-0 right-0 z-30 w-56 flex flex-col md:hidden transition-transform duration-200 bg-hud text-hud-foreground border-l-4 border-border"
            :class="mobileMenuOpen ? 'translate-x-0' : 'translate-x-full'"
        >
            <div class="px-5 py-4 flex items-center justify-between border-b-2 border-border">
                <span class="font-pixel text-[10px] text-primary">MENU</span>
                <button aria-label="Fechar menu" @click="mobileMenuOpen = false">
                    <PhX weight="bold" :size="16" />
                </button>
            </div>
            <nav class="flex-1 p-4 space-y-2">
                <a
                    @click="
                        scrollToSection('sobre');
                        mobileMenuOpen = false;
                    "
                    class="block font-pixel text-[9px] px-3 py-2 border-2 border-transparent hover:border-border hover:bg-card hover:text-card-foreground transition-colors"
                    >SOBRE</a
                >
                <a
                    @click="
                        scrollToSection('regras');
                        mobileMenuOpen = false;
                    "
                    class="block font-pixel text-[9px] px-3 py-2 border-2 border-transparent hover:border-border hover:bg-card hover:text-card-foreground transition-colors"
                    >REGRAS</a
                >
                <a
                    @click="
                        scrollToSection('personagens');
                        mobileMenuOpen = false;
                    "
                    class="block font-pixel text-[9px] px-3 py-2 border-2 border-transparent hover:border-border hover:bg-card hover:text-card-foreground transition-colors"
                    >PERSONAGENS</a
                >
            </nav>
            <div class="p-4 border-t-2 flex flex-col gap-2 border-border space-y-2">
                <RouterLink to="/signup"
                    ><PixelButton variant="success" class="w-full"
                        >CRIAR CONTA</PixelButton
                    ></RouterLink
                >
                <RouterLink to="/login"
                    ><PixelButton variant="primary" class="w-full"
                        ><PhSparkle weight="fill" class="pixel-icon" /> ENTRAR</PixelButton
                    ></RouterLink
                >
            </div>
        </aside>

        <section class="relative overflow-hidden border-b-4 border-border crt-scanlines">
            <div
                class="absolute inset-0 opacity-25 pointer-events-none"
                :style="{
                    backgroundImage:
                        'repeating-linear-gradient(0deg, hsl(var(--border)/0.15) 0 1px, transparent 1px 28px), repeating-linear-gradient(90deg, hsl(var(--border)/0.15) 0 1px, transparent 1px 28px)',
                }"
            />
            <div
                class="p-5 mx-auto sm:container py-14 md:py-20 grid lg:grid-cols-[1.2fr_1fr] gap-10 items-center relative"
            >
                <div>
                    <h1 class="font-pixel text-3xl md:text-5xl leading-tight tracking-wider">
                        <span
                            class="flex items-center flex-wrap justify-center md:justify-start text-2xl text-center md:text-start md:text-4xl items"
                            aria-hidden="true"
                        >
                            <span class="text-secondary uppercase"
                                >Reconhecimento

                                <span class="text-amber-500">vira recompensa</span></span
                            >
                        </span>
                        <span class="sr-only">Lumen Coin</span>
                    </h1>
                    <p
                        class="font-sans justify-center md:justify-start w-full mx-auto md:mx-0 text-center md:text-start text-lg md:text-lg mt-5 max-w-xl text-foreground/85"
                    >
                        Lumen Coin é um sistema gamificado onde professores reconhecem alunos com
                        moedas a cada conquista acadêmica. Os alunos trocam essas moedas por
                        vantagens reais oferecidas por empresas parceiras — tudo em um arcade
                        inspirado no Reino dos Cogumelos.
                    </p>
                    <div class="mt-6 flex flex-wrap gap-3 justify-center md:justify-start">
                        <RouterLink to="/login">
                            <PixelButton variant="primary" size="lg"
                                ><PhSparkle weight="fill" class="pixel-icon" /> PRESS
                                START</PixelButton
                            >
                        </RouterLink>
                        <RouterLink to="/signup">
                            <PixelButton variant="success" size="lg"
                                ><PhTrophy weight="fill" class="pixel-icon" /> NOVO
                                PERSONAGEM</PixelButton
                            >
                        </RouterLink>
                    </div>
                </div>
                <HeroScenario />
            </div>
        </section>

        <section class="bg-hud text-white">
            <div class="container py-14">
                <div class="font-pixel text-[10px] text-primary text-center">▶ COMO FUNCIONA</div>
                <h2 class="font-pixel text-2xl text-center mt-3">A AVENTURA EM 3 FASES</h2>
                <div class="grid md:grid-cols-3 gap-5 mt-8">
                    <PixelCard
                        v-for="step in [
                            {
                                icon: PhGraduationCap,
                                t: '1. PROFESSOR ENVIA',
                                d: 'Cada professor recebe 1000 moedas por semestre e distribui aos alunos com um motivo claro de reconhecimento.',
                            },
                            {
                                icon: PhCoin,
                                t: '2. ALUNO ACUMULA',
                                d: 'O aluno recebe notificações, sobe de nível, ganha XP e acompanha tudo no extrato gamificado.',
                            },
                            {
                                icon: PhStorefront,
                                t: '3. EMPRESA RESGATA',
                                d: 'Vantagens das empresas parceiras viram cupons únicos que o aluno apresenta no estabelecimento.',
                            },
                        ]"
                        :key="step.t"
                        class="p-6"
                    >
                        <div
                            class="border-2 border-border bg-hud text-hud-foreground inline-flex p-3"
                        >
                            <component
                                :is="step.icon"
                                weight="fill"
                                class="pixel-icon text-primary"
                                :size="28"
                            />
                        </div>
                        <div class="font-pixel text-sm mt-4">{{ step.t }}</div>
                        <p class="font-sans text-sm text-foreground/80 mt-2">{{ step.d }}</p>
                    </PixelCard>
                </div>
            </div>
        </section>

        <section id="sobre" class="bg-card text-foreground">
            <div class="container py-10">
                <div class="max-w-4xl mx-auto text-center">
                    <h3 class="font-pixel text-primary text-2xl">SOBRE O SISTEMA</h3>
                    <p class="font-sans mt-4 text-lg text-foreground/80 mb-6">
                        Lumen Coin é uma plataforma inspirada no jogo super mario e foi desenvolvida
                        para reconhecer e premiar esforços acadêmicos. Tenha uma experiencia
                        gamificada que conecta alunos, professores e empresas em uma jornada de
                        conquistas e recompensas.
                    </p>
                    <a @click="scrollToSection('regras')" class="inline-block cursor-pointer">
                        <PixelButton variant="primary" size="lg"> Conheça os perfis</PixelButton>
                    </a>
                </div>
            </div>
        </section>

        <section id="regras" class="border-t-2 border-border">
            <div class="container py-14">
                <div class="font-pixel text-[10px] text-primary text-center">
                    ▶ REGRAS DO ARCADE
                </div>
                <h2 class="font-pixel text-2xl text-center mt-3">REGRAS POR PERFIL</h2>
                <p class="font-sans text-lg text-center max-w-2xl mx-auto mt-3 text-foreground/75">
                    Cada perfil tem suas missões, poderes e limites. Conheça as regras antes de
                    começar a jogar.
                </p>
                <div id="perfis" class="grid md:grid-cols-2 max-w-5xl mx-auto gap-5 mt-10">
                    <PixelCard class="p-6 flex flex-col">
                        <div class="flex items-center gap-3 mb-4">
                            <div
                                class="border-2 border-border bg-primary text-primary-foreground p-2"
                            >
                                <PhBuildings weight="fill" class="pixel-icon" :size="24" />
                            </div>
                            <div>
                                <div class="font-pixel text-sm">INSTITUIÇÃO</div>
                            </div>
                        </div>
                        <div class="flex-1 flex flex-col justify-center">
                            <p class="font-sans text-md text-foreground/80">
                                O castelo do reino. Cria cursos, cadastra professores e acompanha
                                alunos matriculados.
                            </p>
                            <ul class="mt-4 space-y-2 font-sans text-md">
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cadastra e gerencia
                                        <strong>cursos</strong> oferecidos.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cadastra <strong>professores</strong> na plataforma.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Vincula professores a um ou mais cursos.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Visualiza todos os alunos matriculados por curso.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Pode remover professores e cursos quando necessário.</span
                                    >
                                </li>
                            </ul>
                        </div>
                        <div class="mt-auto pt-5">
                            <RouterLink to="/app/instituicao"
                                ><PixelButton variant="primary" class="w-full"
                                    ><PhBuildings weight="fill" class="pixel-icon" /> ENTRAR COMO
                                    INSTITUIÇÃO</PixelButton
                                ></RouterLink
                            >
                        </div>
                    </PixelCard>

                    <PixelCard class="p-6 flex flex-col">
                        <div class="flex items-center gap-3 mb-4">
                            <div
                                class="border-2 border-border bg-secondary text-secondary-foreground p-2"
                            >
                                <PhGraduationCap weight="fill" class="pixel-icon" :size="24" />
                            </div>
                            <div>
                                <div class="font-pixel text-sm">PROFESSOR</div>
                            </div>
                        </div>
                        <div class="flex-1 flex flex-col justify-center">
                            <p class="font-sans text-md text-foreground/80">
                                O mestre do arcade. Reconhece bons alunos com moedas e mantém o
                                histórico transparente.
                            </p>
                            <ul class="mt-4 space-y-2 font-sans text-md">
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cadastro feito pela instituição (não público).</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Recebe <strong>1000 moedas por semestre</strong> para
                                        distribuir.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Pode visualizar todos os alunos das turmas em que
                                        leciona.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cada envio exige um <strong>motivo obrigatório</strong> que
                                        o aluno verá.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Saldo não distribuído não acumula no semestre
                                        seguinte.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Histórico completo de envios fica disponível no
                                        painel.</span
                                    >
                                </li>
                            </ul>
                        </div>
                        <div class="mt-auto pt-5">
                            <RouterLink to="/app/professor"
                                ><PixelButton variant="danger" class="w-full"
                                    ><PhChartBar weight="fill" class="pixel-icon" /> ENTRAR COMO
                                    PROFESSOR</PixelButton
                                ></RouterLink
                            >
                        </div>
                    </PixelCard>

                    <PixelCard class="p-6 flex flex-col">
                        <div class="flex items-center gap-3 mb-4">
                            <div class="border-2 border-border bg-info text-info-foreground p-2">
                                <PhStudent weight="fill" class="pixel-icon" :size="24" />
                            </div>
                            <div>
                                <div class="font-pixel text-sm">ALUNO</div>
                            </div>
                        </div>
                        <div class="flex-1 flex flex-col justify-center">
                            <p class="font-sans text-md text-foreground/80">
                                O herói da jornada. Recebe moedas pelo bom desempenho e troca por
                                recompensas no marketplace.
                            </p>
                            <ul class="mt-4 space-y-2 font-sans text-md">
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cadastro grátis com nome, e-mail, CPF, RG, endereço,
                                        instituição e curso.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Recebe moedas de qualquer professor da sua
                                        instituição.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cada moeda recebida gera notificação com o motivo e o nome
                                        do professor.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Pode resgatar vantagens enquanto houver saldo
                                        suficiente.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cupons gerados são únicos e devem ser apresentados na
                                        empresa parceira.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Saldo nunca expira; conquistas e nível ficam no
                                        perfil.</span
                                    >
                                </li>
                            </ul>
                        </div>
                        <div class="mt-auto pt-5">
                            <RouterLink to="/app/aluno"
                                ><PixelButton variant="info" class="w-full"
                                    ><PhLightning weight="fill" class="pixel-icon" /> ENTRAR COMO
                                    ALUNO</PixelButton
                                ></RouterLink
                            >
                        </div>
                    </PixelCard>

                    <PixelCard class="p-6 flex flex-col">
                        <div class="flex items-center gap-3 mb-4">
                            <div
                                class="border-2 border-border bg-success text-success-foreground p-2"
                            >
                                <PhStorefront weight="fill" class="pixel-icon" :size="24" />
                            </div>
                            <div>
                                <div class="font-pixel text-sm">EMPRESA PARCEIRA</div>
                            </div>
                        </div>
                        <div class="flex-1 flex flex-col justify-center">
                            <p class="font-sans text-md text-foreground/80">
                                A loja do reino. Cria vantagens e valida cupons gerados pelos alunos
                                no resgate.
                            </p>
                            <ul class="mt-4 space-y-2 font-sans text-md">
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cadastro mediante aprovação para virar parceiro
                                        oficial.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Pode criar quantas vantagens quiser, com nome, descrição,
                                        custo e imagem.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Pode <strong>ativar/desativar</strong> qualquer vantagem a
                                        qualquer momento.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Valida cupons únicos no portal antes de liberar a
                                        recompensa.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Cada cupom só pode ser usado uma vez.</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <span class="text-primary font-pixel text-[10px] mt-0.5"
                                        >►</span
                                    >
                                    <span class="text-foreground/85"
                                        >Sem cobrança: a empresa apenas honra a vantagem
                                        oferecida.</span
                                    >
                                </li>
                            </ul>
                        </div>
                        <div class="mt-auto pt-5">
                            <RouterLink to="/app/empresa"
                                ><PixelButton variant="success" class="w-full"
                                    ><PhTicket weight="fill" class="pixel-icon" /> ENTRAR COMO
                                    EMPRESA</PixelButton
                                ></RouterLink
                            >
                        </div>
                    </PixelCard>
                </div>
            </div>
        </section>

        <section id="personagens" class="container py-14">
            <div class="font-pixel text-[10px] text-primary text-center">▶ ROSTER</div>
            <h2 class="font-pixel text-2xl text-center mt-3 flex items-center justify-center gap-2">
                <PhCrown weight="fill" class="pixel-icon text-primary" /> ESCOLHA SEU AVATAR
            </h2>
            <p class="font-sans text-md text-center max-w-2xl mx-auto mt-3 text-foreground/75">
                Inspirados nos heróis do Reino dos Cogumelos. Você escolhe seu personagem na criação
                da conta e pode trocar quando quiser no perfil.
            </p>
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-6 gap-4 mt-8">
                <PixelCard v-for="c in VISIBLE_CHARACTERS" :key="c.id" class="p-4 text-center">
                    <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
                        <MarioAvatar :character="c.id" :size="72" />
                    </div>
                    <div class="font-pixel text-[10px] mt-3">{{ c.name.toUpperCase() }}</div>
                    <div class="font-sans text-xs text-muted-foreground mt-1">{{ c.title }}</div>
                </PixelCard>
            </div>
        </section>

        <section class="border-t-4 border-border bg-hud text-hud-foreground">
            <div class="container py-12 text-center">
                <h3 class="font-pixel text-lg md:text-2xl">PRONTO PARA INICIAR A AVENTURA?</h3>
                <p class="font-display text-lg md:text-xl mt-3 opacity-90">
                    Sua jornada acadêmica nunca foi tão divertida.
                </p>
                <div class="mt-6 flex flex-wrap justify-center gap-3">
                    <RouterLink to="/signup"
                        ><PixelButton variant="primary" size="lg"
                            ><PhTrophy weight="fill" class="pixel-icon" /> CRIAR
                            PERSONAGEM</PixelButton
                        ></RouterLink
                    >
                    <RouterLink to="/login"
                        ><PixelButton variant="success" size="lg"
                            ><PhSparkle weight="fill" class="pixel-icon" /> JÁ TENHO
                            CONTA</PixelButton
                        ></RouterLink
                    >
                </div>
            </div>
        </section>

        <footer class="border-t-4 border-border bg-hud/93 text-hud-foreground mt-auto">
            <div
                class="container py-4 flex items-center justify-between flex-wrap gap-2 font-pixel text-[9px]"
            >
                <span>© 2026 LUMEN COIN</span>
                <RouterLink to="/design-system" class="hover:text-primary"
                    >DESIGN SYSTEM</RouterLink
                >
            </div>
        </footer>
    </div>
</template>

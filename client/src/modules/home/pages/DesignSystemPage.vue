<script setup lang="ts">
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
import { useThemeStore } from '@/shared/stores/theme.store';
import {
    PhArrowRight,
    PhBank,
    PhBell,
    PhCheckCircle,
    PhCoin,
    PhCrown,
    PhEye,
    PhEyeSlash,
    PhFloppyDisk,
    PhGameController,
    PhGraduationCap,
    PhHeart,
    PhHouse,
    PhIdentificationCard,
    PhLayout,
    PhMoon,
    PhPalette,
    PhPencilSimple,
    PhShieldCheck,
    PhSparkle,
    PhStar,
    PhStorefront,
    PhStudent,
    PhSun,
    PhTextT,
    PhTrash,
    PhUser,
    PhX,
} from '@phosphor-icons/vue';
import type { Component } from 'vue';
import { computed } from 'vue';
import { useRouter } from 'vue-router';

type DesignToken = {
    name: string;
    label: string;
    hex: string;
};

type PaletteToken = DesignToken & {
    aliases: string[];
};

type IconItem = {
    name: string;
    description: string;
    icon: Component;
};

type AvatarGroup = {
    title: string;
    description: string;
    characters: MarioCharacter[];
};

const router = useRouter();
const themeStore = useThemeStore();

const designTokens: DesignToken[] = [
    {
        name: '--background',
        label: 'Fundo',
        hex: '#f9f3e7',
    },
    {
        name: '--foreground',
        label: 'Texto',
        hex: '#0b0e28',
    },
    {
        name: '--card',
        label: 'Cartão',
        hex: '#fef9ec',
    },
    {
        name: '--card-foreground',
        label: 'Texto do cartão',
        hex: '#0f122e',
    },
    {
        name: '--primary',
        label: 'Primário',
        hex: '#f9bf10',
    },
    {
        name: '--primary-foreground',
        label: 'Primário invertido',
        hex: '#0a0d29',
    },
    {
        name: '--secondary',
        label: 'Secundário',
        hex: '#da341b',
    },
    {
        name: '--secondary-foreground',
        label: 'Texto secundário',
        hex: '#ffffff',
    },
    {
        name: '--success',
        label: 'Sucesso',
        hex: '#188b48',
    },
    {
        name: '--success-foreground',
        label: 'Texto de sucesso',
        hex: '#ffffff',
    },
    {
        name: '--info',
        label: 'Informação',
        hex: '#105cc6',
    },
    {
        name: '--info-foreground',
        label: 'Texto informativo',
        hex: '#ffffff',
    },
    {
        name: '--accent',
        label: 'Destaque',
        hex: '#148f85',
    },
    {
        name: '--accent-foreground',
        label: 'Texto de destaque',
        hex: '#ffffff',
    },
    {
        name: '--destructive',
        label: 'Destrutivo',
        hex: '#e61919',
    },
    {
        name: '--destructive-foreground',
        label: 'Texto destrutivo',
        hex: '#ffffff',
    },
    {
        name: '--muted',
        label: 'Suave',
        hex: '#e8e0cf',
    },
    {
        name: '--muted-foreground',
        label: 'Texto suave',
        hex: '#24284c',
    },
    {
        name: '--border',
        label: 'Borda',
        hex: '#0a0d29',
    },
    {
        name: '--hud',
        label: 'HUD',
        hex: '#0a0d29',
    },
    {
        name: '--hud-foreground',
        label: 'Texto HUD',
        hex: '#fac938',
    },
];

const iconItems: IconItem[] = [
    { name: 'PhGameController', description: 'Marca e navegação principal', icon: PhGameController },
    { name: 'PhSparkle', description: 'Login, destaque e entrada', icon: PhSparkle },
    { name: 'PhCoin', description: 'Moedas, saldo e recompensas', icon: PhCoin },
    { name: 'PhGraduationCap', description: 'Aluno e formação', icon: PhGraduationCap },
    { name: 'PhStorefront', description: 'Empresa parceira e vitrines', icon: PhStorefront },
    { name: 'PhBank', description: 'Instituição e cadastro educacional', icon: PhBank },
    { name: 'PhUser', description: 'Perfil, usuário e dados pessoais', icon: PhUser },
    { name: 'PhHouse', description: 'Home e pontos de retorno', icon: PhHouse },
    { name: 'PhIdentificationCard', description: 'Cadastro e acesso', icon: PhIdentificationCard },
    { name: 'PhCheckCircle', description: 'Confirmação de etapas e sucesso', icon: PhCheckCircle },
    { name: 'PhArrowRight', description: 'Avanço e próxima etapa', icon: PhArrowRight },
    { name: 'PhEye', description: 'Exibir senha e visualizar conteúdo', icon: PhEye },
    { name: 'PhEyeSlash', description: 'Ocultar senha e conteúdo sensível', icon: PhEyeSlash },
    { name: 'PhFloppyDisk', description: 'Salvar alterações', icon: PhFloppyDisk },
    { name: 'PhPencilSimple', description: 'Editar dados do perfil', icon: PhPencilSimple },
    { name: 'PhTrash', description: 'Excluir conta e remover registros', icon: PhTrash },
    { name: 'PhX', description: 'Cancelar e fechar ações', icon: PhX },
    { name: 'PhPalette', description: 'Sistema de design e identidade visual', icon: PhPalette },
    { name: 'PhTextT', description: 'Tipografia e escala textual', icon: PhTextT },
    { name: 'PhLayout', description: 'Estrutura, composição e padrões', icon: PhLayout },
    { name: 'PhStar', description: 'Destaques, badges e pontos fortes', icon: PhStar },
    { name: 'PhBell', description: 'Notificações e avisos', icon: PhBell },
    { name: 'PhShieldCheck', description: 'Segurança, confiança e validação', icon: PhShieldCheck },
    { name: 'PhCrown', description: 'Etapas, ranking e protagonismo', icon: PhCrown },
    { name: 'PhStudent', description: 'Contexto estudantil e jornada do aluno', icon: PhStudent },
    { name: 'PhHeart', description: 'Valor humano e benefícios', icon: PhHeart },
];

const avatarGroups: AvatarGroup[] = [
    {
        title: 'Personagens jogáveis',
        description: 'Conjunto principal de avatares do fluxo do aluno.',
        characters: ['mario', 'luigi', 'peach', 'toad', 'yoshi', 'bowser'],
    },
    {
        title: 'Perfis do sistema',
        description: 'Avatares especiais para instituição e empresa parceira.',
        characters: ['institution', 'company'],
    },
];

const componentVariants = [
    { variant: 'primary', label: 'Primário' },
    { variant: 'secondary', label: 'Secundário' },
    { variant: 'success', label: 'Sucesso' },
    { variant: 'info', label: 'Info' },
    { variant: 'accent', label: 'Destaque' },
    { variant: 'ghost', label: 'Fantasma' },
    { variant: 'danger', label: 'Perigo' },
] as const;

const componentTones = [
    { tone: 'gold', label: 'Dourado' },
    { tone: 'red', label: 'Vermelho' },
    { tone: 'green', label: 'Verde' },
    { tone: 'blue', label: 'Azul' },
    { tone: 'purple', label: 'Roxo' },
    { tone: 'teal', label: 'Verde-água' },
] as const;

const overviewStats = computed(() => [
    { label: 'PERSONAGENS', value: String(MARIO_CHARACTERS.length) },
    { label: 'ÍCONES', value: String(iconItems.length) },
    { label: 'CORES', value: String(designTokens.length) },
    { label: 'TEMAS', value: '2' },
]);

const paletteTokens = computed<PaletteToken[]>(() => {
    const grouped = new Map<string, PaletteToken>();

    for (const token of designTokens) {
        const existing = grouped.get(token.hex);

        if (existing) {
            existing.aliases.push(token.name);
            continue;
        }

        grouped.set(token.hex, {
            ...token,
            aliases: [],
        });
    }

    return [...grouped.values()];
});

const avatarTitles = Object.fromEntries(MARIO_CHARACTERS.map((character) => [character.id, character.title])) as Record<
    MarioCharacter,
    string
>;

function navigateTo(path: string) {
    void router.push(path);
}
</script>

<template>
    <div class="min-h-screen bg-background text-foreground">
        <header class="sticky top-0 z-50 border-b-4 border-border bg-hud text-hud-foreground">
            <div class="container flex flex-wrap items-center justify-between gap-3 py-3">
                <button
                    class="flex items-center gap-2 font-pixel text-[10px] transition-colors hover:text-primary"
                    type="button"
                    @click="navigateTo('/')"
                >
                    <PhGameController weight="fill" class="pixel-icon" :size="18" />
                    SISTEMA DE DESIGN
                </button>

                <div class="flex flex-wrap items-center gap-2">
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-0.5 active:translate-y-0.5 active:shadow-none"
                        type="button"
                        @click="themeStore.toggle"
                    >
                        <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
                        <PhMoon v-else weight="fill" class="pixel-icon" />
                        {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
                    </button>

                    <PixelButton variant="primary" size="sm" @click="navigateTo('/login')">
                        <PhSparkle weight="fill" class="pixel-icon" />
                        LOGIN
                    </PixelButton>
                </div>
            </div>
        </header>

        <main class="relative overflow-hidden">
            <div class="absolute inset-0 pointer-events-none opacity-35">
                <div class="absolute -top-24 -left-24 h-72 w-72 rounded-full bg-primary/15 blur-3xl" />
                <div class="absolute top-24 right-8 h-64 w-64 rounded-full bg-secondary/10 blur-3xl" />
                <div class="absolute bottom-10 left-1/3 h-80 w-80 rounded-full bg-accent/10 blur-3xl" />
            </div>

            <section class="container relative z-10 py-12 md:py-16 grid lg:grid-cols-[1.15fr_0.85fr] gap-8 items-center mt-5">
                <div class="space-y-5">

                    <div class="space-y-3">
                        <div class="font-pixel text-[10px] text-primary">▶ DESIGN SYSTEM DO PRODUTO</div>
                        <h1 class="font-pixel text-3xl md:text-4xl leading-tight">
                            DESIGN SYSTEM ALINHADO AO PRODUTO REAL
                        </h1>
                        <p class="font-display text-lg md:text-xl text-foreground/85 max-w-2xl">
                            A página mostra exatamente os elementos que a interface usa hoje: avatares
                            do <span class="text-primary">MarioAvatar</span>, cores, ícones
                            Phosphor e componentes pixelados com contraste real.
                        </p>
                    </div>

                    <div class="flex flex-wrap gap-3">
                        <PixelButton variant="primary" size="lg" @click="navigateTo('/')">
                            <PhHouse weight="fill" class="pixel-icon" />
                            IR PARA HOME
                        </PixelButton>
                        <PixelButton variant="ghost" size="lg" @click="navigateTo('/signup')">
                            <PhGraduationCap weight="fill" class="pixel-icon" />
                            CRIAR CONTA
                        </PixelButton>
                    </div>

                    <div class="grid grid-cols-2 md:grid-cols-4 gap-3 pt-2">
                        <PixelCard v-for="stat in overviewStats" :key="stat.label" class="p-4 text-center">
                            <div class="font-pixel text-xl text-primary">{{ stat.value }}</div>
                            <div class="font-pixel text-[8px] text-muted-foreground mt-2">{{ stat.label }}</div>
                        </PixelCard>
                    </div>
                </div>

                <PixelCard class="p-5 md:p-6 crt-scanlines">
                    <div class="flex items-center justify-between gap-3 mb-5">
                        <div>
                            <div class="font-pixel text-[10px] text-primary">▶ RECORTE DO UI KIT</div>
                            <div class="font-pixel text-sm mt-2">COMPONENTES REAIS</div>
                        </div>
                        <PixelBadge tone="teal">TEMA ATUAL</PixelBadge>
                    </div>

                    <div class="grid grid-cols-2 gap-3">
                        <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
                            <MarioAvatar character="mario" :size="88" />
                        </div>
                        <div class="space-y-3">
                            <div class="border-2 border-border bg-card p-3">
                                <div class="font-pixel text-[9px] text-primary mb-2">TIPOGRAFIA</div>
                                <div class="font-pixel text-lg">PRESS START 2P</div>
                                <div class="font-display text-lg text-muted-foreground mt-1">
                                    VT323 para interface e leitura contínua
                                </div>
                            </div>
                            <div class="border-2 border-border bg-card p-3">
                                <div class="font-pixel text-[9px] text-primary mb-2">ESTADO</div>
                                <div class="flex flex-wrap gap-2">
                                    <PixelBadge tone="gold">MOEDAS</PixelBadge>
                                    <PixelBadge tone="green">APROVADO</PixelBadge>
                                    <PixelBadge tone="blue">INFO</PixelBadge>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4 grid grid-cols-4 gap-2">
                        <div
                            v-for="character in ['mario', 'luigi', 'peach', 'toad']"
                            :key="character"
                            class="border-2 border-border bg-hud p-2 flex items-center justify-center"
                        >
                            <MarioAvatar :character="character as MarioCharacter" :size="48" />
                        </div>
                    </div>
                </PixelCard>
            </section>

            <section id="tokens" class="border-y-4 border-border bg-card/70">
                <div class="container py-12">
                    <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
                        <div>
                            <div class="font-pixel text-[10px] text-primary mb-2">▶ PALETA OFICIAL</div>
                            <h2 class="font-pixel text-2xl">CORES DO SISTEMA</h2>
                        </div>
                    </div>

                    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-3">
                        <PixelCard v-for="token in paletteTokens" :key="token.hex" class="overflow-hidden">
                            <div class="h-16 border-b-2 border-border" :style="{ backgroundColor: token.hex }" />
                            <div class="p-3">
                                <div class="font-pixel text-[10px] truncate">{{ token.label }}</div>
                                <div class="mt-1 flex items-center justify-between gap-2">
                                    <div class="font-sans text-[10px] text-muted-foreground truncate">{{ token.name }}</div>
                                    <div class="font-sans text-sm">{{ token.hex }}</div>
                                </div>
                                <div v-if="token.aliases.length" class="font-sans text-[10px] text-muted-foreground mt-1 truncate">
                                    +{{ token.aliases.length }} uso{{ token.aliases.length > 1 ? 's' : '' }}
                                </div>
                            </div>
                        </PixelCard>
                    </div>
                </div>
            </section>

            <section id="tipografia" class="border-b-4 border-border">
                <div class="container py-12">
                    <div class="flex items-center gap-3 mb-8">
                        <PhTextT weight="fill" class="text-primary" :size="24" />
                        <h2 class="font-pixel text-2xl">TIPOGRAFIA E VOZ</h2>
                    </div>

                    <div class="grid lg:grid-cols-2 gap-4">
                        <PixelCard class="p-6">
                            <div class="font-pixel text-[10px] text-primary mb-4">TÍTULOS</div>
                            <div class="font-pixel text-2xl leading-tight">PRESS START 2P</div>
                            <p class="font-display text-lg text-muted-foreground mt-4">
                                Usado em navegação, seções e chamadas de atenção com leitura curta.
                            </p>
                        </PixelCard>

                        <PixelCard class="p-6">
                            <div class="font-pixel text-[10px] text-primary mb-4">CORPO</div>
                            <div class="font-display text-3xl">VT323</div>
                            <p class="font-display text-lg text-muted-foreground mt-4">
                                Base da interface. Funciona bem para descrições, formulários e estados.
                            </p>
                        </PixelCard>
                    </div>
                </div>
            </section>

            <section id="componentes" class="border-b-4 border-border bg-card/60">
                <div class="container py-12">
                    <div class="flex items-center gap-3 mb-8">
                        <PhStar weight="fill" class="text-primary" :size="24" />
                        <h2 class="font-pixel text-2xl">COMPONENTES BASE</h2>
                    </div>

                    <div class="grid xl:grid-cols-2 gap-4">
                        <PixelCard class="p-6 space-y-6">
                            <div class="font-pixel text-[10px] text-primary">BOTÕES</div>
                            <div class="flex flex-wrap gap-3">
                                <PixelButton
                                    v-for="item in componentVariants"
                                    :key="item.label"
                                    :variant="item.variant"
                                    size="sm"
                                >
                                    {{ item.label }}
                                </PixelButton>
                            </div>
                            <div class="flex flex-wrap gap-3">
                                <PixelButton size="sm">Pequeno</PixelButton>
                                <PixelButton size="md">Médio</PixelButton>
                                <PixelButton size="lg">Grande</PixelButton>
                            </div>
                        </PixelCard>

                        <PixelCard class="p-6 space-y-6">
                            <div class="font-pixel text-[10px] text-primary">BADGES E INPUTS</div>
                            <div class="flex flex-wrap gap-2">
                                <PixelBadge v-for="tone in componentTones" :key="tone.label" :tone="tone.tone">
                                    {{ tone.label }}
                                </PixelBadge>
                            </div>
                            <div class="grid gap-3">
                                <PixelInput placeholder="Campo padrão" />
                                <PixelInput placeholder="Campo de e-mail" type="email" />
                                <PixelInput placeholder="Campo desativado" disabled />
                            </div>
                        </PixelCard>

                        <PixelCard class="p-6 space-y-6">
                            <div class="font-pixel text-[10px] text-primary">ESTRUTURA</div>
                            <div class="grid sm:grid-cols-2 gap-4">
                                <PixelCard>
                                    <div class="p-4">
                                        <div class="font-pixel text-sm mb-2">Cartão padrão</div>
                                        <p class="font-display text-lg text-muted-foreground">
                                            Superfície para blocos de informação e ações agrupadas.
                                        </p>
                                    </div>
                                </PixelCard>
                                <PixelCard>
                                    <div class="p-4 bg-primary/10">
                                        <div class="font-pixel text-sm mb-2 text-primary">Cartão de destaque</div>
                                        <p class="font-display text-lg text-muted-foreground">
                                            Bloco com ênfase para chamadas importantes.
                                        </p>
                                    </div>
                                </PixelCard>
                            </div>
                        </PixelCard>

                        <PixelCard class="p-6 space-y-6">
                            <div class="font-pixel text-[10px] text-primary">ESTADO E FLUXO</div>
                            <div class="grid grid-cols-3 gap-3">
                                <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
                                    <MarioAvatar character="institution" :size="56" />
                                </div>
                                <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
                                    <MarioAvatar character="company" :size="56" />
                                </div>
                                <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
                                    <MarioAvatar character="mario" :size="56" />
                                </div>
                            </div>
                        </PixelCard>
                    </div>
                </div>
            </section>

            <section id="avatares" class="border-b-4 border-border">
                <div class="container py-12">
                    <div class="flex items-center gap-3 mb-4">
                        <PhCrown weight="fill" class="text-primary" :size="24" />
                        <h2 class="font-pixel text-2xl">AVATARES DO SISTEMA</h2>
                    </div>
                    <p class="font-display text-lg text-muted-foreground max-w-3xl mb-8">
                        O design system deve mostrar exatamente os avatares que a aplicação usa.
                        Aqui estão os personagens do <span class="text-primary">MarioAvatar</span>,
                        incluindo os perfis especiais de instituição e empresa.
                    </p>

                    <div v-for="group in avatarGroups" :key="group.title" class="mb-8 last:mb-0">
                        <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
                            <div>
                                <div class="font-pixel text-sm">{{ group.title }}</div>
                                <div class="font-display text-lg text-muted-foreground">
                                    {{ group.description }}
                                </div>
                            </div>
                            <PixelBadge tone="teal">{{ group.characters.length }} AVATARES</PixelBadge>
                        </div>

                        <div class="grid sm:grid-cols-2 xl:grid-cols-4 gap-4">
                            <PixelCard v-for="character in group.characters" :key="character" class="p-4">
                                <div class="border-2 border-border bg-hud p-4 flex items-center justify-center mb-4">
                                    <MarioAvatar :character="character" :size="88" />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <div>
                                        <div class="font-pixel text-sm capitalize">{{ character }}</div>
                                        <div class="font-display text-lg text-muted-foreground capitalize">
                                            {{ avatarTitles[character] }}
                                        </div>
                                    </div>
                                    <PixelBadge tone="gold">
                                        {{ character === 'institution' || character === 'company' ? 'ESPECIAL' : 'ALUNO' }}
                                    </PixelBadge>
                                </div>
                            </PixelCard>
                        </div>
                    </div>
                </div>
            </section>

            <section id="icones" class="border-b-4 border-border bg-card/60">
                <div class="container py-12">
                    <div class="flex items-center gap-3 mb-4">
                        <PhPalette weight="fill" class="text-primary" :size="24" />
                        <h2 class="font-pixel text-2xl">ÍCONES PHOSPHOR</h2>
                    </div>
                    <p class="font-display text-lg text-muted-foreground max-w-3xl mb-8">
                        Estes são os ícones que sustentam a navegação e os estados de interface do
                        produto. O design system passou a refletir o mesmo conjunto visual usado nas
                        páginas do app.
                    </p>

                    <div class="grid sm:grid-cols-2 xl:grid-cols-3 gap-4">
                        <PixelCard v-for="item in iconItems" :key="item.name" class="p-4">
                            <div class="flex items-start gap-4">
                                <div class="w-14 h-14 border-2 border-border bg-hud flex items-center justify-center shrink-0">
                                    <component :is="item.icon" weight="fill" class="pixel-icon text-primary" :size="24" />
                                </div>
                                <div class="min-w-0">
                                    <div class="font-pixel text-[10px] truncate">{{ item.name }}</div>
                                    <div class="font-display text-lg text-muted-foreground">
                                        {{ item.description }}
                                    </div>
                                </div>
                            </div>
                        </PixelCard>
                    </div>
                </div>
            </section>

            <section id="padroes" class="border-b-4 border-border">
                <div class="container py-12">
                    <div class="flex items-center gap-3 mb-8">
                        <PhLayout weight="fill" class="text-primary" :size="24" />
                        <h2 class="font-pixel text-2xl">PADRÕES VISUAIS</h2>
                    </div>

                    <div class="grid lg:grid-cols-3 gap-4">
                        <PixelCard class="p-6">
                            <div class="font-pixel text-[10px] text-primary mb-4">BORDAS</div>
                            <div class="space-y-3">
                                <div class="h-20 bg-card border-2 border-border flex items-center justify-center font-pixel text-[10px] pixel-border">
                                    pixel-border
                                </div>
                                <div class="h-20 bg-card border-2 border-border flex items-center justify-center font-pixel text-[10px] pixel-border-thin">
                                    pixel-border-thin
                                </div>
                            </div>
                        </PixelCard>

                        <PixelCard class="p-6">
                            <div class="font-pixel text-[10px] text-primary mb-4">SOMBRAS</div>
                            <div class="space-y-3">
                                <div class="bg-card border-2 border-border pixel-shadow p-4 font-pixel text-[10px]">
                                    pixel-shadow
                                </div>
                                <div class="bg-card border-2 border-border pixel-shadow-lg p-4 font-pixel text-[10px]">
                                    pixel-shadow-lg
                                </div>
                            </div>
                        </PixelCard>

                        <PixelCard class="p-6">
                            <div class="font-pixel text-[10px] text-primary mb-4">MOVIMENTO</div>
                            <div class="space-y-4">
                                <div class="flex items-center gap-4">
                                    <div class="animate-bob font-pixel text-2xl">↕</div>
                                    <div class="font-display text-lg">animate-bob</div>
                                </div>
                                <div class="flex items-center gap-4">
                                    <div class="animate-blink font-pixel text-2xl">⬤</div>
                                    <div class="font-display text-lg">animate-blink</div>
                                </div>
                                <div class="flex items-center gap-4">
                                    <div class="animate-glow font-pixel text-primary text-2xl">✨</div>
                                    <div class="font-display text-lg">animate-glow</div>
                                </div>
                            </div>
                        </PixelCard>
                    </div>
                </div>
            </section>

            <section class="border-t-4 border-border bg-hud text-hud-foreground">
                <div class="container py-12 grid lg:grid-cols-[1fr_auto] gap-6 items-center">
                    <div>
                        <div class="font-pixel text-sm mb-4">CRITÉRIO DE ANÁLISE</div>
                        <p class="font-display text-lg text-hud-foreground/90 max-w-3xl">
                            O design system agora serve como espelho do produto atual: sem ícones ou
                            avatares genéricos, sem paleta inventada e com o mesmo conjunto de peças que
                            aparece nas jornadas de login, cadastro, perfil e dashboard.
                        </p>
                    </div>

                    <div class="flex flex-wrap gap-3">
                        <PixelButton variant="ghost" @click="navigateTo('/login')">
                            <PhArrowRight weight="fill" class="pixel-icon" />
                            ABRIR LOGIN
                        </PixelButton>
                        <PixelButton variant="primary" @click="navigateTo('/')">
                            <PhGameController weight="fill" class="pixel-icon" />
                            VOLTAR AO JOGO
                        </PixelButton>
                    </div>
                </div>
            </section>
        </main>
    </div>
</template>

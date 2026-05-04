<script setup lang="ts">
import PixelAvatar from '@/shared/components/PixelAvatar.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import {
    PhArrowRight,
    PhLayout,
    PhPalette,
    PhStar,
    PhTextT,
} from '@phosphor-icons/vue';
import { computed, ref } from 'vue';
import { RouterLink } from 'vue-router';

const currentTheme = ref<'light' | 'dark'>('light');

const lightModeColors = [
    { name: '--color-primary', value: '#D4B741', label: 'Primário' },
    { name: '--color-secondary', value: '#D9451A', label: 'Secundário' },
    { name: '--color-success', value: '#418A2C', label: 'Sucesso' },
    { name: '--color-info', value: '#3B7CD9', label: 'Informação' },
    { name: '--color-accent', value: '#269A78', label: 'Destaque' },
    { name: '--color-destructive', value: '#D9401E', label: 'Destrutivo' },
    { name: '--color-background', value: '#F0F8F5', label: 'Fundo' },
    { name: '--color-card', value: '#F5F9F8', label: 'Cartão' },
    { name: '--color-foreground', value: '#0A0A1E', label: 'Texto Principal' },
    { name: '--color-muted', value: '#E8EDEC', label: 'Muteado' },
    { name: '--color-border', value: '#0A0A1E', label: 'Borda' },
    { name: '--color-hud', value: '#0A0A1E', label: 'HUD' },
];

const darkModeColors = [
    { name: '--color-primary', value: '#F0D944', label: 'Primário' },
    { name: '--color-secondary', value: '#E84E23', label: 'Secundário' },
    { name: '--color-success', value: '#47D96B', label: 'Sucesso' },
    { name: '--color-info', value: '#3FBAE8', label: 'Informação' },
    { name: '--color-accent', value: '#34DAB3', label: 'Destaque' },
    { name: '--color-destructive', value: '#F04A3A', label: 'Destrutivo' },
    { name: '--color-background', value: '#0E1615', label: 'Fundo' },
    { name: '--color-card', value: '#1C2422', label: 'Cartão' },
    { name: '--color-foreground', value: '#ECEDEE', label: 'Texto Principal' },
    { name: '--color-muted', value: '#3A3F3D', label: 'Muteado' },
    { name: '--color-border', value: '#ECEDEE', label: 'Borda' },
    { name: '--color-hud', value: '#000000', label: 'HUD' },
];

const colors = computed(() => (currentTheme.value === 'light' ? lightModeColors : darkModeColors));

const copyToClipboard = (value: string) => {
    navigator.clipboard.writeText(value);
};
</script>

<template>
    <div class="min-h-screen bg-background text-foreground">
        <!-- Header -->
        <header class="sticky top-0 z-50 bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-4 gap-3">
                <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px] hover:text-primary">
                    <PhPalette weight="fill" class="pixel-icon" :size="18" />
                    DESIGN SYSTEM
                </RouterLink>
                <nav class="hidden md:flex items-center gap-4 font-pixel text-[9px]">
                    <a href="#cores" class="hover:text-primary transition-colors">CORES</a>
                    <a href="#tipografia" class="hover:text-primary transition-colors">TIPOGRAFIA</a>
                    <a href="#componentes" class="hover:text-primary transition-colors">COMPONENTES</a>
                    <a href="#padroes" class="hover:text-primary transition-colors">PADRÕES</a>
                </nav>
            </div>
        </header>

        <!-- Hero -->
        <section class="border-b-4 border-border">
            <div class="container py-12 md:py-16">
                <div class="max-w-2xl">
                    <div class="font-pixel text-[10px] text-primary mb-3">▶ DESIGN TOKENS</div>
                    <h1 class="font-pixel text-3xl md:text-4xl mb-4">
                        Sistema de Design<br /><span class="text-primary">LUMEN COIN</span>
                    </h1>
                    <p class="font-sans text-sm md:text-base text-foreground/85 max-w-xl">
                        Uma documentação visual completa de todos os tokens, componentes e padrões de
                        design utilizados na plataforma Lumen Coin. Explore cores, tipografia,
                        componentes reutilizáveis e técnicas de design pixel-art.
                    </p>
                </div>
            </div>
        </section>

        <!-- Cores -->
        <section id="cores" class="border-b-4 border-border">
            <div class="container py-12">
                <div class="flex items-center gap-3 mb-8">
                    <PhPalette weight="fill" class="text-primary" :size="24" />
                    <h2 class="font-pixel text-2xl">Paleta de Cores</h2>
                </div>

                <div class="bg-card border-2 border-border pixel-shadow p-4 md:p-6 mb-6">
                    <div class="flex items-center gap-3 mb-4">
                        <span class="font-pixel text-xs">Tema:</span>
                        <div class="flex gap-2">
                            <button
                                :class="[
                                    'font-pixel text-xs px-3 py-2 border-2 border-border',
                                    currentTheme === 'light'
                                        ? 'bg-primary text-primary-foreground pixel-shadow'
                                        : 'bg-card text-card-foreground',
                                ]"
                                @click="currentTheme = 'light'"
                            >
                                ☀ Claro
                            </button>
                            <button
                                :class="[
                                    'font-pixel text-xs px-3 py-2 border-2 border-border',
                                    currentTheme === 'dark'
                                        ? 'bg-primary text-primary-foreground pixel-shadow'
                                        : 'bg-card text-card-foreground',
                                ]"
                                @click="currentTheme = 'dark'"
                            >
                                ☾ Escuro
                            </button>
                        </div>
                    </div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    <div
                        v-for="color in colors"
                        :key="color.name"
                        class="bg-card border-2 border-border pixel-shadow p-4 cursor-pointer transition-transform hover:scale-105"
                        @click="copyToClipboard(color.value)"
                    >
                        <div class="mb-3">
                            <div
                                class="h-20 border-2 border-border"
                                :style="{ backgroundColor: color.value }"
                            />
                        </div>
                        <div class="font-pixel text-[10px] mb-1">{{ color.label }}</div>
                        <div class="font-display text-xs text-muted-foreground mb-2">
                            {{ color.name }}
                        </div>
                        <div class="font-sans text-xs font-bold text-primary">{{ color.value }}</div>
                        <div class="font-sans text-[10px] text-muted-foreground mt-1">Clique para copiar</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Tipografia -->
        <section id="tipografia" class="border-b-4 border-border">
            <div class="container py-12">
                <div class="flex items-center gap-3 mb-8">
                    <PhTextT weight="fill" class="text-primary" :size="24" />
                    <h2 class="font-pixel text-2xl">Tipografia</h2>
                </div>

                <div class="space-y-6">
                    <!-- Press Start 2P -->
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-4">PIXEL · HEADING</div>
                            <div class="font-pixel text-5xl mb-2">PRESS START 2P</div>
                            <div class="font-display text-sm text-muted-foreground">
                                Font: "Press Start 2P" | Monospace pixel | Uso: headings, títulos principais
                            </div>
                            <div class="mt-6 space-y-2">
                                <div class="font-pixel text-3xl">H1 - TÍTULO PRINCIPAL</div>
                                <div class="font-pixel text-2xl text-secondary">H2 - SUBTÍTULO</div>
                                <div class="font-pixel text-xl text-accent">H3 - SEÇÃO</div>
                            </div>
                        </div>
                    </PixelCard>

                    <!-- VT323 -->
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-4">DISPLAY · BODY</div>
                            <div class="font-display text-5xl mb-2">VT323</div>
                            <div class="font-display text-sm text-muted-foreground">
                                Font: "VT323" | Terminal monospace | Uso: body, interface, padrão da aplicação
                            </div>
                            <div class="mt-6 space-y-3">
                                <div class="font-display text-lg">
                                    Bem-vindo ao Lumen Coin! Este é o texto padrão da interface.
                                </div>
                                <div class="font-display text-sm text-muted-foreground">
                                    Texto pequeno com cores semânticas
                                </div>
                                <div class="font-display text-base font-bold text-success">
                                    ✓ Mensagem de sucesso com destaque
                                </div>
                            </div>
                        </div>
                    </PixelCard>

                    <!-- Escala de tamanhos -->
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-4">ESCALA DE TAMANHOS</div>
                            <div class="space-y-4">
                                <div>
                                    <div class="font-display text-[10px] text-muted-foreground mb-1">
                                        10px - Micro
                                    </div>
                                    <div class="font-display text-[10px]">Micro label</div>
                                </div>
                                <div>
                                    <div class="font-display text-xs text-muted-foreground mb-1">12px - Small</div>
                                    <div class="font-display text-xs">Pequeno texto</div>
                                </div>
                                <div>
                                    <div class="font-display text-sm text-muted-foreground mb-1">14px - Base</div>
                                    <div class="font-display text-sm">Tamanho base padrão</div>
                                </div>
                                <div>
                                    <div class="font-display text-base text-muted-foreground mb-1">16px - Large</div>
                                    <div class="font-display text-base">Texto grande</div>
                                </div>
                                <div>
                                    <div class="font-pixel text-lg text-muted-foreground mb-1">
                                        20px - XL (Pixel)
                                    </div>
                                    <div class="font-pixel text-lg">MUITO GRANDE</div>
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>
            </div>
        </section>

        <!-- Componentes -->
        <section id="componentes" class="border-b-4 border-border">
            <div class="container py-12">
                <div class="flex items-center gap-3 mb-8">
                    <PhStar weight="fill" class="text-primary" :size="24" />
                    <h2 class="font-pixel text-2xl">Componentes</h2>
                </div>

                <!-- Botões -->
                <div class="mb-10">
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-6">BUTTONS · INTERAÇÃO</div>
                            <div class="space-y-6">
                                <div>
                                    <div class="font-pixel text-sm mb-4">Variantes</div>
                                    <div class="flex flex-wrap gap-3">
                                        <PixelButton variant="primary">Primary</PixelButton>
                                        <PixelButton variant="secondary">Secondary</PixelButton>
                                        <PixelButton variant="success">Success</PixelButton>
                                        <PixelButton variant="info">Info</PixelButton>
                                        <PixelButton variant="accent">Accent</PixelButton>
                                        <PixelButton variant="ghost">Ghost</PixelButton>
                                        <PixelButton variant="danger">Danger</PixelButton>
                                    </div>
                                </div>
                                <div class="border-t border-border pt-4">
                                    <div class="font-pixel text-sm mb-4">Tamanhos</div>
                                    <div class="flex flex-wrap gap-3 items-center">
                                        <PixelButton size="sm">Small</PixelButton>
                                        <PixelButton size="md">Medium</PixelButton>
                                        <PixelButton size="lg">Large</PixelButton>
                                    </div>
                                </div>
                                <div class="border-t border-border pt-4">
                                    <div class="font-pixel text-sm mb-4">Estados</div>
                                    <div class="flex flex-wrap gap-3">
                                        <PixelButton variant="primary">Normal</PixelButton>
                                        <PixelButton variant="primary" disabled>Desativado</PixelButton>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <!-- Badges -->
                <div class="mb-10">
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-4">BADGES · LABELS</div>
                            <div class="flex flex-wrap gap-2">
                                <PixelBadge tone="gold">Gold Badge</PixelBadge>
                                <PixelBadge tone="blue">Blue Badge</PixelBadge>
                                <PixelBadge tone="green">Green Badge</PixelBadge>
                                <PixelBadge tone="red">Red Badge</PixelBadge>
                                <PixelBadge tone="purple">Purple Badge</PixelBadge>
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <!-- Inputs -->
                <div class="mb-10">
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-4">INPUTS · FORMULÁRIOS</div>
                            <div class="space-y-4">
                                <PixelInput placeholder="Input padrão" />
                                <PixelInput placeholder="Input com tipo email" type="email" />
                                <PixelInput placeholder="Input desativado" disabled />
                                <PixelInput placeholder="Com valor pré-preenchido" value="Pré-preenchido" />
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <!-- Cards -->
                <div class="mb-10">
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-6">CARDS · CONTAINERS</div>
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <PixelCard>
                                    <div class="p-4">
                                        <div class="font-pixel text-sm mb-2">Cartão Básico</div>
                                        <div class="font-display text-xs text-muted-foreground">
                                            Container com border e shadow. Padrão para conteúdo.
                                        </div>
                                    </div>
                                </PixelCard>
                                <PixelCard>
                                    <div class="p-4 bg-primary/10 border-primary">
                                        <div class="font-pixel text-sm text-primary mb-2">Cartão Destaque</div>
                                        <div class="font-display text-xs text-muted-foreground">
                                            Pode ser colorido usando estilos inline.
                                        </div>
                                    </div>
                                </PixelCard>
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <!-- Avatars -->
                <div>
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-4">AVATARS · PERSONAGENS</div>
                            <div class="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 gap-4">
                                <PixelAvatar
                                    v-for="char in ['toad', 'yoshi', 'peach', 'luigi', 'bowser', 'mario']"
                                    :key="char"
                                    :character="char"
                                    :size="60"
                                />
                            </div>
                        </div>
                    </PixelCard>
                </div>
            </div>
        </section>

        <!-- Padrões -->
        <section id="padroes" class="border-b-4 border-border">
            <div class="container py-12">
                <div class="flex items-center gap-3 mb-8">
                    <PhLayout weight="fill" class="text-primary" :size="24" />
                    <h2 class="font-pixel text-2xl">Padrões de Design</h2>
                </div>

                <!-- Pixel Borders -->
                <div class="mb-10">
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-6">PIXEL BORDERS · EFEITOS</div>
                            <div class="space-y-4">
                                <div
                                    class="h-24 bg-card border-2 border-border flex items-center justify-center font-pixel text-sm pixel-border"
                                >
                                    pixel-border
                                </div>
                                <div
                                    class="h-24 bg-card border-2 border-border flex items-center justify-center font-pixel text-sm pixel-border-thin"
                                >
                                    pixel-border-thin
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <!-- Shadows -->
                <div class="mb-10">
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-6">SHADOWS · PROFUNDIDADE</div>
                            <div class="space-y-6">
                                <div class="bg-card border-2 border-border pixel-shadow p-4 font-pixel text-sm">
                                    pixel-shadow
                                </div>
                                <div class="bg-card border-2 border-border pixel-shadow-lg p-4 font-pixel text-sm">
                                    pixel-shadow-lg
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <!-- Animações -->
                <div>
                    <PixelCard>
                        <div class="p-6">
                            <div class="font-pixel text-xs text-primary mb-6">ANIMAÇÕES · MOVIMENTO</div>
                            <div class="space-y-6">
                                <div class="flex items-center gap-4">
                                    <div class="animate-bob font-pixel text-2xl">↕</div>
                                    <div class="font-display text-sm">animate-bob</div>
                                </div>
                                <div class="flex items-center gap-4">
                                    <div class="animate-blink font-pixel text-2xl">⬤</div>
                                    <div class="font-display text-sm">animate-blink</div>
                                </div>
                                <div class="flex items-center gap-4">
                                    <div class="animate-glow font-pixel text-primary text-2xl">✨</div>
                                    <div class="font-display text-sm">animate-glow</div>
                                </div>
                                <div class="flex items-center gap-4">
                                    <PixelButton variant="primary" class="animate-pop">Pop!</PixelButton>
                                    <div class="font-display text-sm">animate-pop</div>
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>
            </div>
        </section>

        <!-- Footer -->
        <section class="border-t-4 border-border">
            <div class="container py-12">
                <div class="max-w-2xl">
                    <div class="font-pixel text-sm mb-4">Princípios de Design</div>
                    <div class="space-y-3">
                        <div class="font-display text-sm">
                            <span class="text-primary">▸ Pixel-Perfect:</span> Todos os efeitos visuais utilizam
                            múltiplos de 4px ou 2px para manter a estética pixelada.
                        </div>
                        <div class="font-display text-sm">
                            <span class="text-primary">▸ Sem Cores Hardcoded:</span> Cores devem sempre usar CSS
                            custom properties (hsl variables) para suportar temas claro/escuro.
                        </div>
                        <div class="font-display text-sm">
                            <span class="text-primary">▸ Acessibilidade:</span> Todos os componentes mantêm
                            contraste adequado e suportam navegação por teclado.
                        </div>
                        <div class="font-display text-sm">
                            <span class="text-primary">▸ Performance:</span> Animações usam
                            <code class="bg-muted px-1">steps()</code>
                            para manter a sensação de movimento pixelado sem overhead.
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- CTA -->
        <section class="bg-card border-t-4 border-border">
            <div class="container py-12">
                <div class="bg-primary/10 border-2 border-primary pixel-shadow p-8">
                    <h3 class="font-pixel text-xl mb-2 text-primary">Pronto para começar?</h3>
                    <p class="font-display text-sm mb-6 text-foreground/85">
                        Explore a documentação, utilize os componentes em seus projetos e contribua para
                        melhorar o sistema de design.
                    </p>
                    <div class="flex flex-wrap gap-3">
                        <RouterLink to="/">
                            <PixelButton variant="primary">
                                <PhArrowRight weight="fill" class="pixel-icon" />
                                VOLTAR HOME
                            </PixelButton>
                        </RouterLink>
                        <PixelButton variant="ghost">DOCUMENTAÇÃO</PixelButton>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>

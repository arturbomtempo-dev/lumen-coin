<script setup lang="ts">
import { ref } from 'vue';
import { RouterLink } from 'vue-router';
import {
    PhArrowLeft,
    PhGameController,
    PhSun,
    PhMoon,
    PhStorefront,
    PhTicket,
    PhCheckCircle,
    PhXCircle,
    PhPlus,
    PhEye,
    PhEyeSlash,
    PhTrash,
} from '@phosphor-icons/vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import { vantagensIniciais, type Vantagem } from '@/shared/data/mockData';
import { useThemeStore } from '@/shared/stores/theme.store';
import { toast } from 'vue-sonner';

const themeStore = useThemeStore();

const VALID_CODES = ['LUMEN-ABC12-X9K', 'LUMEN-BYTE1-PZZ'];

type VantagemEmpresa = Vantagem & { ativa: boolean };

const vantagens = ref<VantagemEmpresa[]>(
    vantagensIniciais
        .filter((v) => v.empresa === 'Byte Burger' || v.empresa === 'Arcade World')
        .map((v) => ({ ...v, ativa: true }))
);
const form = ref({ nome: '', descricao: '', custo: 200, emoji: '🎁', categoria: 'Comida' });
const codigo = ref('');
const resultado = ref<{ valido: boolean; msg: string } | null>(null);

const CATEGORIAS = ['Comida', 'Vestuário', 'Tecnologia', 'Lazer', 'Educação'];

function criar(e: Event) {
    e.preventDefault();
    if (!form.value.nome || !form.value.descricao) return;
    const nova: VantagemEmpresa = {
        id: 'v' + Date.now(),
        nome: form.value.nome,
        descricao: form.value.descricao,
        custo: Number(form.value.custo),
        empresa: 'Byte Burger',
        emoji: form.value.emoji,
        categoria: form.value.categoria,
        ativa: true,
    };
    vantagens.value.unshift(nova);
    toast.success(`Vantagem "${nova.nome}" publicada!`);
    form.value = { nome: '', descricao: '', custo: 200, emoji: '🎁', categoria: 'Comida' };
}

function toggleAtiva(id: string) {
    const v = vantagens.value.find((x) => x.id === id);
    if (v) v.ativa = !v.ativa;
}

function remover(id: string) {
    vantagens.value = vantagens.value.filter((v) => v.id !== id);
}

function validar() {
    const c = codigo.value.trim().toUpperCase();
    if (!c) return void (resultado.value = { valido: false, msg: 'Digite um código.' });
    if (VALID_CODES.includes(c) || c.startsWith('LUMEN-')) {
        resultado.value = { valido: true, msg: 'Cupom válido. Recompensa liberada!' };
    } else {
        resultado.value = { valido: false, msg: 'Cupom inválido ou já utilizado.' };
    }
}
</script>

<template>
    <div class="min-h-screen flex flex-col">
        <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-3">
                <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px]">
                    <PhArrowLeft weight="bold" />
                    <PhGameController weight="fill" class="pixel-icon" :size="18" />
                    EMPRESA · PARCEIRA
                </RouterLink>
                <div class="hidden sm:block font-pixel text-[10px]">BYTE BURGER · ADMIN</div>
                <button
                    class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))]"
                    @click="themeStore.toggle()"
                >
                    <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
                    <PhMoon v-else weight="fill" class="pixel-icon" />
                    {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
                </button>
            </div>
        </header>

        <main class="flex-1 container py-6 space-y-8">
            <div>
                <div class="font-pixel text-[10px] text-primary">▶ PARCEIRO</div>
                <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                    <PhStorefront weight="fill" class="pixel-icon" /> PORTAL DA EMPRESA
                </h1>
            </div>

            <div class="grid lg:grid-cols-2 gap-6 items-start">
                <PixelCard class="p-6">
                    <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPlus weight="bold" /> CRIAR VANTAGEM
                    </div>
                    <form class="space-y-4" @submit="criar">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">NOME</label>
                            <PixelInput v-model="form.nome" placeholder="Ex: Combo Byte" required />
                        </div>
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">DESCRIÇÃO</label>
                            <PixelInput
                                v-model="form.descricao"
                                placeholder="Ex: 1 lanche + batata + refri"
                                required
                            />
                        </div>
                        <div class="grid grid-cols-3 gap-3">
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">CUSTO</label>
                                <PixelInput v-model="form.custo" type="number" :min="1" />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">EMOJI</label>
                                <PixelInput v-model="form.emoji" :max-length="2" />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">CATEGORIA</label>
                                <select
                                    v-model="form.categoria"
                                    class="w-full bg-input border-2 border-border px-3 py-2 font-display text-lg focus:outline-none"
                                >
                                    <option v-for="c in CATEGORIAS" :key="c">{{ c }}</option>
                                </select>
                            </div>
                        </div>
                        <PixelButton variant="success" class="w-full" type="submit"
                            ><PhPlus weight="bold" /> PUBLICAR VANTAGEM</PixelButton
                        >
                    </form>
                </PixelCard>

                <PixelCard class="p-6">
                    <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhTicket weight="fill" class="pixel-icon" /> VALIDAR CUPOM
                    </div>
                    <div class="space-y-4">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">CÓDIGO DO CUPOM</label>
                            <PixelInput
                                v-model="codigo"
                                placeholder="LUMEN-XXXXX-XXX"
                                class="uppercase"
                            />
                        </div>
                        <PixelButton variant="info" class="w-full" @click="validar"
                            >VALIDAR CÓDIGO</PixelButton
                        >
                        <div
                            v-if="resultado"
                            class="border-2 border-border p-4 flex items-center gap-3"
                            :class="
                                resultado.valido
                                    ? 'bg-success text-success-foreground'
                                    : 'bg-destructive text-destructive-foreground'
                            "
                        >
                            <PhCheckCircle
                                v-if="resultado.valido"
                                weight="fill"
                                class="pixel-icon"
                                :size="24"
                            />
                            <PhXCircle v-else weight="fill" class="pixel-icon" :size="24" />
                            <div>
                                <div class="font-pixel text-xs">
                                    {{ resultado.valido ? 'CUPOM VÁLIDO' : 'CUPOM INVÁLIDO' }}
                                </div>
                                <div class="font-sans text-sm mt-0.5">{{ resultado.msg }}</div>
                            </div>
                        </div>
                        <div
                            class="border-2 border-dashed border-border p-3 font-sans text-xs text-muted-foreground"
                        >
                            Dica: tente
                            <span class="font-pixel text-[10px]">LUMEN-ABC12-X9K</span> ou qualquer
                            código gerado no app do aluno.
                        </div>
                    </div>
                </PixelCard>
            </div>

            <div>
                <div class="flex items-center justify-between mb-4 flex-wrap gap-3">
                    <div class="font-pixel text-sm">MINHAS VANTAGENS PUBLICADAS</div>
                    <div class="flex gap-2 font-pixel text-[9px]">
                        <PixelBadge tone="green"
                            >{{ vantagens.filter((v) => v.ativa).length }} ATIVAS</PixelBadge
                        >
                        <PixelBadge tone="gold"
                            >{{ vantagens.filter((v) => !v.ativa).length }} INATIVAS</PixelBadge
                        >
                    </div>
                </div>
                <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    <PixelCard
                        v-for="v in vantagens"
                        :key="v.id"
                        class="overflow-hidden"
                        :class="!v.ativa ? 'opacity-60' : ''"
                    >
                        <div
                            class="aspect-[5/3] flex items-center justify-center bg-hud text-hud-foreground crt-scanlines border-b-2 border-border text-6xl relative"
                        >
                            <span :class="v.ativa ? 'animate-bob' : ''">{{ v.emoji }}</span>
                            <div
                                v-if="!v.ativa"
                                class="absolute top-2 right-2 font-pixel text-[8px] bg-destructive text-destructive-foreground border-2 border-border px-1.5 py-0.5"
                            >
                                PAUSADA
                            </div>
                        </div>
                        <div class="p-4">
                            <div class="flex items-start justify-between gap-2">
                                <div class="font-pixel text-xs">{{ v.nome.toUpperCase() }}</div>
                                <PixelBadge tone="gold">{{ v.categoria.toUpperCase() }}</PixelBadge>
                            </div>
                            <p class="font-sans text-xs text-foreground/75 mt-2">
                                {{ v.descricao }}
                            </p>
                            <div class="mt-3 flex items-center justify-between">
                                <div class="font-pixel text-sm flex items-center gap-1">
                                    <CoinIcon :size="12" /> {{ v.custo }}
                                </div>
                                <div class="flex gap-2">
                                    <PixelButton
                                        size="sm"
                                        variant="ghost"
                                        @click="toggleAtiva(v.id)"
                                    >
                                        <PhEye v-if="!v.ativa" weight="fill" :size="14" />
                                        <PhEyeSlash v-else weight="fill" :size="14" />
                                    </PixelButton>
                                    <PixelButton size="sm" variant="danger" @click="remover(v.id)"
                                        ><PhTrash weight="fill" :size="14"
                                    /></PixelButton>
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>
            </div>
        </main>
    </div>
</template>

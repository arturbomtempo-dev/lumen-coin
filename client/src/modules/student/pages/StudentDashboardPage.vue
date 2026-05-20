<script setup lang="ts">
import { useStudentStore } from '@/modules/student/stores/student.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import XPBar from '@/shared/components/XPBar.vue';
import { achievements, initialBenefits } from '@/shared/data/mockData';
import {
    PhLightning,
    PhMagnifyingGlass,
    PhSparkle,
    PhStorefront,
    PhTrophy,
    PhX,
} from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';

const store = useStudentStore();
const { balance, level, xp, name, character, courseName, institutionName } = storeToRefs(store);

const pendingRedemption = ref<{ name: string; cost: number } | null>(null);
const generatedCoupon = ref<{ code: string; name: string } | null>(null);
const searchQuery = ref('');
const selectedCategory = ref('todas');
const sortOrder = ref<'recentes' | 'menor' | 'maior' | 'alfabetica'>('recentes');
const onlyAffordable = ref(false);

const categories = computed(() => Array.from(new Set(initialBenefits.map((v) => v.category))));

const filteredBenefits = computed(() => {
    let list = [...initialBenefits];
    if (searchQuery.value) {
        const q = searchQuery.value.toLowerCase();
        list = list.filter(
            (v) =>
                v.name.toLowerCase().includes(q) ||
                v.description.toLowerCase().includes(q) ||
                v.company.toLowerCase().includes(q)
        );
    }
    if (selectedCategory.value !== 'todas')
        list = list.filter((v) => v.category === selectedCategory.value);
    if (onlyAffordable.value) list = list.filter((v) => balance.value >= v.cost);
    if (sortOrder.value === 'menor') list = list.sort((a, b) => a.cost - b.cost);
    else if (sortOrder.value === 'maior') list = list.sort((a, b) => b.cost - a.cost);
    else if (sortOrder.value === 'alfabetica')
        list = list.sort((a, b) => a.name.localeCompare(b.name));
    return list;
});

function confirmRedemption() {
    if (!pendingRedemption.value) return;
    if (balance.value < pendingRedemption.value.cost) {
        pendingRedemption.value = null;
        return;
    }
    const code = store.spend(pendingRedemption.value.cost, pendingRedemption.value.name);
    generatedCoupon.value = { code, name: pendingRedemption.value.name };
    pendingRedemption.value = null;
}
</script>

<template>
    <div class="space-y-10">
        <section class="grid lg:grid-cols-[1.1fr_1fr] gap-6 items-start">
            <PixelCard class="p-6 crt-scanlines">
                <div class="flex items-center justify-between">
                    <div>
                        <div class="font-pixel text-[10px] text-primary">▶ CARTEIRA DO JOGADOR</div>
                        <div class="font-pixel text-xl mt-2">{{ name.toUpperCase() }}</div>
                        <div class="font-sans text-sm text-muted-foreground">
                            {{ courseName }} · {{ institutionName }}
                        </div>
                    </div>
                    <div class="border-2 border-border bg-hud p-1">
                        <MarioAvatar :character="character" :size="64" />
                    </div>
                </div>

                <div class="mt-6 border-2 border-border bg-hud text-hud-foreground p-5 text-center">
                    <div class="font-pixel text-[10px] mb-2">SALDO ATUAL</div>
                    <div class="flex items-center justify-center gap-3">
                        <CoinIcon :size="36" class="animate-bob" />
                        <span class="font-pixel text-4xl md:text-5xl animate-coin-flash">{{
                            balance.toLocaleString('pt-BR')
                        }}</span>
                    </div>
                    <div class="font-display text-sm mt-2 opacity-80 tracking-wider">
                        LUMEN COINS
                    </div>
                </div>

                <div class="mt-5 grid grid-cols-2 gap-4">
                    <div class="border-2 border-border bg-card p-3">
                        <div class="font-pixel text-[9px] text-muted-foreground">NÍVEL</div>
                        <div class="font-pixel text-md md:text-2xl mt-1 flex items-center gap-2">
                            <PhTrophy weight="fill" class="pixel-icon text-primary" /> LV {{ level }}
                        </div>
                    </div>
                    <div class="border-2 border-border bg-card p-3">
                        <div class="font-pixel text-[9px] text-muted-foreground">PRÓXIMO NÍVEL</div>
                        <div class="mt-2"><XPBar :value="xp" /></div>
                    </div>
                </div>
            </PixelCard>

            <PixelCard class="p-6">
                <div class="font-pixel text-[10px] text-primary">▶ CONQUISTAS</div>
                <div class="font-pixel text-sm mt-1 mb-4">MEDALHAS</div>
                <div class="grid grid-cols-2 gap-3">
                    <div
                        v-for="achievement in achievements"
                        :key="achievement.id"
                        class="border-2 border-border p-3"
                        :class="achievement.unlocked ? 'bg-card' : 'bg-muted opacity-50'"
                    >
                        <PixelBadge
                            :tone="achievement.tone as 'gold' | 'blue' | 'green' | 'red' | 'teal'"
                            >{{ achievement.unlocked ? '✓' : '?' }}
                            {{ achievement.name.toUpperCase() }}</PixelBadge
                        >
                        <p class="font-sans text-xs text-foreground/75 mt-2">
                            {{ achievement.description }}
                        </p>
                    </div>
                </div>
            </PixelCard>
        </section>

        <section>
            <div class="flex items-end justify-between mb-4 flex-wrap gap-3">
                <div>
                    <div class="font-pixel text-[10px] text-primary">▶ MARKETPLACE · WORLD 1-2</div>
                    <h2 class="font-pixel text-xl mt-2 flex items-center gap-2">
                        <PhStorefront weight="fill" class="pixel-icon" /> VANTAGENS
                    </h2>
                </div>
                <div class="font-pixel text-[10px] text-muted-foreground">
                    {{ filteredBenefits.length }} DE {{ initialBenefits.length }} VANTAGENS
                </div>
            </div>

            <PixelCard class="p-4 mb-5">
                <div class="grid md:grid-cols-[1fr_auto_auto] gap-3 items-stretch">
                    <div class="relative">
                        <PhMagnifyingGlass
                            weight="bold"
                            :size="16"
                            class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none"
                        />
                        <PixelInput
                            v-model="searchQuery"
                            placeholder="Buscar vantagem, empresa, descrição..."
                            class="pr-8 w-full"
                            style="padding-left: 2rem"
                        />
                        <button
                            v-if="searchQuery"
                            type="button"
                            class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                            @click="searchQuery = ''"
                        >
                            <PhX weight="bold" :size="14" />
                        </button>
                    </div>
                    <select
                        v-model="sortOrder"
                        class="bg-input border-2 border-border px-3 py-2 font-display text-base focus:outline-none"
                    >
                        <option value="recentes">ORDENAR: PADRÃO</option>
                        <option value="menor">MENOR PREÇO</option>
                        <option value="maior">MAIOR PREÇO</option>
                        <option value="alfabetica">A → Z</option>
                    </select>
                    <label
                        class="flex items-center gap-2 font-pixel text-[9px] border-2 border-border bg-card px-3 py-2 cursor-pointer"
                    >
                        <input v-model="onlyAffordable" type="checkbox" class="accent-primary" /> SÓ
                        POSSO RESGATAR
                    </label>
                </div>
                <div class="mt-3 flex flex-wrap gap-2">
                    <button
                        class="border-2 border-border font-pixel text-[9px] px-2 py-1 transition-all"
                        :class="
                            selectedCategory === 'todas'
                                ? 'bg-primary text-primary-foreground'
                                : 'bg-card hover:-translate-y-0.5'
                        "
                        @click="selectedCategory = 'todas'"
                    >
                        TODAS
                    </button>
                    <button
                        v-for="c in categories"
                        :key="c"
                        class="border-2 border-border font-pixel text-[9px] px-2 py-1 transition-all"
                        :class="
                            selectedCategory === c
                                ? 'bg-primary text-primary-foreground'
                                : 'bg-card hover:-translate-y-0.5'
                        "
                        @click="selectedCategory = c"
                    >
                        {{ c.toUpperCase() }}
                    </button>
                </div>
            </PixelCard>

            <PixelCard v-if="filteredBenefits.length === 0" class="p-8 text-center">
                <div class="font-pixel text-sm">GAME OVER · NENHUMA VANTAGEM</div>
                <p class="font-sans text-sm text-muted-foreground mt-2">
                    Tente outra busca ou remova os filtros.
                </p>
                <div class="mt-4">
                    <PixelButton
                        variant="ghost"
                        @click="
                            searchQuery = '';
                            selectedCategory = 'todas';
                            onlyAffordable = false;
                        "
                        >LIMPAR FILTROS</PixelButton
                    >
                </div>
            </PixelCard>

            <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
                <PixelCard
                    v-for="v in filteredBenefits"
                    :key="v.id"
                    class="overflow-hidden flex flex-col"
                >
                    <div
                        class="aspect-5/3 flex items-center justify-center bg-hud text-hud-foreground crt-scanlines border-b-2 border-border text-7xl"
                    >
                        <span class="animate-bob" style="image-rendering: pixelated">{{
                            v.emoji
                        }}</span>
                    </div>
                    <div class="p-4 flex-1 flex flex-col">
                        <div class="flex items-start justify-between gap-2">
                            <div>
                                <div class="font-pixel text-xs">{{ v.name.toUpperCase() }}</div>
                                <div class="font-sans text-xs text-muted-foreground mt-1">
                                    {{ v.company }}
                                </div>
                            </div>
                            <PixelBadge tone="gold">{{ v.category.toUpperCase() }}</PixelBadge>
                        </div>
                        <p class="font-sans text-sm text-foreground/75 mt-2 flex-1">
                            {{ v.description }}
                        </p>
                        <div class="mt-4 flex items-center justify-between">
                            <div class="font-pixel text-sm flex items-center gap-1">
                                <CoinIcon :size="14" /> {{ v.cost }}
                            </div>
                            <PixelButton
                                size="sm"
                                :variant="balance >= v.cost ? 'success' : 'ghost'"
                                :disabled="balance < v.cost"
                                @click="pendingRedemption = { name: v.name, cost: v.cost }"
                            >
                                <PhSparkle weight="fill" class="pixel-icon" />
                                {{ balance >= v.cost ? 'RESGATAR' : 'FALTAM MOEDAS' }}
                            </PixelButton>
                        </div>
                    </div>
                </PixelCard>
            </div>
        </section>


        <div
            v-if="pendingRedemption"
            class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/70"
            @click="pendingRedemption = null"
        >
            <div
                class="w-full max-w-md bg-card text-card-foreground border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
                @click.stop
            >
                <div
                    class="bg-secondary text-secondary-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs"
                >
                    ⚠ CONFIRMAR RESGATE
                </div>
                <div class="p-5">
                    <p class="font-display text-xl">
                        Resgatar <span class="text-primary">{{ pendingRedemption.name }}</span> por
                        <span class="inline-flex items-center gap-1"
                            ><CoinIcon :size="16" /> {{ pendingRedemption.cost }}</span
                        >?
                    </p>
                    <p class="font-sans text-sm text-muted-foreground mt-3">
                        Um cupom será gerado no seu extrato. Saldo após resgate:
                        <strong>{{
                            (balance - pendingRedemption.cost).toLocaleString('pt-BR')
                        }}</strong
                        >.
                    </p>
                    <div class="mt-5 flex gap-3">
                        <PixelButton
                            variant="ghost"
                            class="flex-1"
                            @click="pendingRedemption = null"
                            >CANCELAR</PixelButton
                        >
                        <PixelButton variant="success" class="flex-1" @click="confirmRedemption"
                            ><PhLightning weight="fill" class="pixel-icon" /> CONFIRMAR</PixelButton
                        >
                    </div>
                </div>
            </div>
        </div>

        <div
            v-if="generatedCoupon"
            class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80"
            @click="generatedCoupon = null"
        >
            <div
                class="w-full max-w-md bg-card border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
                @click.stop
            >
                <div
                    class="bg-success text-success-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs"
                >
                    ★ RECOMPENSA DESBLOQUEADA!
                </div>
                <div class="p-6 text-center">
                    <div class="flex justify-center mb-3">
                        <CoinIcon :size="48" class="animate-bob" />
                    </div>
                    <p class="font-display text-xl">
                        Você resgatou <strong>{{ generatedCoupon.name }}</strong
                        >!
                    </p>
                    <div
                        class="mt-5 border-2 border-dashed border-border bg-hud text-hud-foreground p-4"
                    >
                        <div class="font-pixel text-[9px] mb-2">CUPOM GERADO</div>
                        <div class="font-pixel text-lg md:text-xl break-all">
                            {{ generatedCoupon.code }}
                        </div>
                    </div>
                    <p class="font-sans text-xs text-muted-foreground mt-3">
                        Apresente este código no estabelecimento parceiro.
                    </p>
                    <div class="mt-5">
                        <PixelButton
                            variant="primary"
                            class="w-full"
                            @click="generatedCoupon = null"
                            >FECHAR</PixelButton
                        >
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

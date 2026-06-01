<script setup lang="ts">
import { getBenefitsByCompany, type BenefitResponse } from '@/modules/company/services/benefit.service';
import { getCompanies } from '@/modules/institution/services/institution.service';
import { useStudentStore } from '@/modules/student/stores/student.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import {
    PhLightning,
    PhMagnifyingGlass,
    PhSparkle,
    PhStorefront,
    PhX,
} from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';

type BenefitWithCompany = BenefitResponse & { companyName: string };

const store = useStudentStore();
const { balance } = storeToRefs(store);

const benefits = ref<BenefitWithCompany[]>([]);
const isLoading = ref(false);

const searchQuery = ref('');
const sortOrder = ref<'default' | 'menor' | 'maior' | 'alfabetica'>('default');
const onlyAffordable = ref(false);

const pendingRedemption = ref<{ name: string; cost: number } | null>(null);
const generatedCoupon = ref<{ code: string; name: string } | null>(null);

async function loadBenefits() {
    isLoading.value = true;
    try {
        const companiesRes = await getCompanies();
        const results = await Promise.all(
            companiesRes.data.map(async (company) => {
                const res = await getBenefitsByCompany(company.id);
                return res.data.map((b) => ({ ...b, companyName: company.name }));
            }),
        );
        benefits.value = results.flat();
    } catch {
    } finally {
        isLoading.value = false;
    }
}

const filteredBenefits = computed(() => {
    let list = [...benefits.value];
    if (searchQuery.value) {
        const q = searchQuery.value.toLowerCase();
        list = list.filter(
            (b) =>
                b.name.toLowerCase().includes(q) ||
                b.description.toLowerCase().includes(q) ||
                b.companyName.toLowerCase().includes(q),
        );
    }
    if (onlyAffordable.value) list = list.filter((b) => balance.value >= b.cost);
    if (sortOrder.value === 'menor') list.sort((a, b) => a.cost - b.cost);
    else if (sortOrder.value === 'maior') list.sort((a, b) => b.cost - a.cost);
    else if (sortOrder.value === 'alfabetica') list.sort((a, b) => a.name.localeCompare(b.name));
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

onMounted(() => {
    loadBenefits();
});
</script>

<template>
    <div class="space-y-6">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ MARKETPLACE</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhStorefront weight="fill" class="pixel-icon" /> VANTAGENS
            </h1>
        </div>

        <PixelCard class="p-4">
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
                    <option value="default">ORDENAR: PADRÃO</option>
                    <option value="menor">MENOR PREÇO</option>
                    <option value="maior">MAIOR PREÇO</option>
                    <option value="alfabetica">A → Z</option>
                </select>
                <label
                    class="flex items-center gap-2 font-pixel text-[9px] border-2 border-border bg-card px-3 py-2 cursor-pointer"
                >
                    <input v-model="onlyAffordable" type="checkbox" class="accent-primary" />
                    SÓ POSSO RESGATAR
                </label>
            </div>
        </PixelCard>

        <div
            v-if="isLoading"
            class="font-pixel text-[10px] text-muted-foreground py-8 text-center"
        >
            CARREGANDO VANTAGENS...
        </div>

        <PixelCard v-else-if="filteredBenefits.length === 0" class="p-8 text-center">
            <div class="font-pixel text-sm">NENHUMA VANTAGEM ENCONTRADA</div>
            <p class="font-sans text-sm text-muted-foreground mt-2">
                Tente outra busca ou remova os filtros.
            </p>
            <div class="mt-4">
                <PixelButton
                    variant="ghost"
                    @click="
                        searchQuery = '';
                        onlyAffordable = false;
                    "
                >
                    LIMPAR FILTROS
                </PixelButton>
            </div>
        </PixelCard>

        <div v-else class="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
            <PixelCard
                v-for="benefit in filteredBenefits"
                :key="benefit.id"
                class="overflow-hidden flex flex-col"
            >
                <div class="border-b-2 border-border">
                    <img
                        :src="benefit.imageUrl"
                        :alt="benefit.name"
                        class="w-full aspect-video object-cover"
                    />
                </div>
                <div class="p-4 flex-1 flex flex-col">
                    <div>
                        <div class="font-pixel text-xs">{{ benefit.name.toUpperCase() }}</div>
                        <div class="font-sans text-xs text-muted-foreground mt-1">
                            {{ benefit.companyName }}
                        </div>
                    </div>
                    <p class="font-sans text-sm text-foreground/75 mt-2 flex-1">
                        {{ benefit.description }}
                    </p>
                    <div class="mt-4 flex items-center justify-between">
                        <div class="font-pixel text-sm flex items-center gap-1">
                            <CoinIcon :size="14" /> {{ benefit.cost }}
                        </div>
                        <PixelButton
                            size="sm"
                            :variant="balance >= benefit.cost ? 'success' : 'ghost'"
                            :disabled="balance < benefit.cost"
                            @click="pendingRedemption = { name: benefit.name, cost: benefit.cost }"
                        >
                            <PhSparkle weight="fill" class="pixel-icon" />
                            {{ balance >= benefit.cost ? 'RESGATAR' : 'FALTAM MOEDAS' }}
                        </PixelButton>
                    </div>
                </div>
            </PixelCard>
        </div>
    </div>

    <div
        v-if="pendingRedemption"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/70"
        @click.self="pendingRedemption = null"
    >
        <div
            class="w-full max-w-md bg-card text-card-foreground border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
        >
            <div
                class="bg-secondary text-secondary-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs"
            >
                ⚠ CONFIRMAR RESGATE
            </div>
            <div class="p-5">
                <p class="font-display text-xl">
                    Resgatar
                    <span class="text-primary">{{ pendingRedemption.name }}</span> por
                    <span class="inline-flex items-center gap-1">
                        <CoinIcon :size="16" /> {{ pendingRedemption.cost }}
                    </span>?
                </p>
                <p class="font-sans text-sm text-muted-foreground mt-3">
                    Um cupom será gerado no seu extrato. Saldo após resgate:
                    <strong>{{ (balance - pendingRedemption.cost).toLocaleString('pt-BR') }}</strong
                    >.
                </p>
                <div class="mt-5 flex gap-3">
                    <PixelButton
                        variant="ghost"
                        class="flex-1"
                        @click="pendingRedemption = null"
                    >
                        CANCELAR
                    </PixelButton>
                    <PixelButton variant="success" class="flex-1" @click="confirmRedemption">
                        <PhLightning weight="fill" class="pixel-icon" /> CONFIRMAR
                    </PixelButton>
                </div>
            </div>
        </div>
    </div>

    <div
        v-if="generatedCoupon"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80"
        @click.self="generatedCoupon = null"
    >
        <div
            class="w-full max-w-md bg-card border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
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
                    Você resgatou <strong>{{ generatedCoupon.name }}</strong>!
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
                    >
                        FECHAR
                    </PixelButton>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { getBenefitsByCompany } from '@/modules/company/services/benefit.service';
import { getBenefitsByInstitution } from '@/modules/institution/services/benefit.service';
import {
    getCompanies,
    getInstitutions,
} from '@/modules/institution/services/institution.service';
import {
    getRedeemedBenefitIds,
    redeemBenefit,
} from '@/modules/student/services/benefit-redemption.service';
import { useStudentStore } from '@/modules/student/stores/student.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import {
    PhCheckCircle,
    PhLightning,
    PhMagnifyingGlass,
    PhSparkle,
    PhStorefront,
    PhX,
} from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';

type BenefitWithOwner = {
    id: string;
    name: string;
    description: string;
    imageUrl: string;
    cost: number;
    ownerName: string;
};

const store = useStudentStore();
const { balance } = storeToRefs(store);

const benefits = ref<BenefitWithOwner[]>([]);
const isLoading = ref(true);

const searchQuery = ref('');
const sortOrder = ref<'default' | 'menor' | 'maior' | 'alfabetica'>('default');
const onlyAffordable = ref(false);

const pendingRedemption = ref<{ id: string; name: string; cost: number } | null>(null);
const redemptionSuccess = ref<{ benefitName: string } | null>(null);
const isRedeeming = ref(false);

const pendingBenefitIds = ref<Set<string>>(new Set());
const usedBenefitIds = ref<Set<string>>(new Set());

async function loadBenefits() {
    isLoading.value = true;
    try {
        const [companiesRes, institutionsRes, redeemedRes] = await Promise.all([
            getCompanies(),
            getInstitutions(),
            getRedeemedBenefitIds(),
        ]);
        pendingBenefitIds.value = new Set(redeemedRes.data.pendingBenefitIds);
        usedBenefitIds.value = new Set(redeemedRes.data.usedBenefitIds);
        const [companyBenefits, institutionBenefits] = await Promise.all([
            Promise.all(
                companiesRes.data.map(async (company) => {
                    const res = await getBenefitsByCompany(company.id);
                    return res.data.map((b) => ({ ...b, ownerName: company.name }));
                }),
            ),
            Promise.all(
                institutionsRes.data.map(async (institution) => {
                    const res = await getBenefitsByInstitution(institution.id);
                    return res.data.map((b) => ({ ...b, ownerName: institution.name }));
                }),
            ),
        ]);
        benefits.value = [...companyBenefits.flat(), ...institutionBenefits.flat()];
    } catch {
    } finally {
        isLoading.value = false;
    }
}

const filteredBenefits = computed(() => {
    let list = benefits.value.filter((b) => !usedBenefitIds.value.has(b.id));
    if (searchQuery.value) {
        const q = searchQuery.value.toLowerCase();
        list = list.filter(
            (b) =>
                b.name.toLowerCase().includes(q) ||
                b.description.toLowerCase().includes(q) ||
                b.ownerName.toLowerCase().includes(q),
        );
    }
    if (onlyAffordable.value) list = list.filter((b) => balance.value >= b.cost);
    if (sortOrder.value === 'menor') list.sort((a, b) => a.cost - b.cost);
    else if (sortOrder.value === 'maior') list.sort((a, b) => b.cost - a.cost);
    else if (sortOrder.value === 'alfabetica') list.sort((a, b) => a.name.localeCompare(b.name));
    return list;
});

async function confirmRedemption() {
    if (!pendingRedemption.value || isRedeeming.value) return;
    isRedeeming.value = true;
    try {
        await redeemBenefit(pendingRedemption.value.id);
        redemptionSuccess.value = { benefitName: pendingRedemption.value.name };
        pendingRedemption.value = null;
        const [, redeemedRes] = await Promise.all([store.loadProfile(), getRedeemedBenefitIds()]);
        pendingBenefitIds.value = new Set(redeemedRes.data.pendingBenefitIds);
        usedBenefitIds.value = new Set(redeemedRes.data.usedBenefitIds);
    } catch {
    } finally {
        isRedeeming.value = false;
    }
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
                        placeholder="Buscar vantagem, empresa, instituição, descrição..."
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

        <div v-if="isLoading" class="font-pixel text-[10px] text-muted-foreground py-8 text-center">
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
                            {{ benefit.ownerName }}
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
                            :variant="
                                pendingBenefitIds.has(benefit.id)
                                    ? 'secondary'
                                    : balance >= benefit.cost
                                      ? 'success'
                                      : 'ghost'
                            "
                            :disabled="pendingBenefitIds.has(benefit.id) || balance < benefit.cost"
                            @click="
                                !pendingBenefitIds.has(benefit.id) &&
                                    (pendingRedemption = {
                                        id: benefit.id,
                                        name: benefit.name,
                                        cost: benefit.cost,
                                    })
                            "
                        >
                            <PhSparkle weight="fill" class="pixel-icon" />
                            {{
                                pendingBenefitIds.has(benefit.id)
                                    ? 'JÁ SOLICITADA'
                                    : balance >= benefit.cost
                                      ? 'RESGATAR'
                                      : 'FALTAM MOEDAS'
                            }}
                        </PixelButton>
                    </div>
                </div>
            </PixelCard>
        </div>
    </div>

    <div
        v-if="pendingRedemption"
        class="fixed inset-0 z-60 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4"
        @click.self="pendingRedemption = null"
    >
        <PixelCard class="w-full max-w-lg p-5 space-y-4">
            <div class="flex items-start justify-between gap-4">
                <div>
                    <div class="font-pixel text-[10px] text-primary">▶ CONFIRMAR RESGATE</div>
                    <h3 class="font-pixel text-sm md:text-lg mt-2">
                        {{ pendingRedemption.name.toUpperCase() }}
                    </h3>
                </div>
                <div class="font-pixel text-sm flex items-center gap-1 text-primary">
                    <CoinIcon :size="16" /> {{ pendingRedemption.cost }}
                </div>
            </div>

            <p class="font-sans text-sm text-foreground/75">
                O valor será descontado do seu saldo e o cupom será adicionado aos seus resgates.
            </p>

            <div class="flex flex-col sm:flex-row gap-3 sm:justify-end">
                <PixelButton variant="ghost" @click="pendingRedemption = null">
                    CANCELAR
                </PixelButton>
                <PixelButton variant="success" :disabled="isRedeeming" @click="confirmRedemption">
                    <PhLightning weight="fill" class="pixel-icon" />
                    {{ isRedeeming ? 'RESGATANDO...' : 'CONFIRMAR' }}
                </PixelButton>
            </div>
        </PixelCard>
    </div>

    <div
        v-if="redemptionSuccess"
        class="fixed inset-0 z-70 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4"
        @click.self="redemptionSuccess = null"
    >
        <PixelCard class="w-full max-w-md p-5 text-center space-y-4">
            <PhCheckCircle weight="fill" class="pixel-icon text-success mx-auto" :size="40" />
            <div>
                <div class="font-pixel text-[10px] text-success">▶ RESGATE EFETUADO</div>
                <h3 class="font-pixel text-sm md:text-lg mt-2">
                    {{ redemptionSuccess.benefitName.toUpperCase() }}
                </h3>
            </div>
            <p class="font-sans text-sm text-foreground/75">
                O cupom foi gerado e já está disponível em seus resgates.
            </p>
            <PixelButton variant="success" class="mx-auto" @click="redemptionSuccess = null">
                OK
            </PixelButton>
        </PixelCard>
    </div>
</template>

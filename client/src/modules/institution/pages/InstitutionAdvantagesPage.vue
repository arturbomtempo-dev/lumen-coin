<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    confirmRedemption,
    getRedeemedBenefitIds,
    validateCoupon,
    type ValidateBenefitRedemptionResponse,
} from '@/modules/institution/services/benefit-redemption.service';
import {
    createBenefit,
    deleteBenefit,
    getBenefitsByInstitution,
    updateBenefit,
    type BenefitResponse,
} from '@/modules/institution/services/benefit.service';
import { registerBenefitSchema } from '@/modules/schemas/register-benefit.schema';
import { updateBenefitSchema } from '@/modules/schemas/update-benefit.schema';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import {
    PhBuildings,
    PhCheckCircle,
    PhImageSquare,
    PhLockSimple,
    PhPencil,
    PhPlus,
    PhStar,
    PhTicket,
    PhTrash,
    PhX,
} from '@phosphor-icons/vue';
import { onMounted, ref } from 'vue';
import { toast } from 'vue-sonner';

const authStore = useAuthStore();

// ── Benefits ────────────────────────────────────────────────────────────────

const benefits = ref<BenefitResponse[]>([]);
const isLoading = ref(false);
const redeemedBenefitIds = ref<Set<string>>(new Set());

const createForm = ref({ name: '', description: '', cost: '' });
const createImage = ref<File | null>(null);
const createImagePreview = ref<string | null>(null);
const createErrors = ref<Record<string, string>>({});
const isCreating = ref(false);

const editingBenefit = ref<BenefitResponse | null>(null);
const editForm = ref({ name: '', description: '', cost: '' });
const editImage = ref<File | null>(null);
const editImagePreview = ref<string | null>(null);
const editErrors = ref<Record<string, string>>({});
const isUpdating = ref(false);

async function loadBenefits() {
    if (!authStore.user?.id) return;
    isLoading.value = true;
    try {
        const [benefitsRes, redeemedRes] = await Promise.all([
            getBenefitsByInstitution(authStore.user.id),
            getRedeemedBenefitIds(),
        ]);
        benefits.value = benefitsRes.data;
        const { pendingBenefitIds, usedBenefitIds } = redeemedRes.data;
        redeemedBenefitIds.value = new Set([...pendingBenefitIds, ...usedBenefitIds]);
    } catch {
    } finally {
        isLoading.value = false;
    }
}

function validateCreate(): boolean {
    const result = registerBenefitSchema.safeParse({
        name: createForm.value.name,
        description: createForm.value.description,
        cost: createForm.value.cost,
        image: createImage.value,
    });
    if (!result.success) {
        createErrors.value = {};
        result.error.issues.forEach((issue) => {
            createErrors.value[issue.path[0] as string] = issue.message;
        });
        return false;
    }
    createErrors.value = {};
    return true;
}

function validateEdit(): boolean {
    const result = updateBenefitSchema.safeParse({
        name: editForm.value.name,
        description: editForm.value.description,
        cost: editForm.value.cost,
    });
    if (!result.success) {
        editErrors.value = {};
        result.error.issues.forEach((issue) => {
            editErrors.value[issue.path[0] as string] = issue.message;
        });
        return false;
    }
    editErrors.value = {};
    return true;
}

function handleCreateFileChange(e: Event) {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) {
        createImage.value = file;
        createImagePreview.value = URL.createObjectURL(file);
        delete createErrors.value.image;
    }
}

function handleEditFileChange(e: Event) {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) {
        editImage.value = file;
        editImagePreview.value = URL.createObjectURL(file);
    }
}

async function handleCreate(e: Event) {
    e.preventDefault();
    if (!validateCreate()) return;
    isCreating.value = true;
    try {
        await createBenefit(
            {
                name: createForm.value.name.trim(),
                description: createForm.value.description.trim(),
                cost: Number(createForm.value.cost),
            },
            createImage.value!,
        );
        toast.success('Vantagem criada com sucesso!');
        createForm.value = { name: '', description: '', cost: '' };
        createImage.value = null;
        createImagePreview.value = null;
        createErrors.value = {};
        await loadBenefits();
    } catch {
    } finally {
        isCreating.value = false;
    }
}

function openEdit(benefit: BenefitResponse) {
    editingBenefit.value = benefit;
    editForm.value = {
        name: benefit.name,
        description: benefit.description,
        cost: String(benefit.cost),
    };
    editImage.value = null;
    editImagePreview.value = null;
    editErrors.value = {};
}

function closeEdit() {
    editingBenefit.value = null;
    editErrors.value = {};
}

async function handleUpdate() {
    if (!editingBenefit.value || !validateEdit()) return;
    isUpdating.value = true;
    try {
        await updateBenefit(
            editingBenefit.value.id,
            {
                name: editForm.value.name.trim(),
                description: editForm.value.description.trim(),
                cost: Number(editForm.value.cost),
            },
            editImage.value,
        );
        toast.success('Vantagem atualizada com sucesso!');
        closeEdit();
        await loadBenefits();
    } catch {
    } finally {
        isUpdating.value = false;
    }
}

async function handleDelete(id: string) {
    try {
        await deleteBenefit(id);
        benefits.value = benefits.value.filter((b) => b.id !== id);
        toast.success('Vantagem removida com sucesso!');
    } catch {}
}

// ── Coupon validation ────────────────────────────────────────────────────────

const couponCode = ref('');
const couponError = ref('');
const validatedRedemption = ref<ValidateBenefitRedemptionResponse | null>(null);
const usageNotes = ref('');
const isValidating = ref(false);
const isConfirming = ref(false);
const redemptionConfirmed = ref(false);

function resetCouponForm() {
    couponCode.value = '';
    couponError.value = '';
    validatedRedemption.value = null;
    usageNotes.value = '';
    redemptionConfirmed.value = false;
}

async function validate() {
    const code = couponCode.value.trim().toUpperCase();
    if (!code) {
        couponError.value = 'Digite o código do cupom.';
        return;
    }
    couponError.value = '';
    validatedRedemption.value = null;
    usageNotes.value = '';
    isValidating.value = true;
    try {
        const res = await validateCoupon(code);
        validatedRedemption.value = res.data;
    } catch (err: unknown) {
        const status = (err as { response?: { status?: number } })?.response?.status;
        if (status === 404) {
            couponError.value = 'Código não encontrado ou não pertence a esta instituição.';
        } else if (status === 400) {
            couponError.value = 'Este código já foi utilizado.';
        } else {
            couponError.value = 'Erro ao validar o código. Tente novamente.';
        }
    } finally {
        isValidating.value = false;
    }
}

async function handleConfirmRedemption() {
    if (!validatedRedemption.value || !usageNotes.value.trim() || isConfirming.value) return;
    isConfirming.value = true;
    try {
        await confirmRedemption(validatedRedemption.value.couponCode, usageNotes.value.trim());
        redemptionConfirmed.value = true;
        validatedRedemption.value = null;
        usageNotes.value = '';
        couponCode.value = '';
        couponError.value = '';
    } catch {
    } finally {
        isConfirming.value = false;
    }
}

onMounted(() => {
    loadBenefits();
});
</script>

<template>
    <div class="space-y-6">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ VANTAGENS DA INSTITUIÇÃO</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhBuildings weight="fill" class="pixel-icon" /> GERENCIAR VANTAGENS
            </h1>
        </div>

        <div class="grid lg:grid-cols-2 gap-6 items-start">
            <!-- Left: create form + benefits list -->
            <div class="space-y-6">
                <PixelCard class="p-6">
                    <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPlus weight="bold" /> NOVA VANTAGEM
                    </div>
                    <form class="space-y-4" @submit="handleCreate">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">NOME</label>
                            <PixelInput
                                v-model="createForm.name"
                                placeholder="Ex: Desconto na Cantina"
                                :maxLength="150"
                            />
                            <p
                                v-if="createErrors.name"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ createErrors.name }}
                            </p>
                        </div>

                        <div>
                            <label class="font-pixel text-[10px] block mb-2">DESCRIÇÃO</label>
                            <textarea
                                v-model="createForm.description"
                                placeholder="Descreva a vantagem oferecida..."
                                maxlength="2000"
                                rows="3"
                                class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-sans text-sm shadow-[inset_2px_2px_0_0_hsl(var(--border)/0.25)] focus:outline-none focus:ring-2 focus:ring-ring resize-none"
                            />
                            <p
                                v-if="createErrors.description"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ createErrors.description }}
                            </p>
                        </div>

                        <div>
                            <label class="font-pixel text-[10px] block mb-2">CUSTO (MOEDAS)</label>
                            <PixelInput
                                v-model="createForm.cost"
                                type="number"
                                min="1"
                                placeholder="Ex: 150"
                            />
                            <p
                                v-if="createErrors.cost"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ createErrors.cost }}
                            </p>
                        </div>

                        <div>
                            <label class="font-pixel text-[10px] block mb-2">IMAGEM</label>
                            <label class="block cursor-pointer">
                                <div
                                    class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-base flex items-center gap-2 hover:bg-card transition-colors"
                                >
                                    <PhImageSquare :size="18" />
                                    <span v-if="createImage">{{ createImage.name }}</span>
                                    <span v-else class="opacity-60">Selecionar imagem...</span>
                                </div>
                                <input
                                    type="file"
                                    accept="image/jpeg,image/png,image/webp,image/gif"
                                    class="hidden"
                                    @change="handleCreateFileChange"
                                />
                            </label>
                            <div v-if="createImagePreview" class="mt-2">
                                <img
                                    :src="createImagePreview"
                                    alt="Pré-visualização"
                                    class="w-full aspect-video object-cover border-2 border-border"
                                />
                            </div>
                            <p
                                v-if="createErrors.image"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ createErrors.image }}
                            </p>
                        </div>

                        <PixelButton
                            variant="success"
                            class="w-full"
                            type="submit"
                            :disabled="isCreating"
                        >
                            <PhPlus weight="bold" />
                            {{ isCreating ? 'PUBLICANDO...' : 'PUBLICAR VANTAGEM' }}
                        </PixelButton>
                    </form>
                </PixelCard>

                <div>
                    <div class="flex items-center justify-between mb-4 flex-wrap gap-3">
                        <div class="font-pixel text-sm flex items-center gap-2">
                            <PhBuildings weight="fill" class="pixel-icon" /> MINHAS VANTAGENS
                        </div>
                        <div class="font-pixel text-[9px] text-muted-foreground">
                            {{ benefits.length }} VANTAGENS CADASTRADAS
                        </div>
                    </div>

                    <div
                        v-if="isLoading"
                        class="font-pixel text-[10px] text-muted-foreground py-8 text-center"
                    >
                        CARREGANDO...
                    </div>

                    <PixelCard v-else-if="benefits.length === 0" class="p-8 text-center">
                        <div class="font-pixel text-sm">NENHUMA VANTAGEM</div>
                        <p class="font-sans text-sm text-muted-foreground mt-2">
                            Crie a primeira vantagem da instituição acima.
                        </p>
                    </PixelCard>

                    <div v-else class="space-y-4">
                        <PixelCard
                            v-for="benefit in benefits"
                            :key="benefit.id"
                            class="overflow-hidden flex flex-col sm:flex-row"
                        >
                            <div
                                class="sm:w-32 shrink-0 border-b-2 sm:border-b-0 sm:border-r-2 border-border"
                            >
                                <img
                                    :src="benefit.imageUrl"
                                    :alt="benefit.name"
                                    class="w-full h-32 object-cover"
                                />
                            </div>
                            <div class="p-4 flex-1 flex flex-col">
                                <div class="font-pixel text-xs">
                                    {{ benefit.name.toUpperCase() }}
                                </div>
                                <p
                                    class="font-sans text-xs text-foreground/75 mt-1 flex-1 line-clamp-2"
                                >
                                    {{ benefit.description }}
                                </p>
                                <div class="mt-3 flex items-center justify-between gap-2">
                                    <div
                                        class="font-pixel text-sm flex items-center gap-1 shrink-0"
                                    >
                                        <CoinIcon :size="12" /> {{ benefit.cost }}
                                    </div>
                                    <div
                                        v-if="redeemedBenefitIds.has(benefit.id)"
                                        class="flex items-center gap-1.5 border-2 border-border px-2 py-1 font-pixel text-[8px]"
                                        style="
                                            color: hsl(var(--muted-foreground));
                                            background: hsl(var(--muted));
                                        "
                                    >
                                        <PhLockSimple
                                            weight="fill"
                                            :size="12"
                                            class="pixel-icon shrink-0"
                                        />
                                        JÁ RESGATADA
                                    </div>
                                    <div v-else class="flex gap-2">
                                        <PixelButton
                                            size="sm"
                                            variant="ghost"
                                            @click="openEdit(benefit)"
                                        >
                                            <PhPencil weight="fill" :size="14" />
                                        </PixelButton>
                                        <PixelButton
                                            size="sm"
                                            variant="danger"
                                            @click="handleDelete(benefit.id)"
                                        >
                                            <PhTrash weight="fill" :size="14" />
                                        </PixelButton>
                                    </div>
                                </div>
                            </div>
                        </PixelCard>
                    </div>
                </div>
            </div>

            <!-- Right: coupon validation -->
            <PixelCard class="p-6">
                <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                    <PhTicket weight="fill" class="pixel-icon" /> VALIDAR CUPOM
                </div>
                <div class="space-y-4">
                    <div>
                        <label class="font-pixel text-[10px] block mb-2">CÓDIGO DO CUPOM</label>
                        <PixelInput
                            v-model="couponCode"
                            placeholder="LUMEN-XXXXX-XXX"
                            style="text-transform: uppercase"
                            @keyup.enter="validate"
                        />
                        <p
                            v-if="couponError"
                            class="font-sans text-xs mt-1"
                            style="color: hsl(var(--destructive))"
                        >
                            {{ couponError }}
                        </p>
                    </div>

                    <PixelButton
                        variant="info"
                        class="w-full"
                        :disabled="isValidating"
                        @click="validate"
                    >
                        {{ isValidating ? 'VERIFICANDO...' : 'VERIFICAR CÓDIGO' }}
                    </PixelButton>

                    <div v-if="validatedRedemption" class="space-y-4">
                        <div
                            class="border-2 border-border p-4 flex items-start gap-3"
                            style="
                                border-color: hsl(var(--success));
                                background: hsl(var(--success) / 0.08);
                            "
                        >
                            <PhCheckCircle
                                weight="fill"
                                class="pixel-icon shrink-0 mt-0.5"
                                :size="20"
                                style="color: hsl(var(--success))"
                            />
                            <div class="space-y-1 text-sm">
                                <div
                                    class="font-pixel text-[9px]"
                                    style="color: hsl(var(--success))"
                                >
                                    CÓDIGO VÁLIDO
                                </div>
                                <div class="font-display text-lg">
                                    <span class="font-sans text-xs text-muted-foreground"
                                        >Aluno:
                                    </span>
                                    {{ validatedRedemption.studentName }}
                                </div>
                                <div class="font-display text-lg">
                                    <span class="font-sans text-xs text-muted-foreground"
                                        >Vantagem:
                                    </span>
                                    {{ validatedRedemption.benefitName }}
                                </div>
                                <div class="font-display text-lg flex items-center gap-1">
                                    <span class="font-sans text-xs text-muted-foreground"
                                        >Custo:
                                    </span>
                                    <CoinIcon :size="12" />
                                    {{ validatedRedemption.coinsSpent }}
                                </div>
                            </div>
                        </div>

                        <div>
                            <label class="font-pixel text-[10px] block mb-2">
                                INSTRUÇÕES DE USO DA VANTAGEM
                            </label>
                            <textarea
                                v-model="usageNotes"
                                rows="4"
                                placeholder="Descreva como o aluno pode utilizar esta vantagem..."
                                class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-lg shadow-[inset_2px_2px_0_0_hsl(var(--border)/0.25)] focus:outline-none focus:ring-2 focus:ring-ring resize-none"
                            ></textarea>
                            <p class="font-sans text-xs text-muted-foreground mt-1">
                                Esta mensagem será enviada ao aluno por e-mail.
                            </p>
                        </div>

                        <div class="flex gap-3">
                            <PixelButton
                                variant="ghost"
                                class="flex-1"
                                @click="resetCouponForm"
                            >
                                CANCELAR
                            </PixelButton>
                            <PixelButton
                                variant="success"
                                class="flex-1"
                                :disabled="isConfirming || !usageNotes.trim()"
                                @click="handleConfirmRedemption"
                            >
                                <PhCheckCircle weight="fill" class="pixel-icon" />
                                {{ isConfirming ? 'CONFIRMANDO...' : 'CONFIRMAR RESGATE' }}
                            </PixelButton>
                        </div>
                    </div>

                    <div
                        v-if="redemptionConfirmed"
                        class="border-2 border-border p-4"
                        style="
                            border-color: hsl(var(--success));
                            background: hsl(var(--success) / 0.08);
                        "
                    >
                        <div class="flex items-center gap-3">
                            <PhStar
                                weight="fill"
                                class="pixel-icon shrink-0"
                                :size="20"
                                style="color: hsl(var(--success))"
                            />
                            <div>
                                <div
                                    class="font-pixel text-[9px]"
                                    style="color: hsl(var(--success))"
                                >
                                    RESGATE CONFIRMADO!
                                </div>
                                <div class="font-sans text-sm mt-0.5 text-muted-foreground">
                                    O e-mail com as instruções foi enviado ao aluno.
                                </div>
                            </div>
                        </div>
                        <div class="mt-3">
                            <PixelButton
                                variant="ghost"
                                size="sm"
                                class="w-full"
                                @click="resetCouponForm"
                            >
                                VALIDAR OUTRO CÓDIGO
                            </PixelButton>
                        </div>
                    </div>
                </div>
            </PixelCard>
        </div>
    </div>

    <!-- Edit modal -->
    <div
        v-if="editingBenefit"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/70"
        @click.self="closeEdit"
    >
        <div
            class="w-full max-w-2xl bg-card text-card-foreground border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop overflow-y-auto max-h-[90vh]"
        >
            <div
                class="bg-primary text-primary-foreground border-b-4 border-border px-4 py-2 flex items-center justify-between"
            >
                <span class="font-pixel text-xs">EDITAR VANTAGEM</span>
                <button
                    type="button"
                    class="hover:opacity-75 transition-opacity"
                    aria-label="Fechar"
                    @click="closeEdit"
                >
                    <PhX weight="bold" :size="16" />
                </button>
            </div>

            <div class="p-6 space-y-4">
                <div>
                    <label class="font-pixel text-[10px] block mb-2">NOME</label>
                    <PixelInput v-model="editForm.name" :maxLength="150" />
                    <p
                        v-if="editErrors.name"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ editErrors.name }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[10px] block mb-2">DESCRIÇÃO</label>
                    <textarea
                        v-model="editForm.description"
                        maxlength="2000"
                        rows="3"
                        class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-sans text-sm shadow-[inset_2px_2px_0_0_hsl(var(--border)/0.25)] focus:outline-none focus:ring-2 focus:ring-ring resize-none"
                    />
                    <p
                        v-if="editErrors.description"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ editErrors.description }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[10px] block mb-2">CUSTO (MOEDAS)</label>
                    <PixelInput v-model="editForm.cost" type="number" min="1" />
                    <p
                        v-if="editErrors.cost"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ editErrors.cost }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[10px] block mb-2">IMAGEM (OPCIONAL)</label>
                    <div class="mb-2 border-2 border-border">
                        <img
                            :src="editImagePreview ?? editingBenefit.imageUrl"
                            :alt="editingBenefit.name"
                            class="w-full aspect-video object-cover"
                        />
                    </div>
                    <label class="block cursor-pointer">
                        <div
                            class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-base flex items-center gap-2 hover:bg-card transition-colors"
                        >
                            <PhImageSquare :size="18" />
                            <span v-if="editImage">{{ editImage.name }}</span>
                            <span v-else class="opacity-60">Trocar imagem...</span>
                        </div>
                        <input
                            type="file"
                            accept="image/jpeg,image/png,image/webp,image/gif"
                            class="hidden"
                            @change="handleEditFileChange"
                        />
                    </label>
                </div>

                <div class="flex gap-3 pt-2">
                    <PixelButton
                        variant="ghost"
                        class="flex-1"
                        type="button"
                        @click="closeEdit"
                    >
                        CANCELAR
                    </PixelButton>
                    <PixelButton
                        variant="success"
                        class="flex-1"
                        type="button"
                        :disabled="isUpdating"
                        @click="handleUpdate"
                    >
                        {{ isUpdating ? 'SALVANDO...' : 'SALVAR ALTERAÇÕES' }}
                    </PixelButton>
                </div>
            </div>
        </div>
    </div>
</template>

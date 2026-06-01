<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    createBenefit,
    deleteBenefit,
    getBenefitsByCompany,
    updateBenefit,
    type BenefitResponse,
} from '@/modules/company/services/benefit.service';
import { registerBenefitSchema } from '@/modules/schemas/register-benefit.schema';
import { updateBenefitSchema } from '@/modules/schemas/update-benefit.schema';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import {
    PhImageSquare,
    PhPencil,
    PhPlus,
    PhStorefront,
    PhTrash,
    PhX,
} from '@phosphor-icons/vue';
import { onMounted, ref } from 'vue';
import { toast } from 'vue-sonner';

const authStore = useAuthStore();

const benefits = ref<BenefitResponse[]>([]);
const isLoading = ref(false);

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
        const response = await getBenefitsByCompany(authStore.user.id);
        benefits.value = response.data;
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
    } catch {
    }
}

onMounted(() => {
    loadBenefits();
});
</script>

<template>
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
                        placeholder="Ex: Combo Especial"
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
                        placeholder="Ex: 200"
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
                    <PhStorefront weight="fill" class="pixel-icon" /> MINHAS VANTAGENS
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
                    Crie sua primeira vantagem acima.
                </p>
            </PixelCard>

            <div v-else class="grid sm:grid-cols-2 gap-4">
                <PixelCard
                    v-for="benefit in benefits"
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
                        <div class="font-pixel text-xs">{{ benefit.name.toUpperCase() }}</div>
                        <p class="font-sans text-xs text-foreground/75 mt-2 flex-1 line-clamp-3">
                            {{ benefit.description }}
                        </p>
                        <div class="mt-4 flex items-center justify-between">
                            <div class="font-pixel text-sm flex items-center gap-1">
                                <CoinIcon :size="12" /> {{ benefit.cost }}
                            </div>
                            <div class="flex gap-2">
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

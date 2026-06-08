<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    changeCompanyPassword,
    deleteCompany,
    getCompany,
    updateCompany,
} from '@/modules/company/services/company.service';
import {
    changeCompanyPasswordSchema,
    type ChangeCompanyPasswordFormData,
} from '@/modules/schemas/change-company-password.schema';
import { updateCompanySchema } from '@/modules/schemas/update-company.schema';
import CompanyAdvantagesPage from '@/modules/company/pages/CompanyAdvantagesPage.vue';
import {
    confirmRedemption,
    validateCoupon,
    type ValidateBenefitRedemptionResponse,
} from '@/modules/company/services/benefit-redemption.service';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';

import { useThemeStore } from '@/shared/stores/theme.store';
import {
    PhArrowLeft,
    PhCheckCircle,
    PhEye,
    PhEyeSlash,
    PhFloppyDisk,
    PhGameController,
    PhKey,
    PhMoon,
    PhSignOut,
    PhStar,
    PhStorefront,
    PhSun,
    PhTicket,
    PhTrash,
    PhUser,
    PhXCircle,
} from '@phosphor-icons/vue';
import { vMaska } from 'maska/vue';
import { onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const themeStore = useThemeStore();
const authStore = useAuthStore();
const router = useRouter();

type Tab = 'benefits' | 'account';
const tab = ref<Tab>('benefits');

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
    } catch (err: any) {
        const status = err?.response?.status;
        if (status === 404) {
            couponError.value = 'Código não encontrado ou não pertence a esta empresa.';
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

const {
    fields: profileData,
    errors: profileErrors,
    isSubmitting: profileIsSubmitting,
    validate: validateProfile,
    clearErrors: clearProfileErrors,
} = useForm(updateCompanySchema);

const {
    fields: passwordData,
    errors: passwordErrors,
    isSubmitting: passwordIsSubmitting,
    validate: validatePassword,
    clearErrors: clearPasswordErrors,
} = useForm(changeCompanyPasswordSchema);

passwordData.value = {
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
};

const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmNewPassword = ref(false);

function digitsOnly(value: string): string {
    return value.replace(/\D/g, '');
}

function formatCnpj(cnpj: string): string {
    const digits = cnpj.replace(/\D/g, '');
    return digits.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
}

async function loadProfile() {
    if (!authStore.user?.id) return;
    try {
        const response = await getCompany(authStore.user.id);
        profileData.value = {
            name: response.data.name,
            email: response.data.email,
            cnpj: formatCnpj(response.data.cnpj),
        };
    } catch {}
}

async function handleUpdateProfile(e: Event) {
    e.preventDefault();
    if (!validateProfile() || !authStore.user?.id) return;
    profileIsSubmitting.value = true;
    try {
        await updateCompany(authStore.user.id, {
            name: profileData.value.name.trim(),
            email: profileData.value.email.trim(),
            cnpj: digitsOnly(profileData.value.cnpj),
        });
        toast.success('Perfil atualizado com sucesso!');
        clearProfileErrors();
    } catch {
    } finally {
        profileIsSubmitting.value = false;
    }
}

async function submitPasswordChange() {
    if (!authStore.user?.id) return;
    if (!validatePassword()) return;

    passwordIsSubmitting.value = true;

    try {
        await changeCompanyPassword(
            authStore.user.id,
            passwordData.value as ChangeCompanyPasswordFormData
        );
        passwordData.value = { currentPassword: '', newPassword: '', confirmNewPassword: '' };
        clearPasswordErrors();
        toast.success('Senha alterada com sucesso!');
    } catch {
    } finally {
        passwordIsSubmitting.value = false;
    }
}

const isDeletingAccount = ref(false);

async function handleDeleteAccount() {
    if (!authStore.user?.id) return;
    isDeletingAccount.value = true;
    try {
        await deleteCompany(authStore.user.id);
        await authStore.logout();
        router.push({ name: 'home' });
    } catch {
    } finally {
        isDeletingAccount.value = false;
    }
}

async function handleLogout() {
    await authStore.logout();
    router.push('/login');
}

const tabs = [
    { id: 'benefits' as Tab, label: 'VANTAGENS', icon: PhStorefront },
    { id: 'account' as Tab, label: 'MINHA CONTA', icon: PhUser },
];

onMounted(() => {
    loadProfile();
});
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
                <div class="hidden sm:block font-pixel text-[10px]">
                    {{ authStore.user?.name?.toUpperCase() ?? 'EMPRESA' }} · ADMIN
                </div>
                <div class="flex items-center gap-2">
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))]"
                        @click="themeStore.toggle()"
                    >
                        <PhSun
                            v-if="themeStore.theme === 'night'"
                            weight="fill"
                            class="pixel-icon"
                        />
                        <PhMoon v-else weight="fill" class="pixel-icon" />
                        {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
                    </button>
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))]"
                        aria-label="Sair"
                        @click="handleLogout"
                    >
                        <PhSignOut weight="bold" class="pixel-icon" />
                        SAIR
                    </button>
                </div>
            </div>
        </header>

        <main class="flex-1 container py-6 space-y-6">
            <div>
                <div class="font-pixel text-[10px] text-primary">▶ PARCEIRO</div>
                <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                    <PhStorefront weight="fill" class="pixel-icon" /> PORTAL DA EMPRESA
                </h1>
            </div>

            <div class="flex gap-2 flex-wrap">
                <button
                    v-for="t in tabs"
                    :key="t.id"
                    class="border-2 border-border px-4 py-2 font-pixel text-[10px] flex items-center gap-2 transition-all"
                    :class="
                        tab === t.id
                            ? 'bg-primary text-primary-foreground shadow-[3px_3px_0_0_hsl(var(--border))]'
                            : 'bg-card hover:-translate-y-0.5'
                    "
                    @click="tab = t.id"
                >
                    <component :is="t.icon" weight="fill" class="pixel-icon" :size="14" />
                    {{ t.label }}
                </button>
            </div>

            <template v-if="tab === 'benefits'">
                <div class="grid lg:grid-cols-2 gap-6 items-start">
                    <CompanyAdvantagesPage />

                    <PixelCard class="p-6">
                        <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                            <PhTicket weight="fill" class="pixel-icon" /> VALIDAR CUPOM
                        </div>
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[10px] block mb-2"
                                    >CÓDIGO DO CUPOM</label
                                >
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
                                    style="border-color: hsl(var(--success)); background: hsl(var(--success) / 0.08)"
                                >
                                    <PhCheckCircle
                                        weight="fill"
                                        class="pixel-icon shrink-0 mt-0.5"
                                        :size="20"
                                        style="color: hsl(var(--success))"
                                    />
                                    <div class="space-y-1 text-sm">
                                        <div class="font-pixel text-[9px]" style="color: hsl(var(--success))">
                                            CÓDIGO VÁLIDO
                                        </div>
                                        <div class="font-display text-lg">
                                            <span class="font-sans text-xs text-muted-foreground">Aluno: </span>
                                            {{ validatedRedemption.studentName }}
                                        </div>
                                        <div class="font-display text-lg">
                                            <span class="font-sans text-xs text-muted-foreground">Vantagem: </span>
                                            {{ validatedRedemption.benefitName }}
                                        </div>
                                        <div class="font-display text-lg flex items-center gap-1">
                                            <span class="font-sans text-xs text-muted-foreground">Custo: </span>
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
                                style="border-color: hsl(var(--success)); background: hsl(var(--success) / 0.08)"
                            >
                                <div class="flex items-center gap-3">
                                    <PhStar weight="fill" class="pixel-icon shrink-0" :size="20" style="color: hsl(var(--success))" />
                                    <div>
                                        <div class="font-pixel text-[9px]" style="color: hsl(var(--success))">RESGATE CONFIRMADO!</div>
                                        <div class="font-sans text-sm mt-0.5 text-muted-foreground">
                                            O e-mail com as instruções foi enviado ao aluno.
                                        </div>
                                    </div>
                                </div>
                                <div class="mt-3">
                                    <PixelButton variant="ghost" size="sm" class="w-full" @click="resetCouponForm">
                                        VALIDAR OUTRO CÓDIGO
                                    </PixelButton>
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>
            </template>

            <template v-if="tab === 'account'">
                <div class="space-y-6">
                    <div class="grid lg:grid-cols-2 gap-6 items-start">
                        <PixelCard class="p-6">
                            <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                                <PhUser weight="fill" /> EDITAR PERFIL
                            </h2>
                            <form class="space-y-4" @submit="handleUpdateProfile">
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"
                                        >NOME DA EMPRESA</label
                                    >
                                    <PixelInput v-model="profileData.name" />
                                    <p
                                        v-if="profileErrors.name"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ profileErrors.name }}
                                    </p>
                                </div>
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2">E-MAIL</label>
                                    <PixelInput v-model="profileData.email" type="email" />
                                    <p
                                        v-if="profileErrors.email"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ profileErrors.email }}
                                    </p>
                                </div>
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2">CNPJ</label>
                                    <PixelInput
                                        v-model="profileData.cnpj"
                                        v-maska="'##.###.###/####-##'"
                                        maxlength="18"
                                        placeholder="00.000.000/0000-00"
                                    />
                                    <p
                                        v-if="profileErrors.cnpj"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ profileErrors.cnpj }}
                                    </p>
                                </div>
                                <PixelButton
                                    type="submit"
                                    variant="success"
                                    class="w-full flex items-center gap-2"
                                    :disabled="profileIsSubmitting"
                                >
                                    <PhFloppyDisk weight="bold" /> SALVAR ALTERAÇÕES
                                </PixelButton>
                            </form>
                        </PixelCard>

                        <PixelCard class="p-6">
                            <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                                <PhTrash weight="fill" /> EXCLUIR CONTA
                            </h2>
                            <p class="font-sans text-sm text-muted-foreground mb-6">
                                Esta ação é permanente e não pode ser desfeita. Todas as vantagens
                                publicadas serão removidas.
                            </p>
                            <PixelButton
                                variant="danger"
                                class="w-full"
                                :disabled="isDeletingAccount"
                                @click="handleDeleteAccount"
                            >
                                <PhTrash weight="fill" /> EXCLUIR MINHA CONTA
                            </PixelButton>
                        </PixelCard>
                    </div>

                    <PixelCard class="p-6">
                        <div class="font-pixel text-[10px] text-primary">&#9658; ALTERAR SENHA</div>

                        <form class="mt-4 space-y-3" @submit.prevent="submitPasswordChange">
                            <div>
                                <label class="font-pixel text-[9px] block mb-1">SENHA ATUAL</label>
                                <div class="relative">
                                    <PixelInput
                                        v-model="passwordData.currentPassword"
                                        :type="showCurrentPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="Sua senha atual"
                                    />
                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2"
                                        type="button"
                                        @click="showCurrentPassword = !showCurrentPassword"
                                    >
                                        <PhEyeSlash
                                            v-if="showCurrentPassword"
                                            :size="18"
                                            weight="bold"
                                        />
                                        <PhEye v-else :size="18" weight="bold" />
                                    </button>
                                </div>
                                <p
                                    v-if="passwordErrors.currentPassword"
                                    class="font-sans text-xs mt-1 text-destructive"
                                >
                                    {{ passwordErrors.currentPassword }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-1">NOVA SENHA</label>
                                <div class="relative">
                                    <PixelInput
                                        v-model="passwordData.newPassword"
                                        :type="showNewPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="Mínimo 8 caracteres"
                                    />
                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2"
                                        type="button"
                                        @click="showNewPassword = !showNewPassword"
                                    >
                                        <PhEyeSlash
                                            v-if="showNewPassword"
                                            :size="18"
                                            weight="bold"
                                        />
                                        <PhEye v-else :size="18" weight="bold" />
                                    </button>
                                </div>
                                <PasswordStrengthHint :password="passwordData.newPassword" />
                                <p
                                    v-if="passwordErrors.newPassword"
                                    class="font-sans text-xs mt-1 text-destructive"
                                >
                                    {{ passwordErrors.newPassword }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-1"
                                    >CONFIRMAR NOVA SENHA</label
                                >
                                <div class="relative">
                                    <PixelInput
                                        v-model="passwordData.confirmNewPassword"
                                        :type="showConfirmNewPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="Repita a nova senha"
                                    />
                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2"
                                        type="button"
                                        @click="showConfirmNewPassword = !showConfirmNewPassword"
                                    >
                                        <PhEyeSlash
                                            v-if="showConfirmNewPassword"
                                            :size="18"
                                            weight="bold"
                                        />
                                        <PhEye v-else :size="18" weight="bold" />
                                    </button>
                                </div>
                                <p
                                    v-if="passwordErrors.confirmNewPassword"
                                    class="font-sans text-xs mt-1 text-destructive"
                                >
                                    {{ passwordErrors.confirmNewPassword }}
                                </p>
                            </div>

                            <PixelButton
                                type="submit"
                                variant="success"
                                class="flex items-center gap-2"
                                :disabled="passwordIsSubmitting"
                            >
                                <PhKey weight="bold" /> ATUALIZAR SENHA
                            </PixelButton>
                        </form>
                    </PixelCard>
                </div>
            </template>
        </main>
    </div>
</template>

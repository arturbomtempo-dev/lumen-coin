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
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { initialBenefits, type Benefit } from '@/shared/data/mockData';
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
    PhPlus,
    PhSignOut,
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

const VALID_CODES = ['LUMEN-ABC12-X9K', 'LUMEN-BYTE1-PZZ'];

type BenefitEntry = Benefit & { active: boolean };

const benefits = ref<BenefitEntry[]>(
    initialBenefits
        .filter((v) => v.company === 'Byte Burger' || v.company === 'Arcade World')
        .map((v) => ({ ...v, active: true }))
);
const benefitForm = ref({ name: '', description: '', cost: 200, emoji: '🎁', category: 'Comida' });
const couponCode = ref('');
const validationResult = ref<{ valid: boolean; msg: string } | null>(null);

const CATEGORIES = ['Comida', 'Vestuário', 'Tecnologia', 'Lazer', 'Educação'];

function createBenefit(e: Event) {
    e.preventDefault();
    if (!benefitForm.value.name || !benefitForm.value.description) return;
    const newBenefit: BenefitEntry = {
        id: 'v' + Date.now(),
        name: benefitForm.value.name,
        description: benefitForm.value.description,
        cost: Number(benefitForm.value.cost),
        company: authStore.user?.name ?? 'Empresa',
        emoji: benefitForm.value.emoji,
        category: benefitForm.value.category,
        active: true,
    };
    benefits.value.unshift(newBenefit);
    toast.success(`Vantagem "${newBenefit.name}" publicada!`);
    benefitForm.value = { name: '', description: '', cost: 200, emoji: '🎁', category: 'Comida' };
}

function toggleActive(id: string) {
    const v = benefits.value.find((x) => x.id === id);
    if (v) v.active = !v.active;
}

function removeBenefit(id: string) {
    benefits.value = benefits.value.filter((v) => v.id !== id);
}

function validate() {
    const c = couponCode.value.trim().toUpperCase();
    if (!c) return void (validationResult.value = { valid: false, msg: 'Digite um código.' });
    if (VALID_CODES.includes(c) || c.startsWith('LUMEN-')) {
        validationResult.value = { valid: true, msg: 'Cupom válido. Recompensa liberada!' };
    } else {
        validationResult.value = { valid: false, msg: 'Cupom inválido ou já utilizado.' };
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
                    <PixelCard class="p-6">
                        <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                            <PhPlus weight="bold" /> CRIAR VANTAGEM
                        </div>
                        <form class="space-y-4" @submit="createBenefit">
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">NOME</label>
                                <PixelInput
                                    v-model="benefitForm.name"
                                    placeholder="Ex: Combo Byte"
                                    required
                                />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">DESCRIÇÃO</label>
                                <PixelInput
                                    v-model="benefitForm.description"
                                    placeholder="Ex: 1 lanche + batata + refri"
                                    required
                                />
                            </div>
                            <div class="grid grid-cols-3 gap-3">
                                <div>
                                    <label class="font-pixel text-[10px] block mb-2">CUSTO</label>
                                    <PixelInput v-model="benefitForm.cost" type="number" :min="1" />
                                </div>
                                <div>
                                    <label class="font-pixel text-[10px] block mb-2">EMOJI</label>
                                    <PixelInput v-model="benefitForm.emoji" :max-length="2" />
                                </div>
                                <div>
                                    <label class="font-pixel text-[10px] block mb-2"
                                        >CATEGORIA</label
                                    >
                                    <select
                                        v-model="benefitForm.category"
                                        class="w-full bg-input border-2 border-border px-3 py-2 font-display text-lg focus:outline-none"
                                    >
                                        <option v-for="c in CATEGORIES" :key="c">{{ c }}</option>
                                    </select>
                                </div>
                            </div>
                            <PixelButton variant="success" class="w-full" type="submit">
                                <PhPlus weight="bold" /> PUBLICAR VANTAGEM
                            </PixelButton>
                        </form>
                    </PixelCard>

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
                                    class="uppercase"
                                />
                            </div>
                            <PixelButton variant="info" class="w-full" @click="validate">
                                VALIDAR CÓDIGO
                            </PixelButton>
                            <div
                                v-if="validationResult"
                                class="border-2 border-border p-4 flex items-center gap-3"
                                :class="
                                    validationResult.valid
                                        ? 'bg-success text-success-foreground'
                                        : 'bg-destructive text-destructive-foreground'
                                "
                            >
                                <PhCheckCircle
                                    v-if="validationResult.valid"
                                    weight="fill"
                                    class="pixel-icon"
                                    :size="24"
                                />
                                <PhXCircle v-else weight="fill" class="pixel-icon" :size="24" />
                                <div>
                                    <div class="font-pixel text-xs">
                                        {{
                                            validationResult.valid
                                                ? 'CUPOM VÁLIDO'
                                                : 'CUPOM INVÁLIDO'
                                        }}
                                    </div>
                                    <div class="font-sans text-sm mt-0.5">
                                        {{ validationResult.msg }}
                                    </div>
                                </div>
                            </div>
                            <div
                                class="border-2 border-dashed border-border p-3 font-sans text-xs text-muted-foreground"
                            >
                                Dica: tente
                                <span class="font-pixel text-[10px]">LUMEN-ABC12-X9K</span> ou
                                qualquer código gerado no app do aluno.
                            </div>
                        </div>
                    </PixelCard>
                </div>

                <div>
                    <div class="flex items-center justify-between mb-4 flex-wrap gap-3">
                        <div class="font-pixel text-sm">MINHAS VANTAGENS PUBLICADAS</div>
                        <div class="flex gap-2 font-pixel text-[9px]">
                            <PixelBadge tone="green">
                                {{ benefits.filter((v) => v.active).length }} ATIVAS
                            </PixelBadge>
                            <PixelBadge tone="gold">
                                {{ benefits.filter((v) => !v.active).length }} INATIVAS
                            </PixelBadge>
                        </div>
                    </div>
                    <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        <PixelCard
                            v-for="v in benefits"
                            :key="v.id"
                            class="overflow-hidden"
                            :class="!v.active ? 'opacity-60' : ''"
                        >
                            <div
                                class="aspect-5/3 flex items-center justify-center bg-hud text-hud-foreground crt-scanlines border-b-2 border-border text-6xl relative"
                            >
                                <span :class="v.active ? 'animate-bob' : ''">{{ v.emoji }}</span>
                                <div
                                    v-if="!v.active"
                                    class="absolute top-2 right-2 font-pixel text-[8px] bg-destructive text-destructive-foreground border-2 border-border px-1.5 py-0.5"
                                >
                                    PAUSADA
                                </div>
                            </div>
                            <div class="p-4">
                                <div class="flex items-start justify-between gap-2">
                                    <div class="font-pixel text-xs">{{ v.name.toUpperCase() }}</div>
                                    <PixelBadge tone="gold">{{
                                        v.category.toUpperCase()
                                    }}</PixelBadge>
                                </div>
                                <p class="font-sans text-xs text-foreground/75 mt-2">
                                    {{ v.description }}
                                </p>
                                <div class="mt-3 flex items-center justify-between">
                                    <div class="font-pixel text-sm flex items-center gap-1">
                                        <CoinIcon :size="12" /> {{ v.cost }}
                                    </div>
                                    <div class="flex gap-2">
                                        <PixelButton
                                            size="sm"
                                            variant="ghost"
                                            @click="toggleActive(v.id)"
                                        >
                                            <PhEye v-if="!v.active" weight="fill" :size="14" />
                                            <PhEyeSlash v-else weight="fill" :size="14" />
                                        </PixelButton>
                                        <PixelButton
                                            size="sm"
                                            variant="danger"
                                            @click="removeBenefit(v.id)"
                                        >
                                            <PhTrash weight="fill" :size="14" />
                                        </PixelButton>
                                    </div>
                                </div>
                            </div>
                        </PixelCard>
                    </div>
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

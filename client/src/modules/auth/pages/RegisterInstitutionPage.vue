<script setup lang="ts">
import { registerInstitutionRequest } from '@/modules/auth/services/auth.service';
import {
    registerInstitutionSchema,
    type RegisterInstitutionFormData,
} from '@/modules/schemas/register-institution.schema';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import {
    PhArrowLeft,
    PhArrowRight,
    PhBuildings,
    PhCheckCircle,
    PhEye,
    PhEyeSlash,
    PhHouse,
    PhIdentificationCard,
} from '@phosphor-icons/vue';
import { vMaska } from 'maska/vue';
import { computed, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

interface InstitutionStep {
    label: string;
    icon: object;
}

const router = useRouter();

const step = ref<number>(0);
const attemptedSteps = ref<Record<number, boolean>>({});

const showPassword = ref<boolean>(false);

const showConfirmPassword = ref<boolean>(false);

const {
    fields: institutionForm,
    errors: institutionErrors,
    isSubmitting,
    validate,
    clearErrors,
} = useForm(registerInstitutionSchema);

institutionForm.value = {
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    cnpj: '',
    phone: '',
    zipCode: '',
    street: '',
    number: '',
    neighborhood: '',
    city: '',
};

const institutionSteps = ref<InstitutionStep[]>([
    { label: 'DADOS', icon: PhBuildings },
    { label: 'ENDEREÇO', icon: PhHouse },
    { label: 'ACESSO', icon: PhIdentificationCard },
]);

const progress = computed<number>(() => {
    return ((step.value + 1) / institutionSteps.value.length) * 100;
});

const passwordsMatch = computed<boolean | null>(() => {
    if (!institutionForm.value.password || !institutionForm.value.confirmPassword) {
        return null;
    }

    return institutionForm.value.password === institutionForm.value.confirmPassword;
});

const phoneMaskOptions = {
    mask: ['(##) ####-####', '(##) #####-####'],
};

const stepFields: Record<number, Array<keyof RegisterInstitutionFormData>> = {
    0: ['name', 'cnpj'],
    1: ['zipCode', 'street', 'number', 'neighborhood', 'city'],
    2: ['email', 'password', 'confirmPassword'],
};

function digitsOnly(value: string): string {
    return value.replace(/\D/g, '');
}

function validateCurrentStep(): boolean {
    attemptedSteps.value[step.value] = true;
    const parseResult = registerInstitutionSchema.safeParse(institutionForm.value);
    institutionErrors.value = {};
    if (parseResult.success) return true;
    const fieldsForStep = new Set(stepFields[step.value]);
    let hasError = false;
    parseResult.error.issues.forEach((issue) => {
        const field = issue.path[0] as keyof RegisterInstitutionFormData;
        if (!fieldsForStep.has(field)) return;
        if (institutionErrors.value[field]) return;
        institutionErrors.value[field] = issue.message;
        hasError = true;
    });
    return !hasError;
}

async function fetchAddressByCep(cep: string): Promise<void> {
    try {
        const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        const addressData = await response.json();
        if (addressData.erro) return;
        institutionForm.value.street = addressData.logradouro ?? '';
        institutionForm.value.neighborhood = addressData.bairro ?? '';
        institutionForm.value.city = addressData.localidade ?? '';
    } catch {
        return;
    }
}

function goToPreviousStep(): void {
    if (step.value <= 0) return;
    step.value -= 1;
}

function goBack(): void {
    router.back();
}

function getAddress(): string {
    return [
        institutionForm.value.street.trim(),
        institutionForm.value.number.trim(),
        institutionForm.value.neighborhood.trim(),
        institutionForm.value.city.trim(),
    ]
        .filter((v) => v.length > 0)
        .join(', ')
        .slice(0, 300);
}

async function submitInstitution(): Promise<void> {
    if (!validate()) return;
    isSubmitting.value = true;
    try {
        await registerInstitutionRequest({
            name: institutionForm.value.name.trim(),
            email: institutionForm.value.email.trim(),
            password: institutionForm.value.password,
            cnpj: digitsOnly(institutionForm.value.cnpj),
            zipCode: digitsOnly(institutionForm.value.zipCode),
            address: getAddress(),
        });
        toast.success('Instituição cadastrada com sucesso!', {
            description: 'Faça login para acessar o portal.',
        });
        clearErrors();
        router.push({ name: 'login' });
    } catch {
    } finally {
        isSubmitting.value = false;
    }
}

async function goToNextStep(): Promise<void> {
    if (!validateCurrentStep()) return;
    if (step.value >= institutionSteps.value.length - 1) {
        await submitInstitution();
        return;
    }
    step.value += 1;
}

watch(
    () => digitsOnly(institutionForm.value.zipCode ?? ''),
    (numeric) => {
        if (numeric.length === 8) {
            void fetchAddressByCep(numeric);
        }
    },
);

watch(
    institutionForm,
    () => {
        if (!attemptedSteps.value[step.value]) return;
        validateCurrentStep();
    },
    { deep: true },
);

</script>

<template>
    <main class="min-h-screen overflow-hidden relative">
        <div class="min-h-screen flex items-center justify-center px-4 py-10 relative z-10">
            <PixelCard class="w-full max-w-175 p-6 md:p-8">
                <button
                    class="mb-5 flex items-center gap-3 font-pixel text-[9px] transition-all"
                    type="button"
                    @click="goBack"
                >
                    <div
                        class="w-7 h-7 border-2 border-border bg-primary flex items-center justify-center shadow-[2px_2px_0px_hsl(var(--border))]"
                    >
                        <PhArrowLeft :size="14" weight="bold" />
                    </div>

                    <span>VOLTAR À TELA INICIAL</span>
                </button>

                <div class="flex items-start justify-between gap-4 mb-5">
                    <div class="flex items-center gap-4">
                        <div
                            class="bg-hud border-2 border-border p-2 shadow-[4px_4px_0px_hsl(var(--border))]"
                        >
                            <MarioAvatar character="institution" :size="56" />
                        </div>

                        <div>
                            <h1 class="font-pixel text-sm md:text-lg leading-5">
                                CRIAR CONTA DE INSTITUIÇÃO
                            </h1>
                        </div>
                    </div>

                    <div class="font-pixel text-[9px] min-w-max text-primary">
                        ETAPA {{ step + 1 }}
                    </div>
                </div>

                <div class="mb-6">
                    <div class="flex items-center justify-between font-pixel text-[9px] mb-2">
                        <span>
                            FASE {{ step + 1 }} DE
                            {{ institutionSteps.length }}
                        </span>

                        <span>{{ Math.round(progress) }}%</span>
                    </div>

                    <div class="h-4 border-2 border-border bg-card overflow-hidden">
                        <div
                            class="h-full bg-primary transition-all duration-300"
                            :style="{
                                width: `${progress}%`,
                                backgroundImage:
                                    'repeating-linear-gradient(90deg, hsl(var(--primary)) 0, hsl(var(--primary)) 6px, hsl(var(--primary-shadow)) 6px, hsl(var(--primary-shadow)) 8px)',
                            }"
                        />
                    </div>
                </div>

                <div class="flex justify-between gap-2 mb-6">
                    <div
                        v-for="(item, index) in institutionSteps"
                        :key="item.label"
                        class="flex-1 text-center"
                    >
                        <div
                            class="w-10 h-10 mx-auto mb-2 border-2 border-border flex items-center justify-center transition-all"
                            :class="
                                index === step
                                    ? 'bg-primary scale-110'
                                    : index < step
                                      ? 'bg-success text-success-foreground'
                                      : 'bg-card'
                            "
                        >
                            <PhCheckCircle v-if="index < step" :size="18" weight="fill" />

                            <component :is="item.icon" v-else :size="18" weight="fill" />
                        </div>

                        <p class="font-pixel text-[7px] leading-3">
                            {{ item.label }}
                        </p>
                    </div>
                </div>

                <div class="space-y-4">
                    <template v-if="step === 0">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    NOME DA INSTITUIÇÃO
                                </label>

                                <PixelInput
                                    v-model="institutionForm.name"
                                    placeholder="Ex: Universidade PUC Minas"
                                />
                                <p
                                    v-if="institutionErrors.name"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.name }}
                                </p>
                            </div>

                            <div class="grid grid-cols-2 gap-3">
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"> CNPJ </label>

                                    <PixelInput
                                        v-model="institutionForm.cnpj"
                                        v-maska="'##.###.###/####-##'"
                                        maxlength="18"
                                        placeholder="00.000.000/0000-00"
                                    />
                                    <p
                                        v-if="institutionErrors.cnpj"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ institutionErrors.cnpj }}
                                    </p>
                                </div>

                                <div>
                                    <label class="font-pixel text-[9px] block mb-2">
                                        TELEFONE
                                    </label>

                                    <PixelInput
                                        v-model="institutionForm.phone"
                                        v-maska="phoneMaskOptions"
                                        maxlength="15"
                                        placeholder="(00) 00000-0000"
                                    />
                                    <p
                                        v-if="institutionErrors.phone"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ institutionErrors.phone }}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 1">
                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CEP </label>

                                <PixelInput
                                    v-model="institutionForm.zipCode"
                                    v-maska="'#####-###'"
                                    maxlength="9"
                                    placeholder="00000-000"
                                />
                                <p
                                    v-if="institutionErrors.zipCode"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.zipCode }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CIDADE </label>

                                <PixelInput v-model="institutionForm.city" placeholder="Cidade" />
                                <p
                                    v-if="institutionErrors.city"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.city }}
                                </p>
                            </div>

                            <div class="col-span-2">
                                <label class="font-pixel text-[9px] block mb-2"> RUA </label>

                                <PixelInput
                                    v-model="institutionForm.street"
                                    placeholder="Rua da instituição"
                                />
                                <p
                                    v-if="institutionErrors.street"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.street }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> NÚMERO </label>

                                <PixelInput v-model="institutionForm.number" placeholder="123" />
                                <p
                                    v-if="institutionErrors.number"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.number }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> BAIRRO </label>

                                <PixelInput
                                    v-model="institutionForm.neighborhood"
                                    placeholder="Bairro"
                                />
                                <p
                                    v-if="institutionErrors.neighborhood"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.neighborhood }}
                                </p>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 2">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> E-MAIL </label>

                                <PixelInput
                                    v-model="institutionForm.email"
                                    placeholder="instituicao@email.com"
                                    type="email"
                                />
                                <p
                                    v-if="institutionErrors.email"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.email }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[10px] block mb-2"> SENHA </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="institutionForm.password"
                                        :type="showPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="Mín. 8 chars, maiúscula, número e especial"
                                    />

                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                        type="button"
                                        @click="showPassword = !showPassword"
                                    >
                                        <PhEyeSlash v-if="showPassword" :size="18" weight="bold" />

                                        <PhEye v-else :size="18" weight="bold" />
                                    </button>
                                </div>
                                <PasswordStrengthHint :password="institutionForm.password" />
                                <p
                                    v-if="institutionErrors.password"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ institutionErrors.password }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[10px] block mb-2">
                                    CONFIRMAR SENHA
                                </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="institutionForm.confirmPassword"
                                        :type="showConfirmPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="••••••••"
                                    />

                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                        type="button"
                                        @click="showConfirmPassword = !showConfirmPassword"
                                    >
                                        <PhEyeSlash
                                            v-if="showConfirmPassword"
                                            :size="18"
                                            weight="bold"
                                        />

                                        <PhEye v-else :size="18" weight="bold" />
                                    </button>
                                </div>

                                <p
                                    v-if="
                                        institutionErrors.confirmPassword || passwordsMatch !== null
                                    "
                                    class="font-pixel text-[8px] mt-2"
                                    :class="
                                        institutionErrors.confirmPassword ||
                                        passwordsMatch === false
                                            ? 'text-destructive'
                                            : 'text-success'
                                    "
                                >
                                    {{
                                        institutionErrors.confirmPassword ||
                                        passwordsMatch === false
                                            ? (institutionErrors.confirmPassword ??
                                              'SENHAS DIFERENTES')
                                            : 'SENHAS COINCIDEM'
                                    }}
                                </p>
                            </div>
                        </div>
                    </template>
                </div>

                <div class="flex justify-between gap-3 mt-7">
                    <PixelButton :disabled="step === 0" variant="ghost" @click="goToPreviousStep">
                        <PhArrowLeft :size="16" weight="bold" />

                        VOLTAR
                    </PixelButton>

                    <PixelButton :disabled="isSubmitting" @click="goToNextStep">
                        {{ step === institutionSteps.length - 1 ? 'FINALIZAR' : 'PRÓXIMA' }}

                        <PhArrowRight :size="16" weight="bold" />
                    </PixelButton>
                </div>
            </PixelCard>
        </div>
    </main>
</template>

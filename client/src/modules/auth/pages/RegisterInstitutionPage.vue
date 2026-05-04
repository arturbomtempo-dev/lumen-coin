<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import {
    PhArrowLeft,
    PhArrowRight,
    PhBuildings,
    PhCheckCircle,
    PhCrown,
    PhEye,
    PhEyeSlash,
    PhHouse,
    PhIdentificationCard,
} from '@phosphor-icons/vue';

import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';

import { type MarioCharacter } from '@/shared/data/characters';

interface InstitutionForm {
    name: string;
    email: string;
    password: string;
    confirmPassword: string;
    cnpj: string;
    phone: string;
    zipCode: string;
    street: string;
    number: string;
    neighborhood: string;
    city: string;
}

interface InstitutionStep {
    label: string;
    icon: object;
}

const router = useRouter();

const step = ref<number>(0);

const showPassword = ref<boolean>(false);

const showConfirmPassword = ref<boolean>(false);

const character = ref<MarioCharacter>('institution');

const institutionSteps = ref<InstitutionStep[]>([
    {
        label: 'AVATAR',
        icon: PhCrown,
    },
    {
        label: 'INSTITUIÇÃO',
        icon: PhBuildings,
    },
    {
        label: 'ENDEREÇO',
        icon: PhHouse,
    },
    {
        label: 'ACESSO',
        icon: PhIdentificationCard,
    },
]);

const institutionForm = ref<InstitutionForm>({
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
});

const progress = computed<number>(() => {
    return ((step.value + 1) / institutionSteps.value.length) * 100;
});

const passwordsMatch = computed<boolean | null>(() => {
    if (!institutionForm.value.password || !institutionForm.value.confirmPassword) {
        return null;
    }

    return institutionForm.value.password === institutionForm.value.confirmPassword;
});

function formatCnpj(value: string): string {
    const numericValue = value.replace(/\D/g, '').slice(0, 14);

    return numericValue
        .replace(/^(\d{2})(\d)/, '$1.$2')
        .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
        .replace(/^(\d{2})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3/$4')
        .replace(/(\d{4})(\d{1,2})$/, '$1-$2');
}

function formatPhone(value: string): string {
    const numericValue = value.replace(/\D/g, '').slice(0, 11);

    if (numericValue.length <= 10) {
        return numericValue.replace(/^(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
    }

    return numericValue.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
}

function formatCep(value: string): string {
    const numericValue = value.replace(/\D/g, '').slice(0, 8);

    return numericValue.replace(/^(\d{5})(\d)/, '$1-$2');
}

function handleCepInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    institutionForm.value.zipCode = formatCep(inputElement.value);

    const numericCep = inputElement.value.replace(/\D/g, '');

    if (numericCep.length === 8) {
        fetchAddressByCep(numericCep);
    }
}

async function fetchAddressByCep(cep: string): Promise<void> {
    try {
        const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);

        const addressData = await response.json();

        if (addressData.erro) {
            return;
        }

        institutionForm.value.street = addressData.logradouro ?? '';
        institutionForm.value.neighborhood = addressData.bairro ?? '';
        institutionForm.value.city = addressData.localidade ?? '';
    } catch {
        return;
    }
}

function handleCnpjInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    institutionForm.value.cnpj = formatCnpj(inputElement.value);
}

function handlePhoneInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    institutionForm.value.phone = formatPhone(inputElement.value);
}

function goToNextStep(): void {
    if (step.value >= institutionSteps.value.length - 1) {
        return;
    }

    step.value += 1;
}

function goToPreviousStep(): void {
    if (step.value <= 0) {
        return;
    }

    step.value -= 1;
}

function goBack(): void {
    router.back();
}
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
                            <MarioAvatar :character="character" :size="56" />
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
                        <div>
                            <p class="font-pixel text-[10px] mb-4">AVATAR DA INSTITUIÇÃO</p>

                            <button
                                class="w-full border-2 border-border p-4 text-center bg-primary text-primary-foreground shadow-[4px_4px_0px_hsl(var(--border))] -translate-y-0.5 transition-all"
                                type="button"
                            >
                                <div
                                    class="bg-hud border-2 border-border p-3 flex justify-center mb-3"
                                >
                                    <MarioAvatar :character="character" :size="70" />
                                </div>

                                <p class="font-pixel text-[10px] mb-1">INSTITUIÇÃO</p>

                                <p class="font-sans text-[11px] opacity-90">
                                    Avatar oficial da instituição
                                </p>
                            </button>
                        </div>
                    </template>

                    <template v-if="step === 1">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    NOME DA INSTITUIÇÃO
                                </label>

                                <PixelInput
                                    v-model="institutionForm.name"
                                    placeholder="Digite o nome"
                                />
                            </div>

                            <div class="grid grid-cols-2 gap-3">
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"> CNPJ </label>

                                    <PixelInput
                                        :model-value="institutionForm.zipCode"
                                        maxlength="18"
                                        placeholder="00.000.000/0000-00"
                                        @input="handleCnpjInput"
                                    />
                                </div>

                                <div>
                                    <label class="font-pixel text-[9px] block mb-2">
                                        TELEFONE
                                    </label>

                                    <PixelInput
                                        :model-value="institutionForm.phone"
                                        maxlength="15"
                                        placeholder="(00) 00000-0000"
                                        @input="handlePhoneInput"
                                    />
                                </div>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 2">
                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CEP </label>

                                <PixelInput
                                    :model-value="institutionForm.zipCode"
                                    maxlength="9"
                                    placeholder="00000-000"
                                    @input="handleCepInput"
                                />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CIDADE </label>

                                <PixelInput v-model="institutionForm.city" placeholder="Cidade" />
                            </div>

                            <div class="col-span-2">
                                <label class="font-pixel text-[9px] block mb-2"> RUA </label>

                                <PixelInput
                                    v-model="institutionForm.street"
                                    placeholder="Rua da instituição"
                                />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> NÚMERO </label>

                                <PixelInput v-model="institutionForm.number" placeholder="123" />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> BAIRRO </label>

                                <PixelInput
                                    v-model="institutionForm.neighborhood"
                                    placeholder="Bairro"
                                />
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 3">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> E-MAIL </label>

                                <PixelInput
                                    v-model="institutionForm.email"
                                    placeholder="instituicao@email.com"
                                    type="email"
                                />
                            </div>

                            <div>
                                <label class="font-pixel text-[10px] block mb-2"> SENHA </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="institutionForm.password"
                                        :type="showPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="••••••••"
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
                                    v-if="passwordsMatch !== null"
                                    class="font-pixel text-[8px] mt-2"
                                    :class="passwordsMatch ? 'text-success' : 'text-destructive'"
                                >
                                    {{ passwordsMatch ? 'SENHAS COINCIDEM' : 'SENHAS DIFERENTES' }}
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

                    <PixelButton @click="goToNextStep">
                        {{ step === institutionSteps.length - 1 ? 'FINALIZAR' : 'PRÓXIMA' }}

                        <PhArrowRight :size="16" weight="bold" />
                    </PixelButton>
                </div>
            </PixelCard>
        </div>
    </main>
</template>

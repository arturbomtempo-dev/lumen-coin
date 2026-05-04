<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

import {
    PhArrowLeft,
    PhArrowRight,
    PhCheckCircle,
    PhCrown,
    PhEye,
    PhEyeSlash,
    PhGraduationCap,
    PhHouse,
    PhIdentificationCard,
    PhUser,
} from '@phosphor-icons/vue';

import { toast } from 'vue-sonner';

import { useStudentStore } from '@/modules/student/stores/student.store';

import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
import { cursos, instituicoes } from '@/shared/data/mockData';

import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';

interface StudentForm {
    name: string;
    email: string;
    password: string;
    confirmPassword: string;
    cpf: string;
    rg: string;
    phone: string;
    zipCode: string;
    street: string;
    number: string;
    complement: string;
    neighborhood: string;
    city: string;
    institution: string;
    course: string;
}

interface StudentStep {
    label: string;
    icon: object;
}

const router = useRouter();

const studentStore = useStudentStore();

const step = ref<number>(0);

const showPassword = ref<boolean>(false);

const showConfirmPassword = ref<boolean>(false);

const character = ref<MarioCharacter>('mario');

const studentSteps = ref<StudentStep[]>([
    {
        label: 'AVATAR',
        icon: PhCrown,
    },
    {
        label: 'DADOS',
        icon: PhUser,
    },
    {
        label: 'ENDEREÇO',
        icon: PhHouse,
    },
    {
        label: 'ACADÊMICO',
        icon: PhGraduationCap,
    },
    {
        label: 'ACESSO',
        icon: PhIdentificationCard,
    },
]);

const studentForm = ref<StudentForm>({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    cpf: '',
    rg: '',
    phone: '',
    zipCode: '',
    street: '',
    number: '',
    complement: '',
    neighborhood: '',
    city: '',
    institution: instituicoes[0],
    course: cursos[0],
});

const progress = computed<number>(() => {
    return ((step.value + 1) / studentSteps.value.length) * 100;
});

const passwordsMatch = computed<boolean | null>(() => {
    if (!studentForm.value.password || !studentForm.value.confirmPassword) {
        return null;
    }

    return studentForm.value.password === studentForm.value.confirmPassword;
});

const studentCharacters = computed(() => {
    return MARIO_CHARACTERS.filter((item) => item.id !== 'institution');
});

function formatCpf(value: string): string {
    const numericValue = value.replace(/\D/g, '').slice(0, 11);

    return numericValue
        .replace(/^(\d{3})(\d)/, '$1.$2')
        .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
        .replace(/\.(\d{3})(\d)/, '.$1-$2');
}

function formatRg(value: string): string {
    const numericValue = value.replace(/\D/g, '').slice(0, 9);

    return numericValue
        .replace(/^(\d{2})(\d)/, '$1.$2')
        .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
        .replace(/\.(\d{3})(\d)/, '.$1-$2');
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

function handleCpfInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    studentForm.value.cpf = formatCpf(inputElement.value);
}

function handleRgInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    studentForm.value.rg = formatRg(inputElement.value);
}

function handlePhoneInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    studentForm.value.phone = formatPhone(inputElement.value);
}

function handleCepInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;

    studentForm.value.zipCode = formatCep(inputElement.value);

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

        studentForm.value.street = addressData.logradouro ?? '';
        studentForm.value.neighborhood = addressData.bairro ?? '';
        studentForm.value.city = addressData.localidade ?? '';
    } catch {
        return;
    }
}

function goToNextStep(): void {
    if (step.value < studentSteps.value.length - 1) {
        step.value += 1;

        return;
    }

    studentStore.setCharacter(character.value);

    toast.success(`Bem-vindo(a), ${studentForm.value.name || 'Jogador'}!`, {
        description: 'PERSONAGEM CRIADO! +100 moedas bônus!',
    });

    router.push('/app/aluno');
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
                                CRIAR CONTA DE ALUNO
                            </h1>
                        </div>
                    </div>

                    <div class="font-pixel text-[9px] text-primary">ETAPA {{ step + 1 }}</div>
                </div>

                <div class="mb-6">
                    <div class="flex items-center justify-between font-pixel text-[9px] mb-2">
                        <span>
                            FASE {{ step + 1 }} DE
                            {{ studentSteps.length }}
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
                        v-for="(item, index) in studentSteps"
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
                            <p class="font-pixel text-[10px] mb-4">ESCOLHA SEU PERSONAGEM</p>

                            <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
                                <button
                                    v-for="item in studentCharacters"
                                    :key="item.id"
                                    class="border-2 border-border p-3 text-center transition-all"
                                    :class="
                                        character === item.id
                                            ? 'bg-primary text-primary-foreground shadow-[4px_4px_0px_hsl(var(--border))] -translate-y-0.5'
                                            : 'bg-card hover:-translate-y-0.5'
                                    "
                                    type="button"
                                    @click="character = item.id"
                                >
                                    <div
                                        class="bg-hud border-2 border-border p-3 flex justify-center mb-3"
                                    >
                                        <MarioAvatar :character="item.id" :size="56" />
                                    </div>

                                    <p class="font-pixel text-[10px]">
                                        {{ item.name.toUpperCase() }}
                                    </p>
                                </button>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 1">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    NOME COMPLETO
                                </label>

                                <PixelInput
                                    v-model="studentForm.name"
                                    placeholder="Digite seu nome"
                                />
                            </div>

                            <div class="grid grid-cols-2 gap-3">
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"> CPF </label>

                                    <PixelInput
                                        :model-value="studentForm.cpf"
                                        maxlength="14"
                                        placeholder="000.000.000-00"
                                        @input="handleCpfInput"
                                    />
                                </div>

                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"> RG </label>

                                    <PixelInput
                                        :model-value="studentForm.rg"
                                        maxlength="12"
                                        placeholder="00.000.000-0"
                                        @input="handleRgInput"
                                    />
                                </div>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> TELEFONE </label>

                                <PixelInput
                                    :model-value="studentForm.phone"
                                    maxlength="15"
                                    placeholder="(00) 00000-0000"
                                    @input="handlePhoneInput"
                                />
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 2">
                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CEP </label>

                                <PixelInput
                                    :model-value="studentForm.zipCode"
                                    maxlength="9"
                                    placeholder="00000-000"
                                    @input="handleCepInput"
                                />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CIDADE </label>

                                <PixelInput v-model="studentForm.city" placeholder="Cidade" />
                            </div>

                            <div class="col-span-2">
                                <label class="font-pixel text-[9px] block mb-2"> RUA </label>

                                <PixelInput v-model="studentForm.street" placeholder="Rua" />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> NÚMERO </label>

                                <PixelInput v-model="studentForm.number" placeholder="123" />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> BAIRRO </label>

                                <PixelInput
                                    v-model="studentForm.neighborhood"
                                    placeholder="Bairro"
                                />
                            </div>

                            <div class="col-span-2">
                                <label class="font-pixel text-[9px] block mb-2">
                                    COMPLEMENTO
                                </label>

                                <PixelInput
                                    v-model="studentForm.complement"
                                    placeholder="Apto, bloco..."
                                />
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 3">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    INSTITUIÇÃO
                                </label>

                                <select
                                    v-model="studentForm.institution"
                                    class="w-full border-2 border-border bg-card px-3 py-2 font-pixel text-[10px]"
                                >
                                    <option
                                        v-for="institution in instituicoes"
                                        :key="institution"
                                        :value="institution"
                                    >
                                        {{ institution }}
                                    </option>
                                </select>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CURSO </label>

                                <select
                                    v-model="studentForm.course"
                                    class="w-full border-2 border-border bg-card px-3 py-2 font-pixel text-[10px]"
                                >
                                    <option v-for="course in cursos" :key="course" :value="course">
                                        {{ course }}
                                    </option>
                                </select>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 4">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> E-MAIL </label>

                                <PixelInput
                                    v-model="studentForm.email"
                                    placeholder="voce@email.com"
                                    type="email"
                                />
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> SENHA </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="studentForm.password"
                                        :type="showPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="••••••••"
                                    />

                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2"
                                        type="button"
                                        @click="showPassword = !showPassword"
                                    >
                                        <PhEyeSlash v-if="showPassword" :size="18" weight="bold" />

                                        <PhEye v-else :size="18" weight="bold" />
                                    </button>
                                </div>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    CONFIRMAR SENHA
                                </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="studentForm.confirmPassword"
                                        :type="showConfirmPassword ? 'text' : 'password'"
                                        class="pr-10"
                                        placeholder="••••••••"
                                    />

                                    <button
                                        class="absolute right-3 top-1/2 -translate-y-1/2"
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
                        {{ step === studentSteps.length - 1 ? 'FINALIZAR' : 'PRÓXIMA' }}

                        <PhArrowRight :size="16" weight="bold" />
                    </PixelButton>
                </div>
            </PixelCard>
        </div>
    </main>
</template>

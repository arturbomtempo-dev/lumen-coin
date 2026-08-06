<script setup lang="ts">
import {
    getPublicCourses,
    getPublicInstitutions,
    registerStudentRequest,
    type CourseOption,
    type InstitutionOption,
} from '@/modules/auth/services/auth.service';
import {
    registerStudentSchema,
    type RegisterStudentFormData,
} from '@/modules/schemas/register-student.schema';
import CharacterAvatar from '@/shared/components/CharacterAvatar.vue';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
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
import { cpf } from 'docsbr';
import { vMaska } from 'maska/vue';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

interface StudentStep {
    label: string;
    icon: object;
}

const router = useRouter();

const step = ref(0);
const attemptedSteps = ref<Record<number, boolean>>({});
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const character = ref<MarioCharacter>('mario');
const institutions = ref<InstitutionOption[]>([]);
const courses = ref<CourseOption[]>([]);

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

const {
    fields: studentData,
    errors: studentErrors,
    isSubmitting,
    validate,
    clearErrors,
} = useForm(registerStudentSchema);

studentData.value = {
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    avatar: 'MARIO',
    cpf: '',
    rg: '',
    phone: '',
    zipCode: '',
    street: '',
    number: '',
    complement: '',
    neighborhood: '',
    city: '',
    institutionId: '',
    courseId: '',
};

const progress = computed(() => ((step.value + 1) / studentSteps.value.length) * 100);

const passwordsMatch = computed<boolean | null>(() => {
    if (!studentData.value.password || !studentData.value.confirmPassword) {
        return null;
    }

    return studentData.value.password === studentData.value.confirmPassword;
});

const studentCharacters = computed(() => {
    return MARIO_CHARACTERS.filter((item) => item.id !== 'institution' && item.id !== 'company');
});

const availableCourses = computed(() =>
    courses.value.filter((course) => course.institutionId === studentData.value.institutionId)
);

const avatarByCharacter: Record<
    Exclude<MarioCharacter, 'company' | 'institution'>,
    RegisterStudentFormData['avatar']
> = {
    mario: 'MARIO',
    luigi: 'LUIGI',
    peach: 'PEACH',
    toad: 'TOAD',
    yoshi: 'YOSHI',
    bowser: 'BOWSER',
};

const stepFields: Record<number, Array<keyof RegisterStudentFormData>> = {
    0: ['avatar'],
    1: ['name', 'cpf', 'rg', 'phone'],
    2: ['zipCode', 'street', 'number', 'complement', 'neighborhood', 'city'],
    3: ['institutionId', 'courseId'],
    4: ['email', 'password', 'confirmPassword'],
};

const rgMaskOptions = {
    mask: 'AA-##.###.###',
    tokens: {
        A: {
            pattern: /[A-Za-z]/,
            transform: (char: string) => char.toUpperCase(),
        },
    },
};

const phoneMaskOptions = {
    mask: ['(##) ####-####', '(##) #####-####'],
};

function digitsOnly(value: string) {
    return value.replace(/\D/g, '');
}

function alphaNumericOnly(value: string) {
    return value.replace(/[^A-Za-z0-9]/g, '');
}

function buildAddress() {
    const parts = [
        (studentData.value.street ?? '').trim(),
        (studentData.value.number ?? '').trim(),
        (studentData.value.complement ?? '').trim(),
        (studentData.value.neighborhood ?? '').trim(),
        (studentData.value.city ?? '').trim(),
    ].filter((value) => value.length > 0);

    if (parts.length === 0) return undefined;

    return parts.join(', ').slice(0, 300);
}

function validateCurrentStep() {
    attemptedSteps.value[step.value] = true;

    const parseResult = registerStudentSchema.safeParse(studentData.value);

    studentErrors.value = {};

    if (parseResult.success) {
        return true;
    }

    const fieldsForStep = new Set(stepFields[step.value]);
    let hasError = false;

    parseResult.error.issues.forEach((issue) => {
        const field = issue.path[0] as keyof RegisterStudentFormData;
        if (!fieldsForStep.has(field)) return;
        studentErrors.value[field] = issue.message;
        hasError = true;
    });

    return !hasError;
}

function hasAttemptedCurrentStep() {
    return Boolean(attemptedSteps.value[step.value]);
}

async function fetchAddressByCep(cep: string): Promise<void> {
    try {
        const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        const addressData = await response.json();

        if (addressData.erro) {
            return;
        }

        studentData.value.street = addressData.logradouro ?? '';
        studentData.value.neighborhood = addressData.bairro ?? '';
        studentData.value.city = addressData.localidade ?? '';
    } catch {
        return;
    }
}

function handleZipCodeChange(value: string) {
    studentData.value.zipCode = value;
    const numericCep = digitsOnly(value);

    if (numericCep.length === 8) {
        void fetchAddressByCep(numericCep);
    }
}

function goToPreviousStep() {
    if (step.value <= 0) {
        return;
    }

    step.value -= 1;
}

function goBack() {
    router.back();
}

async function submitStudent() {
    if (!validate()) {
        return;
    }

    isSubmitting.value = true;

    try {
        await registerStudentRequest({
            name: studentData.value.name.trim(),
            email: studentData.value.email.trim(),
            password: studentData.value.password,
            avatar: studentData.value.avatar,
            cpf: cpf.unformat(studentData.value.cpf),
            rg: alphaNumericOnly(studentData.value.rg).toUpperCase(),
            zipCode: digitsOnly(studentData.value.zipCode) || undefined,
            address: buildAddress(),
            institutionId: studentData.value.institutionId,
            courseId: studentData.value.courseId,
        });

        toast.success('Aluno cadastrado com sucesso!', {
            description: 'Faça login para acessar o portal.',
        });

        clearErrors();
        await router.push({ name: 'login' });
    } catch {
    } finally {
        isSubmitting.value = false;
    }
}

async function goToNextStep() {
    if (!validateCurrentStep()) {
        return;
    }

    if (step.value >= studentSteps.value.length - 1) {
        await submitStudent();
        return;
    }

    step.value += 1;
}

async function loadAcademicOptions() {
    const [institutionsResponse, coursesResponse] = await Promise.all([
        getPublicInstitutions(),
        getPublicCourses(),
    ]);

    courses.value = coursesResponse.data;

    const institutionIdsWithCourses = new Set(
        coursesResponse.data.map((course) => course.institutionId)
    );

    institutions.value = institutionsResponse.data.filter((institution) =>
        institutionIdsWithCourses.has(institution.id)
    );

    if (
        !institutions.value.some(
            (institution) => institution.id === studentData.value.institutionId
        )
    ) {
        studentData.value.institutionId = '';
        studentData.value.courseId = '';
    }
}

watch(character, (value) => {
    if (value === 'company' || value === 'institution') {
        return;
    }
    studentData.value.avatar = avatarByCharacter[value];
});

watch(
    () => studentData.value.institutionId,
    () => {
        studentData.value.courseId = '';

        if (hasAttemptedCurrentStep()) {
            validateCurrentStep();
        }
    }
);

watch(
    studentData,
    () => {
        if (!hasAttemptedCurrentStep()) {
            return;
        }

        validateCurrentStep();
    },
    { deep: true }
);

onMounted(async () => {
    await loadAcademicOptions();
});
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
                            <CharacterAvatar :character="character" :size="56" />
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
                                        <CharacterAvatar :character="item.id" :size="56" />
                                    </div>

                                    <p class="font-pixel text-[10px]">
                                        {{ item.name.toUpperCase() }}
                                    </p>
                                </button>
                            </div>
                            <p
                                v-if="studentErrors.avatar"
                                class="font-sans text-xs mt-2"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ studentErrors.avatar }}
                            </p>
                        </div>
                    </template>

                    <template v-if="step === 1">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    NOME COMPLETO
                                </label>

                                <PixelInput
                                    v-model="studentData.name"
                                    placeholder="Digite seu nome"
                                />
                                <p
                                    v-if="studentErrors.name"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.name }}
                                </p>
                            </div>

                            <div class="grid grid-cols-2 gap-3">
                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"> CPF </label>

                                    <PixelInput
                                        v-model="studentData.cpf"
                                        v-maska="'###.###.###-##'"
                                        maxlength="14"
                                        placeholder="000.000.000-00"
                                    />
                                    <p
                                        v-if="studentErrors.cpf"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ studentErrors.cpf }}
                                    </p>
                                </div>

                                <div>
                                    <label class="font-pixel text-[9px] block mb-2"> RG </label>

                                    <PixelInput
                                        v-model="studentData.rg"
                                        v-maska="rgMaskOptions"
                                        maxlength="13"
                                        placeholder="MG-89.183.736"
                                    />
                                    <p
                                        v-if="studentErrors.rg"
                                        class="font-sans text-xs mt-1"
                                        style="color: hsl(var(--destructive))"
                                    >
                                        {{ studentErrors.rg }}
                                    </p>
                                </div>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> TELEFONE </label>

                                <PixelInput
                                    v-model="studentData.phone"
                                    v-maska="phoneMaskOptions"
                                    maxlength="15"
                                    placeholder="(00) 00000-0000"
                                />
                                <p
                                    v-if="studentErrors.phone"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.phone }}
                                </p>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 2">
                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CEP </label>

                                <PixelInput
                                    v-model="studentData.zipCode"
                                    v-maska="'#####-###'"
                                    maxlength="9"
                                    placeholder="00000-000"
                                    @update:model-value="handleZipCodeChange"
                                />
                                <p
                                    v-if="studentErrors.zipCode"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.zipCode }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CIDADE </label>

                                <PixelInput v-model="studentData.city" placeholder="Cidade" />
                                <p
                                    v-if="studentErrors.city"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.city }}
                                </p>
                            </div>

                            <div class="col-span-2">
                                <label class="font-pixel text-[9px] block mb-2"> RUA </label>

                                <PixelInput v-model="studentData.street" placeholder="Rua" />
                                <p
                                    v-if="studentErrors.street"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.street }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> NÚMERO </label>

                                <PixelInput v-model="studentData.number" placeholder="123" />
                                <p
                                    v-if="studentErrors.number"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.number }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> BAIRRO </label>

                                <PixelInput
                                    v-model="studentData.neighborhood"
                                    placeholder="Bairro"
                                />
                                <p
                                    v-if="studentErrors.neighborhood"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.neighborhood }}
                                </p>
                            </div>

                            <div class="col-span-2">
                                <label class="font-pixel text-[9px] block mb-2">
                                    COMPLEMENTO
                                </label>

                                <PixelInput
                                    v-model="studentData.complement"
                                    placeholder="Apto, bloco..."
                                />
                                <p
                                    v-if="studentErrors.complement"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.complement }}
                                </p>
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
                                    v-model="studentData.institutionId"
                                    class="w-full border-2 border-border bg-card px-3 py-2 font-pixel text-[10px]"
                                >
                                    <option disabled value="">Selecione uma instituição</option>
                                    <option
                                        v-for="institution in institutions"
                                        :key="institution.id"
                                        :value="institution.id"
                                    >
                                        {{ institution.name }}
                                    </option>
                                </select>
                                <p
                                    v-if="studentErrors.institutionId"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.institutionId }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> CURSO </label>

                                <select
                                    v-model="studentData.courseId"
                                    :disabled="!studentData.institutionId"
                                    class="w-full border-2 border-border bg-card px-3 py-2 font-pixel text-[10px] disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    <option disabled value="">Selecione um curso</option>
                                    <option
                                        v-for="course in availableCourses"
                                        :key="course.id"
                                        :value="course.id"
                                    >
                                        {{ course.name }}
                                    </option>
                                </select>
                                <p
                                    v-if="studentErrors.courseId"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.courseId }}
                                </p>
                            </div>
                        </div>
                    </template>

                    <template v-if="step === 4">
                        <div class="space-y-4">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> E-MAIL </label>

                                <PixelInput
                                    v-model="studentData.email"
                                    placeholder="voce@email.com"
                                    type="email"
                                />
                                <p
                                    v-if="studentErrors.email"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.email }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2"> SENHA </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="studentData.password"
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
                                <PasswordStrengthHint :password="studentData.password" />
                                <p
                                    v-if="studentErrors.password"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.password }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-2">
                                    CONFIRMAR SENHA
                                </label>

                                <div class="relative">
                                    <PixelInput
                                        v-model="studentData.confirmPassword"
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
                                    v-if="studentErrors.confirmPassword"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ studentErrors.confirmPassword }}
                                </p>

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
                    <PixelButton
                        :disabled="step === 0 || isSubmitting"
                        variant="ghost"
                        @click="goToPreviousStep"
                    >
                        <PhArrowLeft :size="16" weight="bold" />

                        VOLTAR
                    </PixelButton>

                    <PixelButton :disabled="isSubmitting" @click="goToNextStep">
                        {{ step === studentSteps.length - 1 ? 'FINALIZAR' : 'PRÓXIMA' }}

                        <PhArrowRight :size="16" weight="bold" />
                    </PixelButton>
                </div>
            </PixelCard>
        </div>
    </main>
</template>

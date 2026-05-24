<script setup lang="ts">
import { getPublicCourses, getPublicInstitutions } from '@/modules/auth/services/auth.service';
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    changeStudentPasswordSchema,
    type ChangeStudentPasswordFormData,
} from '@/modules/schemas/change-student-password.schema';
import {
    updateStudentSchema,
    type UpdateStudentFormData,
} from '@/modules/schemas/update-student.schema';
import {
    changeStudentPassword,
    deleteStudent,
    getStudent,
    updateStudent,
    type StudentProfile,
    type UpdateStudentDto,
} from '@/modules/student/services/student.service';
import { useStudentStore } from '@/modules/student/stores/student.store';
import { useGamification } from '@/shared/composables/useGamification';
import CharacterAvatar from '@/shared/components/CharacterAvatar.vue';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
import { storeToRefs } from 'pinia';
import {
    PhEye,
    PhEyeSlash,
    PhFloppyDisk,
    PhKey,
    PhPalette,
    PhPencilSimple,
    PhTrash,
    PhTrophy,
    PhUser,
    PhX,
} from '@phosphor-icons/vue';
import { vMaska } from 'maska/vue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const authStore = useAuthStore();
const studentStore = useStudentStore();
const router = useRouter();

const studentProfile = ref<StudentProfile | null>(null);
const institutions = ref<Array<{ id: string; name: string }>>([]);
const courses = ref<Array<{ id: string; name: string; institutionId: string }>>([]);
const isEditingProfile = ref(false);
const isUpdatingAvatar = ref(false);
const isDeletingAccount = ref(false);
const showDeleteConfirmation = ref(false);
const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmNewPassword = ref(false);

const {
    fields: profileData,
    errors: profileErrors,
    isSubmitting: profileIsSubmitting,
    validate: validateProfile,
    clearErrors: clearProfileErrors,
} = useForm(updateStudentSchema);

profileData.value = {
    name: '',
    email: '',
    cpf: '',
    rg: '',
    zipCode: '',
    address: '',
    institutionId: '',
    courseId: '',
};

const {
    fields: passwordData,
    errors: passwordErrors,
    isSubmitting: passwordIsSubmitting,
    validate: validatePassword,
    clearErrors: clearPasswordErrors,
} = useForm(changeStudentPasswordSchema);

passwordData.value = {
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
};

type StudentAvatar = NonNullable<UpdateStudentDto['avatar']>;

const avatarByCharacter: Record<
    Exclude<MarioCharacter, 'institution' | 'company'>,
    StudentAvatar
> = {
    mario: 'MARIO',
    luigi: 'LUIGI',
    peach: 'PEACH',
    toad: 'TOAD',
    yoshi: 'YOSHI',
    bowser: 'BOWSER',
};

const characterByAvatar: Record<StudentAvatar | 'COMPANY' | 'INSTITUTION', MarioCharacter> = {
    MARIO: 'mario',
    LUIGI: 'luigi',
    PEACH: 'peach',
    TOAD: 'toad',
    YOSHI: 'yoshi',
    BOWSER: 'bowser',
    COMPANY: 'company',
    INSTITUTION: 'institution',
};

const studentCharacters = computed(() =>
    MARIO_CHARACTERS.filter((item) => item.id !== 'institution' && item.id !== 'company')
);

const selectedCharacter = computed<MarioCharacter>(() => {
    if (!studentProfile.value?.avatar) return 'mario';
    return (
        characterByAvatar[
            studentProfile.value.avatar as StudentAvatar | 'COMPANY' | 'INSTITUTION'
        ] ?? 'mario'
    );
});

const institutionName = computed(() => {
    const found = institutions.value.find(
        (institution) => institution.id === studentProfile.value?.institutionId
    );
    return found?.name ?? '-';
});

const courseName = computed(() => {
    const found = courses.value.find((course) => course.id === studentProfile.value?.courseId);
    return found?.name ?? '-';
});

function digitsOnly(value: string) {
    return value.replace(/\D/g, '');
}

function alphaNumericOnly(value: string) {
    return value.replace(/[^A-Za-z0-9]/g, '');
}

const rgMaskOptions = {
    mask: 'AA-##.###.###',
    tokens: {
        A: {
            pattern: /[A-Za-z]/,
            transform: (char: string) => char.toUpperCase(),
        },
    },
};

function formatCpf(cpf: string): string {
    const digits = cpf.replace(/\D/g, '');
    return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatRg(rg: string): string {
    const clean = rg.replace(/[^A-Za-z0-9]/g, '').toUpperCase();
    if (!/^[A-Z]{2}\d{8}$/.test(clean)) return rg;
    return `${clean.slice(0, 2)}-${clean.slice(2, 4)}.${clean.slice(4, 7)}.${clean.slice(7)}`;
}

function formatZipCode(zipCode: string): string {
    const digits = zipCode.replace(/\D/g, '');
    return digits.replace(/(\d{5})(\d{3})/, '$1-$2');
}

function syncStudentStore(profile: StudentProfile) {
    studentStore.setName(profile.name);
    studentStore.setBalance(profile.balance);

    if (selectedCharacter.value !== 'company' && selectedCharacter.value !== 'institution') {
        studentStore.setCharacter(selectedCharacter.value);
    }
}

function setProfileForm(profile: StudentProfile) {
    profileData.value = {
        name: profile.name,
        email: profile.email,
        cpf: formatCpf(profile.cpf),
        rg: formatRg(profile.rg),
        zipCode: formatZipCode(profile.zipCode ?? ''),
        address: profile.address ?? '',
        institutionId: profile.institutionId,
        courseId: profile.courseId,
    };
}

async function loadProfile() {
    if (!authStore.user?.id) return;

    try {
        const response = await getStudent(authStore.user.id);
        studentProfile.value = response.data;
        setProfileForm(response.data);
        syncStudentStore(response.data);
    } catch {}
}

async function loadFormOptions() {
    try {
        const [institutionsResponse, coursesResponse] = await Promise.all([
            getPublicInstitutions(),
            getPublicCourses(),
        ]);

        institutions.value = institutionsResponse.data;
        courses.value = coursesResponse.data.map((course) => ({
            id: course.id,
            name: course.name,
            institutionId: course.institutionId,
        }));
    } catch {}
}

function startProfileEdit() {
    if (!studentProfile.value) return;
    setProfileForm(studentProfile.value);
    clearProfileErrors();
    isEditingProfile.value = true;
}

function cancelProfileEdit() {
    if (!studentProfile.value) return;
    setProfileForm(studentProfile.value);
    clearProfileErrors();
    isEditingProfile.value = false;
}

async function submitProfileUpdate() {
    if (!authStore.user?.id) return;
    if (!validateProfile()) return;

    profileIsSubmitting.value = true;

    try {
        const payload: UpdateStudentFormData = {
            name: profileData.value.name.trim(),
            email: profileData.value.email.trim(),
            cpf: digitsOnly(profileData.value.cpf),
            rg: alphaNumericOnly(profileData.value.rg).toUpperCase(),
            zipCode: digitsOnly(profileData.value.zipCode),
            address: profileData.value.address.trim(),
            institutionId: profileData.value.institutionId,
            courseId: profileData.value.courseId,
        };

        const response = await updateStudent(authStore.user.id, payload);

        studentProfile.value = response.data;
        setProfileForm(response.data);
        syncStudentStore(response.data);

        authStore.setUser({
            id: response.data.id,
            name: response.data.name,
            email: response.data.email,
            role: 'student',
        });

        clearProfileErrors();
        isEditingProfile.value = false;
        toast.success('Perfil atualizado com sucesso!');
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
        await changeStudentPassword(
            authStore.user.id,
            passwordData.value as ChangeStudentPasswordFormData
        );
        passwordData.value = { currentPassword: '', newPassword: '', confirmNewPassword: '' };
        clearPasswordErrors();
        toast.success('Senha alterada com sucesso!');
    } catch {
    } finally {
        passwordIsSubmitting.value = false;
    }
}

async function selectAvatar(character: Exclude<MarioCharacter, 'institution' | 'company'>) {
    if (!authStore.user?.id) return;

    isUpdatingAvatar.value = true;

    try {
        const response = await updateStudent(authStore.user.id, {
            avatar: avatarByCharacter[character],
        });

        studentProfile.value = response.data;
        syncStudentStore(response.data);
    } catch {
    } finally {
        isUpdatingAvatar.value = false;
    }
}

function openDeleteConfirmation() {
    showDeleteConfirmation.value = true;
}

function cancelDeleteAccount() {
    showDeleteConfirmation.value = false;
}

async function confirmDeleteAccount() {
    if (!authStore.user?.id) return;

    isDeletingAccount.value = true;

    try {
        await deleteStudent(authStore.user.id);
        await authStore.logout();
        toast.success('Conta excluída com sucesso!');
        await router.push({ name: 'home' });
    } catch {
    } finally {
        isDeletingAccount.value = false;
    }
}

const { transactions } = storeToRefs(studentStore);
const balance = computed(() => studentProfile.value?.balance ?? 0);
const { currentLevel, nextLevel, progressPercent, coinsToNextLevel, medalsWithStatus } =
    useGamification(balance, transactions);

onMounted(async () => {
    await Promise.all([loadProfile(), loadFormOptions()]);
});
</script>

<template>
    <div class="space-y-5 max-w-5xl mx-auto">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ PERFIL</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhUser weight="fill" class="pixel-icon" /> MEUS DADOS
            </h1>
        </div>

        <PixelCard class="p-6 space-y-5">
            <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                <div class="flex items-center gap-4">
                    <div class="border-2 border-border bg-hud p-2">
                        <CharacterAvatar :character="selectedCharacter" :size="64" />
                    </div>

                    <div>
                        <div class="font-pixel text-xl">
                            {{
                                (
                                    studentProfile?.name ??
                                    authStore.user?.name ??
                                    'ALUNO'
                                ).toUpperCase()
                            }}
                        </div>
                        <div class="font-sans md text-muted-foreground mt-1">Perfil de aluno</div>
                    </div>
                </div>

                <div class="flex gap-2">
                    <PixelButton
                        v-if="!isEditingProfile"
                        variant="secondary"
                        size="sm"
                        @click="startProfileEdit"
                    >
                        <PhPencilSimple weight="bold" /> EDITAR
                    </PixelButton>

                    <PixelButton v-else variant="ghost" size="sm" @click="cancelProfileEdit">
                        <PhX weight="bold" /> CANCELAR
                    </PixelButton>
                </div>
            </div>

            <div class="space-y-3">
                <div class="font-pixel md flex items-center gap-2">
                    <PhPalette weight="fill" class="pixel-icon text-accent" />
                    AVATAR
                </div>

                <div class="grid grid-cols-3 sm:grid-cols-6 gap-2">
                    <button
                        v-for="c in studentCharacters"
                        :key="c.id"
                        class="border-2 border-border p-1 transition-all text-center"
                        :class="
                            selectedCharacter === c.id
                                ? 'bg-primary text-primary-foreground -translate-y-0.5 shadow-[3px_3px_0_0_hsl(var(--border))]'
                                : 'bg-card hover:-translate-y-0.5'
                        "
                        :disabled="isUpdatingAvatar"
                        @click="
                            selectAvatar(c.id as Exclude<MarioCharacter, 'institution' | 'company'>)
                        "
                    >
                        <div class="flex justify-center bg-hud py-2 border-2 border-border p-1">
                            <CharacterAvatar :character="c.id" :size="32" />
                        </div>

                        <div class="font-pixel text-[8px] mt-1 leading-none">
                            {{ c.name.toUpperCase() }}
                        </div>
                    </button>
                </div>
            </div>

            <div class="grid md:grid-cols-2 gap-3">
                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">MOEDAS</div>
                    <div class="font-pixel md mt-1 flex items-center gap-1">
                        <CoinIcon :size="12" />
                        {{ (studentProfile?.balance ?? 0).toLocaleString('pt-BR') }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">EMAIL</div>
                    <div class="font-sans md mt-1 break-all">
                        {{ studentProfile?.email ?? authStore.user?.email ?? '-' }}
                    </div>
                </div>
            </div>

            <div v-if="!isEditingProfile" class="grid md:grid-cols-2 gap-3">
                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">CPF</div>
                    <div class="font-sans md">
                        {{ studentProfile?.cpf ? formatCpf(studentProfile.cpf) : '-' }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">RG</div>
                    <div class="font-sans md">
                        {{ studentProfile?.rg ? formatRg(studentProfile.rg) : '-' }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">CEP</div>
                    <div class="font-sans md">
                        {{ studentProfile?.zipCode ? formatZipCode(studentProfile.zipCode) : '-' }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">CURSO</div>
                    <div class="font-sans md">{{ courseName }}</div>
                </div>

                <div class="border-2 border-border bg-card p-3 md:col-span-2">
                    <div class="font-pixel text-[9px] text-muted-foreground">INSTITUIÇÃO</div>
                    <div class="font-sans md">{{ institutionName }}</div>
                </div>

                <div class="border-2 border-border bg-card p-3 md:col-span-2">
                    <div class="font-pixel text-[9px] text-muted-foreground">ENDEREÇO</div>
                    <div class="font-sans md">{{ studentProfile?.address ?? '-' }}</div>
                </div>
            </div>

            <form v-else class="space-y-4" @submit.prevent="submitProfileUpdate">
                <div class="grid md:grid-cols-2 gap-3">
                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="student-name">
                            NOME
                        </label>
                        <PixelInput
                            id="student-name"
                            name="name"
                            v-model="profileData.name"
                            placeholder="Nome completo"
                        />
                        <p
                            v-if="profileErrors.name"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.name }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="student-email">
                            E-MAIL
                        </label>
                        <PixelInput
                            id="student-email"
                            name="email"
                            v-model="profileData.email"
                            type="email"
                            placeholder="aluno@email.com"
                        />
                        <p
                            v-if="profileErrors.email"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.email }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="student-cpf">
                            CPF
                        </label>
                        <PixelInput
                            id="student-cpf"
                            name="cpf"
                            v-model="profileData.cpf"
                            v-maska="'###.###.###-##'"
                            maxlength="14"
                            placeholder="000.000.000-00"
                        />
                        <p
                            v-if="profileErrors.cpf"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.cpf }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="student-rg">
                            RG
                        </label>
                        <PixelInput
                            id="student-rg"
                            name="rg"
                            v-model="profileData.rg"
                            v-maska="rgMaskOptions"
                            maxlength="13"
                            placeholder="AA-00.000.000"
                        />
                        <p
                            v-if="profileErrors.rg"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.rg }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="student-zipcode">
                            CEP
                        </label>
                        <PixelInput
                            id="student-zipcode"
                            name="zipCode"
                            v-model="profileData.zipCode"
                            v-maska="'#####-###'"
                            maxlength="9"
                            placeholder="00000-000"
                        />
                        <p
                            v-if="profileErrors.zipCode"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.zipCode }}
                        </p>
                    </div>

                    <div class="md:col-span-2">
                        <label class="font-pixel text-[9px] block mb-1" for="student-address">
                            ENDEREÇO
                        </label>
                        <PixelInput
                            id="student-address"
                            name="address"
                            v-model="profileData.address"
                            placeholder="Rua, número, bairro, cidade"
                        />
                        <p
                            v-if="profileErrors.address"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.address }}
                        </p>
                    </div>

                    <div>
                        <label
                            class="font-pixel text-[9px] block mb-1"
                            for="student-institution"
                        >
                            INSTITUIÇÃO
                        </label>
                        <PixelInput
                            id="student-institution"
                            name="institution"
                            :model-value="institutionName"
                            disabled
                            class="opacity-90 cursor-not-allowed"
                        />
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="student-course">
                            CURSO
                        </label>
                        <PixelInput
                            id="student-course"
                            name="course"
                            :model-value="courseName"
                            disabled
                            class="opacity-90 cursor-not-allowed"
                        />
                    </div>
                </div>

                <div class="flex gap-2">
                    <PixelButton
                        type="submit"
                        variant="success"
                        class="flex items-center gap-2"
                        :disabled="profileIsSubmitting"
                    >
                        <PhFloppyDisk weight="bold" /> SALVAR
                    </PixelButton>

                    <PixelButton type="button" variant="ghost" @click="cancelProfileEdit">
                        CANCELAR
                    </PixelButton>
                </div>
            </form>
        </PixelCard>

        <div class="grid lg:grid-cols-2 gap-5">
            <!-- Level Progress Card -->
            <PixelCard class="p-6">
                <div class="font-pixel text-[10px] text-primary">▶ PROGRESSO</div>
                <div class="font-pixel text-sm mt-1 mb-5 flex items-center gap-2">
                    <PhTrophy weight="fill" class="pixel-icon" /> NÍVEL
                </div>

                <div class="border-2 border-border bg-hud text-hud-foreground p-4 space-y-4">
                    <div class="flex items-start justify-between gap-3">
                        <div>
                            <div class="font-pixel text-[9px] text-muted-foreground mb-1">
                                NÍVEL ATUAL
                            </div>
                            <div class="font-pixel text-3xl flex items-center gap-2">
                                <PhTrophy weight="fill" class="text-primary" />
                                LV {{ currentLevel.level }}
                            </div>
                        </div>
                        <PixelBadge tone="gold">{{ currentLevel.name.toUpperCase() }}</PixelBadge>
                    </div>

                    <div>
                        <div class="flex justify-between font-pixel text-[9px] mb-2">
                            <span>PROGRESSO</span>
                            <span>{{ progressPercent }}%</span>
                        </div>
                        <div class="relative h-5 border-2 border-border bg-card overflow-hidden">
                            <div
                                class="h-full transition-all duration-700 ease-out"
                                :style="{
                                    width: `${progressPercent}%`,
                                    backgroundImage:
                                        'repeating-linear-gradient(90deg, hsl(var(--primary)) 0, hsl(var(--primary)) 6px, hsl(var(--primary-shadow)) 6px, hsl(var(--primary-shadow)) 8px)',
                                }"
                            />
                        </div>
                    </div>

                    <div
                        v-if="nextLevel"
                        class="font-pixel text-[9px] text-center text-muted-foreground"
                    >
                        Faltam
                        <span class="text-foreground">{{
                            coinsToNextLevel.toLocaleString('pt-BR')
                        }}</span>
                        moedas para alcançar
                        <span class="text-primary">{{ nextLevel.name.toUpperCase() }}</span>
                    </div>
                    <div v-else class="font-pixel text-[9px] text-center text-primary">
                        ★ NÍVEL MÁXIMO ALCANÇADO! ★
                    </div>
                </div>

                <div
                    v-if="nextLevel"
                    class="mt-4 border-2 border-border bg-muted opacity-60 p-3 flex items-center gap-3"
                >
                    <div
                        class="border-2 border-border bg-card px-3 py-2 font-pixel text-[12px] shrink-0"
                    >
                        ?
                    </div>
                    <div>
                        <div class="font-pixel text-[9px] text-muted-foreground">
                            PRÓXIMA CONQUISTA
                        </div>
                        <div class="font-pixel text-xs mt-1">
                            LV {{ nextLevel.level }} · {{ nextLevel.name.toUpperCase() }}
                        </div>
                        <div class="font-pixel text-[9px] text-muted-foreground mt-1">
                            {{ nextLevel.minCoins.toLocaleString('pt-BR') }} moedas necessárias
                        </div>
                    </div>
                </div>
            </PixelCard>

            <!-- Medals Card -->
            <PixelCard class="p-6">
                <div class="font-pixel text-[10px] text-primary">▶ CONQUISTAS</div>
                <div class="font-pixel text-sm mt-1 mb-5">MEDALHAS</div>

                <div class="grid grid-cols-2 gap-3">
                    <div
                        v-for="medal in medalsWithStatus"
                        :key="medal.id"
                        class="border-2 border-border p-3 transition-all"
                        :class="medal.unlocked ? 'bg-card' : 'bg-muted opacity-50'"
                    >
                        <PixelBadge :tone="medal.tone">
                            {{ medal.unlocked ? '✓' : '?' }}
                            {{ medal.name.toUpperCase() }}
                        </PixelBadge>
                        <p class="font-sans text-xs text-foreground/75 mt-2">
                            {{ medal.description }}
                        </p>
                    </div>
                </div>
            </PixelCard>
        </div>

        <PixelCard class="p-6">
            <div class="font-pixel text-[10px] text-primary">&#9658; ALTERAR SENHA</div>

            <form class="mt-4 space-y-3" @submit.prevent="submitPasswordChange">
                <div>
                    <label
                        class="font-pixel text-[9px] block mb-1"
                        for="student-current-password"
                    >
                        SENHA ATUAL
                    </label>
                    <div class="relative">
                        <PixelInput
                            id="student-current-password"
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
                            <PhEyeSlash v-if="showCurrentPassword" :size="18" weight="bold" />
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
                    <label
                        class="font-pixel text-[9px] block mb-1"
                        for="student-new-password"
                    >
                        NOVA SENHA
                    </label>
                    <div class="relative">
                        <PixelInput
                            id="student-new-password"
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
                            <PhEyeSlash v-if="showNewPassword" :size="18" weight="bold" />
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
                    <label
                        class="font-pixel text-[9px] block mb-1"
                        for="student-confirm-new-password"
                    >
                        CONFIRMAR NOVA SENHA
                    </label>
                    <div class="relative">
                        <PixelInput
                            id="student-confirm-new-password"
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
                            <PhEyeSlash v-if="showConfirmNewPassword" :size="18" weight="bold" />
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

        <PixelCard class="p-6">
            <div class="font-pixel md mb-3 text-destructive">EXCLUIR CONTA</div>

            <p class="font-sans md text-muted-foreground mb-4">
                Esta ação é permanente. Ao excluir sua conta, todas as moedas e conquistas serão
                perdidas.
            </p>

            <div v-if="!showDeleteConfirmation">
                <PixelButton variant="danger" @click="openDeleteConfirmation">
                    <PhTrash weight="bold" /> EXCLUIR CONTA
                </PixelButton>
            </div>

            <div v-else class="space-y-3">
                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-destructive">CONFIRMAR EXCLUSÃO</div>
                    <p class="font-sans text-xs text-muted-foreground mt-2">
                        Tem certeza de que deseja excluir sua conta agora?
                    </p>
                </div>

                <div class="flex gap-2 flex-wrap">
                    <PixelButton variant="ghost" @click="cancelDeleteAccount">
                        <PhX weight="bold" /> CANCELAR
                    </PixelButton>

                    <PixelButton
                        variant="danger"
                        :disabled="isDeletingAccount"
                        @click="confirmDeleteAccount"
                    >
                        <PhTrash weight="bold" /> CONFIRMAR EXCLUSÃO
                    </PixelButton>
                </div>
            </div>
        </PixelCard>
    </div>
</template>

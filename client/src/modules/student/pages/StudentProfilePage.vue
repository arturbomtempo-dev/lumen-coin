<script setup lang="ts">
import { getPublicCourses, getPublicInstitutions } from '@/modules/auth/services/auth.service';
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    updateStudentSchema,
    type UpdateStudentFormData,
} from '@/modules/schemas/update-student.schema';
import {
    deleteStudent,
    getStudent,
    updateStudent,
    type StudentProfile,
    type UpdateStudentDto,
} from '@/modules/student/services/student.service';
import { useStudentStore } from '@/modules/student/stores/student.store';
import CharacterAvatar from '@/shared/components/CharacterAvatar.vue';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
import { PhFloppyDisk, PhPalette, PhPencilSimple, PhTrash, PhUser, PhX } from '@phosphor-icons/vue';
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

const {
    fields: profileData,
    isSubmitting: profileIsSubmitting,
    validate: validateProfile,
    clearErrors: clearProfileErrors,
} = useForm(updateStudentSchema);

profileData.value = {
    name: '',
    email: '',
    password: '',
    cpf: '',
    rg: '',
    zipCode: '',
    address: '',
    institutionId: '',
    courseId: '',
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
        password: '',
        cpf: profile.cpf,
        rg: profile.rg,
        zipCode: profile.zipCode ?? '',
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
            password: profileData.value.password,
            cpf: digitsOnly(profileData.value.cpf),
            rg: alphaNumericOnly(profileData.value.rg).toUpperCase(),
            zipCode: digitsOnly(profileData.value.zipCode),
            address: profileData.value.address.trim(),
            institutionId: profileData.value.institutionId,
            courseId: profileData.value.courseId,
        };

        const response = await updateStudent(authStore.user.id, {
            ...payload,
            password: payload.password.length > 0 ? payload.password : undefined,
        });

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
                <div class="flex items-center justify-between">
                    <div class="font-pixel md flex items-center gap-2">
                        <PhPalette weight="fill" class="pixel-icon text-accent" />
                        AVATAR
                    </div>
                    <PixelBadge tone="teal">
                        {{ studentCharacters.length }}
                    </PixelBadge>
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
                    <div class="font-sans md">{{ studentProfile?.cpf ?? '-' }}</div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">RG</div>
                    <div class="font-sans md">{{ studentProfile?.rg ?? '-' }}</div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">CEP</div>
                    <div class="font-sans md">{{ studentProfile?.zipCode ?? '-' }}</div>
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
                    <PixelInput v-model="profileData.name" placeholder="NOME" />
                    <PixelInput v-model="profileData.email" type="email" placeholder="EMAIL" />
                    <PixelInput
                        v-model="profileData.password"
                        type="password"
                        placeholder="SENHA"
                    />
                    <PixelInput v-model="profileData.cpf" placeholder="CPF" />
                    <PixelInput v-model="profileData.rg" placeholder="RG" />
                    <PixelInput v-model="profileData.zipCode" placeholder="CEP" />
                </div>

                <div class="flex gap-2">
                    <PixelButton type="submit" variant="success">
                        <PhFloppyDisk weight="bold" /> SALVAR
                    </PixelButton>

                    <PixelButton type="button" variant="ghost" @click="cancelProfileEdit">
                        CANCELAR
                    </PixelButton>
                </div>
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

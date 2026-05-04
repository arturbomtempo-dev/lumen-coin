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
import CoinIcon from '@/shared/components/CoinIcon.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
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
    errors: profileErrors,
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

const avatarByCharacter: Record<Exclude<MarioCharacter, 'institution' | 'company'>, StudentAvatar> = {
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
    return characterByAvatar[studentProfile.value.avatar as StudentAvatar | 'COMPANY' | 'INSTITUTION'] ?? 'mario';
});

const availableCourses = computed(() =>
    courses.value.filter((course) => course.institutionId === profileData.value.institutionId)
);

const institutionName = computed(() => {
    const found = institutions.value.find((institution) => institution.id === studentProfile.value?.institutionId);
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
    } catch {
    }
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
    } catch {
    }
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
    <div class="space-y-8">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ PERFIL</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhUser weight="fill" class="pixel-icon" /> MEUS DADOS
            </h1>
        </div>

        <PixelCard class="p-6">
            <div class="grid lg:grid-cols-[auto_1fr] gap-6">
                <div class="border-2 border-border bg-hud p-3 flex items-center justify-center">
                    <MarioAvatar :character="selectedCharacter" :size="112" />
                </div>
                <div class="space-y-5">
                    <div class="flex items-start justify-between gap-3 flex-wrap">
                        <div>
                            <div class="font-pixel text-2xl">
                                {{ (studentProfile?.name ?? authStore.user?.name ?? 'ALUNO').toUpperCase() }}
                            </div>
                            <div class="font-sans text-sm text-muted-foreground mt-1">
                                Perfil de aluno
                            </div>
                        </div>
                        <div class="flex gap-2">
                            <PixelButton
                                v-if="!isEditingProfile"
                                variant="secondary"
                                @click="startProfileEdit"
                            >
                                <PhPencilSimple weight="bold" /> EDITAR
                            </PixelButton>
                            <PixelButton
                                v-else
                                variant="ghost"
                                @click="cancelProfileEdit"
                            >
                                <PhX weight="bold" /> CANCELAR
                            </PixelButton>
                        </div>
                    </div>

                    <div class="grid md:grid-cols-2 gap-3">
                        <div class="border-2 border-border bg-card p-3">
                            <div class="font-pixel text-[8px] text-muted-foreground">MOEDAS</div>
                            <div class="font-pixel text-sm mt-1 flex items-center gap-1">
                                <CoinIcon :size="12" />
                                {{ (studentProfile?.balance ?? 0).toLocaleString('pt-BR') }}
                            </div>
                        </div>
                        <div class="border-2 border-border bg-card p-3">
                            <div class="font-pixel text-[8px] text-muted-foreground">EMAIL</div>
                            <div class="font-sans text-sm mt-1 break-all">
                                {{ studentProfile?.email ?? authStore.user?.email ?? '-' }}
                            </div>
                        </div>
                    </div>

                    <div v-if="!isEditingProfile" class="grid md:grid-cols-2 gap-3">
                        <div class="border-2 border-border bg-card p-3 space-y-1">
                            <div class="font-pixel text-[8px] text-muted-foreground">CPF</div>
                            <div class="font-sans text-sm">{{ studentProfile?.cpf ?? '-' }}</div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 space-y-1">
                            <div class="font-pixel text-[8px] text-muted-foreground">RG</div>
                            <div class="font-sans text-sm">{{ studentProfile?.rg ?? '-' }}</div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 space-y-1">
                            <div class="font-pixel text-[8px] text-muted-foreground">CEP</div>
                            <div class="font-sans text-sm">{{ studentProfile?.zipCode ?? '-' }}</div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 space-y-1">
                            <div class="font-pixel text-[8px] text-muted-foreground">CURSO</div>
                            <div class="font-sans text-sm">{{ courseName }}</div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 space-y-1 md:col-span-2">
                            <div class="font-pixel text-[8px] text-muted-foreground">INSTITUIÇÃO</div>
                            <div class="font-sans text-sm">{{ institutionName }}</div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 space-y-1 md:col-span-2">
                            <div class="font-pixel text-[8px] text-muted-foreground">ENDEREÇO</div>
                            <div class="font-sans text-sm">{{ studentProfile?.address ?? '-' }}</div>
                        </div>
                    </div>

                    <form v-else class="space-y-4" @submit.prevent="submitProfileUpdate">
                        <div class="grid md:grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">NOME</label>
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
                                <label class="font-pixel text-[9px] block mb-2">SENHA</label>
                                <PixelInput
                                    v-model="profileData.password"
                                    type="password"
                                    placeholder="Preencha apenas se quiser alterar"
                                />
                                <p
                                    v-if="profileErrors.password"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.password }}
                                </p>
                            </div>
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">CPF</label>
                                <PixelInput v-model="profileData.cpf" />
                                <p
                                    v-if="profileErrors.cpf"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.cpf }}
                                </p>
                            </div>
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">RG</label>
                                <PixelInput v-model="profileData.rg" />
                                <p
                                    v-if="profileErrors.rg"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.rg }}
                                </p>
                            </div>
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">CEP</label>
                                <PixelInput v-model="profileData.zipCode" />
                                <p
                                    v-if="profileErrors.zipCode"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.zipCode }}
                                </p>
                            </div>
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">INSTITUIÇÃO</label>
                                <select
                                    v-model="profileData.institutionId"
                                    class="w-full bg-input border-2 border-border px-3 py-2 font-display text-base focus:outline-none"
                                >
                                    <option value="">Selecione uma instituição</option>
                                    <option
                                        v-for="institution in institutions"
                                        :key="institution.id"
                                        :value="institution.id"
                                    >
                                        {{ institution.name }}
                                    </option>
                                </select>
                                <p
                                    v-if="profileErrors.institutionId"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.institutionId }}
                                </p>
                            </div>
                            <div>
                                <label class="font-pixel text-[9px] block mb-2">CURSO</label>
                                <select
                                    v-model="profileData.courseId"
                                    class="w-full bg-input border-2 border-border px-3 py-2 font-display text-base focus:outline-none"
                                >
                                    <option value="">Selecione um curso</option>
                                    <option
                                        v-for="course in availableCourses"
                                        :key="course.id"
                                        :value="course.id"
                                    >
                                        {{ course.name }}
                                    </option>
                                </select>
                                <p
                                    v-if="profileErrors.courseId"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.courseId }}
                                </p>
                            </div>
                            <div class="md:col-span-2">
                                <label class="font-pixel text-[9px] block mb-2">ENDEREÇO</label>
                                <PixelInput v-model="profileData.address" />
                                <p
                                    v-if="profileErrors.address"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ profileErrors.address }}
                                </p>
                            </div>
                        </div>

                        <div class="flex gap-2 flex-wrap">
                            <PixelButton
                                type="submit"
                                variant="success"
                                :disabled="profileIsSubmitting"
                            >
                                <PhFloppyDisk weight="bold" /> SALVAR ALTERAÇÕES
                            </PixelButton>
                            <PixelButton type="button" variant="ghost" @click="cancelProfileEdit">
                                <PhX weight="bold" /> CANCELAR
                            </PixelButton>
                        </div>
                    </form>
                </div>
            </div>
        </PixelCard>

        <PixelCard class="p-6">
            <div class="flex items-center justify-between mb-4">
                <div class="font-pixel text-sm flex items-center gap-2">
                    <PhPalette weight="fill" class="pixel-icon text-accent" /> TROCAR AVATAR
                </div>
                <PixelBadge tone="teal">{{ studentCharacters.length }} AVATARES</PixelBadge>
            </div>
            <div class="grid grid-cols-3 sm:grid-cols-6 gap-3">
                <button
                    v-for="c in studentCharacters"
                    :key="c.id"
                    class="border-2 border-border p-2 transition-all"
                    :class="
                        selectedCharacter === c.id
                            ? 'bg-primary text-primary-foreground -translate-y-0.5 shadow-[4px_4px_0_0_hsl(var(--border))]'
                            : 'bg-card hover:-translate-y-0.5'
                    "
                    :disabled="isUpdatingAvatar"
                    @click="selectAvatar(c.id as Exclude<MarioCharacter, 'institution' | 'company'>)"
                >
                    <div class="flex justify-center bg-hud border-2 border-border p-1">
                        <MarioAvatar :character="c.id" :size="40" />
                    </div>
                    <div class="font-pixel text-[8px] mt-2">{{ c.name.toUpperCase() }}</div>
                </button>
            </div>
        </PixelCard>

        <PixelCard class="p-6">
            <div class="font-pixel text-sm mb-3 text-destructive">EXCLUIR CONTA</div>
            <p class="font-sans text-sm text-muted-foreground mb-4">
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

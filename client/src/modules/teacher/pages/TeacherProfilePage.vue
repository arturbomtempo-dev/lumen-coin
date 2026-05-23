<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { getInstitution } from '@/modules/institution/services/institution.service';
import {
    updateTeacherProfileSchema,
    type UpdateTeacherProfileFormData,
} from '@/modules/schemas/update-teacher-profile.schema';
import {
    deleteTeacher,
    getTeacher,
    updateTeacher,
    type TeacherAvatar,
    type TeacherProfile,
} from '@/modules/teacher/services/teacher.service';
import { useTeacherStore } from '@/modules/teacher/stores/teacher.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
import { PhFloppyDisk, PhPencilSimple, PhTrash, PhUser, PhX } from '@phosphor-icons/vue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const authStore = useAuthStore();
const teacherStore = useTeacherStore();
const router = useRouter();

const teacherProfile = ref<TeacherProfile | null>(null);
const institutionName = ref('-');
const isEditingProfile = ref(false);
const isDeletingAccount = ref(false);
const showDeleteConfirmation = ref(false);

const {
    fields: profileData,
    errors: profileErrors,
    isSubmitting: profileIsSubmitting,
    validate: validateProfile,
    clearErrors: clearProfileErrors,
} = useForm(updateTeacherProfileSchema);

profileData.value = {
    name: '',
    email: '',
    password: '',
    avatar: 'MARIO',
    cpf: '',
    department: '',
};

const characterByAvatar = {
    MARIO: 'mario',
    LUIGI: 'luigi',
    PEACH: 'peach',
    TOAD: 'toad',
    YOSHI: 'yoshi',
    BOWSER: 'bowser',
} as const;

const teacherCharacters = computed(() =>
    MARIO_CHARACTERS.filter((item) => item.id !== 'institution' && item.id !== 'company')
);

const selectedCharacter = computed(() => {
    const avatar = profileData.value.avatar || teacherProfile.value?.avatar || 'MARIO';
    return characterByAvatar[avatar] ?? 'mario';
});

type TeacherCharacter = Exclude<MarioCharacter, 'institution' | 'company'>;

function selectAvatar(character: TeacherCharacter) {
    profileData.value.avatar = character.toUpperCase() as TeacherAvatar;
}

function digitsOnly(value: string) {
    return value.replace(/\D/g, '');
}

function setProfileForm(profile: TeacherProfile) {
    profileData.value = {
        name: profile.name,
        email: profile.email,
        password: '',
        avatar: (profile.avatar || 'MARIO') as TeacherAvatar,
        cpf: profile.cpf,
        department: profile.department ?? '',
    };
}

function syncTeacherStore(profile: TeacherProfile) {
    teacherStore.setName(profile.name);
    teacherStore.setBalance(profile.balance);
}

async function loadProfile() {
    if (!authStore.user?.id) return;

    try {
        const response = await getTeacher(authStore.user.id);
        teacherProfile.value = response.data;
        setProfileForm(response.data);
        syncTeacherStore(response.data);

        if (response.data.institutionId) {
            const institutionResponse = await getInstitution(response.data.institutionId);
            institutionName.value = institutionResponse.data.name;
        }
    } catch {}
}

function startProfileEdit() {
    if (!teacherProfile.value) return;
    setProfileForm(teacherProfile.value);
    clearProfileErrors();
    isEditingProfile.value = true;
}

function cancelProfileEdit() {
    if (!teacherProfile.value) return;
    setProfileForm(teacherProfile.value);
    clearProfileErrors();
    isEditingProfile.value = false;
}

async function submitProfileUpdate() {
    if (!authStore.user?.id) return;
    if (!validateProfile()) return;

    profileIsSubmitting.value = true;

    try {
        const payload: UpdateTeacherProfileFormData = {
            name: profileData.value.name.trim(),
            email: profileData.value.email.trim(),
            password: profileData.value.password,
            avatar: profileData.value.avatar,
            cpf: digitsOnly(profileData.value.cpf),
            department: profileData.value.department.trim(),
        };

        const response = await updateTeacher(authStore.user.id, {
            ...payload,
            password: payload.password.length > 0 ? payload.password : undefined,
        });

        teacherProfile.value = response.data;
        setProfileForm(response.data);
        syncTeacherStore(response.data);

        authStore.setUser({
            id: response.data.id,
            name: response.data.name,
            email: response.data.email,
            role: 'teacher',
        });

        clearProfileErrors();
        isEditingProfile.value = false;
        toast.success('Perfil atualizado com sucesso!');
    } catch {
    } finally {
        profileIsSubmitting.value = false;
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
        await deleteTeacher(authStore.user.id);
        await authStore.logout();
        toast.success('Conta excluída com sucesso!');
        await router.push({ name: 'home' });
    } catch {
    } finally {
        isDeletingAccount.value = false;
    }
}

onMounted(loadProfile);
</script>

<template>
    <div class="space-y-5 max-w-5xl mx-auto">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ PERFIL DO PROFESSOR</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhUser weight="fill" class="pixel-icon" /> MEUS DADOS
            </h1>
        </div>

        <PixelCard class="p-6 space-y-5">
            <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                <div class="flex items-center gap-4">
                    <div class="border-2 border-border bg-hud p-2">
                        <MarioAvatar :character="selectedCharacter" :size="64" />
                    </div>

                    <div>
                        <div class="font-pixel text-xl">
                            {{
                                (
                                    teacherProfile?.name ??
                                    authStore.user?.name ??
                                    'PROFESSOR'
                                ).toUpperCase()
                            }}
                        </div>
                        <div class="font-sans text-sm text-muted-foreground mt-1">
                            {{ teacherProfile?.department ?? 'Departamento não informado' }}
                        </div>
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

            <div class="grid md:grid-cols-3 gap-3">
                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">MOEDAS</div>
                    <div class="font-pixel text-md mt-1 flex items-center gap-1">
                        <CoinIcon :size="12" />
                        {{ (teacherProfile?.balance ?? 0).toLocaleString('pt-BR') }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">EMAIL</div>
                    <div class="font-sans text-sm mt-1 break-all">
                        {{ teacherProfile?.email ?? authStore.user?.email ?? '-' }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">CPF</div>
                    <div class="font-sans text-sm mt-1">{{ teacherProfile?.cpf ?? '-' }}</div>
                </div>
            </div>

            <div v-if="!isEditingProfile" class="grid gap-3 lg:grid-cols-2">
                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">DEPARTAMENTO</div>
                    <div class="font-sans text-sm mt-1">
                        {{ teacherProfile?.department ?? '-' }}
                    </div>
                </div>

                <div class="border-2 border-border bg-card p-3">
                    <div class="font-pixel text-[9px] text-muted-foreground">INSTITUIÇÃO</div>
                    <div class="font-sans text-xs mt-1">
                        {{ institutionName }}
                    </div>
                </div>
            </div>

            <form v-else class="space-y-4" @submit.prevent="submitProfileUpdate">
                <div class="grid md:grid-cols-2 gap-3">
                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="teacher-name">
                            NOME
                        </label>
                        <PixelInput
                            id="teacher-name"
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
                        <label class="font-pixel text-[9px] block mb-1" for="teacher-email">
                            E-MAIL
                        </label>
                        <PixelInput
                            id="teacher-email"
                            name="email"
                            v-model="profileData.email"
                            type="email"
                            placeholder="contato@escola.com"
                        />
                        <p
                            v-if="profileErrors.email"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.email }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="teacher-password">
                            SENHA NOVA
                        </label>
                        <PixelInput
                            id="teacher-password"
                            name="password"
                            v-model="profileData.password"
                            type="password"
                            placeholder="Deixe em branco para manter"
                        />
                        <p
                            v-if="profileErrors.password"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.password }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1" for="teacher-cpf">
                            CPF
                        </label>
                        <PixelInput
                            id="teacher-cpf"
                            name="cpf"
                            v-model="profileData.cpf"
                            maxlength="11"
                            placeholder="Somente 11 dígitos"
                        />
                        <p v-if="profileErrors.cpf" class="font-sans text-xs mt-1 text-destructive">
                            {{ profileErrors.cpf }}
                        </p>
                    </div>

                    <div class="md:col-span-2">
                        <div class="flex items-center justify-between mb-2">
                            <label class="font-pixel text-[9px] block" for="teacher-avatar">
                                AVATAR
                            </label>
                            <span class="font-pixel text-[9px] text-muted-foreground">
                                ESCOLHA UM PERSONAGEM
                            </span>
                        </div>

                        <div class="grid grid-cols-3 sm:grid-cols-6 gap-2">
                            <button
                                v-for="c in teacherCharacters"
                                :key="c.id"
                                type="button"
                                class="border-2 border-border p-1 transition-all text-center"
                                :class="
                                    selectedCharacter === c.id
                                        ? 'bg-primary text-primary-foreground -translate-y-0.5 shadow-[3px_3px_0_0_hsl(var(--border))]'
                                        : 'bg-card hover:-translate-y-0.5'
                                "
                                @click="selectAvatar(c.id as TeacherCharacter)"
                            >
                                <div
                                    class="flex justify-center bg-hud py-2 border-2 border-border p-1"
                                >
                                    <MarioAvatar :character="c.id" :size="36" />
                                </div>

                                <div class="font-pixel text-[8px] mt-1 leading-none">
                                    {{ c.name.toUpperCase() }}
                                </div>
                            </button>
                        </div>

                        <input
                            id="teacher-avatar"
                            name="avatar"
                            type="hidden"
                            v-model="profileData.avatar"
                        />
                        <p
                            v-if="profileErrors.avatar"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.avatar }}
                        </p>
                    </div>

                    <div class="md:col-span-2 lg:col-span-1">
                        <label class="font-pixel text-[9px] block mb-1" for="teacher-department">
                            DEPARTAMENTO
                        </label>
                        <PixelInput
                            id="teacher-department"
                            name="department"
                            v-model="profileData.department"
                            placeholder="Ex: Ciências da Computação"
                        />
                        <p
                            v-if="profileErrors.department"
                            class="font-sans text-xs mt-1 text-destructive"
                        >
                            {{ profileErrors.department }}
                        </p>
                    </div>

                    <div class="md:col-span-2 lg:col-span-1">
                        <label class="font-pixel text-[9px] block mb-1" for="teacher-institution">
                            INSTITUIÇÃO
                        </label>
                        <PixelInput
                            id="teacher-institution"
                            name="institution"
                            :model-value="institutionName"
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

        <PixelCard class="p-6">
            <div class="font-pixel text-[10px] text-destructive">▶ EXCLUIR CONTA</div>

            <p class="font-sans text-sm text-muted-foreground mt-3 mb-4">
                Esta ação é permanente. Ao excluir a conta do professor, o acesso será removido e
                não será possível recuperar os dados.
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

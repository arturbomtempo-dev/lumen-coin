<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    changeInstitutionPassword,
    createCourse as createCourseApi,
    deleteCourse,
    deleteInstitution,
    deleteTeacher,
    getCourses,
    getInstitution,
    getStudents,
    getTeachers,
    registerTeacher,
    updateCourse,
    updateInstitution,
    updateTeacher,
} from '@/modules/institution/services/institution.service';
import type {
    CourseResponse,
    InstitutionProfile,
    StudentResponse,
    TeacherResponse,
} from '@/modules/institution/services/institution.types';
import {
    changeInstitutionPasswordSchema,
    type ChangeInstitutionPasswordFormData,
} from '@/modules/schemas/change-institution-password.schema';
import { registerCourseSchema } from '@/modules/schemas/register-course.schema';
import { registerTeacherSchema } from '@/modules/schemas/register-teacher.schema';
import { updateInstitutionSchema } from '@/modules/schemas/update-institution.schema';
import { updateTeacherSchema } from '@/modules/schemas/update-teacher.schema';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import {
    PhBookOpen,
    PhBuildings,
    PhEye,
    PhEyeSlash,
    PhGraduationCap,
    PhKey,
    PhPencilSimple,
    PhPlus,
    PhStudent,
    PhTrash,
    PhUser,
    PhX,
} from '@phosphor-icons/vue';
import { vMaska } from 'maska/vue';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const router = useRouter();

type Tab = 'courses' | 'teachers' | 'students' | 'profile';
const tab = ref<Tab>('courses');

const authStore = useAuthStore();

const institutionProfile = ref<InstitutionProfile | null>(null);
const institutionName = ref('INSTITUIÇÃO');
const isEditingProfile = ref(false);
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
} = useForm(updateInstitutionSchema);

profileData.value = {
    name: '',
    email: '',
    cnpj: '',
    zipCode: '',
    address: '',
};

const {
    fields: passwordData,
    errors: passwordErrors,
    isSubmitting: passwordIsSubmitting,
    validate: validatePassword,
    clearErrors: clearPasswordErrors,
} = useForm(changeInstitutionPasswordSchema);

passwordData.value = {
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
};

const courses = ref<CourseResponse[]>([]);

const tabs = [
    { id: 'courses' as Tab, label: 'CURSOS', icon: PhBookOpen },
    { id: 'teachers' as Tab, label: 'PROFESSORES', icon: PhGraduationCap },
    { id: 'students' as Tab, label: 'ALUNOS', icon: PhStudent },
    { id: 'profile' as Tab, label: 'PERFIL', icon: PhUser },
];

const SHIFT_LABELS: Record<string, string> = {
    DAYTIME: 'Integral',
    MORNING: 'Matutino',
    AFTERNOON: 'Vespertino',
    NIGHT: 'Noturno',
};

const SHIFT_OPTIONS = [
    { value: 'DAYTIME', label: 'Integral' },
    { value: 'MORNING', label: 'Matutino' },
    { value: 'AFTERNOON', label: 'Vespertino' },
    { value: 'NIGHT', label: 'Noturno' },
];

const editingCourseId = ref<string | null>(null);

async function loadCourses() {
    const response = await getCourses();
    courses.value = response.data;
}

const {
    fields: courseData,
    errors: courseErrors,
    isSubmitting: courseIsSubmitting,
    validate: validateCourse,
    clearErrors: clearCourseErrors,
} = useForm(registerCourseSchema);

function startEditCourse(course: CourseResponse) {
    editingCourseId.value = course.id;
    courseData.value = {
        name: course.name,
        shift: course.shift,
        periods: course.periods,
    };
    clearCourseErrors();
}

async function submitCourse(e: Event) {
    e.preventDefault();
    if (!validateCourse() || !authStore.user?.id) return;
    courseIsSubmitting.value = true;
    try {
        const payload = {
            name: courseData.value.name.trim(),
            shift: courseData.value.shift,
            periods: courseData.value.periods,
        };

        if (editingCourseId.value) {
            const response = await updateCourse(editingCourseId.value, payload);
            const idx = courses.value.findIndex((course) => course.id === editingCourseId.value);
            if (idx !== -1) courses.value[idx] = response.data;
            toast.success(`Curso "${response.data.name}" atualizado!`);
        } else {
            const response = await createCourseApi(payload);
            courses.value.unshift(response.data);
            toast.success(`Curso "${response.data.name}" criado!`);
        }

        // reset form after submit
        courseData.value = { name: '', shift: '', periods: 1 };
        editingCourseId.value = null;
    } catch {
    } finally {
        courseIsSubmitting.value = false;
    }
}

async function handleDeleteCourse(id: string) {
    try {
        await deleteCourse(id);
        courses.value = courses.value.filter((course) => course.id !== id);
        toast.success('Curso excluído!');
    } catch {}
}

const showDeleteCourseModal = ref(false);
const courseToDeleteId = ref<string | null>(null);

const showDeleteTeacherModal = ref(false);
const teacherToDeleteId = ref<string | null>(null);

const courseToDeleteName = computed(
    () => courses.value.find((c) => c.id === courseToDeleteId.value)?.name ?? ''
);
const teacherToDeleteName = computed(
    () => teachers.value.find((t) => t.id === teacherToDeleteId.value)?.name ?? ''
);

function confirmDeleteCourse(id: string) {
    courseToDeleteId.value = id;
    showDeleteCourseModal.value = true;
}

function cancelDeleteCourse() {
    courseToDeleteId.value = null;
    showDeleteCourseModal.value = false;
}

async function proceedDeleteCourse() {
    if (!courseToDeleteId.value) return;
    await handleDeleteCourse(courseToDeleteId.value);
    cancelDeleteCourse();
}

const teachers = ref<TeacherResponse[]>([]);

const {
    fields: teacherData,
    errors: teacherErrors,
    isSubmitting: teacherIsSubmitting,
    validate: validateTeacher,
    clearErrors: clearTeacherErrors,
} = useForm(registerTeacherSchema);

const editingTeacherId = ref<string | null>(null);

const {
    fields: editTeacherData,
    errors: editTeacherErrors,
    isSubmitting: editTeacherIsSubmitting,
    validate: validateEditTeacher,
    clearErrors: clearEditTeacherErrors,
} = useForm(updateTeacherSchema);

async function loadTeachers() {
    const response = await getTeachers();
    teachers.value = response.data;
}

async function submitTeacher(e: Event) {
    e.preventDefault();
    if (!validateTeacher()) return;
    teacherIsSubmitting.value = true;
    try {
        const response = await registerTeacher({
            ...teacherData.value,
            cpf: (teacherData.value.cpf ?? '').replace(/\D/g, ''),
        });
        teachers.value.unshift(response.data);
        toast.success(`Professor "${response.data.name}" cadastrado!`);
        teacherData.value = {} as typeof teacherData.value;
        clearTeacherErrors();
    } catch {
    } finally {
        teacherIsSubmitting.value = false;
    }
}

function startEditTeacher(teacher: TeacherResponse) {
    editingTeacherId.value = teacher.id;
    editTeacherData.value = {
        name: teacher.name,
        email: teacher.email,
        department: teacher.department,
    };
    clearEditTeacherErrors();
}

function cancelEditTeacher() {
    editingTeacherId.value = null;
    clearEditTeacherErrors();
}

async function submitEditTeacher(e: Event) {
    e.preventDefault();
    if (!validateEditTeacher()) return;
    editTeacherIsSubmitting.value = true;
    try {
        const id = editingTeacherId.value!;
        const response = await updateTeacher(id, editTeacherData.value);
        const idx = teachers.value.findIndex((teacher) => teacher.id === id);
        if (idx !== -1) teachers.value[idx] = response.data;
        editingTeacherId.value = null;
        clearEditTeacherErrors();
        toast.success('Professor atualizado!');
    } catch {
    } finally {
        editTeacherIsSubmitting.value = false;
    }
}

async function handleDeleteTeacher(id: string) {
    try {
        await deleteTeacher(id);
        teachers.value = teachers.value.filter((teacher) => teacher.id !== id);
        toast.success('Professor excluído!');
    } catch {}
}

function confirmDeleteTeacher(id: string) {
    teacherToDeleteId.value = id;
    showDeleteTeacherModal.value = true;
}

function cancelDeleteTeacher() {
    teacherToDeleteId.value = null;
    showDeleteTeacherModal.value = false;
}

async function proceedDeleteTeacher() {
    if (!teacherToDeleteId.value) return;
    await handleDeleteTeacher(teacherToDeleteId.value);
    cancelDeleteTeacher();
}

function formatCpf(cpf: string): string {
    const digits = cpf.replace(/\D/g, '');
    return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

const allStudents = ref<StudentResponse[]>([]);

async function loadStudents() {
    const response = await getStudents();
    allStudents.value = response.data;
}

function formatDate(value: string) {
    return new Intl.DateTimeFormat('pt-BR', {
        dateStyle: 'medium',
        timeStyle: 'short',
    }).format(new Date(value));
}

function formatCnpj(cnpj: string): string {
    const digits = cnpj.replace(/\D/g, '');
    return digits.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5');
}

function formatZipCode(zipCode: string): string {
    const digits = zipCode.replace(/\D/g, '');
    return digits.replace(/(\d{5})(\d{3})/, '$1-$2');
}

async function loadInstitutionProfile() {
    if (!authStore.user?.id) return;

    try {
        const response = await getInstitution(authStore.user.id);
        institutionProfile.value = response.data;
        institutionName.value = response.data.name;
        profileData.value = {
            name: response.data.name,
            email: response.data.email,
            cnpj: formatCnpj(response.data.cnpj),
            zipCode: formatZipCode(response.data.zipCode ?? ''),
            address: response.data.address ?? '',
        };
    } catch {}
}

function startEditInstitutionProfile() {
    if (!institutionProfile.value) return;
    profileData.value = {
        name: institutionProfile.value.name,
        email: institutionProfile.value.email,
        cnpj: formatCnpj(institutionProfile.value.cnpj),
        zipCode: formatZipCode(institutionProfile.value.zipCode ?? ''),
        address: institutionProfile.value.address ?? '',
    };
    clearProfileErrors();
    isEditingProfile.value = true;
}

function cancelEditInstitutionProfile() {
    if (!institutionProfile.value) return;
    profileData.value = {
        name: institutionProfile.value.name,
        email: institutionProfile.value.email,
        cnpj: formatCnpj(institutionProfile.value.cnpj),
        zipCode: formatZipCode(institutionProfile.value.zipCode ?? ''),
        address: institutionProfile.value.address ?? '',
    };
    clearProfileErrors();
    isEditingProfile.value = false;
}

async function handleUpdateInstitutionProfile(e: Event) {
    e.preventDefault();

    if (!validateProfile() || !authStore.user?.id) return;

    profileIsSubmitting.value = true;

    try {
        const response = await updateInstitution(authStore.user.id, {
            name: profileData.value.name.trim(),
            email: profileData.value.email.trim(),
            cnpj: profileData.value.cnpj.replace(/\D/g, ''),
            zipCode: profileData.value.zipCode.replace(/\D/g, ''),
            address: profileData.value.address.trim(),
        });

        institutionProfile.value = response.data;
        institutionName.value = response.data.name;
        profileData.value = {
            name: response.data.name,
            email: response.data.email,
            cnpj: formatCnpj(response.data.cnpj),
            zipCode: formatZipCode(response.data.zipCode ?? ''),
            address: response.data.address ?? '',
        };

        authStore.setUser({
            id: response.data.id,
            name: response.data.name,
            email: response.data.email,
            role: authStore.user?.role ?? 'institution',
        });

        clearProfileErrors();
        isEditingProfile.value = false;
        toast.success('Perfil da instituição atualizado com sucesso!');
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
        await changeInstitutionPassword(
            authStore.user.id,
            passwordData.value as ChangeInstitutionPasswordFormData
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

function openDeleteConfirmation() {
    showDeleteConfirmation.value = true;
}

function cancelDeleteConfirmation() {
    showDeleteConfirmation.value = false;
}

async function handleDeleteInstitution() {
    if (!authStore.user?.id) return;

    isDeletingAccount.value = true;

    try {
        await deleteInstitution(authStore.user.id);
        await authStore.logout();
        toast.success('Conta da instituição excluída.');
        await router.push({ name: 'home' });
    } catch {
    } finally {
        isDeletingAccount.value = false;
    }
}

async function confirmDeleteInstitution() {
    await handleDeleteInstitution();
    showDeleteConfirmation.value = false;
}

const courseFilter = ref('all');
const studentSearch = ref('');

const institutionCourses = computed(() => {
    if (!authStore.user?.id) return [];
    return courses.value.filter((course) => course.institutionId === authStore.user?.id);
});

const courseNameById = computed(() => {
    const entries = courses.value.map((course) => [course.id, course.name] as const);
    return Object.fromEntries(entries) as Record<string, string>;
});

const studentsForInstitution = computed(() => {
    if (!authStore.user?.id) return [];
    return allStudents.value.filter((student) => student.institutionId === authStore.user?.id);
});

const enrolledStudentsCount = computed(() => studentsForInstitution.value.length);

const filteredStudents = computed(() => {
    return studentsForInstitution.value.filter((student) => {
        const matchCourse = courseFilter.value === 'all' || student.courseId === courseFilter.value;
        const normalizedSearch = studentSearch.value.toLowerCase();
        const matchSearch =
            student.name.toLowerCase().includes(normalizedSearch) ||
            student.email.toLowerCase().includes(normalizedSearch);
        return matchCourse && matchSearch;
    });
});

onMounted(async () => {
    await Promise.all([loadCourses(), loadStudents(), loadTeachers(), loadInstitutionProfile()]);
});
</script>

<template>
    <div class="space-y-6">
            <section class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <PixelCard class="p-4 flex items-center gap-3">
                    <PhBookOpen weight="fill" class="pixel-icon text-primary" :size="28" />
                    <div>
                        <div class="font-pixel text-[9px] text-muted-foreground">CURSOS</div>
                        <div class="font-pixel text-2xl">{{ courses.length }}</div>
                    </div>
                </PixelCard>
                <PixelCard class="p-4 flex items-center gap-3">
                    <PhGraduationCap weight="fill" class="pixel-icon text-info" :size="28" />
                    <div>
                        <div class="font-pixel text-[9px] text-muted-foreground">PROFESSORES</div>
                        <div class="font-pixel text-2xl">{{ teachers.length }}</div>
                    </div>
                </PixelCard>
                <PixelCard class="p-4 flex items-center gap-3">
                    <PhStudent weight="fill" class="pixel-icon text-success" :size="28" />
                    <div>
                        <div class="font-pixel text-[9px] text-muted-foreground">
                            ALUNOS MATRICULADOS
                        </div>
                        <div class="font-pixel text-2xl">{{ enrolledStudentsCount }}</div>
                    </div>
                </PixelCard>
            </section>

            <section class="flex flex-wrap gap-2">
                <button
                    v-for="item in tabs"
                    :key="item.id"
                    class="border-2 border-border bg-card px-3 py-2 font-pixel text-[9px] flex items-center gap-2 shadow-[2px_2px_0_0_hsl(var(--border))]"
                    :class="tab === item.id ? 'bg-primary text-primary-foreground' : ''"
                    @click="tab = item.id"
                >
                    <component :is="item.icon" weight="fill" class="pixel-icon" :size="14" />
                    {{ item.label }}
                </button>
            </section>

            <div v-if="tab === 'courses'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
                <div>
                    <h2 class="font-pixel text-sm mb-4">Cadastrar / Editar Curso</h2>
                    <PixelCard class="p-5">
                        <form class="space-y-4" @submit.prevent="submitCourse">
                            <div>
                                <label class="font-pixel text-[9px] block mb-1"
                                    >NOME DO CURSO</label
                                >
                                <PixelInput
                                    v-model="courseData.name"
                                    placeholder="Ex: Engenharia Civil"
                                />
                                <p
                                    v-if="courseErrors.name"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ courseErrors.name }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-1">TURNO</label>
                                <select
                                    v-model="courseData.shift"
                                    class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-base focus:outline-none"
                                >
                                    <option value="" disabled>Selecione um turno</option>
                                    <option
                                        v-for="option in SHIFT_OPTIONS"
                                        :key="option.value"
                                        :value="option.value"
                                    >
                                        {{ option.label }}
                                    </option>
                                </select>
                                <p
                                    v-if="courseErrors.shift"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ courseErrors.shift }}
                                </p>
                            </div>

                            <div>
                                <label class="font-pixel text-[9px] block mb-1"
                                    >NÚMERO DE PERÍODOS</label
                                >
                                <PixelInput
                                    :model-value="courseData.periods"
                                    type="number"
                                    min="1"
                                    placeholder="Ex: 8"
                                    @update:model-value="(value) => (courseData.periods = +value)"
                                />
                                <p
                                    v-if="courseErrors.periods"
                                    class="font-sans text-xs mt-1"
                                    style="color: hsl(var(--destructive))"
                                >
                                    {{ courseErrors.periods }}
                                </p>
                            </div>

                            <div class="flex gap-2 pt-1">
                                <PixelButton
                                    type="submit"
                                    variant="success"
                                    class="flex-1"
                                    :disabled="courseIsSubmitting"
                                    >{{ editingCourseId ? 'SALVAR' : 'CRIAR CURSO' }}</PixelButton
                                >
                                <PixelButton
                                    type="button"
                                    variant="ghost"
                                    class="flex-1"
                                    @click.prevent="
                                        () => {
                                            editingCourseId = null;
                                            courseData.name = '';
                                            courseData.shift = '';
                                            courseData.periods = 1;
                                            clearCourseErrors();
                                        }
                                    "
                                    >CANCELAR</PixelButton
                                >
                            </div>
                        </form>
                    </PixelCard>
                </div>

                <div>
                    <h2 class="font-pixel text-sm mb-4">Cursos cadastrados</h2>
                    <PixelCard class="p-5 space-y-3">
                        <p
                            v-if="courses.length === 0"
                            class="font-display text-sm text-muted-foreground"
                        >
                            Nenhum curso cadastrado.
                        </p>

                        <div
                            v-for="course in courses"
                            :key="course.id"
                            class="border-2 border-border bg-card p-3 shadow-[3px_3px_0_0_hsl(var(--border))]"
                        >
                            <div class="flex items-start justify-between gap-3">
                                <div class="min-w-0">
                                    <div class="font-pixel text-xs">{{ course.name }}</div>
                                    <div class="font-display text-sm text-muted-foreground mt-1">
                                        {{ SHIFT_LABELS[course.shift] }} ·
                                        {{ course.periods }} período(s)
                                    </div>
                                </div>
                                <div class="flex gap-1.5 shrink-0">
                                    <PixelButton
                                        size="sm"
                                        variant="ghost"
                                        @click="startEditCourse(course)"
                                        ><PhPencilSimple weight="bold" :size="13"
                                    /></PixelButton>
                                    <PixelButton
                                        size="sm"
                                        variant="danger"
                                        @click="confirmDeleteCourse(course.id)"
                                        ><PhTrash weight="bold" :size="13"
                                    /></PixelButton>
                                </div>
                            </div>
                        </div>
                    </PixelCard>
                </div>
            </div>

            <div v-if="tab === 'teachers'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPlus weight="bold" /> CADASTRAR PROFESSOR
                    </h2>
                    <form class="space-y-3" @submit="submitTeacher">
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">NOME COMPLETO</label>
                            <PixelInput
                                v-model="teacherData.name"
                                placeholder="Prof. Fulano de Tal"
                            />
                            <p
                                v-if="teacherErrors.name"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ teacherErrors.name }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1"
                                >E-MAIL INSTITUCIONAL</label
                            >
                            <PixelInput
                                v-model="teacherData.email"
                                type="email"
                                placeholder="prof@unifei.edu.br"
                            />
                            <p
                                v-if="teacherErrors.email"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ teacherErrors.email }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">CPF</label>
                            <PixelInput
                                v-model="teacherData.cpf"
                                v-maska="'###.###.###-##'"
                                placeholder="000.000.000-00"
                                maxlength="14"
                            />
                            <p
                                v-if="teacherErrors.cpf"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ teacherErrors.cpf }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1"
                                >DEPARTAMENTO (OPCIONAL)</label
                            >
                            <PixelInput
                                v-model="teacherData.department"
                                placeholder="Ex: Engenharia de Software"
                            />
                            <p
                                v-if="teacherErrors.department"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ teacherErrors.department }}
                            </p>
                        </div>
                        <PixelButton
                            type="submit"
                            variant="success"
                            class="w-full"
                            :disabled="teacherIsSubmitting"
                        >
                            <PhPlus weight="bold" /> CADASTRAR
                        </PixelButton>
                    </form>
                </PixelCard>

                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4">PROFESSORES CADASTRADOS</h2>
                    <div class="space-y-3">
                        <p
                            v-if="teachers.length === 0"
                            class="font-display text-sm text-muted-foreground"
                        >
                            Nenhum professor cadastrado.
                        </p>
                        <div
                            v-for="teacher in teachers"
                            :key="teacher.id"
                            class="border-2 border-border bg-card p-3"
                        >
                            <template v-if="editingTeacherId !== teacher.id">
                                <div class="flex items-start justify-between gap-2">
                                    <div class="min-w-0">
                                        <div class="font-pixel text-xs">{{ teacher.name }}</div>
                                        <div class="font-sans text-xs text-muted-foreground mt-0.5">
                                            {{ teacher.email }} · {{ formatCpf(teacher.cpf) }}
                                        </div>
                                        <div class="flex flex-wrap gap-1.5 mt-2">
                                            <PixelBadge v-if="teacher.department" tone="teal">{{
                                                teacher.department
                                            }}</PixelBadge>
                                        </div>
                                    </div>
                                    <div class="flex gap-1.5 shrink-0">
                                        <PixelButton
                                            size="sm"
                                            variant="ghost"
                                            @click="startEditTeacher(teacher)"
                                            ><PhPencilSimple weight="bold" :size="13"
                                        /></PixelButton>
                                        <PixelButton
                                            size="sm"
                                            variant="danger"
                                            @click="confirmDeleteTeacher(teacher.id)"
                                            ><PhTrash weight="bold" :size="13"
                                        /></PixelButton>
                                    </div>
                                </div>
                            </template>
                            <template v-else>
                                <form class="space-y-2" @submit="submitEditTeacher">
                                    <div class="font-pixel text-[9px] text-primary mb-2">
                                        EDITANDO: {{ teacher.name }}
                                    </div>
                                    <div>
                                        <label class="font-pixel text-[9px] block mb-1">NOME</label>
                                        <PixelInput v-model="editTeacherData.name" />
                                        <p
                                            v-if="editTeacherErrors.name"
                                            class="font-sans text-xs mt-1"
                                            style="color: hsl(var(--destructive))"
                                        >
                                            {{ editTeacherErrors.name }}
                                        </p>
                                    </div>
                                    <div>
                                        <label class="font-pixel text-[9px] block mb-1"
                                            >E-MAIL</label
                                        >
                                        <PixelInput v-model="editTeacherData.email" type="email" />
                                        <p
                                            v-if="editTeacherErrors.email"
                                            class="font-sans text-xs mt-1"
                                            style="color: hsl(var(--destructive))"
                                        >
                                            {{ editTeacherErrors.email }}
                                        </p>
                                    </div>
                                    <div>
                                        <label class="font-pixel text-[9px] block mb-1"
                                            >DEPARTAMENTO</label
                                        >
                                        <PixelInput v-model="editTeacherData.department" />
                                        <p
                                            v-if="editTeacherErrors.department"
                                            class="font-sans text-xs mt-1"
                                            style="color: hsl(var(--destructive))"
                                        >
                                            {{ editTeacherErrors.department }}
                                        </p>
                                    </div>
                                    <div class="flex gap-2 pt-1">
                                        <PixelButton
                                            type="submit"
                                            variant="success"
                                            size="sm"
                                            :disabled="editTeacherIsSubmitting"
                                            >SALVAR</PixelButton
                                        >
                                        <PixelButton
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            @click="cancelEditTeacher"
                                            ><PhX weight="bold" :size="13" /> CANCELAR</PixelButton
                                        >
                                    </div>
                                </form>
                            </template>
                        </div>
                    </div>
                </PixelCard>
            </div>

            <div v-if="tab === 'profile'" class="grid lg:grid-cols-[1fr_0.9fr] gap-6 items-start">
                <PixelCard class="p-5">
                    <div class="flex items-center justify-between gap-3 mb-4">
                        <h2 class="font-pixel text-sm flex items-center gap-2">
                            <PhPencilSimple weight="bold" /> PERFIL DA INSTITUIÇÃO
                        </h2>
                        <div class="flex gap-2">
                            <PixelButton
                                v-if="!isEditingProfile"
                                variant="secondary"
                                size="sm"
                                @click="startEditInstitutionProfile"
                            >
                                EDITAR
                            </PixelButton>
                            <PixelButton
                                v-else
                                variant="ghost"
                                size="sm"
                                @click="cancelEditInstitutionProfile"
                            >
                                CANCELAR
                            </PixelButton>
                        </div>
                    </div>

                    <div v-if="!isEditingProfile" class="grid md:grid-cols-2 gap-3">
                        <div class="border-2 border-border bg-card p-3 md:col-span-2">
                            <div class="font-pixel text-[9px] text-muted-foreground">NOME</div>
                            <div class="font-sans text-sm mt-1">
                                {{ institutionProfile?.name ?? authStore.user?.name ?? '-' }}
                            </div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 md:col-span-2">
                            <div class="font-pixel text-[9px] text-muted-foreground">E-MAIL</div>
                            <div class="font-sans text-sm mt-1 break-all">
                                {{ institutionProfile?.email ?? authStore.user?.email ?? '-' }}
                            </div>
                        </div>
                        <div class="border-2 border-border bg-card p-3">
                            <div class="font-pixel text-[9px] text-muted-foreground">CNPJ</div>
                            <div class="font-sans text-sm mt-1">
                                {{
                                    institutionProfile?.cnpj
                                        ? formatCnpj(institutionProfile.cnpj)
                                        : '-'
                                }}
                            </div>
                        </div>
                        <div class="border-2 border-border bg-card p-3">
                            <div class="font-pixel text-[9px] text-muted-foreground">CEP</div>
                            <div class="font-sans text-sm mt-1">
                                {{
                                    institutionProfile?.zipCode
                                        ? formatZipCode(institutionProfile.zipCode)
                                        : '-'
                                }}
                            </div>
                        </div>
                        <div class="border-2 border-border bg-card p-3 md:col-span-2">
                            <div class="font-pixel text-[9px] text-muted-foreground">ENDEREÇO</div>
                            <div class="font-sans text-sm mt-1">
                                {{ institutionProfile?.address ?? '-' }}
                            </div>
                        </div>
                    </div>

                    <form v-else class="space-y-4" @submit.prevent="handleUpdateInstitutionProfile">
                        <div>
                            <label class="font-pixel text-[9px] block mb-1"
                                >NOME DA INSTITUIÇÃO</label
                            >
                            <PixelInput
                                v-model="profileData.name"
                                placeholder="Nome da instituição"
                            />
                            <p
                                v-if="profileErrors.name"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ profileErrors.name }}
                            </p>
                        </div>

                        <div>
                            <label class="font-pixel text-[9px] block mb-1">E-MAIL</label>
                            <PixelInput
                                v-model="profileData.email"
                                type="email"
                                placeholder="contato@instituicao.com"
                            />
                            <p
                                v-if="profileErrors.email"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ profileErrors.email }}
                            </p>
                        </div>

                        <div>
                            <label class="font-pixel text-[9px] block mb-1">CNPJ</label>
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

                        <div>
                            <label class="font-pixel text-[9px] block mb-1">CEP</label>
                            <PixelInput
                                v-model="profileData.zipCode"
                                v-maska="'#####-###'"
                                maxlength="9"
                                placeholder="00000-000"
                            />
                            <p
                                v-if="profileErrors.zipCode"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ profileErrors.zipCode }}
                            </p>
                        </div>

                        <div>
                            <label class="font-pixel text-[9px] block mb-1"
                                >ENDEREÇO COMPLETO</label
                            >
                            <PixelInput
                                v-model="profileData.address"
                                placeholder="Rua, número, bairro, cidade"
                            />
                            <p
                                v-if="profileErrors.address"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ profileErrors.address }}
                            </p>
                        </div>

                        <div class="flex gap-2 pt-1">
                            <PixelButton
                                type="submit"
                                variant="success"
                                class="flex-1"
                                :disabled="profileIsSubmitting"
                                ><PhPencilSimple weight="bold" /> SALVAR ALTERAÇÕES</PixelButton
                            >
                            <PixelButton
                                type="button"
                                variant="ghost"
                                class="flex-1"
                                @click.prevent="cancelEditInstitutionProfile"
                                >CANCELAR</PixelButton
                            >
                        </div>
                    </form>
                </PixelCard>

                <PixelCard class="p-5">
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

                <PixelCard class="p-5 space-y-4">
                    <h2 class="font-pixel text-sm flex items-center gap-2">
                        <PhBuildings weight="fill" class="pixel-icon" /> DADOS DA CONTA
                    </h2>

                    <div class="border-2 border-border bg-card p-4 space-y-3">
                        <div class="flex items-center gap-3">
                            <div
                                class="w-14 h-14 border-2 border-border bg-hud flex items-center justify-center shadow-[3px_3px_0_0_hsl(var(--border))]"
                            >
                                <PhBuildings weight="fill" :size="28" class="text-primary" />
                            </div>
                            <div class="min-w-0">
                                <div class="font-pixel text-xs truncate">
                                    {{
                                        institutionProfile?.name ??
                                        authStore.user?.name ??
                                        'INSTITUIÇÃO'
                                    }}
                                </div>
                                <div class="font-sans text-xs text-muted-foreground truncate mt-1">
                                    {{ institutionProfile?.email ?? authStore.user?.email ?? '-' }}
                                </div>
                            </div>
                        </div>

                        <div class="flex flex-wrap gap-2">
                            <PixelBadge tone="blue">
                                CNPJ:
                                {{
                                    institutionProfile?.cnpj
                                        ? formatCnpj(institutionProfile.cnpj)
                                        : '-'
                                }}
                            </PixelBadge>
                            <PixelBadge tone="green">
                                CEP:
                                {{
                                    institutionProfile?.zipCode
                                        ? formatZipCode(institutionProfile.zipCode)
                                        : '-'
                                }}
                            </PixelBadge>
                        </div>

                        <div class="space-y-2 text-sm font-sans">
                            <p>
                                <span class="font-pixel text-[9px]">ENDEREÇO:</span>
                                {{ institutionProfile?.address ?? '-' }}
                            </p>
                            <p>
                                <span class="font-pixel text-[9px]">CRIADO EM:</span>
                                {{
                                    institutionProfile
                                        ? formatDate(institutionProfile.createdAt)
                                        : '-'
                                }}
                            </p>
                            <p>
                                <span class="font-pixel text-[9px]">ATUALIZADO EM:</span>
                                {{
                                    institutionProfile
                                        ? formatDate(institutionProfile.updatedAt)
                                        : '-'
                                }}
                            </p>
                        </div>
                    </div>

                    <div class="border-2 border-border bg-card p-4 space-y-3">
                        <div class="font-pixel text-[10px] text-destructive">EXCLUIR CONTA</div>
                        <p class="font-sans text-sm text-muted-foreground">
                            Esta ação remove permanentemente a conta da instituição e todos os dados
                            vinculados ao acesso.
                        </p>
                        <PixelButton
                            type="button"
                            variant="danger"
                            class="w-full"
                            :disabled="isDeletingAccount"
                            @click="openDeleteConfirmation"
                        >
                            <PhTrash weight="bold" /> EXCLUIR CONTA
                        </PixelButton>
                    </div>
                </PixelCard>
            </div>

            <div
                v-if="showDeleteConfirmation"
                class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80"
                @click="cancelDeleteConfirmation"
            >
                <div
                    class="w-full max-w-md bg-card border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
                    @click.stop
                >
                    <div
                        class="bg-secondary text-secondary-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs"
                    >
                        ⚠ CONFIRMAR EXCLUSÃO
                    </div>
                    <div class="p-5">
                        <p class="font-display text-xl">
                            Excluir a conta da instituição
                            <span class="text-primary">{{ institutionName }}</span
                            >?
                        </p>
                        <p class="font-sans text-sm text-muted-foreground mt-3">
                            Esta ação é permanente e remove todos os dados vinculados ao acesso.
                        </p>
                        <div class="mt-5 flex gap-3">
                            <PixelButton
                                class="flex-1"
                                variant="ghost"
                                @click="cancelDeleteConfirmation"
                            >
                                CANCELAR
                            </PixelButton>
                            <PixelButton
                                class="flex-1"
                                variant="danger"
                                :disabled="isDeletingAccount"
                                @click="confirmDeleteInstitution"
                            >
                                <PhTrash weight="bold" /> CONFIRMAR
                            </PixelButton>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="tab === 'students'" class="space-y-4">
                <div class="flex flex-wrap gap-3 items-end">
                    <div class="flex flex-wrap gap-2">
                        <button
                            class="border-2 border-border font-pixel text-[9px] px-2 py-1"
                            :class="
                                courseFilter === 'all'
                                    ? 'bg-primary text-primary-foreground'
                                    : 'bg-card'
                            "
                            @click="courseFilter = 'all'"
                        >
                            TODOS
                        </button>
                        <button
                            v-for="c in institutionCourses"
                            :key="c.id"
                            class="border-2 border-border font-pixel text-[9px] px-2 py-1"
                            :class="
                                courseFilter === c.id
                                    ? 'bg-primary text-primary-foreground'
                                    : 'bg-card'
                            "
                            @click="courseFilter = c.id"
                        >
                            {{ c.name }}
                        </button>
                    </div>
                    <PixelInput
                        v-model="studentSearch"
                        placeholder="Buscar aluno..."
                        class="w-64"
                    />
                </div>

                <div class="font-pixel text-[9px] text-muted-foreground">
                    {{ filteredStudents.length }} aluno(s) encontrado(s)
                </div>

                <PixelCard class="overflow-hidden">
                    <div
                        class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] grid grid-cols-[2fr_1fr_auto_auto] gap-2"
                    >
                        <span>NOME</span><span>CURSO</span><span>AVATAR</span><span>MOEDAS</span>
                    </div>
                    <div
                        v-if="filteredStudents.length === 0"
                        class="p-6 text-center font-sans text-sm text-muted-foreground"
                    >
                        Nenhum aluno encontrado.
                    </div>
                    <div
                        v-for="a in filteredStudents"
                        :key="a.id"
                        class="px-4 py-3 border-t-2 border-border grid grid-cols-[2fr_1fr_auto_auto] gap-2 items-center"
                    >
                        <div class="font-pixel text-[10px] truncate">{{ a.name }}</div>
                        <div class="font-sans text-xs text-muted-foreground truncate">
                            {{ courseNameById[a.courseId] ?? 'Curso não identificado' }}
                        </div>
                        <PixelBadge tone="blue">{{ a.avatar }}</PixelBadge>
                        <div class="font-pixel text-xs">{{ a.balance }}</div>
                    </div>
                </PixelCard>
            </div>
    </div>

    <div
        v-if="showDeleteCourseModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80"
        @click="cancelDeleteCourse"
    >
        <div
            class="w-full max-w-md bg-card border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
            @click.stop
        >
            <div
                class="bg-secondary text-secondary-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs"
            >
                ⚠ CONFIRMAR EXCLUSÃO
            </div>
            <div class="p-5">
                <p class="font-display text-xl">
                    Excluir o curso
                    <span class="text-primary">{{ courseToDeleteName }}</span
                    >?
                </p>
                <p class="font-sans text-sm text-muted-foreground mt-3">
                    Esta ação é permanente e removerá o curso.
                </p>
                <div class="mt-5 flex gap-3">
                    <PixelButton class="flex-1" variant="ghost" @click="cancelDeleteCourse"
                        >CANCELAR</PixelButton
                    >
                    <PixelButton
                        class="flex-1"
                        variant="danger"
                        @click="proceedDeleteCourse"
                        ><PhTrash weight="bold" /> EXCLUIR</PixelButton
                    >
                </div>
            </div>
        </div>
    </div>

    <div
        v-if="showDeleteTeacherModal"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80"
        @click="cancelDeleteTeacher"
    >
        <div
            class="w-full max-w-md bg-card border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
            @click.stop
        >
            <div
                class="bg-secondary text-secondary-foreground border-b-4 border-border px-4 py-2 font-pixel text-xs"
            >
                ⚠ CONFIRMAR EXCLUSÃO
            </div>
            <div class="p-5">
                <p class="font-display text-xl">
                    Excluir o professor
                    <span class="text-primary">{{ teacherToDeleteName }}</span
                    >?
                </p>
                <p class="font-sans text-sm text-muted-foreground mt-3">
                    Esta ação é permanente e removerá o professor.
                </p>
                <div class="mt-5 flex gap-3">
                    <PixelButton class="flex-1" variant="ghost" @click="cancelDeleteTeacher"
                        >CANCELAR</PixelButton
                    >
                    <PixelButton
                        class="flex-1"
                        variant="danger"
                        @click="proceedDeleteTeacher"
                        ><PhTrash weight="bold" /> EXCLUIR</PixelButton
                    >
                </div>
            </div>
        </div>
    </div>
</template>

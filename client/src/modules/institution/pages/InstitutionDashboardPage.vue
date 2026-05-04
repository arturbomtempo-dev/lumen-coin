<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    createCourse as createCourseApi,
    deleteCourse,
    deleteInstitution,
    deleteTeacher,
    getCompanies,
    getCourses,
    getInstitution,
    getStudents,
    getTeachers,
    registerCompany,
    registerTeacher,
    updateInstitution,
    updateTeacher,
} from '@/modules/institution/services/institution.service';
import type {
    CompanyResponse,
    CourseResponse,
    InstitutionProfile,
    StudentResponse,
    TeacherResponse,
} from '@/modules/institution/services/institution.types';
import { registerCompanyAdminSchema } from '@/modules/schemas/register-company-admin.schema';
import { registerCourseSchema } from '@/modules/schemas/register-course.schema';
import { registerTeacherSchema } from '@/modules/schemas/register-teacher.schema';
import { updateInstitutionSchema } from '@/modules/schemas/update-institution.schema';
import { updateTeacherSchema } from '@/modules/schemas/update-teacher.schema';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { useThemeStore } from '@/shared/stores/theme.store';
import {
    PhArrowLeft,
    PhBookOpen,
    PhBriefcase,
    PhBuildings,
    PhGameController,
    PhGraduationCap,
    PhMoon,
    PhPencilSimple,
    PhPlus,
    PhStudent,
    PhSun,
    PhTrash,
    PhUser,
    PhX,
} from '@phosphor-icons/vue';
import { computed, onMounted, ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const themeStore = useThemeStore();
const router = useRouter();

const INSTITUICAO_NOME = 'UNIFEI · Mushroom Academy';

const TEACHER_AVATARS = [
    { value: 'MARIO', label: 'Mario' },
    { value: 'LUIGI', label: 'Luigi' },
    { value: 'PEACH', label: 'Peach' },
    { value: 'TOAD', label: 'Toad' },
    { value: 'YOSHI', label: 'Yoshi' },
    { value: 'BOWSER', label: 'Bowser' },
];

type Tab = 'courses' | 'teachers' | 'companies' | 'students' | 'profile';
const tab = ref<Tab>('courses');

const authStore = useAuthStore();

const institutionProfile = ref<InstitutionProfile | null>(null);

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

const courses = ref<CourseResponse[]>([]);

const {
    fields: courseData,
    errors: courseErrors,
    isSubmitting: courseIsSubmitting,
    validate: validateCourse,
    clearErrors: clearCourseErrors,
} = useForm(registerCourseSchema);

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

async function loadCourses() {
    const response = await getCourses();
    courses.value = response.data;
}

async function submitCourse(e: Event) {
    e.preventDefault();
    if (!validateCourse()) return;
    courseIsSubmitting.value = true;
    try {
        const response = await createCourseApi(courseData.value);
        courses.value.unshift(response.data);
        toast.success(`Curso "${response.data.name}" criado!`);
        courseData.value = {} as typeof courseData.value;
        clearCourseErrors();
    } catch {
    } finally {
        courseIsSubmitting.value = false;
    }
}

async function handleDeleteCourse(id: string) {
    try {
        await deleteCourse(id);
        courses.value = courses.value.filter((c) => c.id !== id);
        toast.success('Curso excluído!');
    } catch {}
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
        const response = await registerTeacher(teacherData.value);
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
        const idx = teachers.value.findIndex((t) => t.id === id);
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
        teachers.value = teachers.value.filter((t) => t.id !== id);
        toast.success('Professor excluído!');
    } catch {}
}

const companies = ref<CompanyResponse[]>([]);
const allStudents = ref<StudentResponse[]>([]);

const {
    fields: companyData,
    errors: companyErrors,
    isSubmitting: companyIsSubmitting,
    validate: validateCompany,
    clearErrors: clearCompanyErrors,
} = useForm(registerCompanyAdminSchema);

async function loadCompanies() {
    const response = await getCompanies();
    companies.value = response.data;
}

async function submitCompany(e: Event) {
    e.preventDefault();
    if (!validateCompany()) return;
    companyIsSubmitting.value = true;
    try {
        const response = await registerCompany(companyData.value);
        companies.value.unshift(response.data);
        toast.success(`Empresa "${response.data.name}" cadastrada!`);
        companyData.value = {} as typeof companyData.value;
        clearCompanyErrors();
    } catch {
    } finally {
        companyIsSubmitting.value = false;
    }
}

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

async function loadInstitutionProfile() {
    if (!authStore.user?.id) return;

    try {
        const response = await getInstitution(authStore.user.id);
        institutionProfile.value = response.data;
        profileData.value = {
            name: response.data.name,
            email: response.data.email,
            cnpj: response.data.cnpj,
            zipCode: response.data.zipCode,
            address: response.data.address,
        };
    } catch {}
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
        profileData.value = {
            name: response.data.name,
            email: response.data.email,
            cnpj: response.data.cnpj,
            zipCode: response.data.zipCode,
            address: response.data.address,
        };

        authStore.setUser({
            id: response.data.id,
            name: response.data.name,
            email: response.data.email,
            role: authStore.user?.role ?? 'institution',
        });

        clearProfileErrors();
        toast.success('Perfil da instituição atualizado com sucesso!');
    } catch {
    } finally {
        profileIsSubmitting.value = false;
    }
}

const isDeletingAccount = ref(false);

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

const tabs = [
    { id: 'courses' as Tab, label: 'CURSOS', icon: PhBookOpen },
    { id: 'teachers' as Tab, label: 'PROFESSORES', icon: PhGraduationCap },
    { id: 'companies' as Tab, label: 'EMPRESAS', icon: PhBriefcase },
    { id: 'students' as Tab, label: 'ALUNOS', icon: PhStudent },
    { id: 'profile' as Tab, label: 'PERFIL', icon: PhUser },
];

onMounted(async () => {
    await Promise.all([
        loadCourses(),
        loadStudents(),
        loadTeachers(),
        loadCompanies(),
        loadInstitutionProfile(),
    ]);
});
</script>

<template>
    <div class="min-h-screen flex flex-col bg-background">
        <header class="bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-3 gap-3">
                <RouterLink to="/" class="flex items-center gap-2 font-pixel text-[10px]">
                    <PhArrowLeft weight="bold" />
                    <PhGameController weight="fill" class="pixel-icon text-primary" :size="18" />
                    VOLTAR
                </RouterLink>
                <div class="flex items-center gap-2 font-pixel text-[10px]">
                    <PhBuildings weight="fill" class="pixel-icon text-info" :size="18" />
                    INSTITUIÇÃO · {{ INSTITUICAO_NOME.toUpperCase() }}
                </div>
                <button
                    class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))]"
                    @click="themeStore.toggle()"
                >
                    <PhSun v-if="themeStore.theme === 'night'" weight="fill" class="pixel-icon" />
                    <PhMoon v-else weight="fill" class="pixel-icon" />
                    {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
                </button>
            </div>
        </header>

        <main class="flex-1 container py-8 space-y-6">
            <section class="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
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
                    <PhBriefcase weight="fill" class="pixel-icon text-accent" :size="28" />
                    <div>
                        <div class="font-pixel text-[9px] text-muted-foreground">EMPRESAS</div>
                        <div class="font-pixel text-2xl">{{ companies.length }}</div>
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

            <!-- CURSOS TAB -->
            <div v-if="tab === 'courses'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPlus weight="bold" /> NOVO CURSO
                    </h2>
                    <form class="space-y-3" @submit="submitCourse">
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">NOME DO CURSO</label>
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
                                <option v-for="s in SHIFT_OPTIONS" :key="s.value" :value="s.value">
                                    {{ s.label }}
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
                                placeholder="Ex: 8"
                                min="1"
                                @update:model-value="(v) => (courseData.periods = +v)"
                            />
                            <p
                                v-if="courseErrors.periods"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ courseErrors.periods }}
                            </p>
                        </div>
                        <PixelButton
                            type="submit"
                            variant="success"
                            class="w-full"
                            :disabled="courseIsSubmitting"
                        >
                            <PhPlus weight="bold" /> CRIAR CURSO
                        </PixelButton>
                    </form>
                </PixelCard>

                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4">CURSOS CADASTRADOS</h2>
                    <div class="space-y-3">
                        <p
                            v-if="courses.length === 0"
                            class="font-display text-sm text-muted-foreground"
                        >
                            Nenhum curso cadastrado.
                        </p>
                        <div
                            v-for="c in courses"
                            :key="c.id"
                            class="border-2 border-border bg-card p-3 shadow-[3px_3px_0_0_hsl(var(--border))]"
                        >
                            <div class="flex items-start justify-between gap-3">
                                <div class="min-w-0">
                                    <div class="font-pixel text-xs">{{ c.name }}</div>
                                    <div class="font-display text-sm text-muted-foreground mt-1">
                                        {{ SHIFT_LABELS[c.shift] }} · {{ c.periods }} período(s)
                                    </div>
                                </div>
                                <PixelButton
                                    variant="danger"
                                    size="sm"
                                    @click="handleDeleteCourse(c.id)"
                                >
                                    <PhTrash weight="bold" /> EXCLUIR
                                </PixelButton>
                            </div>
                        </div>
                    </div>
                </PixelCard>
            </div>

            <!-- PROFESSORES TAB -->
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
                            <label class="font-pixel text-[9px] block mb-1">
                                E-MAIL INSTITUCIONAL
                            </label>
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
                                placeholder="Somente 11 dígitos"
                                maxlength="11"
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
                            <label class="font-pixel text-[9px] block mb-1">SENHA</label>
                            <PixelInput
                                v-model="teacherData.password"
                                type="password"
                                placeholder="Mínimo 8 caracteres"
                            />
                            <p
                                v-if="teacherErrors.password"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ teacherErrors.password }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">AVATAR</label>
                            <select
                                v-model="teacherData.avatar"
                                class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-base focus:outline-none"
                            >
                                <option value="" disabled>Selecione um avatar</option>
                                <option
                                    v-for="avatar in TEACHER_AVATARS"
                                    :key="avatar.value"
                                    :value="avatar.value"
                                >
                                    {{ avatar.label }}
                                </option>
                            </select>
                            <p
                                v-if="teacherErrors.avatar"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ teacherErrors.avatar }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">
                                DEPARTAMENTO (OPCIONAL)
                            </label>
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
                            v-for="t in teachers"
                            :key="t.id"
                            class="border-2 border-border bg-card p-3"
                        >
                            <!-- View mode -->
                            <template v-if="editingTeacherId !== t.id">
                                <div class="flex items-start justify-between gap-2">
                                    <div class="min-w-0">
                                        <div class="font-pixel text-xs">{{ t.name }}</div>
                                        <div class="font-sans text-xs text-muted-foreground mt-0.5">
                                            {{ t.email }} · {{ t.cpf }}
                                        </div>
                                        <div class="flex flex-wrap gap-1.5 mt-2">
                                            <PixelBadge tone="blue">{{ t.avatar }}</PixelBadge>
                                            <PixelBadge v-if="t.department" tone="teal">
                                                {{ t.department }}
                                            </PixelBadge>
                                        </div>
                                    </div>
                                    <div class="flex gap-1.5 shrink-0">
                                        <PixelButton
                                            size="sm"
                                            variant="ghost"
                                            @click="startEditTeacher(t)"
                                        >
                                            <PhPencilSimple weight="bold" :size="13" />
                                        </PixelButton>
                                        <PixelButton
                                            size="sm"
                                            variant="danger"
                                            @click="handleDeleteTeacher(t.id)"
                                        >
                                            <PhTrash weight="bold" :size="13" />
                                        </PixelButton>
                                    </div>
                                </div>
                            </template>

                            <!-- Edit mode -->
                            <template v-else>
                                <form class="space-y-2" @submit="submitEditTeacher">
                                    <div class="font-pixel text-[9px] text-primary mb-2">
                                        EDITANDO: {{ t.name }}
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
                                        >
                                            SALVAR
                                        </PixelButton>
                                        <PixelButton
                                            type="button"
                                            variant="ghost"
                                            size="sm"
                                            @click="cancelEditTeacher"
                                        >
                                            <PhX weight="bold" :size="13" /> CANCELAR
                                        </PixelButton>
                                    </div>
                                </form>
                            </template>
                        </div>
                    </div>
                </PixelCard>
            </div>

            <!-- EMPRESAS TAB -->
            <div v-if="tab === 'companies'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPlus weight="bold" /> CADASTRAR EMPRESA
                    </h2>
                    <form class="space-y-3" @submit="submitCompany">
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">NOME DA EMPRESA</label>
                            <PixelInput
                                v-model="companyData.name"
                                placeholder="Ex: Loja do Mario"
                            />
                            <p
                                v-if="companyErrors.name"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ companyErrors.name }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">E-MAIL</label>
                            <PixelInput
                                v-model="companyData.email"
                                type="email"
                                placeholder="contato@empresa.com"
                            />
                            <p
                                v-if="companyErrors.email"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ companyErrors.email }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">CNPJ</label>
                            <PixelInput
                                v-model="companyData.cnpj"
                                placeholder="Somente 14 dígitos"
                                maxlength="14"
                            />
                            <p
                                v-if="companyErrors.cnpj"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ companyErrors.cnpj }}
                            </p>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">SENHA</label>
                            <PixelInput
                                v-model="companyData.password"
                                type="password"
                                placeholder="Mínimo 8 caracteres"
                            />
                            <p
                                v-if="companyErrors.password"
                                class="font-sans text-xs mt-1"
                                style="color: hsl(var(--destructive))"
                            >
                                {{ companyErrors.password }}
                            </p>
                        </div>
                        <PixelButton
                            type="submit"
                            variant="success"
                            class="w-full"
                            :disabled="companyIsSubmitting"
                        >
                            <PhPlus weight="bold" /> CADASTRAR
                        </PixelButton>
                    </form>
                </PixelCard>

                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4">EMPRESAS PARCEIRAS</h2>
                    <div class="space-y-3">
                        <p
                            v-if="companies.length === 0"
                            class="font-display text-sm text-muted-foreground"
                        >
                            Nenhuma empresa cadastrada.
                        </p>
                        <div
                            v-for="c in companies"
                            :key="c.id"
                            class="border-2 border-border bg-card p-3"
                        >
                            <div class="min-w-0">
                                <div class="font-pixel text-xs">{{ c.name }}</div>
                                <div class="font-sans text-xs text-muted-foreground mt-0.5">
                                    {{ c.email }}
                                </div>
                                <div class="mt-2">
                                    <PixelBadge tone="gold">CNPJ: {{ c.cnpj }}</PixelBadge>
                                </div>
                            </div>
                        </div>
                    </div>
                </PixelCard>
            </div>

            <div v-if="tab === 'profile'" class="grid lg:grid-cols-[1fr_0.9fr] gap-6 items-start">
                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPencilSimple weight="bold" /> EDITAR PERFIL
                    </h2>
                    <form class="space-y-3" @submit="handleUpdateInstitutionProfile">
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

                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-1">CEP</label>
                                <PixelInput
                                    v-model="profileData.zipCode"
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
                        </div>

                        <PixelButton
                            type="submit"
                            variant="success"
                            class="w-full"
                            :disabled="profileIsSubmitting"
                        >
                            <PhPencilSimple weight="bold" /> SALVAR ALTERAÇÕES
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
                            <PixelBadge tone="blue"
                                >CNPJ: {{ institutionProfile?.cnpj ?? '-' }}</PixelBadge
                            >
                            <PixelBadge tone="green"
                                >CEP: {{ institutionProfile?.zipCode ?? '-' }}</PixelBadge
                            >
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
                            @click="handleDeleteInstitution"
                        >
                            <PhTrash weight="bold" /> EXCLUIR CONTA
                        </PixelButton>
                    </div>
                </PixelCard>
            </div>

            <!-- ALUNOS TAB -->
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
        </main>
    </div>
</template>

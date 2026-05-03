<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import {
    PhArrowLeft,
    PhGameController,
    PhSun,
    PhMoon,
    PhBuildings,
    PhBookOpen,
    PhGraduationCap,
    PhStudent,
    PhBriefcase,
    PhPlus,
    PhTrash,
} from '@phosphor-icons/vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import {
    cursosInstituicao,
    alunos as alunosBase,
    cursoToId,
    type Curso,
} from '@/shared/data/mockData';
import { useThemeStore } from '@/shared/stores/theme.store';
import { useForm } from '@/shared/composables/useForm';
import { registerTeacherSchema } from '@/modules/schemas/register-teacher.schema';
import { registerCompanyAdminSchema } from '@/modules/schemas/register-company-admin.schema';
import {
    getTeachers,
    registerTeacher,
    getCompanies,
    registerCompany,
} from '@/modules/institution/services/institution.service';
import type {
    TeacherResponse,
    CompanyResponse,
} from '@/modules/institution/services/institution.types';
import { toast } from 'vue-sonner';

const themeStore = useThemeStore();

const INSTITUICAO_NOME = 'UNIFEI · Mushroom Academy';

const TEACHER_AVATARS = [
    { value: 'MARIO', label: 'Mario' },
    { value: 'LUIGI', label: 'Luigi' },
    { value: 'PEACH', label: 'Peach' },
    { value: 'TOAD', label: 'Toad' },
    { value: 'YOSHI', label: 'Yoshi' },
    { value: 'BOWSER', label: 'Bowser' },
];

type Tab = 'cursos' | 'professores' | 'empresas' | 'alunos';
const tab = ref<Tab>('cursos');

const cursos = ref<Curso[]>([...cursosInstituicao]);

const novoCurso = ref({ nome: '', periodo: '2025.1', duracao: '8 semestres', cargaHoraria: 3200 });

function criarCurso(e: Event) {
    e.preventDefault();
    if (!novoCurso.value.nome) return;
    const c: Curso = {
        id: 'cur-' + Date.now(),
        ...novoCurso.value,
        cargaHoraria: Number(novoCurso.value.cargaHoraria),
    };
    cursos.value.unshift(c);
    toast.success(`Curso "${c.nome}" criado!`);
    novoCurso.value = { nome: '', periodo: '2025.1', duracao: '8 semestres', cargaHoraria: 3200 };
}

function removerCurso(id: string) {
    cursos.value = cursos.value.filter((c) => c.id !== id);
}

const teachers = ref<TeacherResponse[]>([]);

const {
    data: teacherData,
    errors: teacherErrors,
    isSubmitting: teacherIsSubmitting,
    validate: validateTeacher,
    clearErrors: clearTeacherErrors,
} = useForm(registerTeacherSchema);

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

const companies = ref<CompanyResponse[]>([]);

const {
    data: companyData,
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

const filtroCursoAluno = ref('todos');
const buscaAluno = ref('');

const alunosFiltrados = computed(() =>
    alunosBase.filter((a) => {
        const cid = cursoToId[a.curso] ?? '';
        const matchCurso = filtroCursoAluno.value === 'todos' || cid === filtroCursoAluno.value;
        const matchBusca = a.nome.toLowerCase().includes(buscaAluno.value.toLowerCase());
        return matchCurso && matchBusca;
    })
);

const tabs = [
    { id: 'cursos' as Tab, label: 'CURSOS', icon: PhBookOpen },
    { id: 'professores' as Tab, label: 'PROFESSORES', icon: PhGraduationCap },
    { id: 'empresas' as Tab, label: 'EMPRESAS', icon: PhBriefcase },
    { id: 'alunos' as Tab, label: 'ALUNOS', icon: PhStudent },
];

onMounted(async () => {
    await Promise.all([loadTeachers(), loadCompanies()]);
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
                        <div class="font-pixel text-2xl">{{ cursos.length }}</div>
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
                        <div class="font-pixel text-2xl">{{ alunosBase.length }}</div>
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
            <div v-if="tab === 'cursos'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4 flex items-center gap-2">
                        <PhPlus weight="bold" /> NOVO CURSO
                    </h2>
                    <form class="space-y-3" @submit="criarCurso">
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">NOME DO CURSO</label>
                            <PixelInput
                                v-model="novoCurso.nome"
                                placeholder="Ex: Engenharia Civil"
                                required
                            />
                        </div>
                        <div class="grid grid-cols-2 gap-3">
                            <div>
                                <label class="font-pixel text-[9px] block mb-1">PERÍODO</label>
                                <PixelInput v-model="novoCurso.periodo" />
                            </div>
                            <div>
                                <label class="font-pixel text-[9px] block mb-1">DURAÇÃO</label>
                                <PixelInput v-model="novoCurso.duracao" />
                            </div>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">CARGA HORÁRIA</label>
                            <PixelInput v-model="novoCurso.cargaHoraria" type="number" />
                        </div>
                        <PixelButton type="submit" variant="success" class="w-full">
                            <PhPlus weight="bold" /> CRIAR CURSO
                        </PixelButton>
                    </form>
                </PixelCard>

                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4">CURSOS CADASTRADOS</h2>
                    <div class="space-y-3">
                        <p
                            v-if="cursos.length === 0"
                            class="font-display text-sm text-muted-foreground"
                        >
                            Nenhum curso cadastrado.
                        </p>
                        <div
                            v-for="c in cursos"
                            :key="c.id"
                            class="border-2 border-border bg-card p-3 shadow-[3px_3px_0_0_hsl(var(--border))]"
                        >
                            <div class="flex items-start justify-between gap-3">
                                <div class="min-w-0">
                                    <div class="font-pixel text-xs">{{ c.nome }}</div>
                                    <div class="font-display text-sm text-muted-foreground mt-1">
                                        {{ c.duracao }} · {{ c.cargaHoraria }}h · {{ c.periodo }}
                                    </div>
                                    <div class="flex gap-2 mt-2 flex-wrap">
                                        <PixelBadge tone="green">
                                            {{
                                                alunosBase.filter(
                                                    (a) => cursoToId[a.curso] === c.id
                                                ).length
                                            }}
                                            alunos
                                        </PixelBadge>
                                    </div>
                                </div>
                                <PixelButton variant="danger" size="sm" @click="removerCurso(c.id)">
                                    <PhTrash weight="bold" /> EXCLUIR
                                </PixelButton>
                            </div>
                        </div>
                    </div>
                </PixelCard>
            </div>

            <!-- PROFESSORES TAB -->
            <div v-if="tab === 'professores'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
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
                        </div>
                    </div>
                </PixelCard>
            </div>

            <!-- EMPRESAS TAB -->
            <div v-if="tab === 'empresas'" class="grid lg:grid-cols-[1fr_1.4fr] gap-6">
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

            <!-- ALUNOS TAB -->
            <div v-if="tab === 'alunos'" class="space-y-4">
                <div class="flex flex-wrap gap-3 items-end">
                    <div class="flex flex-wrap gap-2">
                        <button
                            class="border-2 border-border font-pixel text-[9px] px-2 py-1"
                            :class="
                                filtroCursoAluno === 'todos'
                                    ? 'bg-primary text-primary-foreground'
                                    : 'bg-card'
                            "
                            @click="filtroCursoAluno = 'todos'"
                        >
                            TODOS
                        </button>
                        <button
                            v-for="c in cursos"
                            :key="c.id"
                            class="border-2 border-border font-pixel text-[9px] px-2 py-1"
                            :class="
                                filtroCursoAluno === c.id
                                    ? 'bg-primary text-primary-foreground'
                                    : 'bg-card'
                            "
                            @click="filtroCursoAluno = c.id"
                        >
                            {{ c.nome }}
                        </button>
                    </div>
                    <PixelInput v-model="buscaAluno" placeholder="Buscar aluno..." class="w-64" />
                </div>

                <div class="font-pixel text-[9px] text-muted-foreground">
                    {{ alunosFiltrados.length }} aluno(s) encontrado(s)
                </div>

                <PixelCard class="overflow-hidden">
                    <div
                        class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] grid grid-cols-[2fr_1fr_auto_auto] gap-2"
                    >
                        <span>NOME</span><span>CURSO</span><span>LV</span><span>MOEDAS</span>
                    </div>
                    <div
                        v-if="alunosFiltrados.length === 0"
                        class="p-6 text-center font-sans text-sm text-muted-foreground"
                    >
                        Nenhum aluno encontrado.
                    </div>
                    <div
                        v-for="a in alunosFiltrados"
                        :key="a.id"
                        class="px-4 py-3 border-t-2 border-border grid grid-cols-[2fr_1fr_auto_auto] gap-2 items-center"
                    >
                        <div class="font-pixel text-[10px] truncate">{{ a.nome }}</div>
                        <div class="font-sans text-xs text-muted-foreground truncate">
                            {{ a.curso }}
                        </div>
                        <PixelBadge tone="blue">{{ a.nivel }}</PixelBadge>
                        <div class="font-pixel text-xs">{{ a.moedas }}</div>
                    </div>
                </PixelCard>
            </div>
        </main>
    </div>
</template>

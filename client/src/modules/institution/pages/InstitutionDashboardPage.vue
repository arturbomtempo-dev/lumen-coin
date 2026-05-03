<script setup lang="ts">
import { ref, computed } from 'vue';
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
    PhPlus,
    PhTrash,
} from '@phosphor-icons/vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import {
    cursosInstituicao,
    professoresInstituicao,
    alunos as alunosBase,
    cursoToId,
    type Curso,
    type ProfessorInst,
} from '@/shared/data/mockData';
import { useThemeStore } from '@/shared/stores/theme.store';
import { toast } from 'vue-sonner';

const themeStore = useThemeStore();

const INSTITUICAO_NOME = 'UNIFEI · Mushroom Academy';
type Tab = 'cursos' | 'professores' | 'alunos';
const tab = ref<Tab>('cursos');

const cursos = ref<Curso[]>([...cursosInstituicao]);
const professores = ref<ProfessorInst[]>([...professoresInstituicao]);

// CURSOS
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
    professores.value = professores.value.map((p) => ({
        ...p,
        cursoIds: p.cursoIds.filter((cid) => cid !== id),
    }));
}

// PROFESSORES
const novoProf = ref({ nome: '', email: '', titulacao: 'Mestre', cursoIds: [] as string[] });

function toggleCursoProf(id: string) {
    const idx = novoProf.value.cursoIds.indexOf(id);
    if (idx === -1) novoProf.value.cursoIds.push(id);
    else novoProf.value.cursoIds.splice(idx, 1);
}

function cadastrarProf(e: Event) {
    e.preventDefault();
    if (!novoProf.value.nome || !novoProf.value.email || novoProf.value.cursoIds.length === 0)
        return;
    const p: ProfessorInst = { id: 'p' + Date.now(), ...novoProf.value };
    professores.value.unshift(p);
    toast.success(`Professor "${p.nome}" cadastrado!`);
    novoProf.value = { nome: '', email: '', titulacao: 'Mestre', cursoIds: [] };
}

function removerProf(id: string) {
    professores.value = professores.value.filter((p) => p.id !== id);
}

// ALUNOS
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

function cursoNome(id: string) {
    return cursos.value.find((c) => c.id === id)?.nome ?? '—';
}

const tabs = [
    { id: 'cursos' as Tab, label: 'CURSOS', icon: PhBookOpen },
    { id: 'professores' as Tab, label: 'PROFESSORES', icon: PhGraduationCap },
    { id: 'alunos' as Tab, label: 'ALUNOS', icon: PhStudent },
];
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
            <!-- Summary -->
            <section class="grid sm:grid-cols-3 gap-4">
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
                        <div class="font-pixel text-2xl">{{ professores.length }}</div>
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

            <!-- Tabs -->
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
                        <PixelButton type="submit" variant="success" class="w-full"
                            ><PhPlus weight="bold" /> CRIAR CURSO</PixelButton
                        >
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
                                        <PixelBadge tone="blue"
                                            >{{
                                                professores.filter((p) => p.cursoIds.includes(c.id))
                                                    .length
                                            }}
                                            prof.</PixelBadge
                                        >
                                        <PixelBadge tone="green"
                                            >{{
                                                alunosBase.filter(
                                                    (a) => cursoToId[a.curso] === c.id
                                                ).length
                                            }}
                                            alunos</PixelBadge
                                        >
                                    </div>
                                </div>
                                <PixelButton variant="danger" size="sm" @click="removerCurso(c.id)"
                                    ><PhTrash weight="bold" /> EXCLUIR</PixelButton
                                >
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
                    <form class="space-y-3" @submit="cadastrarProf">
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">NOME COMPLETO</label>
                            <PixelInput
                                v-model="novoProf.nome"
                                placeholder="Prof. Fulano de Tal"
                                required
                            />
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1"
                                >E-MAIL INSTITUCIONAL</label
                            >
                            <PixelInput
                                v-model="novoProf.email"
                                type="email"
                                placeholder="prof@unifei.edu.br"
                                required
                            />
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-1">TITULAÇÃO</label>
                            <select
                                v-model="novoProf.titulacao"
                                class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-base focus:outline-none"
                            >
                                <option>Especialista</option>
                                <option>Mestre</option>
                                <option>Doutor</option>
                                <option>Pós-Doutor</option>
                            </select>
                        </div>
                        <div>
                            <label class="font-pixel text-[9px] block mb-2"
                                >VINCULAR A CURSOS</label
                            >
                            <div
                                class="space-y-1.5 max-h-48 overflow-auto border-2 border-border p-2 bg-card"
                            >
                                <label
                                    v-for="c in cursos"
                                    :key="c.id"
                                    class="flex items-center gap-2 px-2 py-1.5 border-2 cursor-pointer font-display text-sm"
                                    :class="
                                        novoProf.cursoIds.includes(c.id)
                                            ? 'border-primary bg-primary/10'
                                            : 'border-transparent hover:border-border'
                                    "
                                >
                                    <input
                                        type="checkbox"
                                        :checked="novoProf.cursoIds.includes(c.id)"
                                        class="accent-primary"
                                        @change="toggleCursoProf(c.id)"
                                    />
                                    {{ c.nome }}
                                </label>
                                <p
                                    v-if="cursos.length === 0"
                                    class="font-sans text-xs text-muted-foreground p-2"
                                >
                                    Crie cursos primeiro.
                                </p>
                            </div>
                        </div>
                        <PixelButton type="submit" variant="success" class="w-full"
                            ><PhPlus weight="bold" /> CADASTRAR</PixelButton
                        >
                    </form>
                </PixelCard>

                <PixelCard class="p-5">
                    <h2 class="font-pixel text-sm mb-4">PROFESSORES CADASTRADOS</h2>
                    <div class="space-y-3">
                        <p
                            v-if="professores.length === 0"
                            class="font-display text-sm text-muted-foreground"
                        >
                            Nenhum professor cadastrado.
                        </p>
                        <div
                            v-for="p in professores"
                            :key="p.id"
                            class="border-2 border-border bg-card p-3"
                        >
                            <div class="flex items-start justify-between gap-3">
                                <div class="min-w-0">
                                    <div class="font-pixel text-xs">{{ p.nome }}</div>
                                    <div class="font-sans text-xs text-muted-foreground mt-0.5">
                                        {{ p.email }} · {{ p.titulacao }}
                                    </div>
                                    <div class="flex flex-wrap gap-1.5 mt-2">
                                        <PixelBadge
                                            v-for="cid in p.cursoIds"
                                            :key="cid"
                                            tone="blue"
                                            >{{ cursoNome(cid) }}</PixelBadge
                                        >
                                    </div>
                                </div>
                                <PixelButton variant="danger" size="sm" @click="removerProf(p.id)"
                                    ><PhTrash weight="bold"
                                /></PixelButton>
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

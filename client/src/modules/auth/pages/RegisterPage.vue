<script setup lang="ts">
import { ref, computed } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import {
    PhArrowLeft,
    PhArrowRight,
    PhCheckCircle,
    PhUser,
    PhIdentificationCard,
    PhHouse,
    PhGraduationCap,
    PhCrown,
} from '@phosphor-icons/vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import { MARIO_CHARACTERS, type MarioCharacter } from '@/shared/data/characters';
import { cursos, instituicoes } from '@/shared/data/mockData';
import { useStudentStore } from '@/modules/student/stores/student.store';
import { toast } from 'vue-sonner';

const router = useRouter();
const studentStore = useStudentStore();

const steps = [
    { label: 'IDENTIDADE', icon: PhUser },
    { label: 'DOCUMENTOS', icon: PhIdentificationCard },
    { label: 'ENDEREÇO', icon: PhHouse },
    { label: 'ACADÊMICO', icon: PhGraduationCap },
    { label: 'AVATAR', icon: PhCrown },
];

const step = ref(0);
const personagem = ref<MarioCharacter>('mario');
const form = ref({
    nome: '',
    email: '',
    cpf: '',
    rg: '',
    cep: '',
    rua: '',
    numero: '',
    complemento: '',
    bairro: '',
    cidade: '',
    instituicao: instituicoes[0],
    curso: cursos[0],
});

const progress = computed(() => ((step.value + 1) / steps.length) * 100);

function next() {
    if (step.value < steps.length - 1) {
        step.value++;
    } else {
        studentStore.setPersonagem(personagem.value);
        toast.success(`Bem-vindo(a), ${form.value.nome || 'Jogador'}! Sua jornada começa agora.`, {
            description: 'PERSONAGEM CRIADO! +100 moedas bônus!',
        });
        router.push('/app/aluno');
    }
}

function prev() {
    step.value = Math.max(0, step.value - 1);
}
</script>

<template>
    <div class="min-h-screen flex items-center justify-center p-4">
        <div class="w-full max-w-2xl">
            <RouterLink
                to="/"
                class="inline-flex items-center gap-2 font-pixel text-[10px] mb-4 hover:text-primary"
            >
                <PhArrowLeft weight="bold" /> VOLTAR À TELA INICIAL
            </RouterLink>

            <PixelCard class="p-6 md:p-8">
                <div class="flex items-center gap-4 mb-6">
                    <div class="border-2 border-border bg-hud p-2">
                        <MarioAvatar :character="personagem" :size="56" />
                    </div>
                    <div>
                        <div class="font-pixel text-[10px] text-primary mb-1">
                            ▶ CRIAÇÃO DE PERSONAGEM
                        </div>
                        <h1 class="font-pixel text-lg md:text-xl">NOVO JOGADOR</h1>
                    </div>
                </div>

                <!-- Progress -->
                <div class="mb-6">
                    <div class="flex items-center justify-between font-pixel text-[9px] mb-2">
                        <span>FASE {{ step + 1 }} DE {{ steps.length }}</span>
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
                    <div class="flex justify-between mt-3">
                        <div
                            v-for="(s, i) in steps"
                            :key="s.label"
                            class="flex flex-col items-center gap-1 flex-1"
                        >
                            <div
                                class="w-8 h-8 flex items-center justify-center border-2 border-border"
                                :class="
                                    i === step
                                        ? 'bg-primary text-primary-foreground animate-glow'
                                        : i < step
                                          ? 'bg-success text-success-foreground'
                                          : 'bg-card'
                                "
                            >
                                <PhCheckCircle v-if="i < step" weight="fill" :size="16" />
                                <component v-else :is="s.icon" weight="fill" :size="16" />
                            </div>
                            <span
                                class="font-pixel text-[7px]"
                                :class="i === step ? 'text-primary' : 'text-muted-foreground'"
                                >{{ s.label }}</span
                            >
                        </div>
                    </div>
                </div>

                <!-- Step fields -->
                <div class="space-y-4">
                    <!-- Step 0: Identity -->
                    <template v-if="step === 0">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">NOME COMPLETO</label>
                            <PixelInput v-model="form.nome" placeholder="Ex: Maria Luiza Souza" />
                        </div>
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">E-MAIL</label>
                            <PixelInput
                                v-model="form.email"
                                type="email"
                                placeholder="voce@email.com"
                            />
                        </div>
                    </template>

                    <!-- Step 1: Documents -->
                    <template v-if="step === 1">
                        <div class="grid sm:grid-cols-2 gap-4">
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">CPF</label>
                                <PixelInput v-model="form.cpf" placeholder="000.000.000-00" />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">RG</label>
                                <PixelInput v-model="form.rg" placeholder="00.000.000-0" />
                            </div>
                        </div>
                    </template>

                    <!-- Step 2: Address -->
                    <template v-if="step === 2">
                        <div class="grid sm:grid-cols-2 gap-4">
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">CEP</label>
                                <PixelInput v-model="form.cep" placeholder="00000-000" />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">CIDADE</label>
                                <PixelInput v-model="form.cidade" placeholder="Ex: Itajubá/MG" />
                            </div>
                            <div class="sm:col-span-2">
                                <label class="font-pixel text-[10px] block mb-2"
                                    >RUA / LOGRADOURO</label
                                >
                                <PixelInput v-model="form.rua" placeholder="Ex: Av. BPS" />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">NÚMERO</label>
                                <PixelInput v-model="form.numero" placeholder="123" />
                            </div>
                            <div>
                                <label class="font-pixel text-[10px] block mb-2">COMPLEMENTO</label>
                                <PixelInput
                                    v-model="form.complemento"
                                    placeholder="Apto / Bloco (opcional)"
                                />
                            </div>
                            <div class="sm:col-span-2">
                                <label class="font-pixel text-[10px] block mb-2">BAIRRO</label>
                                <PixelInput v-model="form.bairro" placeholder="Ex: Pinheirinho" />
                            </div>
                        </div>
                    </template>

                    <!-- Step 3: Academic -->
                    <template v-if="step === 3">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">INSTITUIÇÃO</label>
                            <select
                                v-model="form.instituicao"
                                class="w-full border-2 border-border bg-card text-card-foreground font-pixel text-[10px] px-3 py-2"
                            >
                                <option v-for="inst in instituicoes" :key="inst" :value="inst">
                                    {{ inst }}
                                </option>
                            </select>
                        </div>
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">CURSO</label>
                            <select
                                v-model="form.curso"
                                class="w-full border-2 border-border bg-card text-card-foreground font-pixel text-[10px] px-3 py-2"
                            >
                                <option v-for="c in cursos" :key="c" :value="c">{{ c }}</option>
                            </select>
                        </div>
                    </template>

                    <!-- Step 4: Avatar -->
                    <template v-if="step === 4">
                        <label class="font-pixel text-[10px] block mb-3"
                            >ESCOLHA SEU PERSONAGEM</label
                        >
                        <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
                            <button
                                v-for="c in MARIO_CHARACTERS"
                                :key="c.id"
                                type="button"
                                class="border-2 border-border p-3 text-center transition-all"
                                :class="
                                    personagem === c.id
                                        ? 'bg-primary text-primary-foreground shadow-[4px_4px_0_0_hsl(var(--border))] -translate-y-0.5'
                                        : 'bg-card hover:-translate-y-0.5'
                                "
                                @click="personagem = c.id"
                            >
                                <div class="flex justify-center bg-hud border-2 border-border p-2">
                                    <MarioAvatar :character="c.id" :size="56" />
                                </div>
                                <div class="font-pixel text-[10px] mt-2">
                                    {{ c.nome.toUpperCase() }}
                                </div>
                                <div
                                    class="font-sans text-[11px] mt-1"
                                    :class="
                                        personagem === c.id ? 'opacity-90' : 'text-muted-foreground'
                                    "
                                >
                                    {{ c.classe }}
                                </div>
                            </button>
                        </div>
                    </template>
                </div>

                <!-- Navigation -->
                <div class="flex items-center justify-between mt-8">
                    <PixelButton variant="ghost" :disabled="step === 0" @click="prev">
                        <PhArrowLeft weight="bold" /> VOLTAR
                    </PixelButton>
                    <PixelButton variant="primary" @click="next">
                        {{ step === steps.length - 1 ? 'FINALIZAR' : 'PRÓXIMA FASE' }}
                        <PhArrowRight weight="bold" />
                    </PixelButton>
                </div>
            </PixelCard>
        </div>
    </div>
</template>

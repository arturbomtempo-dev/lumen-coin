<script setup lang="ts">
import {
    getSentTransfers,
    type CoinTransferResponse,
} from '@/modules/coin-transfer/services/coin-transfer.service';
import {
    getStudentsByInstitution,
    type StudentSummary,
} from '@/modules/teacher/services/teacher.service';
import { useTeacherStore } from '@/modules/teacher/stores/teacher.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import type { MarioCharacter } from '@/shared/data/characters';
import {
    PhArrowRight,
    PhGraduationCap,
    PhLightning,
    PhPaperPlaneTilt,
    PhReceipt,
    PhUsers,
} from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';

const teacherStore = useTeacherStore();
const { name: _name, balance, institutionId } = storeToRefs(teacherStore);

const recentTransfers = ref<CoinTransferResponse[]>([]);
const students = ref<StudentSummary[]>([]);
const isLoading = ref(true);

const avatarFor = (avatar: string): MarioCharacter => {
    const map: Record<string, MarioCharacter> = {
        MARIO: 'mario',
        LUIGI: 'luigi',
        PEACH: 'peach',
        TOAD: 'toad',
        YOSHI: 'yoshi',
        BOWSER: 'bowser',
    };
    return (map[avatar] ?? 'mario') as MarioCharacter;
};

function formatDate(iso: string) {
    return new Intl.DateTimeFormat('pt-BR', {
        timeZone: 'America/Sao_Paulo',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
    }).format(new Date(iso));
}

onMounted(async () => {
    try {
        if (!teacherStore.isLoaded) {
            await teacherStore.loadProfile();
        }
        const [transfersRes, studentsRes] = await Promise.all([
            getSentTransfers(),
            institutionId.value
                ? getStudentsByInstitution(institutionId.value)
                : Promise.resolve({ data: [] as StudentSummary[] }),
        ]);
        recentTransfers.value = transfersRes.data.slice(0, 5);
        students.value = studentsRes.data;
    } finally {
        isLoading.value = false;
    }
});
</script>

<template>
    <div class="space-y-8">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ MESA DE CONTROLE</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhGraduationCap weight="fill" class="pixel-icon" /> PAINEL DO PROFESSOR
            </h1>
        </div>

        <div class="grid md:grid-cols-3 gap-4">
            <PixelCard class="p-5 md:col-span-2 crt-scanlines">
                <div class="font-pixel text-[10px] text-muted-foreground">SALDO DISPONÍVEL</div>
                <div class="flex items-baseline gap-3 mt-2">
                    <CoinIcon :size="36" class="animate-bob" />
                    <span class="font-pixel text-4xl text-primary animate-coin-flash">{{
                        balance.toLocaleString('pt-BR')
                    }}</span>
                    <span class="font-pixel text-sm text-muted-foreground">LUMEN COINS</span>
                </div>
            </PixelCard>
            <PixelCard class="p-5">
                <div class="font-pixel text-[10px] text-muted-foreground">ALUNOS NA INST.</div>
                <div class="font-pixel text-3xl mt-2 text-info">{{ students.length }}</div>
                <div class="font-sans text-xs text-muted-foreground mt-1">
                    disponíveis para reconhecimento
                </div>
            </PixelCard>
        </div>

        <div class="flex flex-wrap gap-3">
            <RouterLink to="/app/professor/enviar-moedas">
                <PixelButton variant="success" class="flex items-center gap-2">
                    <PhPaperPlaneTilt weight="fill" class="pixel-icon" /> ENVIAR MOEDAS
                </PixelButton>
            </RouterLink>
            <RouterLink to="/app/professor/extrato">
                <PixelButton variant="ghost" class="flex items-center gap-2">
                    <PhReceipt weight="fill" class="pixel-icon" /> VER EXTRATO
                </PixelButton>
            </RouterLink>
        </div>

        <PixelCard class="overflow-hidden">
            <div
                class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] flex items-center justify-between"
            >
                <span class="flex items-center gap-2"
                    ><PhReceipt weight="fill" /> ENVIOS RECENTES</span
                >
                <RouterLink
                    to="/app/professor/extrato"
                    class="flex items-center gap-1 hover:text-primary transition-colors"
                >
                    VER TODOS <PhArrowRight weight="bold" :size="12" />
                </RouterLink>
            </div>
            <div v-if="isLoading" class="p-6 font-sans text-sm text-muted-foreground text-center">
                Carregando...
            </div>
            <div
                v-else-if="recentTransfers.length === 0"
                class="p-6 font-sans text-sm text-muted-foreground text-center"
            >
                Nenhuma transferência realizada ainda.
            </div>
            <div
                v-for="t in recentTransfers"
                :key="t.id"
                class="px-4 py-3 border-t-2 border-border grid grid-cols-[1fr_auto] gap-2"
            >
                <div>
                    <div class="font-pixel text-[10px]">{{ t.recipientName }}</div>
                    <div class="font-sans text-sm text-foreground/80 mt-1">{{ t.message }}</div>
                    <div class="font-pixel text-[9px] text-muted-foreground mt-1">
                        {{ formatDate(t.sentAt) }}
                    </div>
                </div>
                <div class="font-pixel text-sm flex items-center gap-1 text-success self-start">
                    +<CoinIcon :size="12" /> {{ t.amount }}
                </div>
            </div>
        </PixelCard>

        <section>
            <div class="font-pixel text-[10px] text-primary mb-2">▶ ALUNOS DA INSTITUIÇÃO</div>
            <h2 class="font-pixel text-lg flex items-center gap-2">
                <PhUsers weight="fill" class="pixel-icon" /> MEUS ALUNOS
            </h2>
            <div v-if="isLoading" class="mt-4 font-sans text-sm text-muted-foreground">
                Carregando...
            </div>
            <div
                v-else-if="students.length === 0"
                class="mt-4 font-sans text-sm text-muted-foreground"
            >
                Nenhum aluno encontrado na sua instituição.
            </div>
            <div v-else class="mt-4 grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
                <PixelCard v-for="s in students" :key="s.id" class="p-3 flex items-center gap-3">
                    <div class="border-2 border-border bg-hud p-0.5 shrink-0">
                        <MarioAvatar :character="avatarFor(s.avatar)" :size="36" />
                    </div>
                    <div class="min-w-0">
                        <div class="font-pixel text-[10px] truncate">{{ s.name }}</div>
                        <div class="flex items-center gap-1 mt-1">
                            <PixelBadge tone="gold">
                                <CoinIcon :size="10" /> {{ s.balance }}
                            </PixelBadge>
                        </div>
                    </div>
                </PixelCard>
            </div>
        </section>

        <div>
            <div class="font-pixel text-[10px] text-primary mb-3">▶ INFO</div>
            <div class="flex flex-wrap gap-3">
                <PixelBadge tone="gold">
                    <PhLightning weight="fill" class="pixel-icon" />
                    {{ balance }} MOEDAS DISPONÍVEIS
                </PixelBadge>
                <PixelBadge tone="blue">{{ students.length }} ALUNOS</PixelBadge>
            </div>
        </div>
    </div>
</template>

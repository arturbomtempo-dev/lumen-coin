<script setup lang="ts">
import { sendCoins } from '@/modules/coin-transfer/services/coin-transfer.service';
import {
    getStudentsByInstitution,
    type StudentSummary,
} from '@/modules/teacher/services/teacher.service';
import { useTeacherStore } from '@/modules/teacher/stores/teacher.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { PhPaperPlaneTilt } from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref } from 'vue';
import { toast } from 'vue-sonner';

const teacherStore = useTeacherStore();
const { balance, institutionId } = storeToRefs(teacherStore);

const students = ref<StudentSummary[]>([]);
const studentId = ref('');
const amount = ref<number | ''>(50);
const message = ref('');
const errors = ref<Record<string, string>>({});
const isSubmitting = ref(false);
const isLoadingStudents = ref(true);

onMounted(async () => {
    if (!teacherStore.isLoaded) {
        await teacherStore.loadProfile();
    }
    if (institutionId.value) {
        const { data } = await getStudentsByInstitution(institutionId.value);
        students.value = data;
    }
    isLoadingStudents.value = false;
});

async function submit(e: Event) {
    e.preventDefault();
    errors.value = {};
    const v = Number(amount.value);
    if (!v || v <= 0) {
        errors.value.amount = 'Informe um valor válido.';
        return;
    }
    if (v > balance.value) {
        errors.value.amount = `Saldo insuficiente. Disponível: ${balance.value} moedas.`;
        return;
    }
    if (!message.value.trim()) {
        errors.value.message = 'O motivo é obrigatório.';
        return;
    }
    if (!studentId.value) {
        errors.value.studentId = 'Selecione um aluno.';
        return;
    }

    isSubmitting.value = true;
    try {
        await sendCoins({ studentId: studentId.value, amount: v, message: message.value });
        await teacherStore.loadProfile();
        const student = students.value.find((s) => s.id === studentId.value);
        toast.success(`+${v} moedas enviadas para ${student?.name ?? 'aluno'}!`);
        studentId.value = '';
        message.value = '';
        amount.value = 50;
        errors.value = {};
    } catch {
        errors.value.submit = 'Erro ao enviar moedas. Tente novamente.';
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <div class="space-y-6 max-w-5xl mx-auto">
        <div>
            <div class="font-pixel text-[10px] text-primary mb-1">▶ ENVIAR RECONHECIMENTO</div>
            <h1 class="font-pixel text-xl flex items-center gap-2">
                <PhPaperPlaneTilt weight="fill" class="text-primary" />
                ENVIAR MOEDAS
            </h1>
        </div>

        <PixelCard class="p-4 flex items-center gap-4">
            <CoinIcon :size="32" class="animate-bob shrink-0" />
            <div>
                <div class="font-pixel text-[9px] text-muted-foreground mb-1">SEU SALDO</div>
                <div class="font-pixel text-3xl text-primary leading-none">
                    {{ balance.toLocaleString('pt-BR') }}
                </div>
            </div>
        </PixelCard>

        <PixelCard class="p-6">
            <div class="font-pixel text-sm mb-6 pb-3 border-b-2 border-border">
                FORMULÁRIO DE ENVIO
            </div>

            <form class="space-y-5" @submit.prevent="submit">
                <div>
                    <label class="font-pixel text-[9px] text-muted-foreground block mb-2"
                        >ALUNO</label
                    >
                    <div
                        v-if="isLoadingStudents"
                        class="font-sans text-sm text-muted-foreground py-2"
                    >
                        Carregando alunos...
                    </div>
                    <div v-else>
                        <select
                            v-model="studentId"
                            class="w-full bg-background border-2 border-border px-3 py-2.5 font-display text-base focus:outline-none focus:border-primary cursor-pointer"
                        >
                            <option value="" disabled>Selecione um aluno</option>
                            <option v-for="s in students" :key="s.id" :value="s.id">
                                {{ s.name }}
                            </option>
                        </select>
                        <p
                            v-if="errors.studentId"
                            class="font-sans text-xs mt-1"
                            style="color: hsl(var(--destructive))"
                        >
                            {{ errors.studentId }}
                        </p>
                    </div>
                </div>

                <div>
                    <label class="font-pixel text-[9px] text-muted-foreground block mb-2">
                        VALOR (MOEDAS)
                    </label>
                    <PixelInput v-model="amount" type="number" placeholder="Ex: 50" />
                    <p
                        v-if="errors.amount"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.amount }}
                    </p>
                    <div class="font-pixel text-[8px] text-muted-foreground mt-1.5">
                        MÁX: {{ balance.toLocaleString('pt-BR') }} MOEDAS
                    </div>
                </div>

                <div>
                    <label class="font-pixel text-[9px] text-muted-foreground block mb-2">
                        MOTIVO <span class="text-destructive">*</span>
                    </label>
                    <textarea
                        v-model="message"
                        placeholder="Ex: Excelência na apresentação do projeto"
                        rows="3"
                        class="w-full bg-background border-2 border-border px-3 py-2.5 font-sans text-sm focus:outline-none focus:border-primary resize-none"
                    />
                    <p
                        v-if="errors.message"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.message }}
                    </p>
                </div>

                <div
                    v-if="errors.submit"
                    class="font-sans text-xs"
                    style="color: hsl(var(--destructive))"
                >
                    {{ errors.submit }}
                </div>

                <PixelButton
                    variant="success"
                    class="w-full"
                    type="submit"
                    :disabled="isSubmitting"
                >
                    <PhPaperPlaneTilt weight="fill" />
                    {{ isSubmitting ? 'ENVIANDO...' : 'ENVIAR RECONHECIMENTO' }}
                </PixelButton>
            </form>
        </PixelCard>
    </div>
</template>

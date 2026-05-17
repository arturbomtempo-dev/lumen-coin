<script setup lang="ts">
import { sendCoins } from '@/modules/coin-transfer/services/coin-transfer.service';
import {
    getStudentsByInstitution,
    type StudentSummary,
} from '@/modules/teacher/services/teacher.service';
import { useTeacherStore } from '@/modules/teacher/stores/teacher.store';
import { storeToRefs } from 'pinia';
import { onMounted, ref } from 'vue';
import { toast } from 'vue-sonner';

const teacherStore = useTeacherStore();
const { balance, institutionId } = storeToRefs(teacherStore);

const students = ref<StudentSummary[]>([]);
const studentId = ref('');
const amount = ref<number | ''>(50);
const message = ref('');
const errorMessage = ref('');
const isSubmitting = ref(false);
const isLoadingStudents = ref(true);

onMounted(async () => {
    if (!teacherStore.isLoaded) {
        await teacherStore.loadProfile();
    }
    if (institutionId.value) {
        const { data } = await getStudentsByInstitution(institutionId.value);
        students.value = data;
        if (data.length > 0) studentId.value = data[0].id;
    }
    isLoadingStudents.value = false;
});

async function submit(e: Event) {
    e.preventDefault();
    errorMessage.value = '';
    const v = Number(amount.value);
    if (!v || v <= 0) return void (errorMessage.value = 'Informe um valor válido.');
    if (v > balance.value)
        return void (errorMessage.value = `Saldo insuficiente. Disponível: ${balance.value} moedas.`);
    if (!message.value.trim()) return void (errorMessage.value = 'O motivo é obrigatório.');
    if (!studentId.value) return void (errorMessage.value = 'Selecione um aluno.');

    isSubmitting.value = true;
    try {
        await sendCoins({ studentId: studentId.value, amount: v, message: message.value });
        await teacherStore.loadProfile();
        const student = students.value.find((s) => s.id === studentId.value);
        toast.success(`+${v} moedas enviadas para ${student?.name ?? 'aluno'}!`);
        message.value = '';
        amount.value = 50;
    } catch {
        errorMessage.value = 'Erro ao enviar moedas. Tente novamente.';
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <div class="space-y-8">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ ENVIAR RECONHECIMENTO</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhPaperPlaneTilt weight="fill" class="pixel-icon" /> ENVIAR MOEDAS
            </h1>
        </div>

        <PixelCard class="p-4 flex items-center gap-3">
            <CoinIcon :size="28" class="animate-bob" />
            <div>
                <div class="font-pixel text-[10px] text-muted-foreground">SEU SALDO</div>
                <div class="font-pixel text-2xl text-primary">
                    {{ balance.toLocaleString('pt-BR') }}
                </div>
            </div>
        </PixelCard>

        <PixelCard class="p-6">
            <div class="font-pixel text-sm mb-4">FORMULÁRIO DE ENVIO</div>
            <form class="space-y-4" @submit.prevent="submit">
                <div>
                    <label class="font-pixel text-[10px] block mb-2">ALUNO</label>
                    <div v-if="isLoadingStudents" class="font-sans text-sm text-muted-foreground">
                        Carregando alunos...
                    </div>
                    <select
                        v-else
                        v-model="studentId"
                        class="w-full bg-input border-2 border-border px-3 py-2 font-display text-lg focus:outline-none"
                    >
                        <option v-for="s in students" :key="s.id" :value="s.id">
                            {{ s.name }}
                        </option>
                    </select>
                </div>
                <div>
                    <label class="font-pixel text-[10px] block mb-2">VALOR (MOEDAS)</label>
                    <PixelInput
                        v-model="amount"
                        type="number"
                        :min="1"
                        :max="balance"
                        placeholder="Ex: 50"
                    />
                </div>
                <div>
                    <label class="font-pixel text-[10px] block mb-2"
                        >MOTIVO <span class="text-secondary">*</span></label
                    >
                    <PixelInput
                        v-model="message"
                        placeholder="Ex: Excelência na apresentação do projeto"
                    />
                </div>
                <div v-if="errorMessage" class="font-pixel text-[10px] text-destructive">
                    ⚠ {{ errorMessage }}
                </div>
                <PixelButton
                    variant="success"
                    class="w-full"
                    type="submit"
                    :disabled="isSubmitting"
                >
                    <PhPaperPlaneTilt weight="fill" class="pixel-icon" />
                    {{ isSubmitting ? 'ENVIANDO...' : 'ENVIAR RECONHECIMENTO' }}
                </PixelButton>
            </form>
        </PixelCard>
    </div>
</template>

<template>
    <div>
        <h2 class="text-2xl font-bold mb-6">Enviar Moedas</h2>
        <p class="text-muted">Selecione um aluno e envie moedas como reconhecimento.</p>
    </div>
</template>

<script setup lang="ts">
import {
    getReceivedTransfers,
    type CoinTransferResponse,
} from '@/modules/coin-transfer/services/coin-transfer.service';
import { useStudentStore } from '@/modules/student/stores/student.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import { PhReceipt, PhTicket } from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref } from 'vue';

const store = useStudentStore();
const { coupons } = storeToRefs(store);

const transfers = ref<CoinTransferResponse[]>([]);
const isLoading = ref(true);

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
        const { data } = await getReceivedTransfers();
        transfers.value = data;
    } finally {
        isLoading.value = false;
    }
});
</script>

<template>
    <div class="space-y-8 max-w-5xl mx-auto">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ HISTÓRICO</div>
            <h1 class="font-pixel text-md md:text-xl mt-2 flex items-center gap-2">
                <PhReceipt weight="fill" class="pixel-icon" /> EXTRATO DE MOEDAS
            </h1>
        </div>

        <PixelCard class="overflow-hidden">
            <div
                class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] grid grid-cols-[1fr_auto] sm:grid-cols-4 gap-6"
            >
                <span>PROFESSOR</span>
                <span class="hidden sm:block">MOTIVO</span>
                <span class="hidden sm:block">DATA</span>
                <span>VALOR</span>
            </div>
            <div v-if="isLoading" class="p-6 font-sans text-sm text-muted-foreground text-center">
                Carregando...
            </div>
            <div
                v-else-if="transfers.length === 0"
                class="p-6 font-sans text-sm text-muted-foreground text-center"
            >
                Nenhuma transação registrada.
            </div>
            <div
                v-for="t in transfers"
                :key="t.id"
                class="px-4 py-3 border-t-2 border-border grid grid-cols-[1fr_auto] sm:grid-cols-4 gap-6 items-center"
            >
                <div>
                    <div class="font-pixel text-[10px]">{{ t.senderName }}</div>
                    <div class="font-sans text-xs text-muted-foreground sm:hidden mt-1">
                        {{ t.message }}
                    </div>
                    <div class="font-sans text-xs text-muted-foreground sm:hidden">
                        {{ formatDate(t.sentAt) }}
                    </div>
                </div>
                <div class="hidden sm:block font-sans text-sm">{{ t.message }}</div>
                <div class="hidden sm:block font-pixel text-[9px] text-muted-foreground">
                    {{ formatDate(t.sentAt) }}
                </div>
                <div class="font-pixel text-sm flex items-center gap-1 text-success">
                    +<CoinIcon :size="12" /> {{ t.amount }}
                </div>
            </div>
        </PixelCard>

        <div>
            <h2 class="font-pixel text-sm md:text-lg mb-4 flex items-center gap-2">
                <PhTicket weight="fill" class="pixel-icon text-secondary" /> MEUS CUPONS
            </h2>
            <PixelCard
                v-if="coupons.length === 0"
                class="p-6 text-center font-sans text-muted-foreground text-sm"
            >
                Você ainda não resgatou nenhuma vantagem. Vá ao marketplace e desbloqueie
                recompensas!
            </PixelCard>
            <div v-else class="grid sm:grid-cols-2 gap-4">
                <PixelCard v-for="c in coupons" :key="c.id" class="p-4">
                    <div class="flex items-center justify-between">
                        <div class="font-pixel text-xs">{{ c.benefit.toUpperCase() }}</div>
                        <PixelBadge :tone="c.used ? 'red' : 'green'">{{
                            c.used ? 'USADO' : 'VÁLIDO'
                        }}</PixelBadge>
                    </div>
                    <div
                        class="mt-3 border-2 border-dashed border-border bg-hud text-hud-foreground p-3"
                    >
                        <div class="font-pixel text-[9px] opacity-70">CÓDIGO</div>
                        <div class="font-pixel text-sm break-all">{{ c.code }}</div>
                    </div>
                    <div class="font-sans text-xs text-muted-foreground mt-2">
                        Gerado em {{ c.date }}
                    </div>
                </PixelCard>
            </div>
        </div>
    </div>
</template>

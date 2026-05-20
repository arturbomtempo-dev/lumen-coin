<script setup lang="ts">
import {
    getSentTransfers,
    type CoinTransferResponse,
} from '@/modules/coin-transfer/services/coin-transfer.service';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import { PhReceipt } from '@phosphor-icons/vue';
import { onMounted, ref } from 'vue';

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
        const { data } = await getSentTransfers();
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
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhReceipt weight="fill" class="pixel-icon" /> EXTRATO DE ENVIOS
            </h1>
        </div>

        <PixelCard class="overflow-hidden">
            <div
                class="bg-hud text-hud-foreground px-4 py-2 font-pixel text-[10px] grid grid-cols-[1fr_2fr_auto_auto] gap-2"
            >
                <span>ALUNO</span>
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
                Nenhuma transferência realizada ainda.
            </div>
            <div
                v-for="t in transfers"
                :key="t.id"
                class="px-4 py-3 border-t-2 border-border grid grid-cols-[1fr_auto] sm:grid-cols-[1fr_2fr_auto_auto] gap-2 items-center"
            >
                <div>
                    <div class="font-pixel text-[10px]">{{ t.recipientName }}</div>
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
    </div>
</template>

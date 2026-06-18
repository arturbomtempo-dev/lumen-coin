<script setup lang="ts">
import {
    scanRedemption,
    validateCoupon,
    type ValidateBenefitRedemptionResponse,
} from '@/modules/company/services/benefit-redemption.service';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import {
    PhCamera,
    PhCheckCircle,
    PhDeviceMobile,
    PhQrCode,
    PhWarning,
    PhXCircle,
} from '@phosphor-icons/vue';
import { Html5Qrcode } from 'html5-qrcode';
import { nextTick, onUnmounted, ref } from 'vue';
import { toast } from 'vue-sonner';

type ScanState = 'idle' | 'scanning' | 'validating' | 'validated' | 'confirmed' | 'error';

const scanState = ref<ScanState>('idle');
const scanError = ref('');
const validatedRedemption = ref<ValidateBenefitRedemptionResponse | null>(null);
const isConfirming = ref(false);
const scannedCode = ref('');

let scanner: Html5Qrcode | null = null;

async function startScanner() {
    scanState.value = 'scanning';
    scanError.value = '';
    validatedRedemption.value = null;
    scannedCode.value = '';

    await nextTick();

    scanner = new Html5Qrcode('qr-reader');

    try {
        await scanner.start(
            { facingMode: 'environment' },
            { fps: 10, qrbox: { width: 240, height: 240 } },
            onScanSuccess,
            undefined
        );
    } catch {
        scanState.value = 'error';
        scanError.value =
            'Não foi possível acessar a câmera. Verifique as permissões do navegador.';
        scanner = null;
    }
}

async function stopScanner() {
    if (scanner) {
        try {
            await scanner.stop();
            scanner.clear();
        } catch {}
        scanner = null;
    }
}

async function onScanSuccess(decodedText: string) {
    await stopScanner();
    scanState.value = 'validating';

    const code = decodedText.trim().toUpperCase();
    scannedCode.value = code;

    try {
        const res = await validateCoupon(code);
        validatedRedemption.value = res.data;
        scanState.value = 'validated';
    } catch (err: unknown) {
        const status = (err as { response?: { status?: number } })?.response?.status;
        scanState.value = 'error';
        if (status === 404) {
            scanError.value = 'QR Code inválido ou não pertence a esta empresa.';
        } else if (status === 400) {
            scanError.value = 'Este QR Code já foi utilizado anteriormente.';
        } else {
            scanError.value = 'Erro ao validar o QR Code. Tente novamente.';
        }
    }
}

async function handleConfirm() {
    if (!validatedRedemption.value || isConfirming.value) return;
    isConfirming.value = true;
    try {
        await scanRedemption(validatedRedemption.value.couponCode);
        scanState.value = 'confirmed';
        toast.success('Resgate confirmado com sucesso!');
    } catch {
        scanState.value = 'error';
        scanError.value = 'Erro ao confirmar o resgate. Tente novamente.';
    } finally {
        isConfirming.value = false;
    }
}

function reset() {
    stopScanner();
    scanState.value = 'idle';
    scanError.value = '';
    validatedRedemption.value = null;
    scannedCode.value = '';
    isConfirming.value = false;
}

onUnmounted(() => {
    stopScanner();
});
</script>

<template>
    <PixelCard class="p-6">
        <div class="font-pixel text-sm mb-4 flex items-center gap-2">
            <PhQrCode weight="fill" class="pixel-icon" /> ESCANEAR QR CODE
        </div>

        <div v-if="scanState === 'idle'" class="space-y-4">
            <p class="font-sans text-sm" style="color: hsl(var(--muted-foreground))">
                Aponte a câmera para o QR Code do cupom do aluno para validar e confirmar o resgate
                automaticamente.
            </p>
            <div
                class="hidden md:flex border-2 items-start gap-2 p-3"
                style="
                    border-color: hsl(var(--border));
                    background: hsl(var(--muted-foreground) / 0.06);
                "
            >
                <PhDeviceMobile
                    weight="fill"
                    :size="16"
                    class="pixel-icon shrink-0 mt-0.5"
                    style="color: hsl(var(--muted-foreground))"
                />
                <p class="font-pixel text-[8px]" style="color: hsl(var(--muted-foreground))">
                    CASO NÃO CONSIGA ACESSAR O QR CODE CORRETAMENTE PELO COMPUTADOR, RECOMENDA-SE
                    ACESSAR PELO CELULAR.
                </p>
            </div>
            <PixelButton variant="primary" class="w-full" @click="startScanner">
                <PhCamera weight="fill" class="pixel-icon mr-2" />
                INICIAR CÂMERA
            </PixelButton>
        </div>

        <div v-if="scanState === 'scanning'" class="space-y-4">
            <div
                id="qr-reader"
                class="w-full border-2 border-border"
                style="min-height: 300px"
            ></div>
            <p
                class="font-pixel text-[9px] text-center"
                style="color: hsl(var(--muted-foreground))"
            >
                APONTE PARA O QR CODE DO CUPOM
            </p>
            <PixelButton variant="secondary" class="w-full" @click="reset"> CANCELAR </PixelButton>
        </div>

        <div v-if="scanState === 'validating'" class="space-y-4">
            <div class="flex items-center justify-center gap-3 py-8">
                <span class="font-pixel text-[10px]" style="color: hsl(var(--muted-foreground))"
                    >VALIDANDO QR CODE...</span
                >
            </div>
        </div>

        <div v-if="scanState === 'error'" class="space-y-4">
            <div
                class="border-2 border-border p-4 flex items-start gap-3"
                style="
                    border-color: hsl(var(--destructive));
                    background: hsl(var(--destructive) / 0.08);
                "
            >
                <PhXCircle
                    weight="fill"
                    class="pixel-icon shrink-0 mt-0.5"
                    :size="20"
                    style="color: hsl(var(--destructive))"
                />
                <div class="space-y-1">
                    <div class="font-pixel text-[9px]" style="color: hsl(var(--destructive))">
                        ERRO
                    </div>
                    <div class="font-sans text-sm">{{ scanError }}</div>
                </div>
            </div>
            <PixelButton variant="secondary" class="w-full" @click="reset">
                TENTAR NOVAMENTE
            </PixelButton>
        </div>

        <div v-if="scanState === 'confirmed'" class="space-y-4">
            <div
                class="border-2 border-border p-4 flex items-start gap-3"
                style="border-color: hsl(var(--success)); background: hsl(var(--success) / 0.08)"
            >
                <PhCheckCircle
                    weight="fill"
                    class="pixel-icon shrink-0 mt-0.5"
                    :size="20"
                    style="color: hsl(var(--success))"
                />
                <div class="space-y-1">
                    <div class="font-pixel text-[9px]" style="color: hsl(var(--success))">
                        RESGATE CONFIRMADO!
                    </div>
                    <div class="font-sans text-sm">
                        O resgate foi registrado com sucesso via QR Code.
                    </div>
                </div>
            </div>
            <PixelButton variant="secondary" class="w-full" @click="reset">
                ESCANEAR NOVO QR CODE
            </PixelButton>
        </div>

        <div v-if="scanState === 'validated' && validatedRedemption" class="space-y-4">
            <div
                class="border-2 border-border p-4 flex items-start gap-3"
                style="border-color: hsl(var(--success)); background: hsl(var(--success) / 0.08)"
            >
                <PhCheckCircle
                    weight="fill"
                    class="pixel-icon shrink-0 mt-0.5"
                    :size="20"
                    style="color: hsl(var(--success))"
                />
                <div class="space-y-1 text-sm">
                    <div class="font-pixel text-[9px]" style="color: hsl(var(--success))">
                        QR CODE VÁLIDO
                    </div>
                    <div class="font-display text-lg">
                        <span class="font-sans text-xs text-muted-foreground">Aluno: </span>
                        {{ validatedRedemption.studentName }}
                    </div>
                    <div class="font-display text-lg">
                        <span class="font-sans text-xs text-muted-foreground">Vantagem: </span>
                        {{ validatedRedemption.benefitName }}
                    </div>
                    <div class="font-display text-lg flex items-center gap-1">
                        <span class="font-sans text-xs text-muted-foreground">Custo: </span>
                        <CoinIcon :size="12" />
                        {{ validatedRedemption.coinsSpent }}
                    </div>
                </div>
            </div>

            <div
                class="border-2 border-border p-3 flex items-center gap-2"
                style="background: hsl(var(--warning) / 0.1); border-color: hsl(var(--warning))"
            >
                <PhWarning
                    weight="fill"
                    :size="16"
                    style="color: hsl(var(--warning))"
                    class="shrink-0"
                />
                <p class="font-pixel text-[8px]" style="color: hsl(var(--warning))">
                    AO CONFIRMAR, O RESGATE SERÁ MARCADO COMO UTILIZADO.
                </p>
            </div>

            <div class="flex gap-3">
                <PixelButton variant="secondary" class="flex-1" @click="reset">
                    CANCELAR
                </PixelButton>
                <PixelButton
                    variant="success"
                    class="flex-1"
                    :disabled="isConfirming"
                    @click="handleConfirm"
                >
                    {{ isConfirming ? 'CONFIRMANDO...' : 'CONFIRMAR RESGATE' }}
                </PixelButton>
            </div>
        </div>
    </PixelCard>
</template>

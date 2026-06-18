<script setup lang="ts">
import {
    confirmRedemption,
    validateCoupon,
    type ValidateBenefitRedemptionResponse,
} from '@/modules/institution/services/benefit-redemption.service';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { PhCheckCircle, PhStar, PhTicket } from '@phosphor-icons/vue';
import { ref } from 'vue';

const couponCode = ref('');
const couponError = ref('');
const validatedRedemption = ref<ValidateBenefitRedemptionResponse | null>(null);
const usageNotes = ref('');
const isValidating = ref(false);
const isConfirming = ref(false);
const redemptionConfirmed = ref(false);

function resetForm() {
    couponCode.value = '';
    couponError.value = '';
    validatedRedemption.value = null;
    usageNotes.value = '';
    redemptionConfirmed.value = false;
}

async function validate() {
    const code = couponCode.value.trim().toUpperCase();
    if (!code) {
        couponError.value = 'Digite o código do cupom.';
        return;
    }
    couponError.value = '';
    validatedRedemption.value = null;
    usageNotes.value = '';
    isValidating.value = true;
    try {
        const res = await validateCoupon(code);
        validatedRedemption.value = res.data;
    } catch (err: unknown) {
        const status = (err as { response?: { status?: number } })?.response?.status;
        if (status === 404) {
            couponError.value = 'Código não encontrado ou não pertence a esta instituição.';
        } else if (status === 400) {
            couponError.value = 'Este código já foi utilizado.';
        } else {
            couponError.value = 'Erro ao validar o código. Tente novamente.';
        }
    } finally {
        isValidating.value = false;
    }
}

async function handleConfirmRedemption() {
    if (!validatedRedemption.value || !usageNotes.value.trim() || isConfirming.value) return;
    isConfirming.value = true;
    try {
        await confirmRedemption(validatedRedemption.value.couponCode, usageNotes.value.trim());
        redemptionConfirmed.value = true;
        validatedRedemption.value = null;
        usageNotes.value = '';
        couponCode.value = '';
        couponError.value = '';
    } catch {
    } finally {
        isConfirming.value = false;
    }
}
</script>

<template>
    <div class="space-y-6">
        <div>
            <div class="font-pixel text-[10px] text-primary">▶ RESGATES</div>
            <h1 class="font-pixel text-xl mt-2 flex items-center gap-2">
                <PhTicket weight="fill" class="pixel-icon" /> VALIDAR CUPOM
            </h1>
        </div>

        <PixelCard class="p-6 max-w-xl">
            <div class="font-pixel text-sm mb-4 flex items-center gap-2">
                <PhTicket weight="fill" class="pixel-icon" /> CÓDIGO DO CUPOM
            </div>

            <div class="space-y-4">
                <div>
                    <label class="font-pixel text-[10px] block mb-2">INSIRA O CÓDIGO</label>
                    <PixelInput
                        v-model="couponCode"
                        placeholder="LUMEN-XXXXX-XXX"
                        style="text-transform: uppercase"
                        @keyup.enter="validate"
                    />
                    <p
                        v-if="couponError"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ couponError }}
                    </p>
                </div>

                <PixelButton
                    variant="info"
                    class="w-full"
                    :disabled="isValidating"
                    @click="validate"
                >
                    {{ isValidating ? 'VERIFICANDO...' : 'VERIFICAR CÓDIGO' }}
                </PixelButton>

                <div v-if="validatedRedemption" class="space-y-4">
                    <div
                        class="border-2 border-border p-4 flex items-start gap-3"
                        style="
                            border-color: hsl(var(--success));
                            background: hsl(var(--success) / 0.08);
                        "
                    >
                        <PhCheckCircle
                            weight="fill"
                            class="pixel-icon shrink-0 mt-0.5"
                            :size="20"
                            style="color: hsl(var(--success))"
                        />
                        <div class="space-y-1 text-sm">
                            <div class="font-pixel text-[9px]" style="color: hsl(var(--success))">
                                CÓDIGO VÁLIDO
                            </div>
                            <div class="font-display text-lg">
                                <span class="font-sans text-xs text-muted-foreground">Aluno: </span>
                                {{ validatedRedemption.studentName }}
                            </div>
                            <div class="font-display text-lg">
                                <span class="font-sans text-xs text-muted-foreground"
                                    >Vantagem:
                                </span>
                                {{ validatedRedemption.benefitName }}
                            </div>
                            <div class="font-display text-lg flex items-center gap-1">
                                <span class="font-sans text-xs text-muted-foreground">Custo: </span>
                                <CoinIcon :size="12" />
                                {{ validatedRedemption.coinsSpent }}
                            </div>
                        </div>
                    </div>

                    <div>
                        <label class="font-pixel text-[10px] block mb-2">
                            INSTRUÇÕES DE USO DA VANTAGEM
                        </label>
                        <textarea
                            v-model="usageNotes"
                            rows="4"
                            placeholder="Descreva como o aluno pode utilizar esta vantagem..."
                            class="w-full bg-input text-foreground border-2 border-border px-3 py-2 font-display text-lg shadow-[inset_2px_2px_0_0_hsl(var(--border)/0.25)] focus:outline-none focus:ring-2 focus:ring-ring resize-none"
                        ></textarea>
                        <p class="font-sans text-xs text-muted-foreground mt-1">
                            Esta mensagem será enviada ao aluno por e-mail.
                        </p>
                    </div>

                    <div class="flex gap-3">
                        <PixelButton variant="ghost" class="flex-1" @click="resetForm">
                            CANCELAR
                        </PixelButton>
                        <PixelButton
                            variant="success"
                            class="flex-1"
                            :disabled="isConfirming || !usageNotes.trim()"
                            @click="handleConfirmRedemption"
                        >
                            <PhCheckCircle weight="fill" class="pixel-icon" />
                            {{ isConfirming ? 'CONFIRMANDO...' : 'CONFIRMAR RESGATE' }}
                        </PixelButton>
                    </div>
                </div>

                <div
                    v-if="redemptionConfirmed"
                    class="border-2 border-border p-4"
                    style="
                        border-color: hsl(var(--success));
                        background: hsl(var(--success) / 0.08);
                    "
                >
                    <div class="flex items-center gap-3">
                        <PhStar
                            weight="fill"
                            class="pixel-icon shrink-0"
                            :size="20"
                            style="color: hsl(var(--success))"
                        />
                        <div>
                            <div class="font-pixel text-[9px]" style="color: hsl(var(--success))">
                                RESGATE CONFIRMADO!
                            </div>
                            <div class="font-sans text-sm mt-0.5 text-muted-foreground">
                                O e-mail com as instruções foi enviado ao aluno.
                            </div>
                        </div>
                    </div>
                    <div class="mt-3">
                        <PixelButton variant="ghost" size="sm" class="w-full" @click="resetForm">
                            VALIDAR OUTRO CÓDIGO
                        </PixelButton>
                    </div>
                </div>
            </div>
        </PixelCard>
    </div>
</template>

<script setup lang="ts">
import { useStudentStore } from '@/modules/student/stores/student.store';
import { useGamification } from '@/shared/composables/useGamification';
import CharacterAvatar from '@/shared/components/CharacterAvatar.vue';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelBadge from '@/shared/components/PixelBadge.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import XPBar from '@/shared/components/XPBar.vue';
import { PhTrophy } from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';

const store = useStudentStore();
const { balance, name, character, courseName, institutionName, transactions } = storeToRefs(store);
const { currentLevel, progressPercent, medalsWithStatus } = useGamification(balance, transactions);
</script>

<template>
    <div class="space-y-10">
        <section class="grid lg:grid-cols-[1.1fr_1fr] gap-6 items-start">
            <PixelCard class="p-6 crt-scanlines">
                <div class="flex items-center justify-between">
                    <div>
                        <div class="font-pixel text-[10px] text-primary">▶ CARTEIRA DO JOGADOR</div>
                        <div class="font-pixel text-xl mt-2">{{ name.toUpperCase() }}</div>
                        <div class="font-sans text-sm text-muted-foreground">
                            {{ courseName }} · {{ institutionName }}
                        </div>
                    </div>
                    <div class="border-2 border-border bg-hud p-1">
                        <CharacterAvatar :character="character" :size="64" />
                    </div>
                </div>

                <div class="mt-6 border-2 border-border bg-hud text-hud-foreground p-5 text-center">
                    <div class="font-pixel text-[10px] mb-2">SALDO ATUAL</div>
                    <div class="flex items-center justify-center gap-3">
                        <CoinIcon :size="36" class="animate-bob" />
                        <span class="font-pixel text-4xl md:text-5xl animate-coin-flash">{{
                            balance.toLocaleString('pt-BR')
                        }}</span>
                    </div>
                    <div class="font-display text-sm mt-2 opacity-80 tracking-wider">
                        LUMEN COINS
                    </div>
                </div>

                <div class="mt-5 grid grid-cols-2 gap-4">
                    <div class="border-2 border-border bg-card p-3">
                        <div class="font-pixel text-[9px] text-muted-foreground">NÍVEL</div>
                        <div class="font-pixel text-md md:text-2xl mt-1 flex items-center gap-2">
                            <PhTrophy weight="fill" class="pixel-icon text-primary" /> LV
                            {{ currentLevel.level }}
                        </div>
                        <div class="font-pixel text-[9px] text-muted-foreground mt-1">
                            {{ currentLevel.name.toUpperCase() }}
                        </div>
                    </div>
                    <div class="border-2 border-border bg-card p-3">
                        <div class="font-pixel text-[9px] text-muted-foreground">PRÓXIMO NÍVEL</div>
                        <div class="mt-2"><XPBar :value="progressPercent" /></div>
                        <div class="font-pixel text-[9px] text-muted-foreground mt-1">
                            {{ progressPercent }}%
                        </div>
                    </div>
                </div>
            </PixelCard>

            <PixelCard class="p-6">
                <div class="font-pixel text-[10px] text-primary">▶ CONQUISTAS</div>
                <div class="font-pixel text-sm mt-1 mb-4">MEDALHAS</div>
                <div class="grid grid-cols-2 gap-3">
                    <div
                        v-for="medal in medalsWithStatus"
                        :key="medal.id"
                        class="border-2 border-border p-3"
                        :class="medal.unlocked ? 'bg-card' : 'bg-muted opacity-50'"
                    >
                        <PixelBadge :tone="medal.tone"
                            >{{ medal.unlocked ? '✓' : '?' }}
                            {{ medal.name.toUpperCase() }}</PixelBadge
                        >
                        <p class="font-sans text-xs text-foreground/75 mt-2">
                            {{ medal.description }}
                        </p>
                    </div>
                </div>
            </PixelCard>
        </section>
    </div>
</template>

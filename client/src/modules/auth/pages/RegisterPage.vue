<script setup lang="ts">
import { useRouter } from 'vue-router';
import { PhArrowLeft, PhGraduationCap, PhStorefront } from '@phosphor-icons/vue';
import PixelCard from '@/shared/components/PixelCard.vue';

const router = useRouter();
</script>

<template>
    <main class="min-h-screen flex items-center justify-center px-4 py-10">
        <div class="w-full max-w-xl">
            <button
                class="mb-5 flex items-center gap-3 font-pixel text-[9px] transition-all"
                type="button"
                @click="router.push({ name: 'home' })"
            >
                <div
                    class="w-7 h-7 border-2 border-border bg-primary flex items-center justify-center shadow-[2px_2px_0px_hsl(var(--border))]"
                >
                    <PhArrowLeft :size="14" weight="bold" />
                </div>
                <span>VOLTAR À TELA INICIAL</span>
            </button>

            <PixelCard class="p-6 md:p-8">
                <div class="font-pixel text-[9px] text-primary mb-2">▶ CRIAR CONTA</div>
                <h1 class="font-pixel text-xl mb-8">ESCOLHA SEU PERFIL</h1>

                <div class="grid sm:grid-cols-2 gap-4">
                    <button
                        class="border-2 border-border bg-card p-6 text-left transition-all hover:-translate-y-1 hover:shadow-[4px_4px_0_0_hsl(var(--border))] active:translate-y-0"
                        type="button"
                        @click="router.push({ name: 'register-student' })"
                    >
                        <div
                            class="w-14 h-14 border-2 border-border bg-hud flex items-center justify-center mb-4"
                        >
                            <PhGraduationCap
                                weight="fill"
                                :size="32"
                                class="text-primary"
                            />
                        </div>
                        <div class="font-pixel text-sm mb-2">ALUNO</div>
                        <p class="font-sans text-xs text-muted-foreground">
                            Estudante de instituição parceira. Acumule moedas e troque por vantagens.
                        </p>
                    </button>

                    <button
                        class="border-2 border-border bg-card p-6 text-left transition-all hover:-translate-y-1 hover:shadow-[4px_4px_0_0_hsl(var(--border))] active:translate-y-0"
                        type="button"
                        @click="router.push({ name: 'register-company' })"
                    >
                        <div
                            class="w-14 h-14 border-2 border-border bg-hud flex items-center justify-center mb-4"
                        >
                            <PhStorefront
                                weight="fill"
                                :size="32"
                                class="text-accent"
                            />
                        </div>
                        <div class="font-pixel text-sm mb-2">EMPRESA PARCEIRA</div>
                        <p class="font-sans text-xs text-muted-foreground">
                            Empresa que oferece vantagens e benefícios aos estudantes da rede.
                        </p>
                    </button>
                </div>

                <p class="font-pixel text-[9px] text-center text-muted-foreground mt-6">
                    JÁ TEM CONTA?
                    <button
                        class="text-primary underline underline-offset-2"
                        type="button"
                        @click="router.push({ name: 'login' })"
                    >
                        FAZER LOGIN
                    </button>
                </p>
            </PixelCard>
        </div>
    </main>
</template>

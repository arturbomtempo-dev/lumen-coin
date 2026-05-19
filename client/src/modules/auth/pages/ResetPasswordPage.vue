<script setup lang="ts">
import { resetPasswordRequest } from '@/modules/auth/services/auth.service';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { PhArrowLeft, PhEye, PhEyeSlash, PhLock } from '@phosphor-icons/vue';
import { ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { toast } from 'vue-sonner';
import Cloud from './_components/Cloud.vue';

const route = useRoute();
const router = useRouter();

const token = String(route.query.token ?? '');
const newPassword = ref('');
const confirmPassword = ref('');
const showPwd = ref(false);
const showConfirm = ref(false);
const isSubmitting = ref(false);

async function handleSubmit(e: Event) {
    e.preventDefault();
    if (newPassword.value.length < 6) {
        toast.error('A senha deve ter pelo menos 6 caracteres.');
        return;
    }
    if (newPassword.value !== confirmPassword.value) {
        toast.error('As senhas não coincidem.');
        return;
    }
    if (!token) {
        toast.error('Token inválido. Solicite um novo link de recuperação.');
        return;
    }
    isSubmitting.value = true;
    try {
        await resetPasswordRequest(token, newPassword.value);
        toast.success('Senha redefinida com sucesso!');
        router.push({ name: 'login' });
    } catch {
        toast.error('Link inválido ou expirado. Solicite um novo link de recuperação.');
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <div class="min-h-screen flex flex-col">
        <main class="flex-1 relative overflow-hidden bg-login-bg transition-colors duration-300">
            <div class="absolute inset-0 bg-info/10" />
            <div class="absolute top-10 left-8">
                <Cloud />
            </div>
            <div class="absolute top-24 right-16 opacity-80">
                <Cloud />
            </div>
            <div class="absolute top-40 left-1/3 opacity-70">
                <Cloud />
            </div>
            <div
                class="absolute bottom-24 left-0 right-0 h-32 opacity-30"
                :style="{
                    backgroundImage:
                        'radial-gradient(circle at 15% 100%, hsl(var(--success)) 0 80px, transparent 81px), radial-gradient(circle at 65% 100%, hsl(var(--success)) 0 100px, transparent 101px)',
                }"
            />
            <div
                class="absolute bottom-0 left-0 right-0 h-24 border-t-4 border-border bg-secondary"
                :style="{
                    backgroundImage:
                        'repeating-linear-gradient(0deg, hsl(var(--secondary-shadow)) 0 2px, transparent 2px 24px), repeating-linear-gradient(90deg, hsl(var(--secondary-shadow)) 0 2px, transparent 2px 48px)',
                }"
            />

            <div
                class="relative min-h-screen container z-10 py-10 flex items-center justify-center"
            >
                <PixelCard class="w-full max-w-md p-6 md:p-8 space-y-5 crt-scanlines">
                    <RouterLink
                        to="/login"
                        class="mb-5 flex items-center gap-3 font-pixel text-[9px] cursor-pointer transition-all"
                    >
                        <div
                            class="w-7 h-7 border-2 border-border bg-primary flex items-center justify-center shadow-[2px_2px_0px_hsl(var(--border))] translate-y-0 hover:translate-y-0.5 hover:translate-x-0.5 hover:shadow-none transition-transform"
                        >
                            <PhArrowLeft :size="14" weight="bold" />
                        </div>
                        <span class="hover:text-primary">VOLTAR AO LOGIN</span>
                    </RouterLink>

                    <div class="flex items-center gap-3 mb-5">
                        <div>
                            <h1 class="font-pixel text-lg">NOVA SENHA</h1>
                        </div>
                        <div class="ml-auto animate-bob">
                            <CoinIcon :size="28" class="animate-bob" />
                        </div>
                    </div>

                    <p class="font-pixel text-[8px] text-muted-foreground leading-loose">
                        ESCOLHA UMA NOVA SENHA PARA SUA CONTA.
                    </p>

                    <form class="space-y-4" @submit="handleSubmit">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">NOVA SENHA</label>
                            <div class="relative">
                                <PixelInput
                                    v-model="newPassword"
                                    :type="showPwd ? 'text' : 'password'"
                                    placeholder="••••••••"
                                    required
                                />
                                <button
                                    type="button"
                                    class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                    aria-label="Mostrar senha"
                                    @click="showPwd = !showPwd"
                                >
                                    <PhEyeSlash v-if="showPwd" weight="bold" />
                                    <PhEye v-else weight="bold" />
                                </button>
                            </div>
                        </div>

                        <div>
                            <label class="font-pixel text-[10px] block mb-2">CONFIRMAR SENHA</label>
                            <div class="relative">
                                <PixelInput
                                    v-model="confirmPassword"
                                    :type="showConfirm ? 'text' : 'password'"
                                    placeholder="••••••••"
                                    required
                                />
                                <button
                                    type="button"
                                    class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                    aria-label="Mostrar confirmação"
                                    @click="showConfirm = !showConfirm"
                                >
                                    <PhEyeSlash v-if="showConfirm" weight="bold" />
                                    <PhEye v-else weight="bold" />
                                </button>
                            </div>
                        </div>

                        <PixelButton
                            variant="primary"
                            class="w-full cursor-pointer"
                            type="submit"
                            :disabled="isSubmitting"
                        >
                            <PhLock weight="fill" class="pixel-icon" />
                            REDEFINIR SENHA
                        </PixelButton>
                    </form>
                </PixelCard>
            </div>
        </main>
    </div>
</template>

<script setup lang="ts">
import { loginRequest, meRequest } from '@/modules/auth/services/auth.service';
import type { UserRole } from '@/modules/auth/stores/auth.store';
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { PhArrowLeft, PhEye, PhEyeSlash, PhSparkle } from '@phosphor-icons/vue';
import { ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { toast } from 'vue-sonner';
import Cloud from './_components/Cloud.vue';

const router = useRouter();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const showPwd = ref(false);
const isSubmitting = ref(false);

const ROLE_ROUTES: Record<string, string> = {
    STUDENT: 'student-dashboard',
    TEACHER: 'teacher-dashboard',
    INSTITUTION: 'institution-dashboard',
    COMPANY: 'company-dashboard',
};

async function handleLogin(e: Event) {
    e.preventDefault();
    if (!email.value || !password.value) return;
    isSubmitting.value = true;
    try {
        await loginRequest({ email: email.value, password: password.value });
        const { data } = await meRequest();
        authStore.setUser({
            id: data.id,
            name: data.name,
            email: data.email,
            role: data.role.toLowerCase() as UserRole,
        });
        router.push({ name: ROLE_ROUTES[data.role.toUpperCase()] ?? 'home' });
    } catch {
        toast.error('Não foi possível fazer login. Verifique suas credenciais.');
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
            <div class="absolute bottom-32 right-10 hidden md:block">
                <div
                    class="w-16 h-16 bg-primary border-4 border-border shadow-[4px_4px_0_0_hsl(var(--border))] flex items-center justify-center font-pixel text-2xl text-primary-foreground animate-bob"
                >
                    ?
                </div>
            </div>

            <div class="absolute bottom-24 left-10 hidden md:block">
                <div class="w-20 h-8 bg-success border-4 border-border" />
                <div class="w-16 h-16 bg-success border-4 border-t-0 border-border ml-2" />
            </div>
            <div class="absolute bottom-24 right-1/4 block animate-bob z-0">
                <MarioAvatar character="mario" :size="72" />
            </div>

            <div
                class="relative min-h-screen container z-10 py-10 flex items-center justify-center"
            >
                <PixelCard class="w-full max-w-md p-6 md:p-8 space-y-5 crt-scanlines">
                    <RouterLink
                        to="/"
                        class="mb-5 flex items-center gap-3 font-pixel text-[9px] cursor-pointer transition-all"
                    >
                        <div
                            class="w-7 h-7 border-2 border-border bg-primary flex items-center justify-center shadow-[2px_2px_0px_hsl(var(--border))] translate-y-0 hover:translate-y-0.5 hover:translate-x-0.5 hover:shadow-none transition-transform"
                        >
                            <PhArrowLeft :size="14" weight="bold" />
                        </div>

                        <span class="hover:text-primary">VOLTAR À TELA INICIAL</span>
                    </RouterLink>
                    <div class="flex items-center gap-3 mb-5">
                        <div>
                            <h1 class="font-pixel text-lg">ENTRAR</h1>
                        </div>
                        <div class="ml-auto animate-bob">
                            <CoinIcon :size="28" class="animate-bob" />
                        </div>
                    </div>

                    <form class="space-y-4" @submit="handleLogin">
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">E-MAIL</label>
                            <PixelInput
                                v-model="email"
                                type="email"
                                placeholder="voce@email.com"
                                required
                            />
                        </div>
                        <div>
                            <label class="font-pixel text-[10px] block mb-2">SENHA</label>
                            <div class="relative">
                                <PixelInput
                                    v-model="password"
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

                        <div class="flex items-center justify-between font-pixel text-[9px]">
                            <label class="flex items-center gap-2">
                                <input type="checkbox" class="accent-primary" /> LEMBRAR
                            </label>
                            <a href="#" class="hover:text-primary">ESQUECI A SENHA</a>
                        </div>

                        <PixelButton
                            variant="primary"
                            class="w-full cursor-pointer"
                            type="submit"
                            :disabled="isSubmitting"
                        >
                            <PhSparkle weight="fill" class="pixel-icon cursor-pointer" /> PRESS
                            START
                        </PixelButton>
                    </form>

                    <div
                        class="my-5 flex items-center gap-2 font-pixel text-[9px] text-muted-foreground"
                    >
                        <div class="flex-1 h-px bg-border" />
                        OU
                        <div class="flex-1 h-px bg-border" />
                    </div>

                    <RouterLink to="/signup">
                        <PixelButton variant="success" class="w-full">CRIAR PERSONAGEM</PixelButton>
                    </RouterLink>
                </PixelCard>
            </div>
        </main>
    </div>
</template>

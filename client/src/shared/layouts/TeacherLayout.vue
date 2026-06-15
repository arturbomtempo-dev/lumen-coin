<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';
import {
    changeInitialPasswordSchema,
    type ChangeInitialPasswordFormData,
} from '@/modules/schemas/change-initial-password.schema';
import { changeInitialPassword } from '@/modules/teacher/services/teacher.service';
import { useTeacherStore } from '@/modules/teacher/stores/teacher.store';
import CoinIcon from '@/shared/components/CoinIcon.vue';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { useThemeStore } from '@/shared/stores/theme.store';
import {
    PhEye,
    PhEyeSlash,
    PhGameController,
    PhGraduationCap,
    PhKey,
    PhMoon,
    PhPaperPlaneTilt,
    PhReceipt,
    PhSignOut,
    PhSun,
    PhUser,
} from '@phosphor-icons/vue';
import { storeToRefs } from 'pinia';
import { onMounted, ref } from 'vue';
import { RouterLink, useRoute, useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const themeStore = useThemeStore();
const teacherStore = useTeacherStore();
const authStore = useAuthStore();
const { name, balance, firstLogin } = storeToRefs(teacherStore);
const route = useRoute();
const router = useRouter();

const navItems = [
    { to: '/app/professor', icon: PhGraduationCap, label: 'PAINEL' },
    { to: '/app/professor/enviar-moedas', icon: PhPaperPlaneTilt, label: 'ENVIAR' },
    { to: '/app/professor/extrato', icon: PhReceipt, label: 'EXTRATO' },
    { to: '/app/professor/perfil', icon: PhUser, label: 'PERFIL' },
];

const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

const {
    fields: passwordData,
    errors: passwordErrors,
    isSubmitting: isChangingPassword,
    validate: validatePassword,
} = useForm<ChangeInitialPasswordFormData>(changeInitialPasswordSchema);

passwordData.value = { newPassword: '', confirmPassword: '' };

async function handleChangeInitialPassword() {
    if (!validatePassword() || isChangingPassword.value) return;
    isChangingPassword.value = true;
    try {
        await changeInitialPassword(authStore.user!.id, {
            newPassword: passwordData.value.newPassword,
            confirmPassword: passwordData.value.confirmPassword,
        });
        teacherStore.firstLogin = false;
        toast.success('Senha definida com sucesso!');
    } catch {
    } finally {
        isChangingPassword.value = false;
    }
}

async function handleLogout() {
    await authStore.logout();
    router.push('/login');
}

onMounted(() => {
    if (!teacherStore.isLoaded) {
        teacherStore.loadProfile();
    }
});
</script>

<template>
    <div class="min-h-screen flex flex-col">
        <header class="sticky top-0 z-40 bg-hud text-hud-foreground border-b-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-3">
                <RouterLink to="/app/professor" class="flex items-center gap-2 min-w-0">
                    <div
                        class="pixel-icon border-2 border-border bg-primary text-primary-foreground p-1"
                    >
                        <PhGameController :size="18" weight="fill" />
                    </div>
                    <div class="min-w-0">
                        <div class="font-pixel text-[10px] leading-tight">LUMEN COIN</div>
                        <div class="font-display text-xs opacity-70 -mt-0.5 truncate">
                            {{ name ? name.toUpperCase() : 'PROFESSOR' }}
                        </div>
                    </div>
                </RouterLink>

                <div class="hidden sm:flex items-center gap-4 font-pixel text-[10px]">
                    <span class="flex items-center gap-1">
                        <CoinIcon :size="14" />
                        {{ balance.toLocaleString('pt-BR') }}
                    </span>
                </div>

                <div class="flex items-center gap-2">
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-0.5 active:translate-y-0.5 active:shadow-none"
                        aria-label="Alternar tema"
                        @click="themeStore.toggle"
                    >
                        <PhSun
                            v-if="themeStore.theme === 'night'"
                            weight="fill"
                            class="pixel-icon"
                        />
                        <PhMoon v-else weight="fill" class="pixel-icon" />
                        {{ themeStore.theme === 'night' ? 'DIA' : 'NOITE' }}
                    </button>
                    <button
                        class="font-pixel text-[9px] flex items-center gap-2 border-2 border-border bg-card text-card-foreground px-2 py-1 shadow-[2px_2px_0_0_hsl(var(--border))] active:translate-x-0.5 active:translate-y-0.5 active:shadow-none"
                        aria-label="Sair"
                        @click="handleLogout"
                    >
                        <PhSignOut weight="bold" class="pixel-icon" />
                        SAIR
                    </button>
                </div>
            </div>
        </header>

        <main class="flex-1 container py-6">
            <router-view />
        </main>

        <nav class="sticky bottom-0 z-40 bg-hud text-hud-foreground border-t-4 border-border">
            <div class="container flex items-center justify-between py-2 gap-2">
                <div class="hidden md:flex items-center gap-2 font-pixel text-[10px]">
                    <CoinIcon :size="14" /> {{ balance.toLocaleString('pt-BR') }} LUMEN
                </div>
                <ul
                    class="flex flex-1 md:flex-none items-center justify-around md:justify-end gap-1 md:gap-2"
                >
                    <li v-for="item in navItems" :key="item.to">
                        <RouterLink
                            :to="item.to"
                            class="relative flex flex-col items-center gap-0.5 px-3 py-1.5 border-2 border-border font-pixel text-[8px] transition-transform"
                            :class="
                                route.path === item.to
                                    ? 'bg-primary text-primary-foreground shadow-[2px_2px_0_0_hsl(var(--border))] animate-glow'
                                    : 'bg-card text-card-foreground hover:-translate-y-0.5'
                            "
                        >
                            <component
                                :is="item.icon"
                                :size="18"
                                weight="fill"
                                class="pixel-icon"
                            />
                            <span class="hidden sm:inline">{{ item.label }}</span>
                        </RouterLink>
                    </li>
                </ul>
            </div>
        </nav>
    </div>

    <div
        v-if="firstLogin && teacherStore.isLoaded"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80"
        @keydown.esc.prevent
    >
        <div
            class="w-full max-w-md bg-card text-card-foreground border-4 border-border shadow-[8px_8px_0_0_hsl(var(--border))] animate-pop"
            @click.stop
        >
            <div
                class="bg-primary text-primary-foreground border-b-4 border-border px-4 py-3 font-pixel text-xs flex items-center gap-2"
            >
                <PhKey weight="fill" class="pixel-icon" />
                PRIMEIRO ACESSO — DEFINA SUA SENHA
            </div>

            <div class="p-5">
                <p class="font-display text-lg leading-snug">
                    Por segurança, você precisa definir uma
                    <span class="text-primary">nova senha</span> antes de continuar.
                </p>
                <p class="font-sans text-sm text-muted-foreground mt-2">
                    Sua conta foi criada pela instituição com uma senha temporária. Ela não pode ser
                    usada após este passo.
                </p>

                <form class="mt-5 space-y-4" @submit.prevent="handleChangeInitialPassword">
                    <div>
                        <label class="font-pixel text-[9px] block mb-1">NOVA SENHA</label>
                        <div class="relative">
                            <PixelInput
                                v-model="passwordData.newPassword"
                                :type="showNewPassword ? 'text' : 'password'"
                                placeholder="Mínimo 8 caracteres"
                            />
                            <button
                                type="button"
                                class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                @click="showNewPassword = !showNewPassword"
                            >
                                <PhEyeSlash v-if="showNewPassword" weight="bold" :size="16" />
                                <PhEye v-else weight="bold" :size="16" />
                            </button>
                        </div>
                        <PasswordStrengthHint :password="passwordData.newPassword" />
                        <p
                            v-if="passwordErrors.newPassword"
                            class="font-sans text-xs text-destructive mt-1"
                        >
                            {{ passwordErrors.newPassword }}
                        </p>
                    </div>

                    <div>
                        <label class="font-pixel text-[9px] block mb-1">CONFIRMAR SENHA</label>
                        <div class="relative">
                            <PixelInput
                                v-model="passwordData.confirmPassword"
                                :type="showConfirmPassword ? 'text' : 'password'"
                                placeholder="Repita a nova senha"
                            />
                            <button
                                type="button"
                                class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                                @click="showConfirmPassword = !showConfirmPassword"
                            >
                                <PhEyeSlash v-if="showConfirmPassword" weight="bold" :size="16" />
                                <PhEye v-else weight="bold" :size="16" />
                            </button>
                        </div>
                        <p
                            v-if="passwordErrors.confirmPassword"
                            class="font-sans text-xs text-destructive mt-1"
                        >
                            {{ passwordErrors.confirmPassword }}
                        </p>
                    </div>

                    <PixelButton
                        type="submit"
                        variant="success"
                        class="w-full mt-2"
                        :disabled="isChangingPassword"
                    >
                        <PhKey weight="fill" class="pixel-icon" />
                        {{ isChangingPassword ? 'SALVANDO...' : 'DEFINIR SENHA E CONTINUAR' }}
                    </PixelButton>
                </form>
            </div>
        </div>
    </div>
</template>

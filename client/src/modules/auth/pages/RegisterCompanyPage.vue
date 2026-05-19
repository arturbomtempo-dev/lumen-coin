<script setup lang="ts">
import { registerCompanyRequest } from '@/modules/auth/services/auth.service';
import { registerCompanySchema } from '@/modules/schemas/register-company.schema';
import PasswordStrengthHint from '@/shared/components/PasswordStrengthHint.vue';
import PixelButton from '@/shared/components/PixelButton.vue';
import PixelCard from '@/shared/components/PixelCard.vue';
import MarioAvatar from '@/shared/components/MarioAvatar.vue';
import PixelInput from '@/shared/components/PixelInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { PhArrowLeft, PhEye, PhEyeSlash, PhStorefront } from '@phosphor-icons/vue';
import { vMaska } from 'maska/vue';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from 'vue-sonner';

const router = useRouter();

const { fields, errors, isSubmitting, validate, clearErrors } = useForm(registerCompanySchema);

fields.value = {
    name: '',
    email: '',
    cnpj: '',
    password: '',
    confirmPassword: '',
};

const showPassword = ref(false);
const showConfirmPassword = ref(false);

async function handleSubmit(e: Event) {
    e.preventDefault();
    if (!validate()) return;
    isSubmitting.value = true;
    try {
        await registerCompanyRequest({
            name: fields.value.name,
            email: fields.value.email,
            password: fields.value.password,
            cnpj: fields.value.cnpj.replace(/\D/g, ''),
        });
        toast.success('Empresa cadastrada com sucesso!', {
            description: 'Faça login para acessar o portal.',
        });
        clearErrors();
        router.push({ name: 'login' });
    } catch {
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <main class="min-h-screen flex items-center justify-center px-4 py-10">
        <PixelCard class="w-full max-w-lg p-6 md:p-8">
            <button
                class="mb-5 flex items-center gap-3 font-pixel text-[9px] transition-all"
                type="button"
                @click="router.back()"
            >
                <div
                    class="w-7 h-7 border-2 border-border bg-primary flex items-center justify-center shadow-[2px_2px_0px_hsl(var(--border))]"
                >
                    <PhArrowLeft :size="14" weight="bold" />
                </div>
                <span>VOLTAR</span>
            </button>

            <div class="flex items-center gap-3 mb-6">
                 <div
                            class="bg-hud border-2 border-border p-2 shadow-[4px_4px_0px_hsl(var(--border))]"
                        >
                            <MarioAvatar character="company" :size="56" />
                        </div>
                <div>
                    <div class="font-pixel text-[9px] text-primary mb-1">▶ CADASTRO</div>
                    <h1 class="font-pixel text-lg leading-5">CRIAR CONTA DE EMPRESA</h1>
                </div>
            </div>

            <form class="space-y-4" @submit="handleSubmit">
                <div>
                    <label class="font-pixel text-[9px] block mb-2">NOME DA EMPRESA</label>
                    <PixelInput v-model="fields.name" placeholder="Ex: Byte Burger" />
                    <p
                        v-if="errors.name"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.name }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[9px] block mb-2">E-MAIL</label>
                    <PixelInput
                        v-model="fields.email"
                        type="email"
                        placeholder="contato@empresa.com"
                    />
                    <p
                        v-if="errors.email"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.email }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[9px] block mb-2">CNPJ</label>
                    <PixelInput
                        v-model="fields.cnpj"
                        v-maska="'##.###.###/####-##'"
                        placeholder="00.000.000/0000-00"
                        maxlength="18"
                    />
                    <p
                        v-if="errors.cnpj"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.cnpj }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[9px] block mb-2">SENHA</label>
                    <div class="relative">
                        <PixelInput
                            v-model="fields.password"
                            :type="showPassword ? 'text' : 'password'"
                            class="pr-10"
                            placeholder="Mínimo 8 caracteres"
                        />
                        <button
                            class="absolute right-3 top-1/2 -translate-y-1/2"
                            type="button"
                            @click="showPassword = !showPassword"
                        >
                            <PhEyeSlash v-if="showPassword" :size="18" weight="bold" />
                            <PhEye v-else :size="18" weight="bold" />
                        </button>
                    </div>
                    <PasswordStrengthHint :password="fields.password" />
                    <p
                        v-if="errors.password"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.password }}
                    </p>
                </div>

                <div>
                    <label class="font-pixel text-[9px] block mb-2">CONFIRMAR SENHA</label>
                    <div class="relative">
                        <PixelInput
                            v-model="fields.confirmPassword"
                            :type="showConfirmPassword ? 'text' : 'password'"
                            class="pr-10"
                            placeholder="Repita a senha"
                        />
                        <button
                            class="absolute right-3 top-1/2 -translate-y-1/2"
                            type="button"
                            @click="showConfirmPassword = !showConfirmPassword"
                        >
                            <PhEyeSlash v-if="showConfirmPassword" :size="18" weight="bold" />
                            <PhEye v-else :size="18" weight="bold" />
                        </button>
                    </div>
                    <p
                        v-if="errors.confirmPassword"
                        class="font-sans text-xs mt-1"
                        style="color: hsl(var(--destructive))"
                    >
                        {{ errors.confirmPassword }}
                    </p>
                </div>

                <PixelButton
                    type="submit"
                    variant="success"
                    class="w-full"
                    :disabled="isSubmitting"
                >
                    CADASTRAR EMPRESA
                </PixelButton>
            </form>
        </PixelCard>
    </main>
</template>

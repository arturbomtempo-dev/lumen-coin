<script setup lang="ts">
import BaseInput from '@/shared/components/BaseInput.vue';
import { useForm } from '@/shared/composables/useForm';
import { loginSchema, type LoginFormData } from '@/modules/schemas/login.schema';
import { loginRequest } from '../services/auth.service';
import { useAuthStore } from '../stores/auth.store';
import { useRouter } from 'vue-router';

const { data, errors, validate, isSubmitting } = useForm<LoginFormData>(loginSchema);
const auth = useAuthStore();
const router = useRouter();

async function handleSubmit() {
    if (!validate()) return;

    isSubmitting.value = true;

    try {
        await loginRequest(data.value);
        await auth.checkAuth();
        router.push(`/${auth.user?.role}/dashboard`);
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <div
        class="min-h-screen flex items-center justify-center px-4"
        style="background-color: var(--color-bg)"
    >
        <div class="w-full max-w-sm">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold" style="color: var(--color-primary)">
                    Lumen Coin
                </h1>
                <p class="mt-2 text-muted">Sign in to your account</p>
            </div>

            <form class="card flex flex-col gap-4" @submit.prevent="handleSubmit">
                <BaseInput
                    v-model="data.email"
                    label="Email"
                    type="email"
                    placeholder="you@example.com"
                    :error="errors.email"
                />

                <BaseInput
                    v-model="data.password"
                    label="Password"
                    type="password"
                    placeholder="••••••••"
                    :error="errors.password"
                />

                <button type="submit" class="btn w-full mt-2" :disabled="isSubmitting">
                    {{ isSubmitting ? 'Signing in...' : 'Sign In' }}
                </button>

                <p class="text-center text-sm text-muted">
                    Don't have an account?
                    <router-link to="/register" class="font-medium" style="color: var(--color-primary)">
                        Register
                    </router-link>
                </p>
            </form>
        </div>
    </div>
</template>

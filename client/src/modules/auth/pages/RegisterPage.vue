<script setup lang="ts">
import { ref } from 'vue';
import BaseInput from '@/shared/components/BaseInput.vue';
import { useForm } from '@/shared/composables/useForm';
import {
    registerStudentSchema,
    type RegisterStudentFormData,
} from '@/modules/schemas/register.schema';
import { registerStudentRequest } from '../services/auth.service';
import { useAuthStore } from '../stores/auth.store';
import { useRouter } from 'vue-router';

type RegisterTab = 'student' | 'company';

const activeTab = ref<RegisterTab>('student');
const { data, errors, validate, isSubmitting } =
    useForm<RegisterStudentFormData>(registerStudentSchema);
const auth = useAuthStore();
const router = useRouter();

async function handleSubmit() {
    if (!validate()) return;

    isSubmitting.value = true;

    try {
        await registerStudentRequest(data.value);
        await auth.checkAuth();
        router.push('/student/dashboard');
    } finally {
        isSubmitting.value = false;
    }
}
</script>

<template>
    <div
        class="min-h-screen flex items-center justify-center px-4 py-8"
        style="background-color: var(--color-bg)"
    >
        <div class="w-full max-w-lg">
            <div class="text-center mb-8">
                <h1 class="text-3xl font-bold" style="color: var(--color-primary)">
                    Lumen Coin
                </h1>
                <p class="mt-2 text-muted">Create your account</p>
            </div>

            <div class="card">
                <div class="flex gap-2 mb-6">
                    <button
                        :class="['btn-secondary flex-1', activeTab === 'student' && 'btn']"
                        @click="activeTab = 'student'"
                    >
                        Student
                    </button>
                    <button
                        :class="['btn-secondary flex-1', activeTab === 'company' && 'btn']"
                        @click="activeTab = 'company'"
                    >
                        Company
                    </button>
                </div>

                <form v-if="activeTab === 'student'" class="flex flex-col gap-4" @submit.prevent="handleSubmit">
                    <div class="grid grid-cols-2 gap-4">
                        <BaseInput
                            v-model="data.name"
                            label="Full Name"
                            placeholder="John Doe"
                            :error="errors.name"
                        />
                        <BaseInput
                            v-model="data.email"
                            label="Email"
                            type="email"
                            placeholder="you@example.com"
                            :error="errors.email"
                        />
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <BaseInput
                            v-model="data.cpf"
                            label="CPF"
                            placeholder="00000000000"
                            :error="errors.cpf"
                        />
                        <BaseInput
                            v-model="data.rg"
                            label="RG"
                            placeholder="0000000"
                            :error="errors.rg"
                        />
                    </div>

                    <BaseInput
                        v-model="data.address"
                        label="Address"
                        placeholder="Street, number, city"
                        :error="errors.address"
                    />

                    <BaseInput
                        v-model="data.course"
                        label="Course"
                        placeholder="Computer Science"
                        :error="errors.course"
                    />

                    <div class="grid grid-cols-2 gap-4">
                        <BaseInput
                            v-model="data.password"
                            label="Password"
                            type="password"
                            placeholder="••••••••"
                            :error="errors.password"
                        />
                        <BaseInput
                            v-model="data.confirmPassword"
                            label="Confirm Password"
                            type="password"
                            placeholder="••••••••"
                            :error="errors.confirmPassword"
                        />
                    </div>

                    <button type="submit" class="btn w-full mt-2" :disabled="isSubmitting">
                        {{ isSubmitting ? 'Creating account...' : 'Create Account' }}
                    </button>
                </form>

                <p class="text-center text-sm text-muted mt-4">
                    Already have an account?
                    <router-link to="/login" class="font-medium" style="color: var(--color-primary)">
                        Sign in
                    </router-link>
                </p>
            </div>
        </div>
    </div>
</template>

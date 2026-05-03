<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();
</script>

<template>
    <div
        class="min-h-screen flex flex-col items-center justify-center px-4 text-center"
        style="background-color: var(--color-bg)"
    >
        <h1 class="text-6xl font-bold mb-4" style="color: var(--color-primary)">Lumen Coin</h1>
        <p class="text-xl mb-2 text-body">A merit recognition system</p>
        <p class="text-muted mb-8 max-w-md">
            Earn and redeem coins for your academic achievements and outstanding behavior.
        </p>
        <div class="flex gap-4">
            <button class="btn px-6" @click="router.push('/login')">Sign In</button>
            <button class="btn-secondary px-6" @click="router.push('/register')">Register</button>
        </div>
    </div>
</template>

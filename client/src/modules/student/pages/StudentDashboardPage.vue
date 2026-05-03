<script setup lang="ts">
import { useAuthStore } from '@/modules/auth/stores/auth.store';

const auth = useAuthStore();
</script>

<template>
    <div>
        <h2 class="text-2xl font-bold mb-6">Dashboard</h2>
        <p class="text-muted">Welcome, {{ auth.user?.name }}</p>
    </div>
</template>

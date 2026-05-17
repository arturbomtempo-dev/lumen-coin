<script setup lang="ts">
import { PhCheckCircle, PhXCircle } from '@phosphor-icons/vue';
import { computed } from 'vue';

const props = defineProps<{
    password: string;
}>();

const checks = computed(() => [
    { label: 'Letra maiúscula', met: /[A-Z]/.test(props.password) },
    { label: 'Letra minúscula', met: /[a-z]/.test(props.password) },
    { label: 'Um número', met: /\d/.test(props.password) },
    { label: 'Caractere especial', met: /[^A-Za-z0-9]/.test(props.password) },
    { label: 'Mínimo de 8 caracteres', met: props.password.length >= 8 },
]);
</script>

<template>
    <div
        v-if="password.length > 0"
        class="mt-2 border-2 border-border bg-card p-2 shadow-[2px_2px_0px_hsl(var(--border))]"
    >
        <p class="font-pixel text-[7px] text-muted-foreground mb-2">SUA SENHA DEVE CONTER:</p>
        <div class="grid grid-cols-2 gap-x-3 gap-y-1.5">
            <div
                v-for="check in checks"
                :key="check.label"
                class="flex items-center gap-1.5"
                :class="check.label === 'Mínimo de 8 caracteres' ? 'col-span-2' : ''"
            >
                <PhCheckCircle
                    v-if="check.met"
                    :size="12"
                    weight="fill"
                    class="text-success shrink-0"
                />
                <PhXCircle v-else :size="12" weight="fill" class="text-destructive shrink-0" />
                <span
                    class="font-sans text-[11px] leading-tight"
                    :class="check.met ? 'text-success' : 'text-muted-foreground'"
                >
                    {{ check.label }}
                </span>
            </div>
        </div>
    </div>
</template>

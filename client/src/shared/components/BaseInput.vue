<script setup lang="ts">
defineProps<{
    modelValue: string;
    label?: string;
    placeholder?: string;
    type?: string;
    error?: string;
    disabled?: boolean;
}>();

defineEmits<{
    'update:modelValue': [value: string];
}>();
</script>

<template>
    <div class="flex flex-col gap-1">
        <label v-if="label" class="text-sm font-medium text-muted">
            {{ label }}
        </label>
        <input
            :value="modelValue"
            :placeholder="placeholder"
            :type="type ?? 'text'"
            :disabled="disabled"
            :class="['input', error && 'border-red-500']"
            @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        />
        <span v-if="error" class="text-xs" style="color: var(--color-danger)">
            {{ error }}
        </span>
    </div>
</template>

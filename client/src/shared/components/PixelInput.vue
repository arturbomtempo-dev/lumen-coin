<script setup lang="ts">
defineProps<{
    modelValue?: string | number;
    type?: string;
    placeholder?: string;
    required?: boolean;
    disabled?: boolean;
    min?: number | string;
    max?: number | string;
    maxLength?: number;
    class?: string;
    readonly?: boolean;
}>();

const emit = defineEmits<{
    'update:modelValue': [value: string];
    change: [event: Event];
}>();
</script>

<template>
    <input
        :value="modelValue"
        :type="type || 'text'"
        :placeholder="placeholder"
        :required="required"
        :disabled="disabled"
        :min="min"
        :max="max"
        :maxlength="maxLength"
        :readonly="readonly"
        :class="[
            'w-full bg-input text-foreground placeholder:text-muted-foreground',
            'border-2 border-border px-3 py-2 font-display text-lg',
            'shadow-[inset_2px_2px_0_0_hsl(var(--border)/0.25)]',
            'focus:outline-none focus:ring-2 focus:ring-ring',
            $attrs.class,
        ]"
        v-bind="{ ...$attrs, class: undefined }"
        @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        @change="emit('change', $event)"
    />
</template>

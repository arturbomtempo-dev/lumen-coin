import { ref } from 'vue';
import type { ZodSchema } from 'zod';

export function useForm<T>(schema: ZodSchema<T>) {
    const fields = ref<T>({} as T);
    const errors = ref<Record<string, string>>({});
    const isSubmitting = ref(false);

    function validate(): boolean {
        const result = schema.safeParse(fields.value);

        if (!result.success) {
            errors.value = {};

            result.error.issues.forEach((issue) => {
                const field = issue.path[0] as string;
                errors.value[field] = issue.message;
            });

            return false;
        }

        errors.value = {};
        return true;
    }

    function clearErrors() {
        errors.value = {};
    }

    return {
        fields,
        errors,
        isSubmitting,
        validate,
        clearErrors,
    };
}

import { ref } from 'vue';
import type { ZodSchema } from 'zod';

export function useForm<T>(schema: ZodSchema<T>) {
    const data = ref<T>({} as T);
    const errors = ref<Record<string, string>>({});
    const isSubmitting = ref(false);

    function validate(): boolean {
        const result = schema.safeParse(data.value);

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
        data,
        errors,
        isSubmitting,
        validate,
        clearErrors,
    };
}

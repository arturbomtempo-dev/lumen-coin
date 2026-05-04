import { z } from 'zod';

export const registerCourseSchema = z.object({
    name: z
        .string()
        .min(1, 'O nome é obrigatório')
        .max(150, 'O nome deve ter no máximo 150 caracteres'),
    shift: z.string().min(1, 'Selecione um turno'),
    periods: z.coerce
        .number()
        .refine((value) => Number.isFinite(value), {
            message: 'Informe o número de períodos',
        })
        .min(1, 'O número de períodos deve ser pelo menos 1'),
});

export type RegisterCourseFormData = z.infer<typeof registerCourseSchema>;

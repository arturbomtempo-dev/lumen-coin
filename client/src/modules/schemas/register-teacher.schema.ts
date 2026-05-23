import { z } from 'zod';

const digitsOnly = (value: string) => value.replace(/\D/g, '');

export const registerTeacherSchema = z.object({
    name: z
        .string()
        .trim()
        .min(2, 'O nome deve ter pelo menos 2 caracteres')
        .max(150, 'O nome deve ter no máximo 150 caracteres'),
    email: z
        .string()
        .trim()
        .email('E-mail inválido')
        .max(255, 'O e-mail deve ter no máximo 255 caracteres'),
    cpf: z.string().refine((value) => /^\d{11}$/.test(digitsOnly(value)), {
        message: 'O CPF é inválido',
    }),
    department: z
        .string()
        .trim()
        .max(150, 'O departamento deve ter no máximo 150 caracteres')
        .optional(),
});

export type RegisterTeacherFormData = z.infer<typeof registerTeacherSchema>;

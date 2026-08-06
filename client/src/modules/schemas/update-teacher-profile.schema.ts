import { cpf } from 'docsbr';
import { z } from 'zod';

const teacherAvatars = ['MARIO', 'LUIGI', 'PEACH', 'TOAD', 'YOSHI', 'BOWSER'] as const;

export const updateTeacherProfileSchema = z.object({
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
    avatar: z.enum(teacherAvatars),
    cpf: z.string().refine((value) => cpf.isValid(value), {
        message: 'O CPF informado não é válido',
    }),
    department: z
        .string()
        .trim()
        .max(150, 'O departamento deve ter no máximo 150 caracteres')
        .refine((value) => value.length === 0 || value.length >= 2, {
            message: 'O departamento deve ter pelo menos 2 caracteres',
        }),
});

export type UpdateTeacherProfileFormData = z.infer<typeof updateTeacherProfileSchema>;

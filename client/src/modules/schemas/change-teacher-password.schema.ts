import { z } from 'zod';

export const changeTeacherPasswordSchema = z
    .object({
        currentPassword: z.string().min(1, 'A senha atual é obrigatória'),
        newPassword: z
            .string()
            .min(8, 'A nova senha deve ter pelo menos 8 caracteres')
            .max(72, 'A nova senha deve ter no máximo 72 caracteres'),
        confirmNewPassword: z.string().min(1, 'Confirme a nova senha'),
    })
    .refine((data) => data.newPassword === data.confirmNewPassword, {
        message: 'As senhas não conferem',
        path: ['confirmNewPassword'],
    });

export type ChangeTeacherPasswordFormData = z.infer<typeof changeTeacherPasswordSchema>;

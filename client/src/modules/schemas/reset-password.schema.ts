import { z } from 'zod';

export const resetPasswordSchema = z
    .object({
        newPassword: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
        confirmPassword: z.string().min(1, 'Confirmação obrigatória'),
    })
    .refine((data) => data.newPassword === data.confirmPassword, {
        message: 'As senhas não coincidem',
        path: ['confirmPassword'],
    });

export type ResetPasswordForm = z.infer<typeof resetPasswordSchema>;

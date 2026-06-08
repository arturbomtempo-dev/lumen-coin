import { z } from 'zod';

export const changeInitialPasswordSchema = z
    .object({
        newPassword: z
            .string()
            .min(8, 'A senha deve ter pelo menos 8 caracteres')
            .max(72, 'A senha deve ter no máximo 72 caracteres'),
        confirmPassword: z.string().min(1, 'Confirme a nova senha'),
    })
    .refine((data) => data.newPassword === data.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type ChangeInitialPasswordFormData = z.infer<typeof changeInitialPasswordSchema>;

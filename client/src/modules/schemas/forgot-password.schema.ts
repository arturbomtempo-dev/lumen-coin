import { z } from 'zod';

export const forgotPasswordSchema = z.object({
    email: z.string().min(1, 'E-mail obrigatório').email('E-mail inválido'),
});

export type ForgotPasswordForm = z.infer<typeof forgotPasswordSchema>;

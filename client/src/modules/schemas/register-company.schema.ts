import { z } from 'zod';

export const registerCompanySchema = z
    .object({
        name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres'),
        email: z.string().email('E-mail inválido'),
        cnpj: z.string().length(14, 'O CNPJ deve ter exatamente 14 dígitos'),
        password: z.string().min(8, 'A senha deve ter pelo menos 8 caracteres'),
        confirmPassword: z.string().min(1, 'Confirme sua senha'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterCompanyFormData = z.infer<typeof registerCompanySchema>;

import { z } from 'zod';

const digitsOnly = (v: string) => v.replace(/\D/g, '');

export const registerCompanySchema = z
    .object({
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
        cnpj: z
            .string()
            .min(1, 'O CNPJ é obrigatório')
            .refine((v) => digitsOnly(v).length === 14, 'O CNPJ deve ter exatamente 14 dígitos'),
        password: z
            .string()
            .min(8, 'A senha deve ter pelo menos 8 caracteres')
            .max(72, 'A senha deve ter no máximo 72 caracteres')
            .regex(/[a-z]/, 'A senha deve conter pelo menos uma letra minúscula')
            .regex(/[A-Z]/, 'A senha deve conter pelo menos uma letra maiúscula')
            .regex(/\d/, 'A senha deve conter pelo menos um número')
            .regex(/[^A-Za-z0-9]/, 'A senha deve conter pelo menos um caractere especial'),
        confirmPassword: z.string().min(1, 'Confirme sua senha'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterCompanyFormData = z.infer<typeof registerCompanySchema>;

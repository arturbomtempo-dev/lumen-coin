import { z } from 'zod';

const digitsOnly = (v: string) => v.replace(/\D/g, '');

export const registerInstitutionSchema = z
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
        password: z
            .string()
            .min(8, 'A senha deve ter pelo menos 8 caracteres')
            .max(72, 'A senha deve ter no máximo 72 caracteres')
            .regex(/[a-z]/, 'A senha deve conter pelo menos uma letra minúscula')
            .regex(/[A-Z]/, 'A senha deve conter pelo menos uma letra maiúscula')
            .regex(/\d/, 'A senha deve conter pelo menos um número')
            .regex(/[^A-Za-z0-9]/, 'A senha deve conter pelo menos um caractere especial'),
        confirmPassword: z.string().min(1, 'Confirme sua senha'),
        cnpj: z
            .string()
            .min(1, 'O CNPJ é obrigatório')
            .refine((v) => digitsOnly(v).length === 14, 'O CNPJ deve ter exatamente 14 dígitos'),
        phone: z
            .string()
            .refine(
                (v) => !v || [10, 11].includes(digitsOnly(v).length),
                'O telefone deve ter 10 ou 11 dígitos'
            )
            .optional(),
        zipCode: z
            .string()
            .min(1, 'O CEP é obrigatório')
            .refine((v) => digitsOnly(v).length === 8, 'O CEP deve ter exatamente 8 dígitos'),
        street: z
            .string()
            .trim()
            .min(1, 'A rua é obrigatória')
            .max(150, 'A rua deve ter no máximo 150 caracteres'),
        number: z
            .string()
            .trim()
            .min(1, 'O número é obrigatório')
            .max(20, 'O número deve ter no máximo 20 caracteres'),
        neighborhood: z
            .string()
            .trim()
            .min(1, 'O bairro é obrigatório')
            .max(120, 'O bairro deve ter no máximo 120 caracteres'),
        city: z
            .string()
            .trim()
            .min(1, 'A cidade é obrigatória')
            .max(120, 'A cidade deve ter no máximo 120 caracteres'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterInstitutionFormData = z.infer<typeof registerInstitutionSchema>;

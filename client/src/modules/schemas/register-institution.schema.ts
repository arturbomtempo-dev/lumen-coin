import { z } from 'zod';

const digitCount = (value: string) => value.replace(/\D/g, '').length;

export const registerInstitutionSchema = z
    .object({
        name: z.string().trim().min(2, 'O nome deve ter pelo menos 2 caracteres'),
        email: z.string().trim().email('E-mail inválido'),
        password: z.string().min(8, 'A senha deve ter pelo menos 8 caracteres'),
        confirmPassword: z.string().min(1, 'Confirme sua senha'),
        cnpj: z.string().refine((value) => digitCount(value) === 14, {
            message: 'O CNPJ deve ter exatamente 14 dígitos',
        }),
        phone: z.string().max(15, 'O telefone deve ter no máximo 15 caracteres').optional(),
        zipCode: z.string().refine((value) => digitCount(value) === 8, {
            message: 'O CEP deve ter exatamente 8 dígitos',
        }),
        street: z.string().trim().min(2, 'A rua deve ter pelo menos 2 caracteres'),
        number: z.string().trim().min(1, 'Informe o número'),
        neighborhood: z.string().trim().min(2, 'O bairro deve ter pelo menos 2 caracteres'),
        city: z.string().trim().min(2, 'A cidade deve ter pelo menos 2 caracteres'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterInstitutionFormData = z.infer<typeof registerInstitutionSchema>;
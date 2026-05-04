import { z } from 'zod';

const digitsOnly = (value: string) => value.replace(/\D/g, '');
const alphaNumericOnly = (value: string) => value.replace(/[^A-Za-z0-9]/g, '');

export const updateStudentSchema = z.object({
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
        .max(72, 'A senha deve ter no máximo 72 caracteres')
        .refine((value) => value.length === 0 || value.length >= 8, {
            message: 'A senha deve ter pelo menos 8 caracteres',
        }),
    cpf: z.string().refine((value) => /^\d{11}$/.test(digitsOnly(value)), {
        message: 'O CPF deve conter exatamente 11 dígitos',
    }),
    rg: z
        .string()
        .refine((value) => /^[A-Z]{2}\d{8}$/.test(alphaNumericOnly(value).toUpperCase()), {
            message: 'O RG deve seguir o formato XX00000000',
        }),
    zipCode: z.string().refine((value) => /^\d{8}$/.test(digitsOnly(value)), {
        message: 'O CEP deve conter exatamente 8 dígitos',
    }),
    address: z
        .string()
        .trim()
        .min(5, 'Informe um endereço válido')
        .max(300, 'O endereço deve ter no máximo 300 caracteres'),
    institutionId: z.string().uuid('Selecione uma instituição'),
    courseId: z.string().uuid('Selecione um curso'),
});

export type UpdateStudentFormData = z.infer<typeof updateStudentSchema>;

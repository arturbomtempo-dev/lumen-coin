import { z } from 'zod';

const digitCount = (value: string) => value.replace(/\D/g, '').length;

export const updateCompanySchema = z.object({
    name: z.string().trim().min(2, 'O nome deve ter pelo menos 2 caracteres'),
    email: z.string().trim().email('E-mail inválido'),
    cnpj: z.string().refine((value) => digitCount(value) === 14, {
        message: 'O CNPJ deve ter exatamente 14 dígitos',
    }),
});

export type UpdateCompanyFormData = z.infer<typeof updateCompanySchema>;

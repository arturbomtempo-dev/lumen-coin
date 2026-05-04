import { z } from 'zod';

export const updateCompanySchema = z.object({
    name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres').optional(),
    email: z.string().email('E-mail inválido').optional(),
    cnpj: z.string().length(14, 'O CNPJ deve ter exatamente 14 dígitos').optional(),
});

export type UpdateCompanyFormData = z.infer<typeof updateCompanySchema>;

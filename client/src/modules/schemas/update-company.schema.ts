import { cnpj } from 'docsbr';
import { z } from 'zod';

export const updateCompanySchema = z.object({
    name: z.string().trim().min(2, 'O nome deve ter pelo menos 2 caracteres'),
    email: z.string().trim().email('E-mail inválido'),
    cnpj: z.string().refine((value) => cnpj.isValid(value), {
        message: 'O CNPJ informado não é válido',
    }),
});

export type UpdateCompanyFormData = z.infer<typeof updateCompanySchema>;

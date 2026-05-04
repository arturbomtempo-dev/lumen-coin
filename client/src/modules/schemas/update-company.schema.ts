import { z } from 'zod';

export const updateCompanySchema = z.object({
    name: z.string().min(2, 'Name must be at least 2 characters').optional(),
    email: z.string().email('Invalid email address').optional(),
    cnpj: z.string().length(14, 'CNPJ must have 14 digits').optional(),
});

export type UpdateCompanyFormData = z.infer<typeof updateCompanySchema>;

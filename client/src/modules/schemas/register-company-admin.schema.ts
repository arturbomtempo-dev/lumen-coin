import { z } from 'zod';

export const registerCompanyAdminSchema = z.object({
    name: z.string().min(2, 'Name must be at least 2 characters'),
    email: z.string().email('Invalid email address'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
    cnpj: z.string().length(14, 'CNPJ must have 14 digits'),
});

export type RegisterCompanyAdminFormData = z.infer<typeof registerCompanyAdminSchema>;

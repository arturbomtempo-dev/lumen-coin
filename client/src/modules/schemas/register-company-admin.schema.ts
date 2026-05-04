import { z } from 'zod';

export const registerCompanyAdminSchema = z.object({
    name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres'),
    email: z.string().email('E-mail inválido'),
    password: z.string().min(8, 'A senha deve ter pelo menos 8 caracteres'),
    cnpj: z.string().length(14, 'O CNPJ deve ter exatamente 14 dígitos'),
});

export type RegisterCompanyAdminFormData = z.infer<typeof registerCompanyAdminSchema>;

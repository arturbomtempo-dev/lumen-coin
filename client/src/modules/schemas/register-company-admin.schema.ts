import { cnpj } from 'docsbr';
import { z } from 'zod';

export const registerCompanyAdminSchema = z.object({
    name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres'),
    email: z.string().email('E-mail inválido'),
    password: z.string().min(8, 'A senha deve ter pelo menos 8 caracteres'),
    cnpj: z.string().refine((value) => cnpj.isValid(value), 'CNPJ inválido'),
});

export type RegisterCompanyAdminFormData = z.infer<typeof registerCompanyAdminSchema>;

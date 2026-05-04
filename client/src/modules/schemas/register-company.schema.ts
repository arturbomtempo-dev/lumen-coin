import { z } from 'zod';

export const registerCompanySchema = z
    .object({
        name: z.string().min(2, 'Name must be at least 2 characters'),
        email: z.string().email('Invalid email address'),
        cnpj: z.string().length(14, 'CNPJ must have 14 digits'),
        password: z.string().min(8, 'Password must be at least 8 characters'),
        confirmPassword: z.string().min(1, 'Confirm your password'),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: 'Passwords do not match',
        path: ['confirmPassword'],
    });

export type RegisterCompanyFormData = z.infer<typeof registerCompanySchema>;

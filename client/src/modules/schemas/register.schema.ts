import { z } from 'zod';

export const registerStudentSchema = z
    .object({
        name: z.string().min(2, 'Name must be at least 2 characters'),
        email: z.string().email('Invalid email address'),
        cpf: z.string().length(11, 'CPF must have 11 digits'),
        rg: z.string().min(7, 'Invalid RG'),
        address: z.string().min(5, 'Address is required'),
        institutionId: z.string().min(1, 'Select an institution'),
        course: z.string().min(2, 'Course is required'),
        password: z.string().min(6, 'Password must be at least 6 characters'),
        confirmPassword: z.string().min(6, 'Password must be at least 6 characters'),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: 'Passwords do not match',
        path: ['confirmPassword'],
    });

export type RegisterStudentFormData = z.infer<typeof registerStudentSchema>;

export const registerCompanySchema = z
    .object({
        name: z.string().min(2, 'Company name must be at least 2 characters'),
        email: z.string().email('Invalid email address'),
        password: z.string().min(6, 'Password must be at least 6 characters'),
        confirmPassword: z.string().min(6, 'Password must be at least 6 characters'),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: 'Passwords do not match',
        path: ['confirmPassword'],
    });

export type RegisterCompanyFormData = z.infer<typeof registerCompanySchema>;

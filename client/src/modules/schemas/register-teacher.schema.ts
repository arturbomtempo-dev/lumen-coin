import { z } from 'zod';

export const registerTeacherSchema = z.object({
    name: z.string().min(2, 'Name must be at least 2 characters'),
    email: z.string().email('Invalid email address'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
    cpf: z.string().length(11, 'CPF must have 11 digits'),
    avatar: z.string().min(1, 'Select an avatar'),
    department: z.string().max(150, 'Department must be at most 150 characters').optional(),
});

export type RegisterTeacherFormData = z.infer<typeof registerTeacherSchema>;

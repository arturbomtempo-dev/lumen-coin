import { z } from 'zod';

export const updateTeacherSchema = z.object({
    name: z.string().min(2, 'Name must be at least 2 characters').optional(),
    email: z.string().email('Invalid email address').optional(),
    department: z.string().max(150, 'Department must be at most 150 characters').optional(),
});

export type UpdateTeacherFormData = z.infer<typeof updateTeacherSchema>;

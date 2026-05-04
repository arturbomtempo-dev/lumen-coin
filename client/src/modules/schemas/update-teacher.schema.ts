import { z } from 'zod';

export const updateTeacherSchema = z.object({
    name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres').optional(),
    email: z.string().email('E-mail inválido').optional(),
    department: z.string().max(150, 'O departamento deve ter no máximo 150 caracteres').optional(),
});

export type UpdateTeacherFormData = z.infer<typeof updateTeacherSchema>;

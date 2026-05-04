import { z } from 'zod';

export const registerTeacherSchema = z.object({
    name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres'),
    email: z.string().email('E-mail inválido'),
    password: z.string().min(8, 'A senha deve ter pelo menos 8 caracteres'),
    cpf: z.string().length(11, 'O CPF deve ter exatamente 11 dígitos'),
    avatar: z.string().min(1, 'Selecione um avatar'),
    department: z.string().max(150, 'O departamento deve ter no máximo 150 caracteres').optional(),
});

export type RegisterTeacherFormData = z.infer<typeof registerTeacherSchema>;

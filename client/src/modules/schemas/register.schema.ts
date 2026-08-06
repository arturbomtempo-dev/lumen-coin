import { cpf } from 'docsbr';
import { z } from 'zod';

export const registerStudentSchema = z
    .object({
        name: z.string().min(2, 'O nome deve ter pelo menos 2 caracteres'),
        email: z.string().email('E-mail inválido'),
        cpf: z.string().refine((value) => cpf.isValid(value), 'CPF inválido'),
        rg: z.string().min(7, 'RG inválido'),
        address: z.string().min(5, 'Informe o endereço'),
        institutionId: z.string().min(1, 'Selecione uma instituição'),
        course: z.string().min(2, 'Informe o curso'),
        password: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
        confirmPassword: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterStudentFormData = z.infer<typeof registerStudentSchema>;

export const registerCompanySchema = z
    .object({
        name: z.string().min(2, 'O nome da empresa deve ter pelo menos 2 caracteres'),
        email: z.string().email('E-mail inválido'),
        password: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
        confirmPassword: z.string().min(6, 'A senha deve ter pelo menos 6 caracteres'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterCompanyFormData = z.infer<typeof registerCompanySchema>;

import { cpf } from 'docsbr';
import { z } from 'zod';

const avatarValues = ['MARIO', 'LUIGI', 'PEACH', 'TOAD', 'YOSHI', 'BOWSER'] as const;

const digitsOnly = (value: string) => value.replace(/\D/g, '');
const alphaNumericOnly = (value: string) => value.replace(/[^A-Za-z0-9]/g, '');

export const registerStudentSchema = z
    .object({
        name: z
            .string()
            .trim()
            .min(2, 'O nome deve ter pelo menos 2 caracteres')
            .max(150, 'O nome deve ter no máximo 150 caracteres'),
        email: z
            .string()
            .trim()
            .email('E-mail inválido')
            .max(255, 'O e-mail deve ter no máximo 255 caracteres'),
        password: z
            .string()
            .min(8, 'A senha deve ter pelo menos 8 caracteres')
            .max(72, 'A senha deve ter no máximo 72 caracteres')
            .regex(/[a-z]/, 'A senha deve conter pelo menos uma letra minúscula')
            .regex(/[A-Z]/, 'A senha deve conter pelo menos uma letra maiúscula')
            .regex(/\d/, 'A senha deve conter pelo menos um número')
            .regex(/[^A-Za-z0-9]/, 'A senha deve conter pelo menos um caractere especial'),
        confirmPassword: z.string().min(1, 'Confirme sua senha'),
        avatar: z.enum(avatarValues, { message: 'Selecione um avatar' }),
        cpf: z.string().refine((value) => cpf.isValid(value), {
            message: 'O CPF informado não é válido',
        }),
        rg: z
            .string()
            .refine((value) => /^[A-Z]{2}\d{8}$/.test(alphaNumericOnly(value).toUpperCase()), {
                message: 'O RG deve seguir o formato XX00000000',
            }),
        phone: z
            .string()
            .trim()
            .min(1, 'O telefone é obrigatório')
            .max(15, 'O telefone deve ter no máximo 15 caracteres')
            .refine((value) => {
                const digits = digitsOnly(value);
                return digits.length === 10 || digits.length === 11;
            }, 'O telefone deve conter 10 ou 11 dígitos'),
        zipCode: z
            .string()
            .trim()
            .min(1, 'O CEP é obrigatório')
            .refine((value) => {
                const digits = digitsOnly(value);
                return /^\d{8}$/.test(digits);
            }, 'O CEP deve conter exatamente 8 dígitos'),
        street: z
            .string()
            .trim()
            .min(1, 'A rua é obrigatória')
            .max(150, 'A rua deve ter no máximo 150 caracteres'),
        number: z
            .string()
            .trim()
            .min(1, 'O número é obrigatório')
            .max(20, 'O número deve ter no máximo 20 caracteres'),
        complement: z
            .string()
            .trim()
            .max(100, 'O complemento deve ter no máximo 100 caracteres')
            .optional(),
        neighborhood: z
            .string()
            .trim()
            .min(1, 'O bairro é obrigatório')
            .max(120, 'O bairro deve ter no máximo 120 caracteres'),
        city: z
            .string()
            .trim()
            .min(1, 'A cidade é obrigatória')
            .max(120, 'A cidade deve ter no máximo 120 caracteres'),
        institutionId: z.string().uuid('Selecione uma instituição'),
        courseId: z.string().uuid('Selecione um curso'),
    })
    .refine((formFields) => formFields.password === formFields.confirmPassword, {
        message: 'As senhas não conferem',
        path: ['confirmPassword'],
    });

export type RegisterStudentFormData = z.infer<typeof registerStudentSchema>;

import { z } from 'zod';

export const registerBenefitSchema = z.object({
    name: z
        .string()
        .trim()
        .min(1, 'O nome é obrigatório')
        .max(150, 'O nome deve ter no máximo 150 caracteres'),
    description: z
        .string()
        .trim()
        .min(1, 'A descrição é obrigatória')
        .max(2000, 'A descrição deve ter no máximo 2000 caracteres'),
    cost: z.coerce
        .number({ error: 'O custo deve ser um número' })
        .int('O custo deve ser um número inteiro')
        .min(1, 'O custo mínimo é 1 moeda'),
    image: z.instanceof(File, { message: 'Selecione uma imagem para a vantagem' }),
});

export type RegisterBenefitFormData = z.infer<typeof registerBenefitSchema>;

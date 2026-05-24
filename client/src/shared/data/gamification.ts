export interface LevelDefinition {
    level: number;
    name: string;
    minCoins: number;
}

export interface MedalDefinition {
    id: string;
    name: string;
    description: string;
    tone: 'gold' | 'red' | 'green' | 'blue' | 'purple' | 'teal';
}

export const LEVELS: LevelDefinition[] = [
    { level: 1, name: 'Iniciante', minCoins: 0 },
    { level: 2, name: 'Aprendiz', minCoins: 100 },
    { level: 3, name: 'Explorador', minCoins: 250 },
    { level: 4, name: 'Destaque', minCoins: 450 },
    { level: 5, name: 'Veterano', minCoins: 700 },
    { level: 6, name: 'Elite', minCoins: 1000 },
    { level: 7, name: 'Mestre', minCoins: 1500 },
    { level: 8, name: 'Especialista', minCoins: 2200 },
    { level: 9, name: 'Lendário', minCoins: 3000 },
    { level: 10, name: 'Luminar', minCoins: 5000 },
];

export const MEDALS: MedalDefinition[] = [
    {
        id: 'first-coin',
        name: 'Primeira Moeda',
        description: 'Recebeu sua primeira moeda no sistema.',
        tone: 'gold',
    },
    {
        id: 'marathoner',
        name: 'Maratonista',
        description: 'Recebeu moedas por 7 dias seguidos.',
        tone: 'red',
    },
    {
        id: 'participative',
        name: 'Participativo',
        description: 'Foi reconhecido por diferentes professores.',
        tone: 'blue',
    },
    {
        id: 'accumulator',
        name: 'Acumulador',
        description: 'Acumulou mais de 1000 moedas.',
        tone: 'green',
    },
];

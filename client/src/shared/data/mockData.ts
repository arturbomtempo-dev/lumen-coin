export type Role = 'student' | 'teacher' | 'company' | 'institution';

export interface Transaction {
    id: string;
    student: string;
    teacher: string;
    amount: number;
    reason: string;
    date: string;
}

export interface Benefit {
    id: string;
    name: string;
    description: string;
    cost: number;
    company: string;
    emoji: string;
    category: string;
}

export interface AppNotification {
    id: string;
    title: string;
    message: string;
    teacher?: string;
    amount?: number;
    date: string;
    isRead: boolean;
}

export const instituicoes = [
    'UNIFEI - Universidade Federal de Itajubá',
    'USP - Universidade de São Paulo',
    'UFMG - Universidade Federal de Minas Gerais',
    'PUC-Rio',
    'UNICAMP',
];

export const cursos = [
    'Engenharia de Software',
    'Ciência da Computação',
    'Sistemas de Informação',
    'Engenharia de Computação',
    'Design Digital',
];

export const alunos = [
    {
        id: 'a1',
        name: 'Maria Luiza Souza',
        course: 'Eng. de Software',
        level: 7,
        coins: 1280,
        xp: 72,
        className: 'ES-301',
    },
    {
        id: 'a2',
        name: 'João Pedro Alves',
        course: 'Ciência da Computação',
        level: 6,
        coins: 940,
        xp: 45,
        className: 'CC-201',
    },
    {
        id: 'a3',
        name: 'Amina Kassab',
        course: 'Design Digital',
        level: 8,
        coins: 1580,
        xp: 91,
        className: 'ES-301',
    },
    {
        id: 'a4',
        name: 'Rafael Costa',
        course: 'Eng. de Computação',
        level: 5,
        coins: 620,
        xp: 33,
        className: 'EC-401',
    },
    {
        id: 'a5',
        name: 'Beatriz Nunes',
        course: 'Sistemas de Informação',
        level: 7,
        coins: 1120,
        xp: 60,
        className: 'SI-202',
    },
    {
        id: 'a6',
        name: 'Lucas Ferreira',
        course: 'Eng. de Software',
        level: 4,
        coins: 380,
        xp: 21,
        className: 'ES-301',
    },
    {
        id: 'a7',
        name: 'Camila Rocha',
        course: 'Ciência da Computação',
        level: 6,
        coins: 870,
        xp: 52,
        className: 'CC-201',
    },
    {
        id: 'a8',
        name: 'Diego Almeida',
        course: 'Sistemas de Informação',
        level: 5,
        coins: 540,
        xp: 28,
        className: 'SI-202',
    },
];

export const teacherClasses = [
    {
        id: 'ES-301',
        name: 'Engenharia de Software · 3º Período',
        subject: 'Banco de Dados',
        period: '2025.1',
    },
    {
        id: 'CC-201',
        name: 'Ciência da Computação · 2º Período',
        subject: 'Algoritmos II',
        period: '2025.1',
    },
    {
        id: 'SI-202',
        name: 'Sistemas de Informação · 2º Período',
        subject: 'Engenharia de Requisitos',
        period: '2025.1',
    },
];

export const initialTransactions: Transaction[] = [
    {
        id: 't1',
        student: 'Maria Luiza Souza',
        teacher: 'Prof. Carlos M.',
        amount: 150,
        reason: 'Liderança no projeto de ciências',
        date: '2025-04-22',
    },
    {
        id: 't2',
        student: 'João Pedro Alves',
        teacher: 'Prof. Carlos M.',
        amount: 80,
        reason: 'Entrega antecipada do trabalho final',
        date: '2025-04-21',
    },
    {
        id: 't3',
        student: 'Amina Kassab',
        teacher: 'Prof. Carlos M.',
        amount: 200,
        reason: 'Ajudou colegas com dúvidas de cálculo',
        date: '2025-04-20',
    },
    {
        id: 't4',
        student: 'Rafael Costa',
        teacher: 'Prof. Carlos M.',
        amount: 50,
        reason: 'Participação excepcional em aula',
        date: '2025-04-19',
    },
];

export const initialBenefits: Benefit[] = [
    {
        id: 'v1',
        name: 'Fatia de Pizza',
        description: 'Um pedaço grande na cantina parceira',
        cost: 200,
        company: 'Byte Burger',
        emoji: '🍕',
        category: 'Comida',
    },
    {
        id: 'v2',
        name: 'Camiseta Retrô',
        description: 'Camiseta oficial edição 8-bit',
        cost: 850,
        company: 'Merch Store',
        emoji: '👕',
        category: 'Vestuário',
    },
    {
        id: 'v3',
        name: 'Headset Gamer',
        description: 'Fone com iluminação neon',
        cost: 1500,
        company: 'Tech Lab',
        emoji: '🎧',
        category: 'Tecnologia',
    },
    {
        id: 'v4',
        name: 'Ingresso Arcade',
        description: '50% OFF no Arcade World às sextas',
        cost: 400,
        company: 'Arcade World',
        emoji: '🎮',
        category: 'Lazer',
    },
    {
        id: 'v5',
        name: 'Combo Livraria',
        description: '10% OFF em livros técnicos',
        cost: 600,
        company: 'Livraria Byte',
        emoji: '📚',
        category: 'Educação',
    },
    {
        id: 'v6',
        name: 'Café Especial',
        description: 'Café grão a grão no parceiro',
        cost: 120,
        company: 'Cafeteria Pixel',
        emoji: '☕',
        category: 'Comida',
    },
];

export const initialNotifications: AppNotification[] = [
    {
        id: 'n1',
        title: 'Missão Concluída!',
        message: 'Você recebeu moedas por liderança.',
        teacher: 'Prof. Carlos M.',
        amount: 150,
        date: 'Há 2h',
        isRead: false,
    },
    {
        id: 'n2',
        title: 'Reconhecimento Recebido',
        message: 'Participação excepcional em aula.',
        teacher: 'Profa. Helena R.',
        amount: 50,
        date: 'Ontem',
        isRead: false,
    },
    {
        id: 'n3',
        title: 'Recompensa Desbloqueada',
        message: 'Cupom do Byte Burger gerado.',
        date: '2 dias',
        isRead: true,
    },
];

export const ranking = [...alunos].sort((a, b) => b.coins - a.coins);

export interface Course {
    id: string;
    name: string;
    period: string;
    duration: string;
    workload: number;
}

export interface InstitutionTeacher {
    id: string;
    name: string;
    email: string;
    degree: string;
    courseIds: string[];
}

export const institutionCourses: Course[] = [
    {
        id: 'cur-es',
        name: 'Engenharia de Software',
        period: '2025.1',
        duration: '8 semestres',
        workload: 3600,
    },
    {
        id: 'cur-cc',
        name: 'Ciência da Computação',
        period: '2025.1',
        duration: '8 semestres',
        workload: 3400,
    },
    {
        id: 'cur-si',
        name: 'Sistemas de Informação',
        period: '2025.1',
        duration: '8 semestres',
        workload: 3200,
    },
    {
        id: 'cur-dd',
        name: 'Design Digital',
        period: '2025.1',
        duration: '6 semestres',
        workload: 2400,
    },
];

export const institutionTeachers: InstitutionTeacher[] = [
    {
        id: 'p1',
        name: 'Prof. Carlos Mendes',
        email: 'carlos.m@unifei.edu.br',
        degree: 'Doutor',
        courseIds: ['cur-es', 'cur-cc'],
    },
    {
        id: 'p2',
        name: 'Profa. Helena Ribeiro',
        email: 'helena.r@unifei.edu.br',
        degree: 'Mestre',
        courseIds: ['cur-es'],
    },
    {
        id: 'p3',
        name: 'Prof. André Silveira',
        email: 'andre.s@unifei.edu.br',
        degree: 'Doutor',
        courseIds: ['cur-cc', 'cur-si'],
    },
    {
        id: 'p4',
        name: 'Profa. Júlia Tavares',
        email: 'julia.t@unifei.edu.br',
        degree: 'Mestre',
        courseIds: ['cur-dd'],
    },
];

export const cursoToId: Record<string, string> = {
    'Eng. de Software': 'cur-es',
    'Engenharia de Software': 'cur-es',
    'Ciência da Computação': 'cur-cc',
    'Sistemas de Informação': 'cur-si',
    'Design Digital': 'cur-dd',
    'Eng. de Computação': 'cur-cc',
};

export const achievements = [
    {
        id: 'c1',
        name: 'Primeira Moeda',
        description: 'Recebeu sua primeira Lumen',
        tone: 'gold',
        unlocked: true,
    },
    {
        id: 'c2',
        name: 'Maratonista',
        description: '7 dias de sequência',
        tone: 'red',
        unlocked: true,
    },
    {
        id: 'c3',
        name: 'Erudito',
        description: 'Acumulou 1000 moedas',
        tone: 'blue',
        unlocked: true,
    },
    { id: 'c4', name: 'Lendário', description: 'Alcance nível 10', tone: 'teal', unlocked: false },
];

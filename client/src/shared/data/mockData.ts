export type Role = 'aluno' | 'professor' | 'empresa' | 'instituicao';

export interface Transacao {
    id: string;
    aluno: string;
    professor: string;
    valor: number;
    motivo: string;
    data: string;
}

export interface Vantagem {
    id: string;
    nome: string;
    descricao: string;
    custo: number;
    empresa: string;
    emoji: string;
    categoria: string;
}

export interface Notificacao {
    id: string;
    titulo: string;
    mensagem: string;
    professor?: string;
    valor?: number;
    data: string;
    lida: boolean;
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
        nome: 'Maria Luiza Souza',
        curso: 'Eng. de Software',
        nivel: 7,
        moedas: 1280,
        xp: 72,
        turma: 'ES-301',
    },
    {
        id: 'a2',
        nome: 'João Pedro Alves',
        curso: 'Ciência da Computação',
        nivel: 6,
        moedas: 940,
        xp: 45,
        turma: 'CC-201',
    },
    {
        id: 'a3',
        nome: 'Amina Kassab',
        curso: 'Design Digital',
        nivel: 8,
        moedas: 1580,
        xp: 91,
        turma: 'ES-301',
    },
    {
        id: 'a4',
        nome: 'Rafael Costa',
        curso: 'Eng. de Computação',
        nivel: 5,
        moedas: 620,
        xp: 33,
        turma: 'EC-401',
    },
    {
        id: 'a5',
        nome: 'Beatriz Nunes',
        curso: 'Sistemas de Informação',
        nivel: 7,
        moedas: 1120,
        xp: 60,
        turma: 'SI-202',
    },
    {
        id: 'a6',
        nome: 'Lucas Ferreira',
        curso: 'Eng. de Software',
        nivel: 4,
        moedas: 380,
        xp: 21,
        turma: 'ES-301',
    },
    {
        id: 'a7',
        nome: 'Camila Rocha',
        curso: 'Ciência da Computação',
        nivel: 6,
        moedas: 870,
        xp: 52,
        turma: 'CC-201',
    },
    {
        id: 'a8',
        nome: 'Diego Almeida',
        curso: 'Sistemas de Informação',
        nivel: 5,
        moedas: 540,
        xp: 28,
        turma: 'SI-202',
    },
];

export const turmasProfessor = [
    {
        id: 'ES-301',
        nome: 'Engenharia de Software · 3º Período',
        disciplina: 'Banco de Dados',
        periodo: '2025.1',
    },
    {
        id: 'CC-201',
        nome: 'Ciência da Computação · 2º Período',
        disciplina: 'Algoritmos II',
        periodo: '2025.1',
    },
    {
        id: 'SI-202',
        nome: 'Sistemas de Informação · 2º Período',
        disciplina: 'Engenharia de Requisitos',
        periodo: '2025.1',
    },
];

export const transacoesIniciais: Transacao[] = [
    {
        id: 't1',
        aluno: 'Maria Luiza Souza',
        professor: 'Prof. Carlos M.',
        valor: 150,
        motivo: 'Liderança no projeto de ciências',
        data: '2025-04-22',
    },
    {
        id: 't2',
        aluno: 'João Pedro Alves',
        professor: 'Prof. Carlos M.',
        valor: 80,
        motivo: 'Entrega antecipada do trabalho final',
        data: '2025-04-21',
    },
    {
        id: 't3',
        aluno: 'Amina Kassab',
        professor: 'Prof. Carlos M.',
        valor: 200,
        motivo: 'Ajudou colegas com dúvidas de cálculo',
        data: '2025-04-20',
    },
    {
        id: 't4',
        aluno: 'Rafael Costa',
        professor: 'Prof. Carlos M.',
        valor: 50,
        motivo: 'Participação excepcional em aula',
        data: '2025-04-19',
    },
];

export const vantagensIniciais: Vantagem[] = [
    {
        id: 'v1',
        nome: 'Fatia de Pizza',
        descricao: 'Um pedaço grande na cantina parceira',
        custo: 200,
        empresa: 'Byte Burger',
        emoji: '🍕',
        categoria: 'Comida',
    },
    {
        id: 'v2',
        nome: 'Camiseta Retrô',
        descricao: 'Camiseta oficial edição 8-bit',
        custo: 850,
        empresa: 'Merch Store',
        emoji: '👕',
        categoria: 'Vestuário',
    },
    {
        id: 'v3',
        nome: 'Headset Gamer',
        descricao: 'Fone com iluminação neon',
        custo: 1500,
        empresa: 'Tech Lab',
        emoji: '🎧',
        categoria: 'Tecnologia',
    },
    {
        id: 'v4',
        nome: 'Ingresso Arcade',
        descricao: '50% OFF no Arcade World às sextas',
        custo: 400,
        empresa: 'Arcade World',
        emoji: '🎮',
        categoria: 'Lazer',
    },
    {
        id: 'v5',
        nome: 'Combo Livraria',
        descricao: '10% OFF em livros técnicos',
        custo: 600,
        empresa: 'Livraria Byte',
        emoji: '📚',
        categoria: 'Educação',
    },
    {
        id: 'v6',
        nome: 'Café Especial',
        descricao: 'Café grão a grão no parceiro',
        custo: 120,
        empresa: 'Cafeteria Pixel',
        emoji: '☕',
        categoria: 'Comida',
    },
];

export const notificacoesIniciais: Notificacao[] = [
    {
        id: 'n1',
        titulo: 'Missão Concluída!',
        mensagem: 'Você recebeu moedas por liderança.',
        professor: 'Prof. Carlos M.',
        valor: 150,
        data: 'Há 2h',
        lida: false,
    },
    {
        id: 'n2',
        titulo: 'Reconhecimento Recebido',
        mensagem: 'Participação excepcional em aula.',
        professor: 'Profa. Helena R.',
        valor: 50,
        data: 'Ontem',
        lida: false,
    },
    {
        id: 'n3',
        titulo: 'Recompensa Desbloqueada',
        mensagem: 'Cupom do Byte Burger gerado.',
        data: '2 dias',
        lida: true,
    },
];

export const ranking = [...alunos].sort((a, b) => b.moedas - a.moedas);

export interface Curso {
    id: string;
    nome: string;
    periodo: string;
    duracao: string;
    cargaHoraria: number;
}

export interface ProfessorInst {
    id: string;
    nome: string;
    email: string;
    titulacao: string;
    cursoIds: string[];
}

export const cursosInstituicao: Curso[] = [
    {
        id: 'cur-es',
        nome: 'Engenharia de Software',
        periodo: '2025.1',
        duracao: '8 semestres',
        cargaHoraria: 3600,
    },
    {
        id: 'cur-cc',
        nome: 'Ciência da Computação',
        periodo: '2025.1',
        duracao: '8 semestres',
        cargaHoraria: 3400,
    },
    {
        id: 'cur-si',
        nome: 'Sistemas de Informação',
        periodo: '2025.1',
        duracao: '8 semestres',
        cargaHoraria: 3200,
    },
    {
        id: 'cur-dd',
        nome: 'Design Digital',
        periodo: '2025.1',
        duracao: '6 semestres',
        cargaHoraria: 2400,
    },
];

export const professoresInstituicao: ProfessorInst[] = [
    {
        id: 'p1',
        nome: 'Prof. Carlos Mendes',
        email: 'carlos.m@unifei.edu.br',
        titulacao: 'Doutor',
        cursoIds: ['cur-es', 'cur-cc'],
    },
    {
        id: 'p2',
        nome: 'Profa. Helena Ribeiro',
        email: 'helena.r@unifei.edu.br',
        titulacao: 'Mestre',
        cursoIds: ['cur-es'],
    },
    {
        id: 'p3',
        nome: 'Prof. André Silveira',
        email: 'andre.s@unifei.edu.br',
        titulacao: 'Doutor',
        cursoIds: ['cur-cc', 'cur-si'],
    },
    {
        id: 'p4',
        nome: 'Profa. Júlia Tavares',
        email: 'julia.t@unifei.edu.br',
        titulacao: 'Mestre',
        cursoIds: ['cur-dd'],
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

export const conquistas = [
    {
        id: 'c1',
        nome: 'Primeira Moeda',
        desc: 'Recebeu sua primeira Lumen',
        tone: 'gold',
        desbloqueado: true,
    },
    { id: 'c2', nome: 'Maratonista', desc: '7 dias de sequência', tone: 'red', desbloqueado: true },
    { id: 'c3', nome: 'Erudito', desc: 'Acumulou 1000 moedas', tone: 'blue', desbloqueado: true },
    { id: 'c4', nome: 'Lendário', desc: 'Alcance nível 10', tone: 'teal', desbloqueado: false },
];

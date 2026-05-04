export type TeacherResponse = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    cpf: string;
    department?: string;
    balance: number;
    createdAt: string;
    updatedAt: string;
};

export type CompanyResponse = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    cnpj: string;
    createdAt: string;
    updatedAt: string;
};

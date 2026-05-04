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

export type InstitutionProfile = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    cnpj: string;
    zipCode: string;
    address: string;
    createdAt: string;
    updatedAt: string;
};

export type CourseShift = 'DAYTIME' | 'MORNING' | 'AFTERNOON' | 'NIGHT';

export type CourseResponse = {
    id: string;
    name: string;
    shift: CourseShift;
    periods: number;
    institutionId: string;
    createdAt: string;
    updatedAt: string;
};

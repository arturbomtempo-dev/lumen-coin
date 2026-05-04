import { api } from '@/shared/services/api';

export type LoginDto = {
    email: string;
    password: string;
};

export type RegisterStudentDto = {
    name: string;
    email: string;
    cpf: string;
    rg: string;
    address: string;
    institutionId: string;
    course: string;
    password: string;
};

export type RegisterCompanyDto = {
    name: string;
    email: string;
    password: string;
    cnpj: string;
};

export type RegisterInstitutionDto = {
    name: string;
    email: string;
    password: string;
    cnpj: string;
    zipCode: string;
    address: string;
};

export type AuthResponse = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    role: string;
};

export function loginRequest(dto: LoginDto) {
    return api.post<AuthResponse>('/auth/login', dto);
}

export function registerStudentRequest(dto: RegisterStudentDto) {
    return api.post('/auth/register/student', dto);
}

export function registerCompanyRequest(dto: RegisterCompanyDto) {
    return api.post('/companies', dto);
}

export function registerInstitutionRequest(dto: RegisterInstitutionDto) {
    return api.post('/institutions', dto);
}

export function logoutRequest() {
    return api.post('/auth/logout');
}

export type MeResponse = {
    id: string;
    name: string;
    email: string;
    role: string;
};

export function meRequest() {
    return api.get<MeResponse>('/auth/me');
}

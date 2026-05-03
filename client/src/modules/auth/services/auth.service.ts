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
};

export function loginRequest(dto: LoginDto) {
    return api.post('/auth/login', dto);
}

export function registerStudentRequest(dto: RegisterStudentDto) {
    return api.post('/auth/register/student', dto);
}

export function registerCompanyRequest(dto: RegisterCompanyDto) {
    return api.post('/auth/register/company', dto);
}

export function logoutRequest() {
    return api.post('/auth/logout');
}

export function meRequest() {
    return api.get('/auth/me');
}

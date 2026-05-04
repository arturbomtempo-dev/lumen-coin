import { api } from '@/shared/services/api';
import type { TeacherResponse, CompanyResponse } from './institution.types';

export type RegisterTeacherDto = {
    name: string;
    email: string;
    password: string;
    cpf: string;
    avatar: string;
    department?: string;
};

export type RegisterCompanyDto = {
    name: string;
    email: string;
    password: string;
    cnpj: string;
};

export function getTeachers() {
    return api.get<TeacherResponse[]>('/teachers');
}

export function registerTeacher(dto: RegisterTeacherDto) {
    return api.post<TeacherResponse>('/teachers', dto);
}

export function getCompanies() {
    return api.get<CompanyResponse[]>('/companies');
}

export function registerCompany(dto: RegisterCompanyDto) {
    return api.post<CompanyResponse>('/companies', dto);
}

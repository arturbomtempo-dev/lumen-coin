import { api } from '@/shared/services/api';

export type CompanyProfile = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    cnpj: string;
    createdAt: string;
    updatedAt: string;
};

export type UpdateCompanyDto = {
    name?: string;
    email?: string;
    cnpj?: string;
};

export function getCompany(id: string) {
    return api.get<CompanyProfile>(`/companies/${id}`);
}

export function updateCompany(id: string, dto: UpdateCompanyDto) {
    return api.put<CompanyProfile>(`/companies/${id}`, dto);
}

export function deleteCompany(id: string) {
    return api.delete(`/companies/${id}`);
}

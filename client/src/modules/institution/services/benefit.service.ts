import { api } from '@/shared/services/api';

export type BenefitResponse = {
    id: string;
    companyId: string | null;
    institutionId: string | null;
    name: string;
    description: string;
    imageUrl: string;
    cost: number;
    createdAt: string;
    updatedAt: string;
};

export type CreateBenefitDto = {
    name: string;
    description: string;
    cost: number;
};

export type UpdateBenefitDto = {
    name: string;
    description: string;
    cost: number;
};

export function createBenefit(dto: CreateBenefitDto, image: File) {
    const formData = new FormData();
    formData.append('name', dto.name);
    formData.append('description', dto.description);
    formData.append('cost', String(dto.cost));
    formData.append('image', image);
    return api.post<BenefitResponse>('/benefits/institution', formData);
}

export function getBenefitsByInstitution(institutionId: string) {
    return api.get<BenefitResponse[]>(`/benefits/institution/${institutionId}`);
}

export function updateBenefit(id: string, dto: UpdateBenefitDto, image?: File | null) {
    const formData = new FormData();
    formData.append('name', dto.name);
    formData.append('description', dto.description);
    formData.append('cost', String(dto.cost));
    if (image) {
        formData.append('image', image);
    }
    return api.put<BenefitResponse>(`/benefits/${id}`, formData);
}

export function deleteBenefit(id: string) {
    return api.delete(`/benefits/${id}`);
}

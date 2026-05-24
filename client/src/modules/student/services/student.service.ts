import { api } from '@/shared/services/api';

export type StudentProfile = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    cpf: string;
    rg: string;
    zipCode: string | null;
    address: string | null;
    balance: number;
    institutionId: string;
    courseId: string;
    createdAt: string;
    updatedAt: string;
};

export type UpdateStudentDto = {
    name?: string;
    email?: string;
    password?: string;
    avatar?: 'MARIO' | 'LUIGI' | 'PEACH' | 'TOAD' | 'YOSHI' | 'BOWSER';
    cpf?: string;
    rg?: string;
    zipCode?: string;
    address?: string;
    institutionId?: string;
    courseId?: string;
};

export function getStudent(id: string) {
    return api.get<StudentProfile>(`/students/${id}`);
}

export function updateStudent(id: string, dto: UpdateStudentDto) {
    return api.put<StudentProfile>(`/students/${id}`, dto);
}

export function changeStudentPassword(
    id: string,
    dto: { currentPassword: string; newPassword: string; confirmNewPassword: string }
) {
    return api.patch(`/students/${id}/password`, dto);
}

export function deleteStudent(id: string) {
    return api.delete(`/students/${id}`);
}

import { api } from '@/shared/services/api';

export type TeacherAvatar = 'MARIO' | 'LUIGI' | 'PEACH' | 'TOAD' | 'YOSHI' | 'BOWSER';

export type TeacherProfile = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    cpf: string;
    department: string;
    balance: number;
    institutionId: string;
    createdAt: string;
    updatedAt: string;
};

export type UpdateTeacherDto = {
    name?: string;
    email?: string;
    password?: string;
    avatar?: TeacherAvatar;
    cpf?: string;
    department?: string;
};

export type StudentSummary = {
    id: string;
    name: string;
    email: string;
    avatar: string;
    balance: number;
    institutionId: string;
    courseId: string;
};

export function getTeacher(id: string) {
    return api.get<TeacherProfile>(`/teachers/${id}`);
}

export function updateTeacher(id: string, dto: UpdateTeacherDto) {
    return api.put<TeacherProfile>(`/teachers/${id}`, dto);
}

export function deleteTeacher(id: string) {
    return api.delete(`/teachers/${id}`);
}

export function getStudentsByInstitution(institutionId: string) {
    return api.get<StudentSummary[]>(`/students/by-institution/${institutionId}`);
}

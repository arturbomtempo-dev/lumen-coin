import { api } from '@/shared/services/api';
import type {
    CompanyResponse,
    CourseResponse,
    InstitutionProfile,
    StudentResponse,
    TeacherResponse,
} from './institution.types';

export type RegisterTeacherDto = {
    name: string;
    email: string;
    cpf: string;
    department?: string;
};

export type RegisterCompanyDto = {
    name: string;
    email: string;
    password: string;
    cnpj: string;
};

export type UpdateInstitutionDto = {
    name: string;
    email: string;
    cnpj: string;
    zipCode: string;
    address: string;
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

export function getInstitutions() {
    return api.get<InstitutionProfile[]>('/institutions');
}

export function getInstitution(id: string) {
    return api.get<InstitutionProfile>(`/institutions/${id}`);
}

export function updateInstitution(id: string, dto: UpdateInstitutionDto) {
    return api.put<InstitutionProfile>(`/institutions/${id}`, dto);
}

export function changeInstitutionPassword(
    id: string,
    dto: { currentPassword: string; newPassword: string; confirmNewPassword: string }
) {
    return api.patch(`/institutions/${id}/password`, dto);
}

export function deleteInstitution(id: string) {
    return api.delete(`/institutions/${id}`);
}

export type UpdateTeacherDto = {
    name?: string;
    email?: string;
    department?: string;
};

export function updateTeacher(id: string, dto: UpdateTeacherDto) {
    return api.put<TeacherResponse>(`/teachers/${id}`, dto);
}

export function deleteTeacher(id: string) {
    return api.delete(`/teachers/${id}`);
}

export function getStudents() {
    return api.get<StudentResponse[]>('/students');
}

export type RegisterCourseDto = {
    name: string;
    shift: string;
    periods: number;
};

export type UpdateCourseDto = {
    name?: string;
    shift?: string;
    periods?: number;
};

export function getCourses() {
    return api.get<CourseResponse[]>('/courses');
}

export function getCourse(id: string) {
    return api.get<CourseResponse>(`/courses/${id}`);
}

export function createCourse(dto: RegisterCourseDto) {
    return api.post<CourseResponse>('/courses', dto);
}

export function updateCourse(id: string, dto: UpdateCourseDto) {
    return api.put<CourseResponse>(`/courses/${id}`, dto);
}

export function deleteCourse(id: string) {
    return api.delete(`/courses/${id}`);
}

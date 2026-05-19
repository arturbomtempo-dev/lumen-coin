import { api } from '@/shared/services/api';

export type AvatarType =
    | 'MARIO'
    | 'LUIGI'
    | 'PEACH'
    | 'TOAD'
    | 'YOSHI'
    | 'BOWSER'
    | 'COMPANY'
    | 'INSTITUTION';

export type LoginDto = {
    email: string;
    password: string;
};

export type RegisterStudentDto = {
    name: string;
    email: string;
    password: string;
    avatar: AvatarType;
    cpf: string;
    rg: string;
    zipCode?: string;
    address?: string;
    institutionId: string;
    courseId: string;
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

export type InstitutionOption = {
    id: string;
    name: string;
};

export type CourseOption = {
    id: string;
    name: string;
    shift: 'DAYTIME' | 'MORNING' | 'AFTERNOON' | 'NIGHT';
    periods: number;
    institutionId: string;
};

export function loginRequest(dto: LoginDto) {
    return api.post<AuthResponse>('/auth/login', dto, { skipGlobalErrorToast: true });
}

export function registerStudentRequest(dto: RegisterStudentDto) {
    return api.post('/students', dto);
}

export function getPublicInstitutions() {
    return api.get<InstitutionOption[]>('/institutions');
}

export function getPublicCourses() {
    return api.get<CourseOption[]>('/courses');
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
    return api.get<MeResponse>('/auth/me', {
        skipGlobalErrorToast: true,
        skipAuthRedirect: true,
    });
}

export function forgotPasswordRequest(email: string) {
    return api.post<{ message: string }>('/auth/forgot-password', { email });
}

export function resetPasswordRequest(token: string, newPassword: string) {
    return api.post<{ message: string }>('/auth/reset-password', { token, newPassword }, { skipGlobalErrorToast: true });
}

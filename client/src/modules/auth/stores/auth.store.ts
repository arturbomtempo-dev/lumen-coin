import { defineStore } from 'pinia';
import { ref } from 'vue';
import { meRequest, logoutRequest, type MeResponse } from '../services/auth.service';

export type UserRole = 'student' | 'teacher' | 'company' | 'institution';

export type AuthUser = {
    id: string;
    name: string;
    email: string;
    role: UserRole;
};

export const useAuthStore = defineStore('auth', () => {
    const isAuthenticated = ref(false);
    const user = ref<AuthUser | null>(null);
    const isLoading = ref(true);

    async function checkAuth() {
        try {
            const { data } = await meRequest();
            user.value = {
                id: data.id,
                name: data.name,
                email: data.email,
                role: data.role.toLowerCase() as UserRole,
            };
            isAuthenticated.value = true;
        } catch {
            isAuthenticated.value = false;
            user.value = null;
        } finally {
            isLoading.value = false;
        }
    }

    async function logout() {
        try {
            await logoutRequest();
        } finally {
            isAuthenticated.value = false;
            user.value = null;
        }
    }

    function setUser(authUser: AuthUser) {
        user.value = authUser;
        isAuthenticated.value = true;
    }

    return {
        isAuthenticated,
        user,
        isLoading,
        checkAuth,
        logout,
        setUser,
    };
});

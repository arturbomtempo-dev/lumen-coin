import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { getTeacher } from '@/modules/teacher/services/teacher.service';
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useTeacherStore = defineStore('teacher', () => {
    const name = ref('');
    const balance = ref(0);
    const institutionId = ref<string | null>(null);
    const firstLogin = ref(false);
    const isLoaded = ref(false);

    async function loadProfile() {
        const auth = useAuthStore();
        if (!auth.user) return;
        const { data } = await getTeacher(auth.user.id);
        name.value = data.name;
        balance.value = data.balance;
        institutionId.value = data.institutionId;
        firstLogin.value = data.firstLogin;
        isLoaded.value = true;
    }

    function setName(value: string) {
        name.value = value;
    }

    function setBalance(value: number) {
        balance.value = value;
    }

    function reset() {
        name.value = '';
        balance.value = 0;
        institutionId.value = null;
        firstLogin.value = false;
        isLoaded.value = false;
    }

    return { name, balance, institutionId, firstLogin, isLoaded, loadProfile, setName, setBalance, reset };
});

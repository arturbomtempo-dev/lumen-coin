import { useAuthStore } from '@/modules/auth/stores/auth.store';
import { getStudent } from '@/modules/student/services/student.service';
import type { MarioCharacter } from '@/shared/data/characters';
import {
    initialNotifications,
    initialTransactions,
    type AppNotification,
    type Transaction,
} from '@/shared/data/mockData';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

interface Coupon {
    id: string;
    code: string;
    benefit: string;
    date: string;
    used: boolean;
}

function generateCouponCode() {
    return (
        'LUMEN-' +
        Math.random().toString(36).slice(2, 7).toUpperCase() +
        '-' +
        Math.random().toString(36).slice(2, 5).toUpperCase()
    );
}

export const useStudentStore = defineStore('student', () => {
    const name = ref('Maria L.');
    const balance = ref(1280);
    const level = ref(7);
    const xp = ref(72);
    const character = ref<MarioCharacter>('peach');
    const notifications = ref<AppNotification[]>([...initialNotifications]);
    const transactions = ref<Transaction[]>(
        initialTransactions.filter((t) => t.student === 'Maria Luiza Souza')
    );
    const coupons = ref<Coupon[]>([]);

    const unreadCount = computed(() => notifications.value.filter((n) => !n.isRead).length);

    function setCharacter(c: MarioCharacter) {
        character.value = c;
    }

    function setName(value: string) {
        name.value = value;
    }

    function setBalance(value: number) {
        balance.value = value;
    }

    function spend(amount: number, benefitName: string): string {
        const code = generateCouponCode();
        balance.value = Math.max(0, balance.value - amount);
        coupons.value.unshift({
            id: code,
            code,
            benefit: benefitName,
            date: new Date().toLocaleDateString('pt-BR'),
            used: false,
        });
        notifications.value.unshift({
            id: 'n' + Date.now(),
            title: 'Recompensa Desbloqueada',
            message: `Cupom ${benefitName} gerado.`,
            date: 'Agora',
            isRead: false,
        });
        return code;
    }

    function receive(amount: number, reason: string, teacher: string) {
        balance.value += amount;
        xp.value = Math.min(100, xp.value + Math.floor(amount / 10));
        notifications.value.unshift({
            id: 'n' + Date.now(),
            title: 'Missão Concluída!',
            message: reason,
            teacher,
            amount,
            date: 'Agora',
            isRead: false,
        });
        transactions.value.unshift({
            id: 't' + Date.now(),
            student: 'Maria Luiza Souza',
            teacher,
            amount,
            reason,
            date: new Date().toISOString().slice(0, 10),
        });
    }

    function markAsRead() {
        notifications.value = notifications.value.map((n) => ({ ...n, isRead: true }));
    }

    async function loadProfile() {
        const auth = useAuthStore();
        if (!auth.user) return;
        const { data } = await getStudent(auth.user.id);
        name.value = data.name;
        balance.value = data.balance;
    }

    return {
        name,
        balance,
        level,
        xp,
        character,
        notifications,
        transactions,
        coupons,
        unreadCount,
        setName,
        setBalance,
        setCharacter,
        spend,
        receive,
        markAsRead,
        loadProfile,
    };
});

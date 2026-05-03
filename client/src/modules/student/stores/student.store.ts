import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import {
    notificacoesIniciais,
    transacoesIniciais,
    type Transacao,
    type Notificacao,
} from '@/shared/data/mockData';
import type { MarioCharacter } from '@/shared/data/characters';

interface Cupom {
    id: string;
    codigo: string;
    vantagem: string;
    data: string;
    usado: boolean;
}

function generateCoupon() {
    return (
        'LUMEN-' +
        Math.random().toString(36).slice(2, 7).toUpperCase() +
        '-' +
        Math.random().toString(36).slice(2, 5).toUpperCase()
    );
}

export const useStudentStore = defineStore('student', () => {
    const nome = ref('Maria L.');
    const saldo = ref(1280);
    const nivel = ref(7);
    const xp = ref(72);
    const personagem = ref<MarioCharacter>('peach');
    const notificacoes = ref<Notificacao[]>([...notificacoesIniciais]);
    const transacoes = ref<Transacao[]>(
        transacoesIniciais.filter((t) => t.aluno === 'Maria Luiza Souza')
    );
    const cupons = ref<Cupom[]>([]);

    const unreadCount = computed(() => notificacoes.value.filter((n) => !n.lida).length);

    function setPersonagem(p: MarioCharacter) {
        personagem.value = p;
    }

    function gastar(valor: number, vantagem: string): string {
        const codigo = generateCoupon();
        saldo.value = Math.max(0, saldo.value - valor);
        cupons.value.unshift({
            id: codigo,
            codigo,
            vantagem,
            data: new Date().toLocaleDateString('pt-BR'),
            usado: false,
        });
        notificacoes.value.unshift({
            id: 'n' + Date.now(),
            titulo: 'Recompensa Desbloqueada',
            mensagem: `Cupom ${vantagem} gerado.`,
            data: 'Agora',
            lida: false,
        });
        return codigo;
    }

    function receber(valor: number, motivo: string, professor: string) {
        saldo.value += valor;
        xp.value = Math.min(100, xp.value + Math.floor(valor / 10));
        notificacoes.value.unshift({
            id: 'n' + Date.now(),
            titulo: 'Missão Concluída!',
            mensagem: motivo,
            professor,
            valor,
            data: 'Agora',
            lida: false,
        });
        transacoes.value.unshift({
            id: 't' + Date.now(),
            aluno: 'Maria Luiza Souza',
            professor,
            valor,
            motivo,
            data: new Date().toISOString().slice(0, 10),
        });
    }

    function marcarLidas() {
        notificacoes.value = notificacoes.value.map((n) => ({ ...n, lida: true }));
    }

    return {
        nome,
        saldo,
        nivel,
        xp,
        personagem,
        notificacoes,
        transacoes,
        cupons,
        unreadCount,
        setPersonagem,
        gastar,
        receber,
        marcarLidas,
    };
});

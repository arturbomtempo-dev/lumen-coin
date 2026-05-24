import { LEVELS, MEDALS } from '@/shared/data/gamification';
import type { Transaction } from '@/shared/data/mockData';
import { computed, type Ref } from 'vue';

export function useGamification(balance: Ref<number>, transactions: Ref<Transaction[]>) {
    const currentLevel = computed(() => {
        for (let i = LEVELS.length - 1; i >= 0; i--) {
            if (balance.value >= LEVELS[i].minCoins) return LEVELS[i];
        }
        return LEVELS[0];
    });

    const nextLevel = computed(() => {
        const idx = LEVELS.findIndex((l) => l.level === currentLevel.value.level);
        return LEVELS[idx + 1] ?? null;
    });

    const progressPercent = computed(() => {
        if (!nextLevel.value) return 100;
        const earned = balance.value - currentLevel.value.minCoins;
        const needed = nextLevel.value.minCoins - currentLevel.value.minCoins;
        return Math.min(100, Math.floor((earned / needed) * 100));
    });

    const coinsToNextLevel = computed(() => {
        if (!nextLevel.value) return 0;
        return nextLevel.value.minCoins - balance.value;
    });

    const hasFirstCoin = computed(() => balance.value > 0);

    const hasMarathoner = computed(() => {
        if (transactions.value.length === 0) return false;
        const uniqueDates = [...new Set(transactions.value.map((t) => t.date))].sort();
        if (uniqueDates.length < 7) return false;
        for (let i = 0; i <= uniqueDates.length - 7; i++) {
            let streak = true;
            for (let j = 1; j < 7; j++) {
                const d1 = new Date(uniqueDates[i + j - 1] + 'T00:00:00');
                const d2 = new Date(uniqueDates[i + j] + 'T00:00:00');
                const diffDays = Math.round((d2.getTime() - d1.getTime()) / 86_400_000);
                if (diffDays !== 1) {
                    streak = false;
                    break;
                }
            }
            if (streak) return true;
        }
        return false;
    });

    const hasParticipative = computed(() => {
        const teachers = new Set(transactions.value.map((t) => t.teacher));
        return teachers.size >= 3;
    });

    const hasAccumulator = computed(() => balance.value >= 1000);

    const unlockedIds = computed(() => {
        const ids = new Set<string>();
        if (hasFirstCoin.value) ids.add('first-coin');
        if (hasMarathoner.value) ids.add('marathoner');
        if (hasParticipative.value) ids.add('participative');
        if (hasAccumulator.value) ids.add('accumulator');
        return ids;
    });

    const medalsWithStatus = computed(() =>
        MEDALS.map((m) => ({ ...m, unlocked: unlockedIds.value.has(m.id) }))
    );

    return {
        currentLevel,
        nextLevel,
        progressPercent,
        coinsToNextLevel,
        medalsWithStatus,
    };
}

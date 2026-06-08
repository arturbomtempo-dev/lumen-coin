import { api } from '@/shared/services/api';

export type BenefitRedemptionResponse = {
    id: string;
    studentId: string;
    benefitId: string;
    companyId: string;
    couponCode: string;
    coinsSpent: number;
    status: 'PENDING' | 'USED';
    redeemedAt: string;
    usedAt: string | null;
};

export function redeemBenefit(benefitId: string) {
    return api.post<BenefitRedemptionResponse>('/benefit-redemptions', { benefitId });
}

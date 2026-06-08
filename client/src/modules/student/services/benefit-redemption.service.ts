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

export type RedeemedBenefitIdsResponse = {
    pendingBenefitIds: string[];
    usedBenefitIds: string[];
};

export function redeemBenefit(benefitId: string) {
    return api.post<BenefitRedemptionResponse>('/benefit-redemptions', { benefitId });
}

export function getRedeemedBenefitIds() {
    return api.get<RedeemedBenefitIdsResponse>('/benefit-redemptions/redeemed-benefit-ids');
}

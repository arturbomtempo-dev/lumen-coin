import { api } from '@/shared/services/api';

export type BenefitRedemptionResponse = {
    id: string;
    studentId: string;
    studentName: string;
    benefitId: string;
    benefitName: string;
    companyId: string | null;
    companyName: string | null;
    institutionId: string | null;
    institutionName: string | null;
    couponCode: string;
    coinsSpent: number;
    status: 'PENDING' | 'USED';
    redeemedAt: string;
    usedAt: string | null;
};

export function getMyRedemptions() {
    return api.get<BenefitRedemptionResponse[]>('/benefit-redemptions/my');
}

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

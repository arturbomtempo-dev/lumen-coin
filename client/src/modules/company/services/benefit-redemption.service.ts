import { api } from '@/shared/services/api';

export type ValidateBenefitRedemptionResponse = {
    id: string;
    studentName: string;
    benefitName: string;
    coinsSpent: number;
    redeemedAt: string;
    couponCode: string;
};

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

export function validateCoupon(couponCode: string) {
    return api.get<ValidateBenefitRedemptionResponse>(
        `/benefit-redemptions/validate/${encodeURIComponent(couponCode)}`,
        { skipGlobalErrorToast: true },
    );
}

export function confirmRedemption(couponCode: string, usageNotes: string) {
    return api.patch<BenefitRedemptionResponse>('/benefit-redemptions/use', {
        couponCode,
        usageNotes,
    });
}

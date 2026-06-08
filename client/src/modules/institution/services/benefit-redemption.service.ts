import { api } from '@/shared/services/api';

export type RedeemedBenefitIdsResponse = {
    pendingBenefitIds: string[];
    usedBenefitIds: string[];
};

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
    companyId: string | null;
    institutionId: string | null;
    couponCode: string;
    coinsSpent: number;
    status: 'PENDING' | 'USED';
    redeemedAt: string;
    usedAt: string | null;
};

export function getRedeemedBenefitIds() {
    return api.get<RedeemedBenefitIdsResponse>('/benefit-redemptions/redeemed-benefit-ids');
}

export function getInstitutionRedemptions() {
    return api.get<BenefitRedemptionResponse[]>('/benefit-redemptions/institution');
}

export function validateCoupon(couponCode: string) {
    return api.get<ValidateBenefitRedemptionResponse>(
        `/benefit-redemptions/validate-institution/${encodeURIComponent(couponCode)}`,
        { skipGlobalErrorToast: true },
    );
}

export function confirmRedemption(couponCode: string, usageNotes: string) {
    return api.patch<BenefitRedemptionResponse>('/benefit-redemptions/use-institution', {
        couponCode,
        usageNotes,
    });
}

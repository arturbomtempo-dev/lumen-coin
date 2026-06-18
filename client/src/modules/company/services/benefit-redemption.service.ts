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
    studentName: string;
    benefitId: string;
    benefitName: string;
    companyId: string | null;
    companyName?: string | null;
    institutionId?: string | null;
    institutionName?: string | null;
    couponCode: string;
    coinsSpent: number;
    status: 'PENDING' | 'USED';
    redeemedAt: string;
    usedAt: string | null;
};

export function getCompanyRedemptions() {
    return api.get<BenefitRedemptionResponse[]>('/benefit-redemptions/company');
}

export function getRedeemedBenefitIds() {
    return api.get<RedeemedBenefitIdsResponse>('/benefit-redemptions/redeemed-benefit-ids');
}

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

export function denyRedemption(couponCode: string, denialReason: string) {
    return api.patch('/benefit-redemptions/deny', { couponCode, denialReason });
}

export function scanRedemption(couponCode: string) {
    return api.patch<BenefitRedemptionResponse>('/benefit-redemptions/scan', { couponCode });
}

import { api } from '@/shared/services/api';

export type CoinTransferResponse = {
    id: string;
    senderId: string;
    senderName: string;
    recipientId: string;
    recipientName: string;
    amount: number;
    message: string;
    sentAt: string;
};

export type SendCoinTransferDto = {
    studentId: string;
    amount: number;
    message: string;
};

export function sendCoins(dto: SendCoinTransferDto) {
    return api.post<CoinTransferResponse>('/coin-transfers', dto);
}

export function getSentTransfers() {
    return api.get<CoinTransferResponse[]>('/coin-transfers/sent');
}

export function getReceivedTransfers() {
    return api.get<CoinTransferResponse[]>('/coin-transfers/received');
}

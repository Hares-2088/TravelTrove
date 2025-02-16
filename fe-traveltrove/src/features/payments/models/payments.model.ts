import { TravelerRequestModel } from "../../travelers/model/traveler.model";

export interface PaymentRequestModel {
    amount: number;
    currency: string;
    packageId: string;
    successUrl: string;
    cancelUrl: string;
    bookingId: string;
    travelers: TravelerRequestModel[]; // List of travelers
}

export interface PaymentResponseModel {
    paymentId: string;
    sessionId: string;
    bookingId: string;
    amount: number;
    currency: string;
    paymentStatus: string;
}
export interface NotificationRequestModel {
    to: string;
    subject: string;
    messageContent: string;
}

export interface NotificationContactusRequestModel {
    to: string;
    firstName: string;
    lastName: string;
    email: string;
    subject: string;
    messageContent: string;
}

export interface NotificationResponseModel {
    notificationId: string;
    to: string;
    subject: string;
    messageContent: string;
    sentAt: string;
}

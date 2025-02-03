import {
    NotificationContactusRequestModel,
    NotificationRequestModel,
    NotificationResponseModel
} from '../models/notification.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

// Notification API Hook
export const useNotificationsApi = () => {
    const axiosInstance = useAxiosInstance(); // Use Axios Hook

    // Fetch All Notifications (SSE Stream)
    const getAllNotifications = async (): Promise<NotificationResponseModel[]> => {
        const notifications: NotificationResponseModel[] = [];

        const response = await axiosInstance.get('/notifications', {
            responseType: 'text',
            headers: {
                Accept: 'text/event-stream',
            },
        });

        // Parse SSE Stream Data
        const lines = response.data.split('\n');
        for (const line of lines) {
            const trimmedLine = line.trim();
            if (trimmedLine.startsWith('data:')) {
                try {
                    const notification = JSON.parse(trimmedLine.substring(5).trim());
                    notifications.push(notification);
                } catch (error) {
                    console.error('Error parsing line:', trimmedLine, error);
                }
            }
        }
        return notifications;
    };

    // Fetch Notification by ID
    const getNotificationById = async (notificationId: string): Promise<NotificationResponseModel> => {
        const response = await axiosInstance.get<NotificationResponseModel>(`/notifications/${notificationId}`);
        return response.data;
    };

    // Send a Custom Notification
    const sendCustomNotification = async (notification: NotificationRequestModel): Promise<string> => {
        const response = await axiosInstance.post<string>('/notifications', notification);
        return response.data;
    };

    // Send a Contact Us Notification
    const sendContactUsNotification = async (notification: NotificationContactusRequestModel): Promise<string> => {
        const response = await axiosInstance.post<string>('/notifications/contact-us', notification);
        return response.data;
    }

    // Delete a Notification
    const deleteNotification = async (notificationId: string): Promise<void> => {
        await axiosInstance.delete(`/notifications/${notificationId}`);
    };

    return {
        getAllNotifications,
        getNotificationById,
        sendCustomNotification,
        sendContactUsNotification,
        deleteNotification,
    };
};

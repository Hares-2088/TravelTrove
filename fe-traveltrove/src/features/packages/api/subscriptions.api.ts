import { SubscriptionResponseModel } from "../models/subscription.model";
import { useAxiosInstance } from "../../../shared/axios/useAxiosInstance";

export const useSubscriptionsApi = () => {
    const axiosInstance = useAxiosInstance();

    // Check if the user is subscribed to a package
    const checkSubscription = async (userId: string, packageId: string): Promise<boolean> => {
        const encodedUserId = encodeURIComponent(userId);
        try {
            const response = await axiosInstance.get(`/subscriptions/user/${encodedUserId}`, {
            });

            // Woopee
            const lines = response.data.split('\n');
            const subscriptions: SubscriptionResponseModel[] = [];

            for (const line of lines) {
                const trimmedLine = line.trim();
                if (trimmedLine.startsWith('data:')) {
                    try {
                        const subscription = JSON.parse(trimmedLine.substring(5).trim());
                        subscriptions.push(subscription);
                    } catch (error) {
                    }
                }
            }
            return subscriptions.some(sub => sub.packageId === packageId);
        } catch (error) {
            return false;
        }
    };

    // Subscribe the user to a package
    const subscribeToPackage = async (userId: string, packageId: string): Promise<void> => {
        await axiosInstance.post("/subscriptions/subscribe", null, { params: { userId: userId, packageId } });
    };

    // Unsubscribe the user from a package
    const unsubscribeFromPackage = async (userId: string, packageId: string): Promise<void> => {
        await axiosInstance.delete("/subscriptions/unsubscribe", { params: { userId: userId, packageId } });
    };

    return {
        checkSubscription,
        subscribeToPackage,
        unsubscribeFromPackage,
    };
};
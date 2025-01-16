import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';
import { UserResponseModel } from '../model/users.model';

export const useUsersApi = () => {
    const axiosInstance = useAxiosInstance();

    const getUser = async (userId: string): Promise<UserResponseModel> => {
        const encodedUserId = encodeURIComponent(userId);
        const response = await axiosInstance.get<UserResponseModel>(`/users/${encodedUserId}`);
        return response.data;
    }

    const loginUser = async (userId: string): Promise<UserResponseModel> => {
        const encodedUserId = encodeURIComponent(userId);
        const response = await axiosInstance.post<UserResponseModel>(`/users/${encodedUserId}/login`);
        return response.data;
    };

    // Kinda wonky, this can be improved by using Token Claims to sync instead of fetching from Auth0 each time
    const syncUser = async (userId: string): Promise<UserResponseModel> => {
        const encodedUserId = encodeURIComponent(userId);
        const response = await axiosInstance.put<UserResponseModel>(`/users/${encodedUserId}/sync`);
        return response.data;
    };

    const getUserById = async (userId: string): Promise<UserResponseModel> => {
        const encodedUserId = encodeURIComponent(userId);
        const response = await axiosInstance.get<UserResponseModel>(`/users/${encodedUserId}`);
        return response.data;
    };

    return {
        getUser,
        loginUser,
        syncUser,
        getUserById,
    };
};

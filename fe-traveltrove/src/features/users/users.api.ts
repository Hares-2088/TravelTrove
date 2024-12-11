import { useAxiosInstance} from "../../shared/axios/useAxiosInstance";

export const useUsersApi = () => {
    const axiosInstance = useAxiosInstance();

    const registerUser = async (user: any) => {
        const response = await axiosInstance.post("/users/register", user);
        return response.data;
    };

    const getUserProfile = async () => {
        const response = await axiosInstance.get("/users/profile");
        return response.data;
    };

    const getAllUsers = async () => {
        const response = await axiosInstance.get("/users");
        return response.data;
    };

    const getUserByUserId = async (userId: string) => {
        const response = await axiosInstance.get(`/users/${userId}`);
        return response.data;
    };

    return {
        registerUser,
        getUserProfile,
        getAllUsers,
        getUserByUserId,
    };
};

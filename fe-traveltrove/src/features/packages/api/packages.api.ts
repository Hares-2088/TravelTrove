import { PackageRequestModel, PackageResponseModel, PackageStatus } from '../models/package.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

// Package API Hook
export const usePackagesApi = () => {
    const axiosInstance = useAxiosInstance(); // Use Axios Hook

    // Fetch All Packages (SSE Stream)
    const getAllPackages = async (filters?: { tourId?: string }): Promise<PackageResponseModel[]> => {
        const packages: PackageResponseModel[] = [];

        const response = await axiosInstance.get('/packages', {
            params: filters,
            responseType: 'text',
            headers: {
                Accept: 'text/event-stream',
            },
        });

        // Parse Server-Sent Events (SSE)
        const lines = response.data.split('\n');
        for (const line of lines) {
            const trimmedLine = line.trim();
            if (trimmedLine.startsWith('data:')) {
                try {
                    const pkg = JSON.parse(trimmedLine.substring(5).trim());
                    packages.push(pkg);
                } catch (error) {
                    console.error('Error parsing line:', trimmedLine, error);
                }
            }
        }

        return packages;
    };

    // Fetch Package by ID
    const getPackageById = async (packageId: string): Promise<PackageResponseModel> => {
        const response = await axiosInstance.get<PackageResponseModel>(`/packages/${packageId}`);
        return response.data;
    };

    // Add a New Package
    const addPackage = async (pkg: PackageRequestModel): Promise<PackageResponseModel> => {
        const response = await axiosInstance.post<PackageResponseModel>('/packages', pkg);
        return response.data;
    };

    // Update an Existing Package
    const updatePackage = async (
        packageId: string,
        pkg: PackageRequestModel,
        notificationDetails?: string
    ): Promise<PackageResponseModel> => {
        const url = notificationDetails 
            ? `/packages/${packageId}?notificationDetails=${encodeURIComponent(notificationDetails)}` 
            : `/packages/${packageId}`;
        const response = await axiosInstance.put<PackageResponseModel>(url, pkg);
        return response.data;
    };

    // Delete a Package by ID
    const deletePackage = async (packageId: string): Promise<PackageResponseModel> => {
        const response = await axiosInstance.delete<PackageResponseModel>(`/packages/${packageId}`);
        return response.data;
    };

    const updatePackageStatus = async (
        packageId: string,
        status: PackageStatus
    ): Promise<PackageResponseModel> => {
        if (!status) {
            console.error("❌ Invalid status provided:", status);
            return Promise.reject("Invalid status provided");
        }

        const payload = { status }; // Ensure this key matches the backend DTO



        try {
            const currentPackage = await getPackageById(packageId);
            

            const response = await axiosInstance.patch<PackageResponseModel>(
                `/packages/${packageId}/status`,
                payload
            );


            return response.data;
        } catch (error) {
            console.error(`❌ Error updating package status for ${packageId}:`, error);
            throw error;
        }
    };

    return {
        getAllPackages,
        getPackageById,
        addPackage,
        updatePackage,
        updatePackageStatus,
        deletePackage,
    };
};

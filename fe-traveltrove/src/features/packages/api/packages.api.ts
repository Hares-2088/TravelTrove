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
        pkg: PackageRequestModel
    ): Promise<PackageResponseModel> => {
        const response = await axiosInstance.put<PackageResponseModel>(`/packages/${packageId}`, pkg);
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
            console.error(`❌ updatePackageStatus called with invalid status: ${status}`);
            return Promise.reject("Invalid status provided");
        }

        const payload = { packageStatus: status }; // Ensure correct request structure
        console.log(`📢 Sending request to update package status:`, payload);

        const response = await axiosInstance.patch<PackageResponseModel>(
            `/packages/${packageId}/status`,
            payload
        );

        console.log(`✅ Successfully updated package status:`, response.data);
        return response.data;
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

import {
  PackageRequestModel,
  PackageResponseModel,
} from '../models/package.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

// Define the return type interface
interface PackagesApi {
  getAllPackages: (filters?: {
    tourId?: string;
  }) => Promise<PackageResponseModel[]>;
  getPackageById: (packageId: string) => Promise<PackageResponseModel>;
  addPackage: (pkg: PackageRequestModel) => Promise<PackageResponseModel>;
  updatePackage: (
    packageId: string,
    pkg: PackageRequestModel
  ) => Promise<PackageResponseModel>;
  deletePackage: (packageId: string) => Promise<PackageResponseModel>;
  decreaseAvailableSeats: (
    packageId: string,
    quantity: number
  ) => Promise<PackageResponseModel>;
  increaseAvailableSeats: (
    packageId: string,
    quantity: number
  ) => Promise<PackageResponseModel>;
  getPackageStatus: (pkg: PackageResponseModel) => string;
}

// Package API Hook
export const usePackagesApi = (): PackagesApi => {
  // Explicit return type
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Packages (SSE Stream)
  const getAllPackages = async (filters?: {
    tourId?: string;
  }): Promise<PackageResponseModel[]> => {
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
          pkg.status = getPackageStatus(pkg); // Add status to package
          packages.push(pkg);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }

    return packages;
  };

  // Fetch Package by ID
  const getPackageById = async (
    packageId: string
  ): Promise<PackageResponseModel> => {
    const response = await axiosInstance.get<PackageResponseModel>(
      `/packages/${packageId}`
    );
    return response.data;
  };

  // Add a New Package
  const addPackage = async (
    pkg: PackageRequestModel
  ): Promise<PackageResponseModel> => {
    const response = await axiosInstance.post<PackageResponseModel>(
      '/packages',
      pkg
    );
    return response.data;
  };

  // Update an Existing Package
  const updatePackage = async (
    packageId: string,
    pkg: PackageRequestModel
  ): Promise<PackageResponseModel> => {
    const response = await axiosInstance.put<PackageResponseModel>(
      `/packages/${packageId}`,
      pkg
    );
    return response.data;
  };

  // Delete a Package by ID
  const deletePackage = async (
    packageId: string
  ): Promise<PackageResponseModel> => {
    const response = await axiosInstance.delete<PackageResponseModel>(
      `/packages/${packageId}`
    );
    return response.data;
  };

  const decreaseAvailableSeats = async (
    packageId: string,
    quantity: number
  ): Promise<PackageResponseModel> => {
    const response = await axiosInstance.patch<PackageResponseModel>(
      `/packages/${packageId}/decreaseAvailableSeats?quantity=${quantity}`
    );
    return response.data;
  };

  const increaseAvailableSeats = async (
    packageId: string,
    quantity: number
  ): Promise<PackageResponseModel> => {
    const response = await axiosInstance.patch<PackageResponseModel>(
      `/packages/${packageId}/increaseAvailableSeats?quantity=${quantity}`
    );
    return response.data;
  };

  const getPackageStatus = (pkg: PackageResponseModel): string => {
    if (!pkg) {
      return 'UNAVAILABLE';
    }

    const packageEndDate = new Date(pkg.endDate); // End date
    const today = new Date(); // Today's date

    if (packageEndDate < today) {
      return 'EXPIRED';
    } else if (pkg.availableSeats === 0) {
      return 'FULL';
    } else if (pkg.availableSeats <= pkg.totalSeats * 0.1) {
      return 'NEAR CAPACITY';
    } else {
      return 'AVAILABLE';
    }
  };

  return {
    getAllPackages,
    getPackageById,
    addPackage,
    updatePackage,
    deletePackage,
    decreaseAvailableSeats,
    increaseAvailableSeats,
    getPackageStatus,
  };
};

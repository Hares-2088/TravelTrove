import {
  TravelerRequestModel,
  TravelerResponseModel,
} from '../model/traveler.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

export const useTravelersApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Travelers (SSE Stream)
  const getAllTravelers = async (filters?: {
    firstName?: string;
  }): Promise<TravelerResponseModel[]> => {
    const travelers: TravelerResponseModel[] = [];

    const response = await axiosInstance.get('/travelers', {
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
          const traveler = JSON.parse(trimmedLine.substring(5).trim());
          travelers.push(traveler);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }

    return travelers;
  };

  // Fetch Traveler by ID
  const getTravelerById = async (
    travelerId: string
  ): Promise<TravelerResponseModel> => {
    const response = await axiosInstance.get<TravelerResponseModel>(
      `/travelers/${travelerId}`
    );
    return response.data;
  };

  // Add a New Traveler
  const addTraveler = async (
    traveler: TravelerRequestModel
  ): Promise<TravelerResponseModel> => {
    const response = await axiosInstance.post<TravelerResponseModel>(
      '/travelers',
      traveler
    );
    return response.data;
  };

  // Update an Existing Traveler
  const updateTraveler = async (
    travelerId: string,
    traveler: TravelerRequestModel
  ): Promise<TravelerResponseModel> => {
    const response = await axiosInstance.put<TravelerResponseModel>(
      `/travelers/${travelerId}`,
      traveler
    );
    return response.data;
  };

  // Delete a Traveler by ID
  const deleteTraveler = async (
    travelerId: string
  ): Promise<TravelerResponseModel> => {
    const response = await axiosInstance.delete<TravelerResponseModel>(
      `/travelers/${travelerId}`
    );
    return response.data;
  };

  return {
    getAllTravelers,
    getTravelerById,
    addTraveler,
    updateTraveler,
    deleteTraveler,
  };
};

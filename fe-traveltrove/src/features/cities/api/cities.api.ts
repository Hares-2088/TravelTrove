import { CityRequestModel, CityResponseModel } from '../models/city.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

export const useCitiesApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Cities (SSE Stream)
  const getAllCities = async (): Promise<CityResponseModel[]> => {
    const cities: CityResponseModel[] = [];

    const response = await axiosInstance.get('/cities', {
      responseType: 'text',
      headers: {
        Accept: 'text/event-stream',
      },
    });

    // Parse Server-Sent Events (SSE) Data
    const lines = response.data.split('\n');
    for (const line of lines) {
      const trimmedLine = line.trim();
      if (trimmedLine.startsWith('data:')) {
        try {
          const city = JSON.parse(trimmedLine.substring(5).trim());
          cities.push(city);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }

    return cities;
  };

  // Fetch City by ID
  const getCityById = async (cityId: string): Promise<CityResponseModel> => {
    const response = await axiosInstance.get<CityResponseModel>(
      `/cities/${cityId}`
    );
    return response.data;
  };

  // Add a New City
  const addCity = async (
    city: CityRequestModel
  ): Promise<CityResponseModel> => {
    const response = await axiosInstance.post<CityResponseModel>(
      '/cities',
      city
    );
    return response.data;
  };

  // Update a City
  const updateCity = async (
    cityId: string,
    city: CityRequestModel
  ): Promise<CityResponseModel> => {
    const response = await axiosInstance.put<CityResponseModel>(
      `/cities/${cityId}`,
      city
    );
    return response.data;
  };

  // Delete a City
  const deleteCity = async (cityId: string): Promise<void> => {
    await axiosInstance.delete(`/cities/${cityId}`);
  };

  return {
    getAllCities,
    getCityById,
    addCity,
    updateCity,
    deleteCity,
  };
};

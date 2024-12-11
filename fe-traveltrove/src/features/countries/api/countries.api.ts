import { CountryRequestModel, CountryResponseModel } from '../models/country.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

// Country API Hook
export const useCountriesApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Countries (SSE Stream)
  const getAllCountries = async (): Promise<CountryResponseModel[]> => {
    const countries: CountryResponseModel[] = [];

    const response = await axiosInstance.get('/countries', {
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
          const country = JSON.parse(trimmedLine.substring(5).trim());
          countries.push(country);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }
    return countries;
  };

  // Fetch Country by ID
  const getCountryById = async (countryId: string): Promise<CountryResponseModel> => {
    const response = await axiosInstance.get<CountryResponseModel>(`/countries/${countryId}`);
    return response.data;
  };

  // Add a New Country
  const addCountry = async (country: CountryRequestModel): Promise<CountryResponseModel> => {
    const response = await axiosInstance.post<CountryResponseModel>('/countries', country);
    return response.data;
  };

  // Update a Country
  const updateCountry = async (
    countryId: string,
    country: CountryRequestModel
  ): Promise<CountryResponseModel> => {
    const response = await axiosInstance.put<CountryResponseModel>(
      `/countries/${countryId}`,
      country
    );
    return response.data;
  };

  // Delete a Country
  const deleteCountry = async (countryId: string): Promise<void> => {
    await axiosInstance.delete(`/countries/${countryId}`);
  };

  return {
    getAllCountries,
    getCountryById,
    addCountry,
    updateCountry,
    deleteCountry,
  };
};

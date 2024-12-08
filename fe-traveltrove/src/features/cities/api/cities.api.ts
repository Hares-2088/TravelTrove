import { CityResponseModel } from "../models/city.model";
import axiosInstance from "../../../shared/axios/axios.instance";

export const getAllCities = async (): Promise<CityResponseModel[]> => {
  const cities: CityResponseModel[] = [];

  const response = await axiosInstance.get('/cities', {
    responseType: 'text',
    headers: {
      Accept: 'text/event-stream',
    },
  });

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
}


export const getCityById = async (cityId: string): Promise<CityResponseModel> => {
  const response = await axiosInstance.get<CityResponseModel>(`/cities/${cityId}`);
  return response.data;
}

export const addCity = async (city: CityResponseModel): Promise<CityResponseModel> => {
  const response = await axiosInstance.post<CityResponseModel>("/cities", city);
  return response.data;
}

export const updateCity = async (
  cityId: string,
  city: CityResponseModel
): Promise<CityResponseModel> => {
  const response = await axiosInstance.put<CityResponseModel>(
    `/cities/${cityId}`,
    city
  );
  return response.data;
}

export const deleteCity = async (cityId: string): Promise<void> => {
  await axiosInstance.delete(`/cities/${cityId}`);
}
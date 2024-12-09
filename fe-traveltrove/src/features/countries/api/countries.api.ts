import axiosInstance from '../../../shared/axios/axios.instance';
import { CountryRequestModel, CountryResponseModel } from '../models/country.model';

export const getAllCountries = async (): Promise<CountryResponseModel[]> => {
  const countries: CountryResponseModel[] = [];

  const response = await axiosInstance.get('/countries', {
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
        const country = JSON.parse(trimmedLine.substring(5).trim());
        countries.push(country);
      } catch (error) {
        console.error('Error parsing line:', trimmedLine, error);
      }
    }
  }

  return countries;
};


export const getCountryById = async (countryId: string): Promise<CountryResponseModel> => {
  const response = await axiosInstance.get<CountryResponseModel>(`/countries/${countryId}`);
  return response.data;
};

export const addCountry = async (country: CountryRequestModel): Promise<CountryResponseModel> => {
  const response = await axiosInstance.post<CountryResponseModel>("/countries", country);
  return response.data;
};

export const updateCountry = async (
  countryId: string,
  country: CountryRequestModel
): Promise<CountryResponseModel> => {
  const response = await axiosInstance.put<CountryResponseModel>(
    `/countries/${countryId}`,
    country
  );
  return response.data;
};

export const deleteCountry = async (countryId: string): Promise<void> => {
  await axiosInstance.delete(`/countries/${countryId}`);
};

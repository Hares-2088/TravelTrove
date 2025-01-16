import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';
import {
  AirportRequestModel,
  AirportResponseModel,
} from '../models/airports.model';

// Define the return type explicitly
interface AirportsApi {
  getAllAirports: () => Promise<AirportResponseModel[]>;
  getAirportById: (airportId: string) => Promise<AirportResponseModel>;
  addAirport: (airport: AirportRequestModel) => Promise<AirportResponseModel>;
  updateAirport: (
    airport: AirportRequestModel,
    airportId: string
  ) => Promise<AirportResponseModel>;
  deleteAirport: (airportId: string) => Promise<void>;
}

export const useAirportsApi = (): AirportsApi => {
  const axiosInstance = useAxiosInstance();

  const getAllAirports = async (): Promise<AirportResponseModel[]> => {
    const airports: AirportResponseModel[] = [];

    const response = await axiosInstance.get('/airports', {
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
          const airport = JSON.parse(trimmedLine.substring(5).trim());
          airports.push(airport);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }

    return airports;
  };

  const getAirportById = async (
    airportId: string
  ): Promise<AirportResponseModel> => {
    const response = await axiosInstance.get<AirportResponseModel>(
      `/airports/${airportId}`
    );
    return response.data;
  };

  const addAirport = async (
    airport: AirportRequestModel
  ): Promise<AirportResponseModel> => {
    const response = await axiosInstance.post<AirportResponseModel>(
      '/airports',
      airport
    );
    return response.data;
  };

  const updateAirport = async (
    airport: AirportRequestModel,
    airportId: string
  ): Promise<AirportResponseModel> => {
    const response = await axiosInstance.put<AirportResponseModel>(
      `/airports/${airportId}`,
      airport
    );
    return response.data;
  };

  const deleteAirport = async (airportId: string): Promise<void> => {
    await axiosInstance.delete<AirportResponseModel>(`/airports/${airportId}`);
  };

  return {
    getAllAirports,
    getAirportById,
    addAirport,
    updateAirport,
    deleteAirport,
  };
};

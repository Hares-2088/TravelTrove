import { HotelRequestModel, HotelResponseModel } from "../models/hotel.model";
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

export const useHotelsApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook
    
  const getAllHotels = async (): Promise<HotelResponseModel[]> => {
    const hotels: HotelResponseModel[] = [];

    const response = await axiosInstance.get('/hotels', {
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
          const hotel = JSON.parse(trimmedLine.substring(5).trim());
          hotels.push(hotel);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }

    return hotels;
  }


  const getHotelById = async (hotelId: string): Promise<HotelResponseModel> => {
    const response = await axiosInstance.get<HotelResponseModel>(`/hotels/${hotelId}`);
    return response.data;
  }

  const addHotel = async (hotel: HotelRequestModel): Promise<HotelResponseModel> => {
    const response = await axiosInstance.post<HotelResponseModel>("/hotels", hotel);
    return response.data;
  }

  const updateHotel = async (
    hotelId: string,
    hotel: HotelRequestModel
  ): Promise<HotelResponseModel> => {
    const response = await axiosInstance.put<HotelResponseModel>(
      `/hotels/${hotelId}`,
      hotel
    );
    return response.data;
  }

  const deleteHotel = async (hotelId: string): Promise<void> => {
    await axiosInstance.delete(`/hotels/${hotelId}`);
  }
  
    return {
      getAllHotels,
      getHotelById,
      addHotel,
      updateHotel,
      deleteHotel,
    };
};
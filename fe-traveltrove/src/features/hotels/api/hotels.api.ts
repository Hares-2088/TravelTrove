import { HotelRequestModel, HotelResponseModel } from "../models/hotel.model";
import axiosInstance from "../../../shared/axios/axios.instance";

export const getAllHotels = async (): Promise<HotelResponseModel[]> => {
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


export const getHotelById = async (hotelId: string): Promise<HotelResponseModel> => {
  const response = await axiosInstance.get<HotelResponseModel>(`/hotels/${hotelId}`);
  return response.data;
}

export const addHotel = async (hotel: HotelRequestModel): Promise<HotelResponseModel> => {
  const response = await axiosInstance.post<HotelResponseModel>("/hotels", hotel);
  return response.data;
}

export const updateHotel = async (
  hotelId: string,
  hotel: HotelRequestModel
): Promise<HotelResponseModel> => {
  const response = await axiosInstance.put<HotelResponseModel>(
    `/hotels/${hotelId}`,
    hotel
  );
  return response.data;
}

export const deleteHotel = async (hotelId: string): Promise<void> => {
  await axiosInstance.delete(`/hotels/${hotelId}`);
}
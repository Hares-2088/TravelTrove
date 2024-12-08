import axiosInstance from '../../../shared/axios/axios.instance';
import { TourRequestModel, TourResponseModel } from '../models/Tour';

export const getAllTours = async (): Promise<TourResponseModel[]> => {
const tours: TourResponseModel[] = [];

  const response = await axiosInstance.get('/tours',{
    responseType: 'text',
    headers:{
      Accept: 'text/event-stream',
    },
   });


 const lines = response.data.split('\n');
  for (const line of lines) {
    const trimmedLine = line.trim();
    if (trimmedLine.startsWith('data:')) {
      try {
        const tour = JSON.parse(trimmedLine.substring(5).trim());
        tours.push(tour);
      } catch (error) {
        console.error('Error parsing line:', trimmedLine, error);
      }
    }
  }

  return tours;
}

export const getTourByTourId = async (tourId: string) => {
  const response = await axiosInstance.get(`/tours/${tourId}`);
  return response.data;
};

export const addTour = async (tour: TourRequestModel): Promise<TourResponseModel> => {
  const response = await axiosInstance.post<TourResponseModel> ("/tours", tour);
  return response.data;
};

export const updateTour = async (
  tourId: string,
  tour: TourRequestModel
): Promise<TourResponseModel> => {
  const response = await axiosInstance.put<TourResponseModel>(
    `/tours/${tourId}`,
    tour
  );
  return response.data;
}

export const deleteTour = async (tourId: string): Promise<void> => {
  await axiosInstance.delete(`/turs/${tourId}`);
}
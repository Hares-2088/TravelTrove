import axiosInstance from '../../../shared/axios/axios.instance';

export const getAllTours = async () => {
  const response = await axiosInstance.get('/tours');
  return response.data;
};

export const getTourByTourId = async (tourId: string) => {
  const response = await axiosInstance.get(`/tours/${tourId}`);
  return response.data;
};
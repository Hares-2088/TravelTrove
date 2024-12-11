import { TourRequestModel, TourResponseModel } from "../models/Tour";
import { useAxiosInstance } from "../../../shared/axios/useAxiosInstance";

// Utility function to parse event-stream data
const parseEventStream = (data: string): TourResponseModel[] => {
  const lines = data.split("\n");
  const tours: TourResponseModel[] = [];

  for (const line of lines) {
    const trimmedLine = line.trim();
    if (trimmedLine.startsWith("data:")) {
      try {
        const tour = JSON.parse(trimmedLine.substring(5).trim());
        tours.push(tour);
      } catch (error) {
        console.error("Error parsing line:", trimmedLine, error);
      }
    }
  }

  return tours;
};

// Custom Hook for Tours API
export const useToursApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Tours
  const getAllTours = async (): Promise<TourResponseModel[]> => {
    const response = await axiosInstance.get("/tours", {
      responseType: "text",
      headers: {
        Accept: "text/event-stream",
      },
    });
    return parseEventStream(response.data);
  };

  // Fetch Tour by ID
  const getTourByTourId = async (tourId: string): Promise<TourResponseModel> => {
    const response = await axiosInstance.get<TourResponseModel>(`/tours/${tourId}`);
    return response.data;
  };

  // Add a New Tour
  const addTour = async (tour: TourRequestModel): Promise<TourResponseModel> => {
    const response = await axiosInstance.post<TourResponseModel>("/tours", tour);
    return response.data;
  };

  // Update an Existing Tour
  const updateTour = async (
    tourId: string,
    tour: TourRequestModel
  ): Promise<TourResponseModel> => {
    const response = await axiosInstance.put<TourResponseModel>(`/tours/${tourId}`, tour);
    return response.data;
  };

  // Delete a Tour
  const deleteTour = async (tourId: string): Promise<void> => {
    await axiosInstance.delete(`/tours/${tourId}`);
  };

  return {
    getAllTours,
    getTourByTourId,
    addTour,
    updateTour,
    deleteTour,
  };
};

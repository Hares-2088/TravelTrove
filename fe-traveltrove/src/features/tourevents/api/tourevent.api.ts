import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';
import {
  TourEventRequestModel,
  TourEventResponseModel,
} from '../../tourEvents/model/tourevents.model.ts';

// Utility function to parse event-stream data
const parseEventStream = (data: string): TourEventResponseModel[] => {
  const lines = data.split('\n');
  const events: TourEventResponseModel[] = [];

  for (const line of lines) {
    const trimmedLine = line.trim();
    if (trimmedLine.startsWith('data:')) {
      try {
        const event = JSON.parse(trimmedLine.substring(5).trim());
        events.push(event);
      } catch (error) {
        console.error('Error parsing event line:', trimmedLine, error);
      }
    }
  }
  return events;
};

// Custom Hook for Tour Events API
export const useTourEventsApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Tour Events
  const getAllTourEvents = async (): Promise<TourEventResponseModel[]> => {
    const response = await axiosInstance.get('/tourevents', {
      responseType: 'text',
      headers: { Accept: 'text/event-stream' },
    });
    return parseEventStream(response.data);
  };

  // Fetch Tour Events by Tour ID
  const getTourEventsByTourId = async (tourId: string): Promise<TourEventResponseModel[]> => {
    const response = await axiosInstance.get(`/tourevents/tours/${tourId}`, {
      responseType: 'text',
      headers: { Accept: 'text/event-stream' },
    });
    return parseEventStream(response.data);
  };

  // Fetch a Tour Event by ID
  const getTourEventById = async (tourEventId: string): Promise<TourEventResponseModel | null> => {
    try {
      const response = await axiosInstance.get(`/tourevents/${tourEventId}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching tour event ${tourEventId}:`, error);
      return null;
    }
  };

  // Add a New Tour Event
  const addTourEvent = async (request: TourEventRequestModel): Promise<TourEventResponseModel> => {
    const response = await axiosInstance.post('/tourevents', request);
    return response.data;
  };

  // Update an Existing Tour Event
  const updateTourEvent = async (
    tourEventId: string,
    request: TourEventRequestModel
  ): Promise<TourEventResponseModel | null> => {
    try {
      const response = await axiosInstance.put(`/tourevents/${tourEventId}`, request);
      return response.data;
    } catch (error) {
      console.error(`Error updating tour event ${tourEventId}:`, error);
      return null;
    }
  };

  // Delete a Tour Event
  const deleteTourEvent = async (tourEventId: string): Promise<void> => {
    try {
      await axiosInstance.delete(`/tourevents/${tourEventId}`);
    } catch (error) {
      console.error(`Error deleting tour event ${tourEventId}:`, error);
    }
  };

  return {
    getAllTourEvents,
    getTourEventsByTourId,
    getTourEventById,
    addTourEvent,
    updateTourEvent,
    deleteTourEvent,
  };
};

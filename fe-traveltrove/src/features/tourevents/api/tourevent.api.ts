import axiosInstance from '../../../shared/axios/axios.instance';
import { TourEventRequestModel, TourEventResponseModel } from '../model/tourevents.model';

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


// Get all tour events
export const getAllTourEvents = async (): Promise<TourEventResponseModel[]> => {
  const response = await axiosInstance.get('/tourevents', {
    responseType: 'text',
    headers: { Accept: 'text/event-stream' },
  });
  return parseEventStream(response.data);
};

// Get tour events by tour ID
export const getTourEventsByTourId = async (tourId: string): Promise<TourEventResponseModel[]> => {
  const response = await axiosInstance.get(`/tourevents/tours/${tourId}`, {
    responseType: 'text',
    headers: { Accept: 'text/event-stream' },
  });
  return parseEventStream(response.data);
};

// Get a tour event by ID
export const getTourEventById = async (tourEventId: string): Promise<TourEventResponseModel | null> => {
  try {
    const response = await axiosInstance.get(`/tourevents/${tourEventId}`);
    return response.data;
  } catch {
    return null;
  }
};

// Add a new tour event
export const addTourEvent = async (request: TourEventRequestModel): Promise<TourEventResponseModel> => {
  const response = await axiosInstance.post('/tourevents', request);
  return response.data;
};

// Update an existing tour event
export const updateTourEvent = async (tourEventId: string, request: TourEventRequestModel): Promise<TourEventResponseModel | null> => {
  try {
    const response = await axiosInstance.put(`/tourevents/${tourEventId}`, request);
    return response.data;
  } catch {
    return null;
  }
};

// Delete a tour event
export const deleteTourEvent = async (tourEventId: string): Promise<void> => {
  await axiosInstance.delete(`/tourevents/${tourEventId}`);
};

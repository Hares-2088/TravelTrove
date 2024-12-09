import axiosInstance from '../../../shared/axios/axios.instance';
import { EventRequestModel, EventResponseModel } from '../model/tourEvents.models';

// Fetch all events, with optional filters for cityId and countryId
export const getAllEvents = async (filters?: { cityId?: string; countryId?: string }): Promise<EventResponseModel[]> => {
  const events: EventResponseModel[] = [];

  const response = await axiosInstance.get('/events', {
    params: filters,
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
        const event = JSON.parse(trimmedLine.substring(5).trim());
        events.push(event);
      } catch (error) {
        console.error('Error parsing line:', trimmedLine, error);
      }
    }
  }

  return events;
};

// Fetch a specific event by its ID
export const getEventById = async (eventId: string): Promise<EventResponseModel> => {
  const response = await axiosInstance.get<EventResponseModel>(`/events/${eventId}`);
  return response.data;
};

// Create a new event
export const addEvent = async (event: EventRequestModel): Promise<EventResponseModel> => {
  const response = await axiosInstance.post<EventResponseModel>('/events', event);
  return response.data;
};

// Update an existing event
export const updateEvent = async (eventId: string, event: EventRequestModel): Promise<EventResponseModel> => {
  const response = await axiosInstance.put<EventResponseModel>(`/events/${eventId}`, event);
  return response.data;
};

// Delete an event by its ID
export const deleteEvent = async (eventId: string): Promise<EventResponseModel> => {
  const response = await axiosInstance.delete<EventResponseModel>(`/events/${eventId}`);
  return response.data;
};

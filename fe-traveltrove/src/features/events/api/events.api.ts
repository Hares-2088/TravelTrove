import { EventRequestModel, EventResponseModel } from '../model/events.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

export const useEventsApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch All Events (SSE Stream)
  const getAllEvents = async (filters?: { cityId?: string; countryId?: string }): Promise<EventResponseModel[]> => {
    const events: EventResponseModel[] = [];

    const response = await axiosInstance.get('/events', {
      params: filters,
      responseType: 'text',
      headers: {
        Accept: 'text/event-stream',
      },
    });

    // Parse Server-Sent Events (SSE)
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

  // Fetch Event by ID
  const getEventById = async (eventId: string): Promise<EventResponseModel> => {
    const response = await axiosInstance.get<EventResponseModel>(`/events/${eventId}`);
    return response.data;
  };

  // Add a New Event
  const addEvent = async (event: EventRequestModel): Promise<EventResponseModel> => {
    const response = await axiosInstance.post<EventResponseModel>('/events', event);
    return response.data;
  };

  // Update an Existing Event
  const updateEvent = async (
    eventId: string,
    event: EventRequestModel
  ): Promise<EventResponseModel> => {
    const response = await axiosInstance.put<EventResponseModel>(`/events/${eventId}`, event);
    return response.data;
  };

  // Delete an Event by ID
  const deleteEvent = async (eventId: string): Promise<EventResponseModel> => {
    const response = await axiosInstance.delete<EventResponseModel>(`/events/${eventId}`);
    return response.data;
  };

  return {
    getAllEvents,
    getEventById,
    addEvent,
    updateEvent,
    deleteEvent,
  };
};

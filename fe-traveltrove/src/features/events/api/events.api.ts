import { EventRequestModel, EventResponseModel } from '../model/events.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

// Define the return type interface
interface EventsApi {
  getAllEvents: (filters?: {
    cityId?: string;
    countryId?: string;
  }) => Promise<EventResponseModel[]>;
  getEventById: (eventId: string) => Promise<EventResponseModel>;
  addEvent: (event: EventRequestModel) => Promise<EventResponseModel>;
  updateEvent: (
    eventId: string,
    event: EventRequestModel
  ) => Promise<EventResponseModel>;
  deleteEvent: (eventId: string) => Promise<EventResponseModel>;
}

// Explicit return type added
export const useEventsApi = (): EventsApi => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  const getAllEvents = async (filters?: {
    cityId?: string;
    countryId?: string;
  }): Promise<EventResponseModel[]> => {
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

  const getEventById = async (eventId: string): Promise<EventResponseModel> => {
    const response = await axiosInstance.get<EventResponseModel>(
      `/events/${eventId}`
    );
    return response.data;
  };

  const addEvent = async (
    event: EventRequestModel
  ): Promise<EventResponseModel> => {
    const response = await axiosInstance.post<EventResponseModel>(
      '/events',
      event
    );
    return response.data;
  };

  const updateEvent = async (
    eventId: string,
    event: EventRequestModel
  ): Promise<EventResponseModel> => {
    const response = await axiosInstance.put<EventResponseModel>(
      `/events/${eventId}`,
      event
    );
    return response.data;
  };

  const deleteEvent = async (eventId: string): Promise<EventResponseModel> => {
    const response = await axiosInstance.delete<EventResponseModel>(
      `/events/${eventId}`
    );
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

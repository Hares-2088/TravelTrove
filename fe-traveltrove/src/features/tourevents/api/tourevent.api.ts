import { EventRequestModel, EventResponseModel } from "../models/Event";

export const getEventsByTourId = async (tourId: string): Promise<EventResponseModel[]> => {
  const response = await fetch(`/api/tours/${tourId}/events`);
  return await response.json();
};

export const addEvent = async (eventData: EventRequestModel, tourId: string): Promise<void> => {
  await fetch(`/api/tours/${tourId}/events`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(eventData),
  });
};

export const updateEvent = async (eventId: string, eventData: EventRequestModel): Promise<void> => {
  await fetch(`/api/events/${eventId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(eventData),
  });
};

export const deleteEvent = async (eventId: string): Promise<void> => {
  await fetch(`/api/events/${eventId}`, {
    method: "DELETE",
  });
};

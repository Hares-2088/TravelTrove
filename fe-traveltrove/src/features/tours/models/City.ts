import { Event as CustomEvent } from './Event';

export interface City {
  cityId: string;
  name: string;
  description: string;
  image: string;
  startDate: string;
  events: CustomEvent[];
  hotel: string;
}

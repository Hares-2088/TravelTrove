import { City } from './City';
import { Review } from './Review';
import { Booking } from './Booking';

export interface Tour {
  tourId: string;
  name: string;
  startDate: string;
  endDate: string;
  overallDescription: string;
  available: boolean;
  price: number;
  spotsAvailable: number;
  cities: City[];
  reviews: Review[];
  bookings: Booking[];
  image: string;
  itineraryPicture: string;
}

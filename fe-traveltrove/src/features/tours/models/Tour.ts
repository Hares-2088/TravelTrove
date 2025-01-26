import { City } from './City';
// import { Review } from './Review';
// import { Booking } from './Booking';

export interface Tour {
  tourId: string;
  name: string;
  image: string;
  overallDescription: string;
  startDate: string;
  endDate: string;
  price: number;
  spotsAvailable: number;
  itineraryPicture: string;
  cities: City[];
  events?: Event[];
  tourImageUrl?: string;
}

export interface TourResponseModel {
  tourId: string;
  name: string;
  description: string;
  tourImageUrl?: string;
}

export interface TourRequestModel {
  name: string;
  description: string;
  tourImageUrl?: string;
}

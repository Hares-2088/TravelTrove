export interface TourEventResponseModel {
  tourEventId: string;
  seq: number
  seqDesc: string;
  tourId: string;
  eventId: string;
}

export interface TourEventRequestModel {
  seq: number
  seqDesc: string;
  tourId: string;
  eventId: string;
}
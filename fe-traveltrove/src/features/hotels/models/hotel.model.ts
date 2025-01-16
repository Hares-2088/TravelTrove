export interface HotelResponseModel {
  hotelId: string;
  name: string;
  cityId: string;
  url: string;
}

export interface HotelRequestModel {
  name: string;
  cityId: string;
  url: string;
}

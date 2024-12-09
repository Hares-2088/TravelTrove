export interface EventResponseModel {
    eventId: string;
    cityId: string;
    countryId: string;
    name: string;
    description: string;
    image: string;
}

export interface EventRequestModel {
    cityId: string;
    countryId: string;
    name: string;
    description: string;
    image: string;
}
export interface TravelerResponseModel {
    travelerId: string;
    seq: number;
    firstName: string;
    lastName: string;
    addressLine1: string;
    addressLine2: string;
    city: string;
    state: string;
    email: string;
    countryId: string;
}

export interface TravelerRequestModel {
    seq: number;
    firstName: string;
    lastName: string;
    addressLine1: string;
    addressLine2: string;
    city: string;
    state: string;
    email: string;
    countryId: string;
}

export interface TravelerWithIdRequestModel {
    travelerId: string;
    seq: number;
    firstName: string;
    lastName: string;
    addressLine1: string;
    addressLine2: string;
    city: string;
    state: string;
    email: string;
    countryId: string;
}
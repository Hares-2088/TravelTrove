export interface PackageRequestModel {
    airportId: string;
    tourId: string;
    name: string;
    description: string;
    startDate: string; // Assuming ISO string format for dates
    endDate: string; // Assuming ISO string format for dates
    priceSingle: number;
    priceDouble: number;
    priceTriple: number;
    totalSeats: number;
}

export interface PackageResponseModel {
    airportId: string;
    tourId: string;
    packageId: string; // public id
    name: string;
    description: string;
    startDate: string; // Assuming ISO string format for dates
    endDate: string; // Assuming ISO string format for dates
    priceSingle: number;
    priceDouble: number;
    priceTriple: number;
    availableSeats: number;
    totalSeats: number;
    status?: PackageStatus; // Add status property
}

export enum PackageStatus {
    UPCOMING = 'UPCOMING',
    BOOKING_OPEN = 'BOOKING_OPEN',
    BOOKING_CLOSED = 'BOOKING_CLOSED',
    SOLD_OUT = 'SOLD_OUT',
    CANCELLED = 'CANCELLED',
    COMPLETED = 'COMPLETED',
}

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
}
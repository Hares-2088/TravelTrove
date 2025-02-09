import { TravelerRequestModel } from "../../travelers/model/traveler.model"; // Adjust the path as needed

export enum BookingStatus {
  PAYMENT_PENDING = "PAYMENT_PENDING",
  PAYMENT_ATTEMPT2_PENDING = "PAYMENT_ATTEMPT2_PENDING",
  BOOKING_FAILED = "BOOKING_FAILED",
  BOOKING_CONFIRMED = "BOOKING_CONFIRMED",
  REFUNDED = "REFUNDED",
}

export interface BookingRequestModel {
  userId: string;           // ID of the user making the booking
  packageId: string;        // ID of the package to book
  totalPrice: number;       // Total price of the booking
  status: BookingStatus;    // Status of the booking
  bookingDate: string;      // Date of the booking in ISO 8601 format
  travelers: TravelerRequestModel[]; // List of travelers
}

export interface BookingResponseModel {
  bookingId: string;       // Public ID of the booking
  userId: string;          // ID of the user associated with the booking
  packageId: string;       // ID of the package being booked
  totalPrice: number;      // Total price of the booking
  status: BookingStatus;   // Status of the booking
  bookingDate: string;     // Date of the booking in ISO 8601 format
  travelerIds?: string[];  // Traveler IDs associated with the booking (optional)
}

export interface BookingResponseModel {
  bookingId: string; // Public ID of the booking
  userId: string; // ID of the user associated with the booking
  packageId: string; // ID of the package being booked
  totalPrice: number; // Total price of the booking
  status: BookingStatus; // Status of the booking
  bookingDate: string; // Date of the booking in ISO 8601 format
}

export interface BookingRequestModel {
  userId: string; // ID of the user making the booking
  packageId: string; // ID of the package to book
  totalPrice: number; // Total price of the booking
  status: BookingStatus; // Status of the booking
  bookingDate: string; // Date of the booking in ISO 8601 format
}

// Enum for BookingStatus
export enum BookingStatus {
  PAYMENT_PENDING = 'PAYMENT_PENDING',
  PAYMENT_ATTEMPT2_PENDING = 'PAYMENT_ATTEMPT2_PENDING',
  PAYMENT_ATTEMPT3_PENDING = 'PAYMENT_ATTEMPT3_PENDING',
  BOOKING_FAILED = 'BOOKING_FAILED',
  PAYMENT_SUCCESS = 'PAYMENT_SUCCESS',
  BOOKING_CONFIRMED = 'BOOKING_CONFIRMED',
  BOOKING_FINALIZED = 'BOOKING_FINALIZED',
  BOOKING_EXPIRED = 'BOOKING_EXPIRED',
  BOOKING_CANCELLED = 'BOOKING_CANCELLED',
  REFUNDED = 'REFUNDED',
}

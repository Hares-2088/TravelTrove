import { BookingRequestModel, BookingResponseModel, BookingStatus } from "../models/bookings.model";
import { useAxiosInstance } from "../../../shared/axios/useAxiosInstance";
import { PaymentResponseModel } from "../../payments/models/payments.model";

export const useBookingsApi = () => {
  const axiosInstance = useAxiosInstance();

  const getAllBookings = async (filters?: {
    userId?: string;
    packageId?: string;
    status?: BookingStatus;
  }): Promise<BookingResponseModel[]> => {
    const bookings: BookingResponseModel[] = [];

    const response = await axiosInstance.get('/bookings', {
      params: filters,
      responseType: 'text', // Ensure SSE response is treated as text
      headers: {
        Accept: 'text/event-stream',
      },
    });

    // Parse Server-Sent Events (SSE) line by line
    const lines = response.data.split('\n');
    for (const line of lines) {
      const trimmedLine = line.trim();
      if (trimmedLine.startsWith('data:')) {
        try {
          const booking = JSON.parse(trimmedLine.substring(5).trim());
          bookings.push(booking);
        } catch (error) {
          console.error('Error parsing line:', trimmedLine, error);
        }
      }
    }

    return bookings;
  };


  // Fetch a booking by ID
  const getBookingById = async (bookingId: string): Promise<BookingResponseModel> => {
    const response = await axiosInstance.get<BookingResponseModel>(`/bookings/booking`, {
      params: { bookingId },
    });
    return response.data;
  };

  // Fetch a booking by packageId and userId
  const getBookingByPackageAndUser = async (
    packageId: string,
    userId: string
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.get<BookingResponseModel>(`/bookings/booking`, {
      params: { packageId, userId },
    });
    return response.data;
  };

  // Create a new booking (Requires at least one traveler)
  const createBooking = async (booking: BookingRequestModel): Promise<BookingResponseModel> => {
    if (!booking.travelers || booking.travelers.length === 0) {
      throw new Error("At least one traveler must be provided.");
    }

    const response = await axiosInstance.post<BookingResponseModel>("/bookings", booking);
    return response.data;
  };

  // Update booking status
  const updateBookingStatus = async (
    bookingId: string,
    status: BookingStatus
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.patch<BookingResponseModel>(
      `/bookings/${bookingId}`,
      { status }
    );
    return response.data;
  };

  // Delete a booking
  const deleteBooking = async (bookingId: string): Promise<void> => {
    await axiosInstance.delete(`/bookings/${bookingId}`);
  };

  // Get Payment by Booking ID
  const getPaymentByBookingId = async (bookingId: string): Promise<PaymentResponseModel> => {
    const response = await axiosInstance.get<PaymentResponseModel>(`/payments/booking/${bookingId}`);
    return response.data;
  };

  return {
    getAllBookings,
    getBookingById,
    getBookingByPackageAndUser,
    createBooking,
    updateBookingStatus,
    deleteBooking,
    getPaymentByBookingId,
  };
};

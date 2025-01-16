import {
  BookingRequestModel,
  BookingResponseModel,
  BookingStatus,
} from '../models/bookings.model';
import { useAxiosInstance } from '../../../shared/axios/useAxiosInstance';

// Define the return type interface
interface BookingsApi {
  getAllBookings: (filters?: {
    userId?: string;
    packageId?: string;
    status?: BookingStatus;
  }) => Promise<BookingResponseModel[]>;

  getBookingById: (bookingId: string) => Promise<BookingResponseModel>;

  getBookingByPackageAndUser: (
    packageId: string,
    userId: string
  ) => Promise<BookingResponseModel>;

  createBooking: (
    booking: BookingRequestModel
  ) => Promise<BookingResponseModel>;

  updateBookingStatus: (
    bookingId: string,
    status: BookingStatus
  ) => Promise<BookingResponseModel>;

  deleteBooking: (bookingId: string) => Promise<void>;
}

export const useBookingsApi = (): BookingsApi => {
  // Explicit return type
  const axiosInstance = useAxiosInstance();

  const getAllBookings = async (filters?: {
    userId?: string;
    packageId?: string;
    status?: BookingStatus;
  }): Promise<BookingResponseModel[]> => {
    const bookings: BookingResponseModel[] = [];

    const response = await axiosInstance.get('/bookings', {
      params: filters,
      responseType: 'text',
      headers: {
        Accept: 'text/event-stream',
      },
    });

    // Parse Server-Sent Events (SSE)
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

  const getBookingById = async (
    bookingId: string
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.get<BookingResponseModel>(
      `/bookings/search`,
      {
        params: { bookingId },
      }
    );
    return response.data;
  };

  const getBookingByPackageAndUser = async (
    packageId: string,
    userId: string
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.get<BookingResponseModel>(
      `/bookings/search`,
      {
        params: { packageId, userId },
      }
    );
    return response.data;
  };

  const createBooking = async (
    booking: BookingRequestModel
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.post<BookingResponseModel>(
      '/bookings',
      booking
    );
    return response.data;
  };

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

  const deleteBooking = async (bookingId: string): Promise<void> => {
    await axiosInstance.delete(`/bookings/${bookingId}`);
  };

  return {
    getAllBookings,
    getBookingById,
    getBookingByPackageAndUser,
    createBooking,
    updateBookingStatus,
    deleteBooking,
  };
};

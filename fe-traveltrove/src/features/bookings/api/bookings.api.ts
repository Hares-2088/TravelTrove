import { BookingRequestModel, BookingResponseModel, BookingStatus } from "../models/bookings.model";
import { useAxiosInstance } from "../../../shared/axios/useAxiosInstance";

export const useBookingsApi = () => {
  const axiosInstance = useAxiosInstance();

  // Fetch all bookings (with optional filters)
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

  // Fetch a booking by ID
  const getBookingById = async (
    bookingId: string
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.get<BookingResponseModel>(`/bookings/search`, {
      params: { bookingId },
    });
    return response.data;
  };

  // Fetch a booking by packageId and userId
  const getBookingByPackageAndUser = async (
    packageId: string,
    userId: string
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.get<BookingResponseModel>(`/bookings/search`, {
      params: { packageId, userId },
    });
    return response.data;
  };

  // Create a new booking
  const createBooking = async (
    booking: BookingRequestModel
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.post<BookingResponseModel>("/bookings", booking);
    return response.data;
  };

  // Generalized method to update booking status
  const updateBookingStatus = async (
    bookingId: string,
    status: BookingStatus
  ): Promise<BookingResponseModel> => {
    const response = await axiosInstance.patch<BookingResponseModel>(
      `/bookings/${bookingId}`,
      { status } // Matches the backend's BookingStatusUpdateRequest structure
    );
    return response.data;
  };
  

  // Delete a booking
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

import { BookingRequestModel, BookingResponseModel, BookingStatus } from "../models/bookings.model";
import { useAxiosInstance } from "../../../shared/axios/useAxiosInstance";

export const useBookingsApi = () => {
  const axiosInstance = useAxiosInstance();

  // Fetch all bookings using Server-Sent Events (SSE)
  const getAllBookings = async (filters?: {
    userId?: string;
    packageId?: string;
    status?: BookingStatus;
  }): Promise<BookingResponseModel[]> => {
    return new Promise((resolve, reject) => {
      const bookings: BookingResponseModel[] = [];
      const eventSource = new EventSource(
        `${axiosInstance.defaults.baseURL}/bookings?${new URLSearchParams(filters as any)}`
      );

      eventSource.onmessage = (event) => {
        try {
          const booking = JSON.parse(event.data);
          bookings.push(booking);
        } catch (error) {
          console.error("Error parsing SSE event:", error);
        }
      };

      eventSource.onerror = (error) => {
        eventSource.close();
        reject(error);
      };

      eventSource.onopen = () => {
        setTimeout(() => {
          eventSource.close();
          resolve(bookings);
        }, 5000); // Close after collecting events
      };
    });
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

  return {
    getAllBookings,
    getBookingById,
    getBookingByPackageAndUser,
    createBooking,
    updateBookingStatus,
    deleteBooking,
  };
};

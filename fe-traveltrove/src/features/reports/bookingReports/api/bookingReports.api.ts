import { useAxiosInstance } from "../../../../shared/axios/useAxiosInstance";

export interface BookingReportParams {
  month: number;
  year: number;
}

export const useBookingReportsApi = () => {
  const axiosInstance = useAxiosInstance();

  // Downloads the PDF report
  const getMonthlyBookingReportPDF = async (params: BookingReportParams) => {
    const { month, year } = params;
    const url = `/reports/bookings/monthly/pdf?month=${month}&year=${year}`;
    const response = await axiosInstance.get<Blob>(url, { responseType: "blob" });
    return response;
  };

  // Downloads the CSV report
  const getMonthlyBookingReportCSV = async (params: BookingReportParams) => {
    const { month, year } = params;
    const url = `/reports/bookings/monthly/csv?month=${month}&year=${year}`;
    const response = await axiosInstance.get<Blob>(url, { responseType: "blob" });
    return response;
  };

  return {
    getMonthlyBookingReportPDF,
    getMonthlyBookingReportCSV,
  };
};
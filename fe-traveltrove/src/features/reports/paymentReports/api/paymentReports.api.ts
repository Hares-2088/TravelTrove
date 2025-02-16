// src/features/staff/components/reports/paymentReports.api.ts

import { useAxiosInstance } from "../../../../shared/axios/useAxiosInstance";

export interface RevenueReportParams {
  periodType: "monthly" | "yearly";
  year: number;
  month?: number; // optional for "yearly"
}

export const usePaymentReportsApi = () => {
  const axiosInstance = useAxiosInstance();

  // Downloads the CSV as a Blob
  const getRevenueReport = async (params: RevenueReportParams) => {
    const { periodType, year, month } = params;

    // Construct query string, e.g.: /api/v1/reports/revenue?periodType=monthly&year=2023&month=1
    const queryParams = new URLSearchParams();
    queryParams.append("periodType", periodType);
    queryParams.append("year", year.toString());
    if (month !== undefined) {
      queryParams.append("month", month.toString());
    }

    const url = `/reports/revenue?${queryParams.toString()}`;

    // Request the CSV file as a blob
    const response = await axiosInstance.get<Blob>(url, {
      responseType: "blob",
      // If your backend sets Content-Disposition with filename,
      // you can parse it from response.headers['content-disposition'] if desired.
    });

    return response;
  };

  return {
    getRevenueReport,
  };
};

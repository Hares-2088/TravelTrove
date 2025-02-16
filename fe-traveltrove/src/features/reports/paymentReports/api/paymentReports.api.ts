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

    //  CSV file as a blob
    const response = await axiosInstance.get<Blob>(url, {
        responseType: "blob",
      });
  
      //  filename from the Content-Disposition header
      const contentDisposition = response.headers["content-disposition"];
      let filename = "revenue-report.csv"; // default filename
  
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        if (match?.[1]) {
          filename = match[1]; // filename from header
        }
      }
  
      return { data: response.data, filename };
    };
  
    return {
      getRevenueReport,
    };
  };

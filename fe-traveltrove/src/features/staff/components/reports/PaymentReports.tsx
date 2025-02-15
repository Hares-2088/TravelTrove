// src/features/staff/components/reports/PaymentReports.tsx

import React, { useState } from "react";
import { Form, Button, Row, Col } from "react-bootstrap";
import { RevenueReportParams, usePaymentReportsApi } from "../../../reports/paymentReports/api/paymentReports.api";

const PaymentReports: React.FC = () => {
  const { getRevenueReport } = usePaymentReportsApi();

  // Form state
  const [periodType, setPeriodType] = useState<"monthly" | "yearly">("monthly");
  const [year, setYear] = useState<number>(2023);
  const [month, setMonth] = useState<number>(1);

  const handleDownload = async () => {
    try {
      // Prepare parameters
      const params: RevenueReportParams = { periodType, year };
      if (periodType === "monthly") {
        params.month = month;
      }

      // Call API
      const response = await getRevenueReport(params);

      // Try to get filename from "Content-Disposition" if your backend sets it
      const disposition = response.headers["content-disposition"];
      let filename = "revenue-report.csv";
      if (disposition) {
        const match = disposition.match(/filename="?([^"]+)"?/);
        if (match?.[1]) {
          filename = match[1];
        }
      }

      // Create a URL from the Blob
      const blobUrl = window.URL.createObjectURL(response.data);

      // Create a hidden <a> tag to trigger download
      const link = document.createElement("a");
      link.href = blobUrl;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // Revoke the object URL (cleanup)
      window.URL.revokeObjectURL(blobUrl);
    } catch (error) {
      console.error("Error downloading revenue report:", error);
    }
  };

  return (
    <div>
      <h3>Payment Reports</h3>
      <p>Select the period and download a CSV file of payment revenue reports.</p>

      <Form>
        <Row className="mb-3">
          <Col xs={12} md={4}>
            <Form.Group controlId="periodType">
              <Form.Label>Period Type</Form.Label>
              <Form.Select
                value={periodType}
                onChange={(e) =>
                  setPeriodType(e.target.value as "monthly" | "yearly")
                }
              >
                <option value="monthly">Monthly</option>
                <option value="yearly">Yearly</option>
              </Form.Select>
            </Form.Group>
          </Col>

          <Col xs={12} md={4}>
            <Form.Group controlId="year">
              <Form.Label>Year</Form.Label>
              <Form.Control
                type="number"
                value={year}
                onChange={(e) => setYear(Number(e.target.value))}
                min={2000}
                max={2100}
              />
            </Form.Group>
          </Col>

          {/* Only show "month" if periodType is "monthly" */}
          {periodType === "monthly" && (
            <Col xs={12} md={4}>
              <Form.Group controlId="month">
                <Form.Label>Month</Form.Label>
                <Form.Control
                  type="number"
                  value={month}
                  onChange={(e) => setMonth(Number(e.target.value))}
                  min={1}
                  max={12}
                />
              </Form.Group>
            </Col>
          )}
        </Row>

        <Button variant="primary" onClick={handleDownload}>
          Download Report
        </Button>
      </Form>
    </div>
  );
};

export default PaymentReports;

import React, { useState } from "react";
import { Form, Button, Row, Col } from "react-bootstrap";
import { BookingReportParams, useBookingReportsApi } from "../../../reports/bookingReports/api/bookingReports.api";

const BookingReports: React.FC = () => {
  const { getMonthlyBookingReportPDF, getMonthlyBookingReportCSV } = useBookingReportsApi();

  // Form state
  const [year, setYear] = useState<number>(2025);
  const [month, setMonth] = useState<number>(1);

  const handleDownload = async (format: "pdf" | "csv") => {
    try {
      const params: BookingReportParams = { month, year };

      const response = format === "pdf" ? await getMonthlyBookingReportPDF(params) : await getMonthlyBookingReportCSV(params);

      const disposition = response.headers["content-disposition"];
      let filename = `booking-report.${format}`;
      if (disposition) {
        const match = disposition.match(/filename="?([^"]+)"?/);
        if (match?.[1]) {
          filename = match[1];
        }
      }

      const blobUrl = window.URL.createObjectURL(response.data);

      const link = document.createElement("a");
      link.href = blobUrl;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      window.URL.revokeObjectURL(blobUrl);
    } catch (error) {
      console.error(`Error downloading booking report (${format}):`, error);
    }
  };

  return (
    <div>
      <h3>Booking Reports</h3>
      <p>Select the period and download a PDF or CSV file of booking reports.</p>

      <Form>
        <Row className="mb-3">
          <Col xs={12} md={6}>
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

          <Col xs={12} md={6}>
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
        </Row>

        <Button variant="primary" onClick={() => handleDownload("pdf")} className="me-2">
          Download PDF
        </Button>
        <Button variant="secondary" onClick={() => handleDownload("csv")}>
          Download CSV
        </Button>
      </Form>
    </div>
  );
};

export default BookingReports;
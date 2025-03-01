import React, { useState } from "react";
import { Form, Button, Row, Col } from "react-bootstrap";
import { RevenueReportParams, usePaymentReportsApi } from "../../../reports/paymentReports/api/paymentReports.api";
import { useTranslation } from "react-i18next";

const PaymentReports: React.FC = () => {
  const { getRevenueReport } = usePaymentReportsApi();

  const [periodType, setPeriodType] = useState<"monthly" | "yearly">("monthly");
  const currentYear = new Date().getFullYear();
  const currentMonth = new Date().getMonth() + 1;

  const [year, setYear] = useState<number>(currentYear);
  const [month, setMonth] = useState<number>(currentMonth);
  const { t } = useTranslation();

  const handleDownload = async () => {
    try {
      const params: RevenueReportParams = { periodType, year };
      if (periodType === "monthly") {
        params.month = month;
      }

      const response = await getRevenueReport(params);

      let filename = `revenue-report-${year}.csv`;
      if (periodType === "monthly" && month != null) {
        filename = `revenue-report-${year}-${String(month).padStart(2, '0')}.csv`;
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
      console.error("Error downloading revenue report:", error);
    }
  };

  return (
    <div>
      <h3>{t("paymentReports")}</h3>
      <p>{t("selectPeriod")}</p>

      <Form>
        <Row className="mb-3">
          <Col xs={12} md={4}>
            <Form.Group controlId="periodType">
              <Form.Label>{t("periodType")}</Form.Label>
              <Form.Select
                value={periodType}
                onChange={(e) =>
                  setPeriodType(e.target.value as "monthly" | "yearly")
                }
              >
                <option value="monthly">{t("monthly")}</option>
                <option value="yearly">{t("yearly")}</option>
              </Form.Select>
            </Form.Group>
          </Col>

          <Col xs={12} md={4}>
            <Form.Group controlId="year">
              <Form.Label>{t("year")}</Form.Label>
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
                <Form.Label>{t("month")}</Form.Label>
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
        {t("downloadReport")}
        </Button>
      </Form>
    </div>
  );
};

export default PaymentReports;

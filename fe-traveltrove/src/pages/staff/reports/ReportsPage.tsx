import React from "react";
import Accordion from "react-bootstrap/Accordion";
import PaymentReports from "../../../features/staff/components/reports/PaymentReports";
import { useTranslation } from "react-i18next";

const ReportsPage: React.FC = () => {
  const { t } = useTranslation();

  return (
    <div
      className="d-flex justify-content-center align-items-start p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <div className="container my-4">
        <h2 className="mb-4">{t("reports")}</h2>
        <Accordion>
          {/* Payment Reports Section */}
          <Accordion.Item eventKey="0">
            <Accordion.Header>{t("paymentReports")}</Accordion.Header>
            <Accordion.Body>
              <PaymentReports />
            </Accordion.Body>
          </Accordion.Item>

          {/* Booking Reports Section */}
          <Accordion.Item eventKey="1">
            <Accordion.Header>{t("bookingReports")}</Accordion.Header>
            <Accordion.Body>BookingReports</Accordion.Body>
          </Accordion.Item>

          {/* Add more sections as needed */}
          <Accordion.Item eventKey="2">
            <Accordion.Header>Other Report Type 3</Accordion.Header>
            <Accordion.Body>
              <p>Placeholder for Report Type 3</p>
            </Accordion.Body>
          </Accordion.Item>
        </Accordion>
      </div>
    </div>
  );
};

export default ReportsPage;

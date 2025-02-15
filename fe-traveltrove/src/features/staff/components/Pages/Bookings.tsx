import React, { useEffect, useState } from "react";
import { Button, Table, Modal, Form, Card } from "react-bootstrap"; // Import Card
import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom";
import { useBookingsApi } from "../../../bookings/api/bookings.api";
import {
  BookingRequestModel,
  BookingResponseModel,
  BookingStatus,
} from "../../../bookings/models/bookings.model";
import { PaymentResponseModel } from "../../../payments/models/payments.model"; // Import PaymentResponseModel
import "./Bookings.css";
import "../../../../shared/css/Scrollbar.css";

const Bookings: React.FC = () => {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const packageId = searchParams.get("packageId");
  const { getAllBookings, updateBookingStatus, getPaymentByBookingId } = useBookingsApi(); // Import getPaymentByBookingId
  const [bookings, setBookings] = useState<BookingResponseModel[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState<"create" | "update" | "view">(
    "create"
  );
  const [selectedBooking, setSelectedBooking] =
    useState<BookingResponseModel | null>(null);
  const [formData, setFormData] = useState<BookingRequestModel>({
    userId: "",
    packageId: packageId || "",
    totalPrice: 0,
    status: BookingStatus.PAYMENT_PENDING,
    bookingDate: new Date().toISOString().split("T")[0],
    travelers: [],
  });
  const [formErrors, setFormErrors] = useState({
    userId: false,
    totalPrice: false,
    status: false,
    bookingDate: false,
  });
  const [paymentDetails, setPaymentDetails] = useState<{ [key: string]: PaymentResponseModel }>({});

  useEffect(() => {
    fetchBookings();
  }, [packageId]);

  const fetchBookings = async () => {
    try {
      const data = await getAllBookings({ packageId: packageId || undefined });
      setBookings(data);
      fetchPayments(data);
    } catch (error) {
      console.error("Error fetching bookings:", error);
    }
  };

  const fetchPayments = async (bookings: BookingResponseModel[]) => {
    const paymentDetails: { [key: string]: PaymentResponseModel } = {};
    for (const booking of bookings) {
      try {
        const payment = await getPaymentByBookingId(booking.bookingId);
        paymentDetails[booking.bookingId] = payment;
      } catch (error) {
        console.error(`Error fetching payment for booking ${booking.bookingId}:`, error);
      }
    }
    setPaymentDetails(paymentDetails);
  };

  const handleSave = async () => {
    const errors = {
      userId: !formData.userId,
      totalPrice: formData.totalPrice === null,
      status: !formData.status,
      bookingDate: !formData.bookingDate,
    };
    setFormErrors(errors);

    if (Object.values(errors).some((error) => error)) {
      return;
    }

    try {
      if (modalType === "update" && selectedBooking) {
        await updateBookingStatus(selectedBooking.bookingId, formData.status);
      }
      setShowModal(false);
      await fetchBookings();
    } catch (error) {
      console.error("Error saving booking:", error);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleSave();
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center p-4 dashboard-scrollbar"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <Card
        className="rounded shadow border-0"
        style={{
          width: "1600px",
          height: "800px",
          borderRadius: "15px",
          overflow: "hidden",
        }}
      >
        <Card.Body className="p-4">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h3>{t("Bookings")}</h3>
          </div>

          <Card className="rounded shadow-sm border-0">
            <Card.Body>
              <Table bordered hover responsive className="rounded custom-table">
                <thead className="bg-light">
                  <tr>
                    <th>{t("User ID")}</th>
                    <th>{t("Total Price")}</th>
                    <th>{t("Status")}</th>
                    <th>{t("Booking Date")}</th>
                    <th>{t("revenue")}</th>
                    <th>{t("Actions")}</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.map((booking) => (
                    <tr key={booking.bookingId}>
                      <td>{booking.userId}</td>
                      <td>{booking.totalPrice}</td>
                      <td>{t(booking.status)}</td>
                      <td>{booking.bookingDate}</td>
                      <td>{paymentDetails[booking.bookingId]?.amount !== undefined ? `$${((paymentDetails[booking.bookingId].amount)/100).toFixed(2)}` : t('noAmount')}
                          &nbsp;({paymentDetails[booking.bookingId]?.currency || t('noCurrency')})
                      </td>
                      <td>
                        <Button
                          variant="outline-primary"
                          onClick={() => {
                            setSelectedBooking(booking);
                            setModalType("update");
                            setFormData({
                              userId: booking.userId,
                              packageId: booking.packageId,
                              totalPrice: booking.totalPrice,
                              status: booking.status,
                              bookingDate: booking.bookingDate,
                              travelers: [],
                            });
                            setFormErrors({
                              userId: false,
                              totalPrice: false,
                              status: false,
                              bookingDate: false,
                            });
                            setShowModal(true);
                          }}
                        >
                          {t("Edit Booking")}
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>

          <Modal
            show={showModal}
            onHide={() => setShowModal(false)}
            className="rounded"
          >
            <Modal.Header closeButton>
              <Modal.Title>
                {modalType === "update" ? t("Edit Booking") : t("View Booking")}
              </Modal.Title>
            </Modal.Header>
            <Modal.Body>
              {modalType === "view" ? (
                <div>
                  <p>
                    <strong>{t("User ID")}:</strong> {selectedBooking?.userId}
                  </p>
                  <p>
                    <strong>{t("Total Price")}:</strong>{" "}
                    {selectedBooking?.totalPrice}
                  </p>
                  <p>
                    <strong>{t("Status")}:</strong>{" "}
                    {selectedBooking?.status ? t(selectedBooking.status) : ""}
                  </p>
                  <p>
                    <strong>{t("Booking Date")}:</strong>{" "}
                    {selectedBooking?.bookingDate}
                  </p>
                </div>
              ) : (
                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-3">
                    <Form.Label>{t("User ID")}</Form.Label>
                    <Form.Control
                      required
                      type="text"
                      value={formData.userId}
                      onChange={(e) =>
                        setFormData({ ...formData, userId: e.target.value })
                      }
                      isInvalid={formErrors.userId}
                    />
                    <Form.Control.Feedback type="invalid">
                      {t("User ID is required")}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>{t("Total Price")}</Form.Label>
                    <Form.Control
                      type="number"
                      value={formData.totalPrice}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          totalPrice: +e.target.value,
                        })
                      }
                      isInvalid={formErrors.totalPrice}
                    />
                    <Form.Control.Feedback type="invalid">
                      {t("Total Price is required")}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>{t("Status")}</Form.Label>
                    <Form.Control
                      as="select"
                      value={formData.status}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          status: e.target.value as BookingStatus,
                        })
                      }
                      isInvalid={formErrors.status}
                    >
                      {Object.values(BookingStatus).map((status) => (
                        <option key={status} value={status}>
                          {t(status)}
                        </option>
                      ))}
                    </Form.Control>
                    <Form.Control.Feedback type="invalid">
                      {t("Status is required")}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>{t("Booking Date")}</Form.Label>
                    <Form.Control
                      type="date"
                      value={formData.bookingDate}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          bookingDate: e.target.value,
                        })
                      }
                      isInvalid={formErrors.bookingDate}
                    />
                    <Form.Control.Feedback type="invalid">
                      {t("Booking Date is required")}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Modal.Footer>
                    <Button
                      variant="secondary"
                      onClick={() => setShowModal(false)}
                    >
                      {t("Cancel")}
                    </Button>
                    <Button type="submit" variant="primary">
                      {t("Save")}
                    </Button>
                  </Modal.Footer>
                </Form>
              )}
            </Modal.Body>
          </Modal>
        </Card.Body>
      </Card>
    </div>
  );
};

export default Bookings;
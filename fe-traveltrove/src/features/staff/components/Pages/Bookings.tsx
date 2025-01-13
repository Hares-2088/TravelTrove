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

const Bookings: React.FC = () => {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();
    const packageId = searchParams.get("packageId");
    const { getAllBookings, updateBookingStatus, deleteBooking } = useBookingsApi();
    const [bookings, setBookings] = useState<BookingResponseModel[]>([]);
    const [showModal, setShowModal] = useState(false);
    const { createBooking } = useBookingsApi();
    const [modalType, setModalType] = useState<"create" | "update" | "delete" | "view">(
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
    });
    const [formErrors, setFormErrors] = useState({
        userId: false,
        totalPrice: false,
        status: false,
        bookingDate: false,
    });

    useEffect(() => {
        fetchBookings();
    }, [packageId]);

    const fetchBookings = async () => {
        try {
            const data = await getAllBookings({ packageId: packageId || undefined });
            setBookings(data);
        } catch (error) {
            console.error("Error fetching bookings:", error);
        }
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
            if (modalType === "create") {
                await createBooking(formData);
            } else if (modalType === "update" && selectedBooking) {
                await updateBookingStatus(selectedBooking.bookingId, formData.status);
            }
            setShowModal(false);
            await fetchBookings();
        } catch (error) {
            console.error("Error saving booking:", error);
        }
    };

    const handleDelete = async () => {
        try {
            if (selectedBooking) {
                await deleteBooking(selectedBooking.bookingId);
                setShowModal(false);
                await fetchBookings();
            }
        } catch (error) {
            console.error("Error deleting booking:", error);
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        handleSave();
    };

    return (
        <div
            className="d-flex justify-content-center align-items-center p-4"
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
                        <Button
                            variant="primary"
                            onClick={() => {
                                setModalType("create");
                                setFormData({
                                    userId: "",
                                    packageId: packageId || "",
                                    totalPrice: 0,
                                    status: BookingStatus.PAYMENT_PENDING,
                                    bookingDate: new Date().toISOString().split("T")[0],
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
                            {t("Create Booking")}
                        </Button>
                    </div>

                    <Card className="rounded shadow-sm border-0">
                        <Card.Body>
                            <Table bordered hover responsive className="rounded">
                                <thead className="bg-light">
                                    <tr>
                                        <th>{t("User ID")}</th>
                                        <th>{t("Total Price")}</th>
                                        <th>{t("Status")}</th>
                                        <th>{t("Booking Date")}</th>
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
                                                <Button
                                                    variant="outline-danger"
                                                    className="ms-2"
                                                    onClick={() => {
                                                        setSelectedBooking(booking);
                                                        setModalType("delete");
                                                        setShowModal(true);
                                                    }}
                                                >
                                                    {t("Delete Booking")}
                                                </Button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </Card.Body>
                    </Card>

                    <Modal show={showModal} onHide={() => setShowModal(false)}>
                        <Modal.Header closeButton>
                            <Modal.Title>
                                {modalType === "create"
                                    ? t("Create Booking")
                                    : modalType === "update"
                                    ? t("Edit Booking")
                                    : modalType === "delete"
                                    ? t("Delete Booking")
                                    : t("View Booking")}
                            </Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            {modalType === "delete" ? (
                                <p>{t("areYouSureDelete")}</p>
                            ) : modalType === "view" ? (
                                <div>
                                    <p><strong>{t("User ID")}:</strong> {selectedBooking?.userId}</p>
                                    <p><strong>{t("Total Price")}:</strong> {selectedBooking?.totalPrice}</p>
                                    <p><strong>{t("Status")}:</strong> {selectedBooking?.status ? t(selectedBooking.status) : ""}</p>
                                    <p><strong>{t("Booking Date")}:</strong> {selectedBooking?.bookingDate}</p>
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
                                                setFormData({ ...formData, totalPrice: +e.target.value })
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
                                                setFormData({ ...formData, status: e.target.value as BookingStatus })
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
                                                setFormData({ ...formData, bookingDate: e.target.value })
                                            }
                                            isInvalid={formErrors.bookingDate}
                                        />
                                        <Form.Control.Feedback type="invalid">
                                            {t("Booking Date is required")}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                    <Modal.Footer>
                                        <Button variant="secondary" onClick={() => setShowModal(false)}>
                                            {t("Cancel")}
                                        </Button>
                                        <Button type="submit" variant="primary">
                                            {t("Save")}
                                        </Button>
                                    </Modal.Footer>
                                </Form>
                            )}
                        </Modal.Body>
                        {modalType === "delete" && (
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => setShowModal(false)}>
                                    {t("Cancel")}
                                </Button>
                                <Button variant="danger" onClick={handleDelete}>
                                    {t("Confirm")}
                                </Button>
                            </Modal.Footer>
                        )}
                    </Modal>
                </Card.Body>
            </Card>
        </div>
    );
};

export default Bookings;
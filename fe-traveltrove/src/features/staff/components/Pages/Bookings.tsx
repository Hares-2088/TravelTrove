import React, { useEffect, useState } from "react";
import { Button, Table, Modal, Form, Card, Collapse, Row, Col } from "react-bootstrap"; // Import Collapse, Row, and Col
import { useTranslation } from "react-i18next";
import { useSearchParams } from "react-router-dom";
import { useBookingsApi } from "../../../bookings/api/bookings.api";
import { useUsersApi } from "../../../users/api/users.api"; // Import useUsersApi
import { useTravelersApi } from "../../../travelers/api/traveler.api"; // Import useTravelersApi
import {
    BookingRequestModel,
    BookingResponseModel,
    BookingStatus,
} from "../../../bookings/models/bookings.model";
import { PaymentResponseModel } from "../../../payments/models/payments.model"; // Import PaymentResponseModel
import "./Bookings.css";
import "../../../../shared/css/Scrollbar.css";

const formatStatus = (status: string) => {
    return status
        .toLowerCase()
        .split('_')
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
};

const Bookings: React.FC = () => {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();
    const packageId = searchParams.get("packageId");
    const { getAllBookings, updateBookingStatus, getPaymentByBookingId } = useBookingsApi();
    const { getUserById } = useUsersApi(); // Get user by ID
    const { getTravelerById } = useTravelersApi(); // Get traveler by ID
    const [bookings, setBookings] = useState<BookingResponseModel[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState<"updateStatus" | "view">("updateStatus");
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
    const [userNames, setUserNames] = useState<{ [key: string]: string }>({}); // State for user names
    const [openBookingId, setOpenBookingId] = useState<string | null>(null); // State for toggling travelers list
    const [travelers, setTravelers] = useState<{ [key: string]: { name: string; email: string }[] }>({}); // State for travelers

    useEffect(() => {
        fetchBookings();
    }, [packageId]);

    const fetchBookings = async () => {
        try {
            const data = await getAllBookings({ packageId: packageId || undefined });
            const travelerNamesPromises = data.map(async (booking) => {
                let name = "No Traveler";
                const user = await getUserById(booking.userId);
                let primaryTravelerId = user.travelerId;
                if (!primaryTravelerId && user.travelerIds && user.travelerIds.length > 0) {
                    primaryTravelerId = user.travelerIds[0];
                }
                if (primaryTravelerId) {
                    const traveler = await getTravelerById(primaryTravelerId);
                    if (traveler) {
                        name = `${traveler.firstName} ${traveler.lastName}`;
                    }
                }
                return { bookingId: booking.bookingId, name };
            });
            const travelerNamesData = await Promise.all(travelerNamesPromises);
            const travelerNamesMap = travelerNamesData.reduce((acc, { bookingId, name }) => {
                acc[bookingId] = name;
                return acc;
            }, {} as { [key: string]: string });
            setUserNames(travelerNamesMap);
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
            }
        }
        setPaymentDetails(paymentDetails);
    };

    const fetchTravelers = async (travelerIds: string[]) => {
        try {
            const travelersData = await Promise.all(travelerIds.map((id) => getTravelerById(id)));
            console.log("Fetched travelers:", travelersData); // Log fetched travelers
            return travelersData.map((traveler) => ({
                name: `${traveler.firstName} ${traveler.lastName}`,
                email: traveler.email,
            }));
        } catch (error) {
            console.error("Error fetching travelers:", error);
            return [];
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
            if (modalType === "updateStatus" && selectedBooking) {
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

    const toggleTravelersList = async (bookingId: string, travelerIds: string[]) => {
        if (openBookingId === bookingId) {
            setOpenBookingId(null);
        } else {
            if (!travelers[bookingId]) {
                const travelersData = await fetchTravelers(travelerIds);
                setTravelers((prev) => ({ ...prev, [bookingId]: travelersData }));
            }
            setOpenBookingId(bookingId);
        }
    };

    return (
        <div
            className="d-flex justify-content-center align-items-start p-4 dashboard-scrollbar"
            style={{ backgroundColor: "#f8f9fa", minHeight: "100vh", overflowY: "auto" }}
        >
            <Card
                className="rounded shadow border-0 w-100"
                style={{
                    maxWidth: "1600px",
                    height: "auto",
                    borderRadius: "15px",
                    overflow: "hidden",
                }}
            >
                <Card.Body className="p-4" style={{ maxHeight: "70vh", overflowY: "auto" }}>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h3>{t("bookings.bookings")}</h3>
                    </div>

                    <Row>
                        {bookings.map((booking) => (
                            <Col xs={12} md={6} lg={4} key={booking.bookingId} className="mb-4">
                                <Card className="h-100 booking-card">
                                    <Card.Body>
                                        <Card.Title>{userNames[booking.bookingId]}</Card.Title>
                                        <Card.Text>
                                            <strong>{t("bookings.totalPrice")}:</strong> {booking.totalPrice}<br />
                                            <strong>{t("bookings.status")}:</strong> {formatStatus(booking.status)}<br />
                                            <strong>{t("bookings.bookingDate")}:</strong> {booking.bookingDate}<br />
                                        </Card.Text>
                                        <div className="d-flex justify-content-between">
                                            {booking.status !== BookingStatus.REFUNDED && (
                                                <Button
                                                    variant="outline-primary"
                                                    onClick={() => {
                                                        setSelectedBooking(booking);
                                                        setModalType("updateStatus");
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
                                                    {t("bookings.updateStatus")}
                                                </Button>
                                            )}
                                            <Button
                                                variant="outline-secondary"
                                                onClick={() => booking.travelerIds && toggleTravelersList(booking.bookingId, booking.travelerIds)}
                                            >
                                                {openBookingId === booking.bookingId ? t("bookings.hideTravelers") : t("bookings.showTravelers")}
                                            </Button>
                                        </div>
                                        <Collapse in={openBookingId === booking.bookingId}>
                                            <div className="mt-3">
                                                {travelers[booking.bookingId]?.map((traveler) => (
                                                    <Card key={traveler.email} className="traveler-card mb-2">
                                                        <Card.Body>
                                                            <Card.Title>{traveler.name}</Card.Title>
                                                            <Card.Text>{traveler.email}</Card.Text>
                                                        </Card.Body>
                                                    </Card>
                                                ))}
                                            </div>
                                        </Collapse>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>

                    <Modal
                        show={showModal}
                        onHide={() => setShowModal(false)}
                        className="rounded"
                    >
                        <Modal.Header closeButton>
                            <Modal.Title>
                                {modalType === "updateStatus" ? t("bookings.updateBookingStatus") : t("bookings.viewBooking")}
                            </Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            {modalType === "view" ? (
                                <div>
                                    <p>
                                        <strong>{t("bookings.userName")}:</strong> {userNames[selectedBooking?.userId || ""]}
                                    </p>
                                    <p>
                                        <strong>{t("bookings.totalPrice")}:</strong>{" "}
                                        {selectedBooking?.totalPrice}
                                    </p>
                                    <p>
                                        <strong>{t("bookings.status")}:</strong>{" "}
                                        {selectedBooking?.status ? formatStatus(selectedBooking.status) : ""}
                                    </p>
                                    <p>
                                        <strong>{t("bookings.bookingDate")}:</strong>{" "}
                                        {selectedBooking?.bookingDate}
                                    </p>
                                </div>
                            ) : (
                                <Form onSubmit={handleSubmit}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>{t("bookings.status")}</Form.Label>
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
                                            {/* Always show the current status as the placeholder */}
                                            <option value={selectedBooking?.status} disabled>
                                                {formatStatus(selectedBooking?.status || "Select Status")}
                                            </option>

                                            {/* If status is COMPLETED, only allow REFUNDED */}
                                            {selectedBooking?.status === BookingStatus.BOOKING_CONFIRMED ? (
                                                <option value={BookingStatus.REFUNDED}>{formatStatus(BookingStatus.REFUNDED)}</option>
                                            ) : (
                                                Object.values(BookingStatus)
                                                    .filter((status) =>
                                                        status !== BookingStatus.REFUNDED &&
                                                        !(selectedBooking?.status === BookingStatus.PAYMENT_ATTEMPT2_PENDING && status === BookingStatus.PAYMENT_PENDING)
                                                    )
                                                    .map((status) => (
                                                        <option key={status} value={status}>
                                                            {formatStatus(status)}
                                                        </option>
                                                    ))
                                            )}
                                        </Form.Control>
                                        <Form.Control.Feedback type="invalid">
                                            {t("bookings.statusRequired")}
                                        </Form.Control.Feedback>
                                    </Form.Group>
                                    <Modal.Footer>
                                        <Button
                                            variant="secondary"
                                            onClick={() => setShowModal(false)}
                                        >
                                            {t("bookings.cancel")}
                                        </Button>
                                        <Button type="submit" variant="primary">
                                            {t("bookings.save")}
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
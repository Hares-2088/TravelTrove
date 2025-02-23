import React, { useState, useEffect } from "react";
import { Button, Table, Modal, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { useNotificationsApi } from "../../../notifications/api/notifications.api";
import {
    NotificationRequestModel,
    NotificationResponseModel,
} from "../../../notifications/models/notification.model";
import "../../../../shared/css/Scrollbar.css";

const NotificationsTab: React.FC = () => {
    const {
        getAllNotifications,
        getNotificationById,
        deleteNotification,
        sendCustomNotification,
    } = useNotificationsApi();
    const { t } = useTranslation();
    const [notifications, setNotifications] = useState<NotificationResponseModel[]>([]);
    const [viewingNotification, setViewingNotification] =
        useState<NotificationResponseModel | null>(null);
    const [showModal, setShowModal] = useState(false);
    const [modalType, setModalType] = useState<"create" | "delete">("create");
    const [selectedNotification, setSelectedNotification] =
        useState<NotificationResponseModel | null>(null);
    const [formData, setFormData] = useState<NotificationRequestModel>({
        to: "",
        subject: "",
        messageContent: "",
    });
    const [formErrors, setFormErrors] = useState({
        to: false,
        subject: false,
        messageContent: false,
    });

    useEffect(() => {
        fetchNotifications();
    }, []);

    const fetchNotifications = async () => {
        try {
            const data = await getAllNotifications();
            setNotifications(data);
        } catch (error) {
            console.error("Error fetching notifications:", error);
        }
    };

    const handleViewNotification = async (notificationId: string) => {
        try {
            const notification = await getNotificationById(notificationId);
            setViewingNotification(notification);
        } catch (error) {
            console.error("Error fetching notification details:", error);
        }
    };

    const handleSave = async () => {
        const isToValid = validateEmail(formData.to);
        const isSubjectValid = formData.subject.trim() !== "";
        const isMessageContentValid = formData.messageContent.trim() !== "";

        setFormErrors({
            to: !isToValid,
            subject: !isSubjectValid,
            messageContent: !isMessageContentValid,
        });

        if (!isToValid || !isSubjectValid || !isMessageContentValid) return;

        try {
            if (modalType === "create") {
                await sendCustomNotification(formData);
            }
            setShowModal(false);
            await fetchNotifications();
        } catch (error) {
            console.error("Error sending custom notification:", error);
        }
    };

    const handleDelete = async () => {
        try {
            if (selectedNotification) {
                await deleteNotification(selectedNotification.notificationId);
                setShowModal(false);
                await fetchNotifications();
            }
        } catch (error) {
            console.error("Error deleting notification:", error);
        }
    };

    const validateEmail = (email: string): boolean => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // Simple regex for email validation
        return emailRegex.test(email);
    };

    return (
        <div>
            {/* Viewing a Single Notification */}
            {viewingNotification ? (
                <div>
                    <Button
                        variant="link"
                        className="text-primary mb-3"
                        onClick={() => setViewingNotification(null)}
                        style={{
                            textDecoration: "none",
                            display: "flex",
                            alignItems: "center",
                            gap: "5px",
                        }}
                    >
                        <span>&larr;</span> {t("backToListN")}
                    </Button>
                    <h3>{viewingNotification.subject}</h3>
                    <p>
                        <strong>{t("to")}:</strong> {viewingNotification.to}
                    </p>
                    <p>
                        <strong>{t("message")}:</strong> {viewingNotification.messageContent}
                    </p>
                    <p>
                        <strong>{t("sentAt")}:</strong> {viewingNotification.sentAt}
                    </p>
                </div>
            ) : (
                <>
                    {/* List of Notifications */}
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h3>{t("notificationsTitle")}</h3>
                        <Button
                            variant="primary"
                            onClick={() => {
                                setModalType("create");
                                setFormData({ to: "", subject: "", messageContent: "" });
                                setShowModal(true);
                            }}
                        >
                            {t("create")}
                        </Button>
                    </div>
                    <div
                        
                        style={{ maxHeight: "700px", overflowY: "auto" }}
                    >
                        <Table
                            bordered
                            hover
                            responsive
                            className="rounded"
                            style={{ borderRadius: "12px", overflow: "hidden" }}
                        >
                            <thead className="bg-light">
                            <tr>
                                <th>{t("subject")}</th>
                                <th>{t("actions")}</th>
                            </tr>
                            </thead>
                            <tbody>
                            {notifications.map((notification) => (
                                <tr key={notification.notificationId}>
                                    <td
                                        onClick={() => handleViewNotification(notification.notificationId)}
                                        style={{
                                            cursor: "pointer",
                                            color: "#007bff",
                                            textDecoration: "underline",
                                        }}
                                    >
                                        {notification.subject}
                                    </td>
                                    <td>
                                        <Button
                                            variant="outline-danger"
                                            onClick={() => {
                                                setSelectedNotification(notification);
                                                setModalType("delete");
                                                setShowModal(true);
                                            }}
                                        >
                                            {t("deleteN")}
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                    </div>
                </>
            )}

            {/* Modals for Create and Delete */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {modalType === "create"
                            ? t("createNotification")
                            : t("deleteNotification")}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {modalType === "delete" ? (
                        <p>{t("areYouSureN")}</p>
                    ) : (
                        <Form>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("to")}</Form.Label>
                                <Form.Control
                                    required
                                    type="email"
                                    value={formData.to}
                                    onChange={(e) =>
                                        setFormData({ ...formData, to: e.target.value })
                                    }
                                    isInvalid={formErrors.to}
                                />
                                <div className="invalid-feedback">{t("toRequired")}</div>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("subject")}</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    value={formData.subject}
                                    onChange={(e) =>
                                        setFormData({ ...formData, subject: e.target.value })
                                    }
                                    isInvalid={formErrors.subject}
                                />
                                <div className="invalid-feedback">{t("subjectRequired")}</div>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("message")}</Form.Label>
                                <Form.Control
                                    required
                                    as="textarea"
                                    rows={3}
                                    value={formData.messageContent}
                                    onChange={(e) =>
                                        setFormData({ ...formData, messageContent: e.target.value })
                                    }
                                    isInvalid={formErrors.messageContent}
                                />
                                <div className="invalid-feedback">{t("messageRequired")}</div>
                            </Form.Group>
                        </Form>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        {t("cancelN")}
                    </Button>
                    <Button
                        variant={modalType === "delete" ? "danger" : "primary"}
                        onClick={modalType === "delete" ? handleDelete : handleSave}
                    >
                        {modalType === "delete" ? t("confirmN") : t("save")}
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default NotificationsTab;

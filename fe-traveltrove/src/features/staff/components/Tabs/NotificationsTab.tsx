import React, { useState, useEffect } from "react";
import { Button, Table, Modal } from "react-bootstrap";
import { useTranslation } from "react-i18next"; // Importing useTranslation
import { useNotificationsApi } from "../../../notifications/api/notifications.api";
import {
    NotificationResponseModel,
} from "../../../notifications/models/notification.model";
import "../../../../shared/css/Scrollbar.css";

const NotificationsTab: React.FC = () => {
    const {
        getAllNotifications,
        getNotificationById,
        deleteNotification,
    } = useNotificationsApi(); // Use Hook
    const { t } = useTranslation(); // Initializing the translation hook
    const [notifications, setNotifications] = useState<NotificationResponseModel[]>([]);
    const [viewingNotification, setViewingNotification] =
        useState<NotificationResponseModel | null>(null);
    const [showModal, setShowModal] = useState(false);
    const [selectedNotification, setSelectedNotification] =
        useState<NotificationResponseModel | null>(null);

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
                    </div>
                    <div
                        className="dashboard-scrollbar"
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

            {/* Delete Confirmation Modal */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{t("deleteNotification")}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>{t("areYouSureN")}</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        {t("cancelN")}
                    </Button>
                    <Button variant="danger" onClick={handleDelete}>
                        {t("confirmN")}
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default NotificationsTab;

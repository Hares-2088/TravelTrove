import React, { useState } from "react";
import { useNotificationsApi} from "../features/notifications/api/notifications.api";
import { NotificationContactusRequestModel} from "../features/notifications/models/notification.model";
import { Form, Button, Container, Card, Alert, Spinner } from "react-bootstrap";

const ContactUsPage: React.FC = () => {
    const { sendContactUsNotification } = useNotificationsApi(); // Use API Hook

    const [formData, setFormData] = useState<Omit<NotificationContactusRequestModel, "to">>({
        firstName: "",
        lastName: "",
        email: "",
        subject: "",
        messageContent: "",
    });

    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setSuccess(null);
        setError(null);

        try {
            const requestData: NotificationContactusRequestModel = {
                ...formData,
                to: "traveltrove.notifications@gmail.com",
            };
            await sendContactUsNotification(requestData);
            setSuccess("Your message has been sent successfully!");
            setFormData({ firstName: "", lastName: "", email: "", subject: "", messageContent: "" });
        } catch (err) {
            setError("Failed to send message. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="d-flex justify-content-center align-items-center min-vh-100">
            <Card className="w-100 shadow-lg p-4" style={{ maxWidth: "500px", borderRadius: "12px" }}>
                <h2 className="text-center mb-4">Contact Us</h2>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label htmlFor="firstName">First Name</Form.Label>
                        <Form.Control
                            id="firstName"
                            type="text"
                            name="firstName"
                            placeholder="Enter your first name"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label htmlFor="lastName">Last Name</Form.Label>
                        <Form.Control
                            id="lastName"
                            type="text"
                            name="lastName"
                            placeholder="Enter your last name"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label htmlFor="email">Email</Form.Label>
                        <Form.Control
                            id="email"
                            type="email"
                            name="email"
                            placeholder="Enter your email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label htmlFor="subject">Subject</Form.Label>
                        <Form.Control
                            id="subject"
                            type="text"
                            name="subject"
                            placeholder="Enter the subject"
                            value={formData.subject}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label htmlFor="messageContent">Message</Form.Label>
                        <Form.Control
                            id="messageContent"
                            as="textarea"
                            rows={4}
                            name="messageContent"
                            placeholder="Enter your message"
                            value={formData.messageContent}
                            onChange={handleChange}
                            required
                        />
                    </Form.Group>

                    {success && <Alert variant="success">{success}</Alert>}
                    {error && <Alert variant="danger">{error}</Alert>}

                    <Button type="submit" variant="dark" className="w-100" disabled={loading}>
                        {loading ? <Spinner as="span" animation="border" size="sm" /> : "Submit"}
                    </Button>
                </Form>
            </Card>
        </Container>
    );
};

export default ContactUsPage;

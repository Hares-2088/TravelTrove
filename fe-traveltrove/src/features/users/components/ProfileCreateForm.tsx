import React, { useState, useEffect } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { useTravelersApi } from "../../travelers/api/traveler.api";
import { useUsersApi } from "../api/users.api";
import { useAuth0 } from "@auth0/auth0-react";
import { TravelerWithIdRequestModel } from "../../travelers/model/traveler.model";
import "./ProfileCreateForm.css";
import { AppRoutes } from "../../../shared/models/app.routes";
import { useNavigate } from "react-router-dom";

const ProfileCreateForm: React.FC = () => {
    const { addTravelerUser, getTravelerById } = useTravelersApi();
    const { getUserById } = useUsersApi();
    const { user } = useAuth0();
    const navigate = useNavigate();
    const [formData, setFormData] = useState<TravelerWithIdRequestModel>({
        travelerId: "",
        seq: 0,
        firstName: "",
        lastName: "",
        addressLine1: "",
        addressLine2: "",
        city: "",
        state: "",
        email: "",
        countryId: "",
    });
    const [formErrors, setFormErrors] = useState({
        firstName: false,
        lastName: false,
        addressLine1: false,
        city: false,
        state: false,
        email: false,
        countryId: false,
    });
    const [showModal, setShowModal] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [travelerExists, setTravelerExists] = useState(false);

    useEffect(() => {
        const fetchUserAndTraveler = async () => {
            if (!user?.sub) {
                console.error("Auth0 user sub is undefined.");
                return;
            }

            try {
                const userResponse = await getUserById(user.sub);

                try {
                    await getTravelerById(userResponse.travelerId);
                    setTravelerExists(true); // Traveler already exists
                } catch {
                    setTravelerExists(false); // Traveler does not exist
                    setFormData((prevData) => ({
                        ...prevData,
                        travelerId: userResponse.travelerId,
                    }));
                }
            } catch (error) {
                console.error("Error fetching user or traveler:", error);
            }
        };

        fetchUserAndTraveler();
    }, [getUserById, getTravelerById, user?.sub]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
        setFormErrors({ ...formErrors, [name]: false });
    };

    const validateForm = (): boolean => {
        const errors = {
            firstName: formData.firstName.trim() === "",
            lastName: formData.lastName.trim() === "",
            addressLine1: formData.addressLine1.trim() === "",
            city: formData.city.trim() === "",
            state: formData.state.trim() === "",
            email: formData.email.trim() === "",
            countryId: formData.countryId.trim() === "",
        };
        setFormErrors(errors);
        return !Object.values(errors).includes(true);
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;

        setIsSubmitting(true);
        try {
            await addTravelerUser(formData);
            setShowModal(false);
            setFormData({
                travelerId: "",
                seq: 0,
                firstName: "",
                lastName: "",
                addressLine1: "",
                addressLine2: "",
                city: "",
                state: "",
                email: "",
                countryId: "",
            });
            navigate(AppRoutes.Home);
        } catch (error) {
            console.error("Error creating traveler profile:", error);
        } finally {
            setIsSubmitting(false);
        }
    };

    if (travelerExists) {
        return (
            <div className="profile-create-form">
                <h2>Traveler Profile Already Exists</h2>
                <p>Your traveler profile already exists. No need to create a new one.</p>
                <Button variant="primary" onClick={() => navigate(AppRoutes.Home)}>
                    Go to Home
                </Button>
            </div>
        );
    }

    return (
        <div className="profile-create-form">
            <h2>Create Traveler Profile</h2>
            <Form>
                <Form.Group className="mb-3">
                    <Form.Label>First Name</Form.Label>
                    <Form.Control
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleInputChange}
                        isInvalid={formErrors.firstName}
                    />
                    <Form.Control.Feedback type="invalid">
                        First Name is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Last Name</Form.Label>
                    <Form.Control
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        isInvalid={formErrors.lastName}
                    />
                    <Form.Control.Feedback type="invalid">
                        Last Name is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Address Line 1</Form.Label>
                    <Form.Control
                        type="text"
                        name="addressLine1"
                        value={formData.addressLine1}
                        onChange={handleInputChange}
                        isInvalid={formErrors.addressLine1}
                    />
                    <Form.Control.Feedback type="invalid">
                        Address Line 1 is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Address Line 2 (Optional)</Form.Label>
                    <Form.Control
                        type="text"
                        name="addressLine2"
                        value={formData.addressLine2}
                        onChange={handleInputChange}
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>City</Form.Label>
                    <Form.Control
                        type="text"
                        name="city"
                        value={formData.city}
                        onChange={handleInputChange}
                        isInvalid={formErrors.city}
                    />
                    <Form.Control.Feedback type="invalid">
                        City is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>State</Form.Label>
                    <Form.Control
                        type="text"
                        name="state"
                        value={formData.state}
                        onChange={handleInputChange}
                        isInvalid={formErrors.state}
                    />
                    <Form.Control.Feedback type="invalid">
                        State is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Email</Form.Label>
                    <Form.Control
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        isInvalid={formErrors.email}
                    />
                    <Form.Control.Feedback type="invalid">
                        Email is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Country</Form.Label>
                    <Form.Control
                        type="text"
                        name="countryId"
                        value={formData.countryId}
                        onChange={handleInputChange}
                        isInvalid={formErrors.countryId}
                    />
                    <Form.Control.Feedback type="invalid">
                        Country is required.
                    </Form.Control.Feedback>
                </Form.Group>

                <Button
                    variant="primary"
                    onClick={() => setShowModal(true)}
                    className="mt-3"
                >
                    Submit
                </Button>
            </Form>

            {/* Confirmation Modal */}
            <Modal show={showModal} onHide={() => setShowModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Confirm Creation</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Are you sure you want to create this traveler profile?
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>
                        Cancel
                    </Button>
                    <Button
                        variant="primary"
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? "Submitting..." : "Confirm"}
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default ProfileCreateForm;

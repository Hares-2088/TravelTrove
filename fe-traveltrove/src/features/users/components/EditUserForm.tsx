
import React, { useState } from "react";
import { Modal, Form, Row, Col, Button } from "react-bootstrap";
import { useUsersApi } from "../../../../users/api/users.api"; // Custom API hooks
import { UserResponseModel } from "../../../../users/model/users.model"; // User model interface


const EditUserForm: React.FC<EditUserFormProps> = ({
    user,
    show,
    onHide,
    onSubmit,
  }) => {
    const { updateUser, updateUserRole } = useUsersApi(); // Access APIs
    const [formData, setFormData] = useState({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      roleId: user.roles.length > 0 ? user.roles[0] : "",
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
  
    const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      setIsSubmitting(true);
  
      try {
        // Update user fields
        const { firstName, lastName, email } = formData;
        await updateUser(user.userId, { firstName, lastName, email });
  
        // Update user role
        if (formData.roleId !== user.roles[0]) {
          await updateUserRole(user.userId, [formData.roleId]);
        }
  
        onHide();
      } catch (error) {
        console.error("Failed to update user:", error);
      } finally {
        setIsSubmitting(false);
      }
    };
  
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      const { name, value } = e.target;
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    };
  
    return (
      <Modal show={show} onHide={onHide} centered>
        <Modal.Header closeButton>
          <Modal.Title>Edit User</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label>First Name</Form.Label>
                  <Form.Control
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label>Last Name</Form.Label>
                  <Form.Control
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Role</Form.Label>
              <Form.Select
                name="roleId"
                value={formData.roleId}
                onChange={handleChange}
                required
              >
                <option value="">Select a role</option>
                {AUTH0_ROLES.map((role) => (
                  <option key={role.id} value={role.id}>
                    {role.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={onHide}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Saving..." : "Save Changes"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    );
  };
  
import React, { useState } from "react";
import { Table, Button, Modal, Form, Row, Col, Alert } from "react-bootstrap";
import { Link } from "react-router-dom";
import { PencilSquare, Trash } from "react-bootstrap-icons";
import { UserResponseModel } from "../model/users.model";

interface Auth0Role {
  id: string;
  name: string;
}

const AUTH0_ROLES: Auth0Role[] = [
  { id: "rol_n0x6f30TQGgcKJWo", name: "Admin" },
  { id: "rol_bGEYlXT5XYsHGhcQ", name: "Customer" },
  { id: "rol_e6pFgGUgGlnHZz1D", name: "Employee" },
];

interface UsersListProps {
  users: UserResponseModel[];
  onUpdateUser: (
    userId: string,
    updatedUser: Partial<UserResponseModel>
  ) => Promise<void>;
  onUpdateRole: (userId: string, roles: string[]) => Promise<void>;
}

const UsersList: React.FC<UsersListProps> = ({
  users,
  onUpdateUser,
  onUpdateRole,
}) => {
  const [editingUser, setEditingUser] = useState<UserResponseModel | null>(
    null
  );
  const [formData, setFormData] = useState({
    email: "",
    roleId: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleEditClick = (user: UserResponseModel) => {
    setEditingUser(user);
    setFormData({
      email: user.email,
      roleId: user.roles[0] || "",
    });
    setError(null);
  };

  const handleModalClose = () => {
    setEditingUser(null);
    setError(null);
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingUser) return;

    setIsSubmitting(true);
    setError(null);

    try {
      // Update user details
      await onUpdateUser(editingUser.userId, {
        email: formData.email,
      });

      // If role has changed, update it
      if (editingUser.roles[0] !== formData.roleId) {
        await onUpdateRole(editingUser.userId, [formData.roleId]);
      }

      handleModalClose();
    } catch (err) {
      setError("Failed to update user. Please try again.");
      console.error("Update failed:", err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <Table hover className="align-middle">
        <thead className="table-light">
          <tr>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>{user.email}</td>
              <td>
                {user.roles
                  .map(
                    (roleId) =>
                      AUTH0_ROLES.find((r) => r.id === roleId)?.name || roleId
                  )
                  .join(", ")}
              </td>
              <td>
                <Button
                  variant="outline-primary"
                  size="sm"
                  onClick={() => handleEditClick(user)}
                >
                  <PencilSquare className="me-1" />
                  Edit
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={!!editingUser} onHide={handleModalClose}>
        <Form onSubmit={handleSubmit}>
          <Modal.Header closeButton>
            <Modal.Title>Edit User</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {error && <div className="alert alert-danger mb-3">{error}</div>}
            <Form.Group className="mb-3">
              <Form.Label>Email</Form.Label>
              <Form.Control
                type="email"
                name="email"
                value={formData.email}
                onChange={(e) =>
                  handleChange(e as React.ChangeEvent<HTMLInputElement>)
                }
                readOnly
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
            <Button variant="secondary" onClick={handleModalClose}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Saving..." : "Save Changes"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </>
  );
};

export default UsersList;

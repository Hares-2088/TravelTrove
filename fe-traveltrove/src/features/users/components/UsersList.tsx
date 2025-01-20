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
  { id: "rol_e6pFgGUgGlnHZz1D", name: "Employee" }
];

interface UsersListProps {
  users: UserResponseModel[];
  onUpdateUser: (userId: string, updatedUser: Partial<UserResponseModel>) => Promise<void>;
  onDeleteUser: (userId: string) => Promise<void>;
}

interface DeleteConfirmationProps {
  show: boolean;
  user: UserResponseModel;
  onConfirm: () => Promise<void>;
  onCancel: () => void;
}

interface EditUserFormProps {
  user: UserResponseModel;
  show: boolean;
  onHide: () => void;
  onSubmit: (updatedUser: Partial<UserResponseModel>) => Promise<void>;
}

const DeleteConfirmation: React.FC<DeleteConfirmationProps> = ({
  show,
  user,
  onConfirm,
  onCancel,
}) => {
  const [isDeleting, setIsDeleting] = useState(false);

  const handleConfirm = async () => {
    setIsDeleting(true);
    try {
      await onConfirm();
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <Alert 
      show={show} 
      variant="danger" 
      className="position-fixed top-0 start-50 translate-middle-x mt-3 shadow-sm"
      style={{ zIndex: 1050, maxWidth: '500px' }}
    >
      <Alert.Heading>Confirm Delete</Alert.Heading>
      <p>
        Are you sure you want to delete user <strong>{user.firstName} {user.lastName}</strong>?
        This action cannot be undone.
      </p>
      <hr />
      <div className="d-flex justify-content-end">
        <Button 
          variant="light" 
          onClick={onCancel} 
          className="me-2"
          disabled={isDeleting}
        >
          Cancel
        </Button>
        <Button 
          variant="danger" 
          onClick={handleConfirm}
          disabled={isDeleting}
        >
          {isDeleting ? 'Deleting...' : 'Delete'}
        </Button>
      </div>
    </Alert>
  );
};

// Modified EditUserForm with role dropdown
const EditUserForm: React.FC<EditUserFormProps> = ({ user, show, onHide, onSubmit }) => {
  const [formData, setFormData] = useState({
    firstName: user.firstName,
    lastName: user.lastName,
    email: user.email,
    roles: user.roles
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      await onSubmit(formData);
      onHide();
    } catch (error) {
      console.error('Failed to update user:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
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
                  // onChange={handleChange}
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
                  // onChange={handleChange}
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
              // onChange={handleChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Role</Form.Label>
            <Form.Select
              name="roles"
              value={formData.roles}
              onChange={handleChange}
              required
            >
              <option value="">Select a role</option>
              {AUTH0_ROLES.map(role => (
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
          <Button 
            variant="primary" 
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Saving...' : 'Save Changes'}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
};

// UsersList component remains the same...
const UsersList: React.FC<UsersListProps> = ({ users, onUpdateUser, onDeleteUser }) => {
  const [editingUser, setEditingUser] = useState<UserResponseModel | null>(null);
  const [deletingUser, setDeletingUser] = useState<UserResponseModel | null>(null);

  const handleEditClick = (user: UserResponseModel) => {
    setEditingUser(user);
  };

  const handleDeleteClick = (user: UserResponseModel) => {
    setDeletingUser(user);
  };

  const handleModalClose = () => {
    setEditingUser(null);
  };

  const handleDeleteCancel = () => {
    setDeletingUser(null);
  };

  const handleDeleteConfirm = async () => {
    if (deletingUser) {
      await onDeleteUser(deletingUser.userId);
      setDeletingUser(null);
    }
  };

  const handleUpdateUser = async (updatedUser: Partial<UserResponseModel>) => {
    if (editingUser) {
      await onUpdateUser(editingUser.userId, updatedUser);
    }
  };

  // Helper function to get role name
  const getRoleName = (roleId: string) => {
    const role = AUTH0_ROLES.find(r => r.id === roleId);
    return role ? role.name : roleId;
  };

  return (
    <>
      {deletingUser && (
        <DeleteConfirmation
          show={true}
          user={deletingUser}
          onConfirm={handleDeleteConfirm}
          onCancel={handleDeleteCancel}
        />
      )}

      <Table hover className="align-middle">
        <thead className="table-light">
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>
                <Link
                  to={`/users/${user.userId}`}
                  className="text-primary"
                >
                  {user.firstName} {user.lastName}
                </Link>
              </td>
              <td>{user.email}</td>
              <td>{user.roles.map(roleId => getRoleName(roleId)).join(', ')}</td>
              <td>
                <Button 
                  variant="outline-primary" 
                  size="sm" 
                  className="me-2"
                  onClick={() => handleEditClick(user)}
                >
                  <PencilSquare className="me-1" />
                  Edit
                </Button>
                <Button 
                  variant="outline-danger" 
                  size="sm"
                  onClick={() => handleDeleteClick(user)}
                >
                  <Trash className="me-1" />
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      {editingUser && (
        <EditUserForm
          user={editingUser}
          show={true}
          onHide={handleModalClose}
          onSubmit={handleUpdateUser}
        />
      )}
    </>
  );
};

export default UsersList;
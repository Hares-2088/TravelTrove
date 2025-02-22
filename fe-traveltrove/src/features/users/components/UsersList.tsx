import React, { useState } from "react";
import { Table, Button, Modal, Form } from "react-bootstrap";
import { PencilSquare } from "react-bootstrap-icons";
import { UserResponseModel } from "../model/users.model";
import { useTranslation } from "react-i18next";

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
  onUpdateUser: (userId: string, updatedUser: Partial<UserResponseModel>) => Promise<void>;
  onUpdateRole: (userId: string, roles: string[]) => Promise<void>;
}

const UsersList: React.FC<UsersListProps> = ({ users, onUpdateUser, onUpdateRole }) => {
  const [editingUser, setEditingUser] = useState<UserResponseModel | null>(null);
  const [formData, setFormData] = useState({
    email: "",
    roleId: "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { t } = useTranslation();

  // Updated getRoleName function checks if the roleIdentifier matches an Auth0 role id.
  // If not, it assumes the roleIdentifier is already the English role name.
  const getRoleName = (roleIdentifier: string): string => {
    const role = AUTH0_ROLES.find((r) => r.id === roleIdentifier);
    if (role) {
      return t(`roles.${role.name}`);
    }
    return t(`roles.${roleIdentifier}`);
  };

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

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
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
      await onUpdateUser(editingUser.userId, { email: formData.email });

      if (editingUser.roles[0] !== formData.roleId) {
        await onUpdateRole(editingUser.userId, [formData.roleId]);
      }

      handleModalClose();
    } catch (err) {
      setError(t("updateFailed"));
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <Table hover className="align-middle">
        <thead className="table-light">
          <tr>
            <th>{t("email")}</th>
            <th>{t("role")}</th>
            <th>{t("actions")}</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>{user.email}</td>
              <td>{user.roles.map((roleId) => getRoleName(roleId)).join(", ")}</td>
              <td>
                <Button variant="outline-primary" size="sm" onClick={() => handleEditClick(user)}>
                  <PencilSquare className="me-1" />
                  {t("edit")}
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <Modal show={!!editingUser} onHide={handleModalClose}>
        <Form onSubmit={handleSubmit}>
          <Modal.Header closeButton>
            <Modal.Title>{t("edit")}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {error && <div className="alert alert-danger mb-3">{error}</div>}
            <Form.Group className="mb-3">
              <Form.Label>{t("email")}</Form.Label>
              <Form.Control
                type="email"
                name="email"
                value={formData.email}
                onChange={(e) => handleChange(e as React.ChangeEvent<HTMLInputElement>)}
                readOnly
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>{t("role")}</Form.Label>
              <Form.Select name="roleId" value={formData.roleId} onChange={handleChange} required>
                <option value="">{t("selectRole")}</option>
                {AUTH0_ROLES.map((role) => (
                  <option key={role.id} value={role.id}>
                    {t(`roles.${role.name}`)}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleModalClose}>
              {t("cancel")}
            </Button>
            <Button variant="primary" type="submit" disabled={isSubmitting}>
              {t("save")}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </>
  );
};

export default UsersList;

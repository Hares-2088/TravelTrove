import React, { useEffect, useState } from "react";
import { Card, Button, Row, Col, InputGroup, Form } from "react-bootstrap";
import { PersonPlus, Search } from "react-bootstrap-icons";
import { UserResponseModel } from "../../../../users/model/users.model";
import { useUsersApi } from "../../../../users/api/users.api";
import UsersList from "../../../../users/components/UsersList";
import { useTranslation } from "react-i18next";

const UserManagement: React.FC = () => {
  const { getAllUsers, updateUser, updateUserRole, syncUser } = useUsersApi();
  const [users, setUsers] = useState<UserResponseModel[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedRole, setSelectedRole] = useState("All Roles");
  const { t } = useTranslation();

  const fetchAllUsers = async () => {
    try {
      setLoading(true);
      const data = await getAllUsers();
      setUsers(data || []);
      setError(null);
    } catch (err) {
      setError(t("error.fetchUsers"));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAllUsers();
  }, []);

  const handleUpdateUser = async (
    userId: string,
    updatedUser: Partial<UserResponseModel>
  ) => {
    try {
      setLoading(true);
      const userUpdateData = {
        firstName: updatedUser.firstName,
        lastName: updatedUser.lastName,
        email: updatedUser.email,
      };

      await updateUser(userId, userUpdateData);
      await fetchAllUsers();
    } catch (error) {
      setError(t("error.updateUser"));
      console.error("Failed to update user", error);
    } finally {
      setLoading(false);
    }
  };

  const handleRoleUpdate = async (userId: string, roleIds: string[]) => {
    try {
      setLoading(true);
      await updateUserRole(userId, roleIds);
      await syncUser(userId);
      await fetchAllUsers();
    } catch (error) {
      setError(t("error.updateUserRole"));
      console.error("Failed to update user role", error);
    } finally {
      setLoading(false);
    }
  };

  const filteredUsers = users.filter(user => 
    (selectedRole === "All Roles" || user.roles.some(role => role.toLowerCase() === selectedRole.toLowerCase())) &&
    ((user.firstName?.toLowerCase() || "").includes(searchTerm.toLowerCase()) || 
     (user.lastName?.toLowerCase() || "").includes(searchTerm.toLowerCase()) || 
     (user.email?.toLowerCase() || "").includes(searchTerm.toLowerCase()))
  );

  if (loading) return <div>{t("loading")}</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div
      className="d-flex justify-content-center align-items-start p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <Card
        className="rounded shadow border-0"
        style={{ width: "100%", maxWidth: "1600px" }}
      >
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="mb-0">{t("userManagement.title")}</h2>
          </div>

          <Row className="mb-4 g-3">
            <Col md={3}>
              <Form.Select
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value)}
              >
                <option>{t("userManagement.allRoles")}</option>
                <option>{t("userManagement.admin")}</option>
                <option>{t("userManagement.customer")}</option>
                <option>{t("userManagement.employee")}</option>
              </Form.Select>
            </Col>
          </Row>

          <UsersList
            users={filteredUsers}
            onUpdateUser={handleUpdateUser}
            onUpdateRole={handleRoleUpdate}
          />

          <div className="d-flex justify-content-between align-items-center mt-4">
            <div>
                <span className="text-muted">
                {t("userManagement.showingEntries", { count: filteredUsers.length, total: users.length })}
                </span>
            </div>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};

export default UserManagement;

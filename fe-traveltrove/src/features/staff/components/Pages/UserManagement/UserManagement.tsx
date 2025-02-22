import React, { useEffect, useState } from "react";
import { Card, Row, Col, Form } from "react-bootstrap";
import { UserResponseModel } from "../../../../users/model/users.model";
import { useUsersApi } from "../../../../users/api/users.api";
import UsersList from "../../../../users/components/UsersList";
import { useTranslation } from "react-i18next";

const UserManagement: React.FC = () => {
  const { getAllUsers, updateUser, updateUserRole, syncUser } = useUsersApi();
  const [users, setUsers] = useState<UserResponseModel[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
<<<<<<< HEAD
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedRole, setSelectedRole] = useState("All Roles");
  const { t } = useTranslation();
=======
  const { t } = useTranslation();
  const [selectedRole, setSelectedRole] = useState(t("allRoles"));

  // ROLE_MAPPING is defined here so that translations via t are available.
  const ROLE_MAPPING: Record<string, string> = {
    [t("allRoles")]: "",
    [t("roles.Admin")]: "Admin",
    [t("roles.Customer")]: "Customer",
    [t("roles.Employee")]: "Employee"
  };

  useEffect(() => {
    fetchAllUsers();
  }, []);

  useEffect(() => {
    setSelectedRole(t("allRoles"));
  }, [t]);
>>>>>>> 3a22d2b (translate works for filtering)

  const fetchAllUsers = async () => {
    try {
      setLoading(true);
      const data = await getAllUsers();
      setUsers(data || []);
      setError(null);
    } catch (err) {
<<<<<<< HEAD
      setError(t("error.fetchUsers"));
=======
      setError(t("errorFetchingUsers"));
>>>>>>> 3a22d2b (translate works for filtering)
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateUser = async (
    userId: string,
    updatedUser: Partial<UserResponseModel>
  ): Promise<void> => {
    try {
      setLoading(true);
      await updateUser(userId, updatedUser);
      await fetchAllUsers();
    } catch (error) {
<<<<<<< HEAD
      setError(t("error.updateUser"));
      console.error("Failed to update user", error);
=======
      setError(t("updateFailed"));
>>>>>>> 3a22d2b (translate works for filtering)
    } finally {
      setLoading(false);
    }
  };

  const handleRoleUpdate = async (userId: string, roleIds: string[]): Promise<void> => {
    try {
      setLoading(true);
      await updateUserRole(userId, roleIds);
      await syncUser(userId);
      await fetchAllUsers();
    } catch (error) {
<<<<<<< HEAD
      setError(t("error.updateUserRole"));
      console.error("Failed to update user role", error);
=======
      setError(t("updateFailedRole"));
>>>>>>> 3a22d2b (translate works for filtering)
    } finally {
      setLoading(false);
    }
  };

  const filteredUsers = users.filter((user) => {
    const internalRole = ROLE_MAPPING[selectedRole] || "";
    return internalRole === "" || user.roles.includes(internalRole);
  });

  if (loading) return <div>{t("loading")}</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div className="d-flex justify-content-center align-items-start p-4" style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}>
      <Card className="rounded shadow border-0" style={{ width: "100%", maxWidth: "1600px" }}>
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-4">
<<<<<<< HEAD
            <h2 className="mb-0">{t("userManagement.title")}</h2>
=======
            <h2 className="mb-0">{t("userManagement")}</h2>
>>>>>>> 3a22d2b (translate works for filtering)
          </div>

          <Row className="mb-4 g-3">
            <Col md={3}>
              <Form.Select
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value)}
              >
<<<<<<< HEAD
                <option>{t("userManagement.allRoles")}</option>
                <option>{t("userManagement.admin")}</option>
                <option>{t("userManagement.customer")}</option>
                <option>{t("userManagement.employee")}</option>
=======
                <option>{t("allRoles")}</option>
                <option>{t("roles.Admin")}</option>
                <option>{t("roles.Customer")}</option>
                <option>{t("roles.Employee")}</option>
>>>>>>> 3a22d2b (translate works for filtering)
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
<<<<<<< HEAD
                <span className="text-muted">
                {t("userManagement.showingEntries", { count: filteredUsers.length, total: users.length })}
                </span>
=======
              <span className="text-muted">
                {t("showingEntries", { count: filteredUsers.length, total: users.length })}
              </span>
>>>>>>> 3a22d2b (translate works for filtering)
            </div>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};

export default UserManagement;

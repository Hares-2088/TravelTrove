import React, { useEffect, useState } from "react";
import { Card, Button, Row, Col, InputGroup, Form } from "react-bootstrap";
import { PersonPlus, Search } from "react-bootstrap-icons";
import { useUsersApi } from "../../../../users/api/users.api";
import { UserResponseModel } from "../../../../users/model/users.model";
import UsersList from "../../../../users/components/UsersList";

const UserManagement: React.FC = () => {
  const { getAllUsers } = useUsersApi();
  const [users, setUsers] = useState<UserResponseModel[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch users from API
  useEffect(() => {
    const fetchAllUsers = async () => {
      try {
        const data = await getAllUsers();
        setUsers(data || []);
      } catch (err) {
        setError("Failed to fetch users.");
      } finally {
        setLoading(false);
      }
    };

    fetchAllUsers();
  }, [getAllUsers]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  // Placeholder values for filters
  const roles = ["All Roles", "Administrator", "Manager", "User"];
  const statuses = ["All Status", "Active", "Inactive", "Suspended"];

  return (
    <div
      className="d-flex justify-content-center align-items-start p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <Card
        className="rounded shadow border-0"
        style={{
          width: "100%",
          maxWidth: "1600px",
          borderRadius: "15px",
          overflow: "hidden",
        }}
      >
        <Card.Body>
          {/* Header Section */}
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="mb-0">User Management</h2>
            <Button variant="primary">
              <PersonPlus className="me-2" />
              Add New User
            </Button>
          </div>

          {/* Filters Section */}
          <Row className="mb-4 g-3">
            <Col md={4}>
              <InputGroup>
                <Form.Control
                  placeholder="Search users..."
                  aria-label="Search users"
                />
                <Button variant="outline-secondary">
                  <Search />
                </Button>
              </InputGroup>
            </Col>
            <Col md={3}>
              <Form.Select aria-label="Filter by role">
                {roles.map((role) => (
                  <option key={role}>{role}</option>
                ))}
              </Form.Select>
            </Col>
            <Col md={3}>
              <Form.Select aria-label="Filter by status">
                {statuses.map((status) => (
                  <option key={status}>{status}</option>
                ))}
              </Form.Select>
            </Col>
          </Row>

          {/* Render UsersList */}
          <UsersList users={users} />

          {/* Pagination Placeholder */}
          <div className="d-flex justify-content-between align-items-center mt-4">
            <div>
              <span className="text-muted">Showing 1 to {users.length} of {users.length} entries</span>
            </div>
            <div>
              <Button variant="outline-secondary" size="sm" className="me-2" disabled>
                Previous
              </Button>
              <Button variant="outline-secondary" size="sm" disabled>
                Next
              </Button>
            </div>
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};

export default UserManagement;


import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Button, Row, Col, Spinner, Alert } from "react-bootstrap";
import { ArrowLeft } from "react-bootstrap-icons";
import { useUsersApi } from "../api/users.api";
import { UserResponseModel } from "../model/users.model";

const UsersDetail: React.FC = () => {
  const { getUserById } = useUsersApi();
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();

  const [user, setUser] = useState<UserResponseModel | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch user by ID
  useEffect(() => {
    const fetchUser = async () => {
      if (!userId) return;
      try {
        const data = await getUserById(userId);
        setUser(data);
      } catch (err) {
        setError("Failed to fetch user details.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [getUserById, userId]);

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
        <Spinner animation="border" variant="primary" />
      </div>
    );

  if (error)
    return (
      <Alert variant="danger" className="m-4">
        {error}
      </Alert>
    );

  if (!user)
    return (
      <div className="text-center mt-4">
        <p>No user found.</p>
      </div>
    );

  return (
    <div
      className="d-flex justify-content-center align-items-start p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <Card
        className="rounded shadow border-0"
        style={{
          width: "100%",
          maxWidth: "800px",
          borderRadius: "15px",
          overflow: "hidden",
        }}
      >
        <Card.Body>
          {/* Back Button */}
          <div className="d-flex justify-content-start mb-4">
            <Button variant="outline-secondary" onClick={() => navigate(-1)}>
              <ArrowLeft className="me-2" />
              Back
            </Button>
          </div>

          {/* User Details Section */}
          <div className="mb-4">
            <h2 className="mb-0">{user.firstName} {user.lastName}</h2>
            <p className="text-muted">{user.email}</p>
          </div>

          <Row className="mb-3">
            <Col md={6}>
              <strong>Role:</strong>
              <p>{user.roles}</p>
            </Col>
            <Col md={6}>
              <strong>Status:</strong>
              {/* <p>{user.status}</p> */}
            </Col>
          </Row>

          <Row className="mb-3">
            <Col md={6}>
              <strong>Last Login:</strong>
              {/* <p>{user.lastLogin}</p> */}
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </div>
  );
};

export default UsersDetail;

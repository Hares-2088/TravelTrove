import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Button, Row, Col, Spinner, Alert } from "react-bootstrap";
import { ArrowLeft } from "react-bootstrap-icons";
import { useUsersApi } from "../api/users.api";
import { UserResponseModel } from "../model/users.model";

// Types
interface LoadingState {
  isLoading: boolean;
  error: string | null;
}

// Components
const LoadingSpinner: React.FC = () => (
  <div className="d-flex justify-content-center align-items-center min-vh-100">
    <Spinner animation="border" variant="primary" />
  </div>
);

const ErrorMessage: React.FC<{ message: string }> = ({ message }) => (
  <Alert variant="danger" className="m-4">
    {message}
  </Alert>
);

const BackButton: React.FC = () => {
  const navigate = useNavigate();
  
  return (
    <Button variant="outline-secondary" onClick={() => navigate(-1)}>
      <ArrowLeft className="me-2" />
      Back
    </Button>
  );
};

const UserInformation: React.FC<{ user: UserResponseModel }> = ({ user }) => (
  <>
    <div className="mb-4">
      <h2 className="mb-0">
        {user.firstName} {user.lastName}
      </h2>
      <p className="text-muted">{user.email}</p>
    </div>

    <Row className="mb-3">
      <Col md={6}>
        <strong>Role:</strong>
        <p>{user.roles}</p>
      </Col>
      <Col md={6}>
        <strong>Status:</strong>
        {/* <p>{user.status || 'N/A'}</p> */}
      </Col>
    </Row>

    <Row className="mb-3">
      <Col md={6}>
        <strong>Last Login:</strong>
        {/* <p>{user.lastLogin || 'N/A'}</p> */}
      </Col>
    </Row>
  </>
);

const UsersDetail: React.FC = () => {
  const { getUserById } = useUsersApi();
  const { userId } = useParams<{ userId: string }>();
  const [user, setUser] = useState<UserResponseModel | null>(null);
  const [loadingState, setLoadingState] = useState<LoadingState>({
    isLoading: true,
    error: null,
  });

  useEffect(() => {
    const fetchUser = async () => {
      if (!userId) return;

      try {
        const data = await getUserById(userId);
        setUser(data);
      } catch (err) {
        setLoadingState(prev => ({
          ...prev,
          error: "Failed to fetch user details."
        }));
      } finally {
        setLoadingState(prev => ({
          ...prev,
          isLoading: false
        }));
      }
    };

    fetchUser();
  }, [getUserById, userId]);

  if (loadingState.isLoading) return <LoadingSpinner />;
  if (loadingState.error) return <ErrorMessage message={loadingState.error} />;
  if (!user) return <div className="text-center mt-4">No user found.</div>;

  return (
    <div className="d-flex justify-content-center align-items-start p-4 bg-light min-vh-100">
      <Card className="rounded shadow border-0 w-100" style={{ maxWidth: "800px", borderRadius: "15px" }}>
        <Card.Body>
          <div className="d-flex justify-content-start mb-4">
            <BackButton />
          </div>
          <UserInformation user={user} />
        </Card.Body>
      </Card>
    </div>
  );
};

export default UsersDetail;
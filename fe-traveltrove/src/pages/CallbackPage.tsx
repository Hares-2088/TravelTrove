import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../features/users/api/users.api";
import { AppRoutes } from "../shared/models/app.routes";

const CallbackPage: React.FC = () => {
  const { user, isAuthenticated, isLoading } = useAuth0();
  const { loginUser, syncUser } = useUsersApi();
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  const isProcessingRef = useRef(false);

  useEffect(() => {
    const saveAndSyncUser = async () => {
      if (isAuthenticated && user?.sub && !isProcessingRef.current) {
        try {
          isProcessingRef.current = true; // Set as processing to avoid multiple calls
          await loginUser(user.sub);
          await syncUser(user.sub);
          navigate(AppRoutes.Home);
        } catch (error) {
          console.error("Error during login or sync:", error);
          setError("An error occurred during login or sync. Please try again.");
          isProcessingRef.current = false; // Reset processing state on error
        }
      }
    };

    if (!isLoading && !isProcessingRef.current) {
      saveAndSyncUser();
    }
  }, [isAuthenticated, isLoading, user, loginUser, syncUser, navigate]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return <div />;
};

export default CallbackPage;
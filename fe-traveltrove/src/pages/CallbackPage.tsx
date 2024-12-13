import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../features/users/api/users.api";
import { AppRoutes } from "../shared/models/app.routes";

const CallbackPage: React.FC = () => {
  const { user, isAuthenticated, isLoading } = useAuth0();
  const { loginUser, syncUser } = useUsersApi();
  const navigate = useNavigate();

  useEffect(() => {
    const saveAndSyncUser = async () => {
      if (isAuthenticated && user?.sub) {
        try {
          await loginUser(user.sub);

          await syncUser(user.sub);

          navigate(AppRoutes.Home);

        } catch (error) {
          console.error("Error during login or sync:", error);
        }
      }
    };

    if (!isLoading) {
      void saveAndSyncUser();
    }
  }, [isAuthenticated, isLoading, user, loginUser, syncUser, navigate]);

  // Can improve this page in the future, empty for now
  return <div />;
};

export default CallbackPage;

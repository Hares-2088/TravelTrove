import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useUserContext } from "../context/UserContext"; // Use your existing context

const useAuthorization = (allowedRoles: string[]) => {
  const { roles, isAuthenticated, isLoading } = useUserContext();
  const navigate = useNavigate();

  const isAuthorized = isAuthenticated && roles.some(role => allowedRoles.includes(role));

  useEffect(() => {
    if (!isLoading && isAuthenticated && !isAuthorized) {
      navigate("/unauthorized"); // Redirect unauthorized users
    }
  }, [roles, isAuthenticated, isLoading, navigate, isAuthorized]);

  return { isAuthorized };
};

export default useAuthorization;

// ProtectedRoute.tsx
import React from "react";
import { Navigate } from "react-router-dom";
import { useUserContext } from "../../context/UserContext";
import { log } from "console";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string [];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, requiredRoles }) => {
  const { roles, isAuthenticated, isLoading } = useUserContext();

  console.log("roles:", roles);
  if (isLoading) return <div>Loading...</div>;
  
  if (requiredRoles != null) {
    if (!isAuthenticated || !requiredRoles.some((role) => roles.includes(role))) {
      return <Navigate to="/unauthorized" />;
    }
  }

  return <>{children}</>;
};

export default ProtectedRoute;

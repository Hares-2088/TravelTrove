// ProtectedRoute.tsx
import React from "react";
import { Navigate } from "react-router-dom";
import { useUserContext } from "../../context/UserContext";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, requiredRoles }) => {
  const { user, roles, isAuthenticated, isLoading } = useUserContext();

  if (isLoading) return <div>Loading...</div>;
  
  console.log("(ProtectedRoute) user:", user);
  console.log("(ProtectedRoute) isAuthenticated:", isAuthenticated);
  console.log("(ProtectedRoute) requiredRoles:", requiredRoles);
  console.log("(ProtectedRoute) user roles:", roles);

  if (requiredRoles != null && requiredRoles != undefined) { // if no permissions were passed, allow access
    if (!isAuthenticated || !requiredRoles.some((role) => roles.includes(role))) {
      return <Navigate to="/unauthorized" />;
    }
  }

  return <>{children}</>;
};

export default ProtectedRoute;

import React from "react";
import { Navigate } from "react-router-dom";
import { useUserContext } from "../../context/UserContext";

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRoles,
}) => {
  const { user, roles, isAuthenticated, isLoading } = useUserContext();

  // Normalize roles to lowercase for case-insensitive comparison
  const normalizedRoles = roles.map((role) => role.toLowerCase());
  const normalizedRequiredRoles = requiredRoles?.map((role) => role.toLowerCase());

  const isRoleValid = normalizedRoles.some((role) =>
    normalizedRequiredRoles?.includes(role)
  );

  if (isLoading) return <div>Loading...</div>;

  if (requiredRoles != null && requiredRoles != undefined) {
    // if no permissions were passed, allow access
    if (!isAuthenticated || !isRoleValid) {
      return <Navigate to="/forbidden" />;
    }
  }

  console.log("(ProtectedRoute) user:", user);
  console.log("(ProtectedRoute) isAuthenticated:", isAuthenticated);
  console.log("(ProtectedRoute) requiredRoles:", requiredRoles);
  console.log("(ProtectedRoute) user roles:", roles);

  return <>{children}</>;
};

export default ProtectedRoute;
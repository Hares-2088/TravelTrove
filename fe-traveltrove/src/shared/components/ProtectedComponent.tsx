// ProtectedComponent.tsx
import React from "react";
import { useUserContext } from "../../context/UserContext";

const ProtectedComponent: React.FC = () => {
  const { roles, isAuthenticated, isLoading } = useUserContext(); // Access context values

  if (isLoading) return <div>Loading...</div>; // Handle loading state
  if (!isAuthenticated) return <div>Please log in to access this content.</div>; // Handle unauthenticated state

  if (!roles.includes("admin")) {
    return <div>You do not have permission to view this content.</div>; // Restrict access based on roles
  }

  return <div>Welcome, Admin! Here is your protected content.</div>;
};

export default ProtectedComponent;


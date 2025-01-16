// UserContext.tsx
import React, { createContext, useContext, useState, useEffect } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../features/users/api/users.api";

// Define the UserContextType interface
interface UserContextType {
  user: any;
  roles: string[];
  isAuthenticated: boolean;
  isLoading: boolean;
}

// Create the UserContext with a default value of null
const UserContext = createContext<UserContextType | null>(null);

// Define the UserProvider component
export const UserProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, isAuthenticated, isLoading } = useAuth0(); // Get Auth0 data
  const { getUser } = useUsersApi(); // Custom hook for fetching user data

  // Manage the `roles` state
  const [roles, setRoles] = useState<string[]>([]);

  // Fetch roles when the user changes and roles are empty
  useEffect(() => {
    if (roles.length === 0 && user) {
      // Extract the user id from the context user object
      const userId = user.sub?.toString();
      if (userId) {
        void getUser(userId).then((response) => {
          // Set the roles from the API response
          setRoles(response.roles);
        });
      }
    }
  }, [user, roles.length, getUser]);

  // Log roles for debugging
  console.log("User Roles:", roles);

  // Provide context values
  return (
    <UserContext.Provider value={{ user, roles, isAuthenticated, isLoading }}>
      {children}
    </UserContext.Provider>
  );
};

// Custom hook for accessing the UserContext
export const useUserContext = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUserContext must be used within a UserProvider");
  }
  return context;
};

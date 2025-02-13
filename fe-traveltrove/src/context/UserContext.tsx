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
  handleUserLogout: () => void; // Add handleUserLogout to the context type
}

// Create the UserContext with a default value of null
const UserContext = createContext<UserContextType | null>(null);

// Define the UserProvider component
export const UserProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { user, isAuthenticated, isLoading } = useAuth0();
  const { getUser } = useUsersApi();

  // Manage the `roles` state
  const [roles, setRoles] = useState<string[]>(() => {
    const cachedRoles = sessionStorage.getItem("userRoles");
    return cachedRoles ? JSON.parse(cachedRoles) : [];
  });

  // Manage the `currentUser` state
  const [currentUser, setCurrentUser] = useState<any>(user);

  // Fetch roles when the user changes and roles are empty
  useEffect(() => {
    if (user && user?.sub && isAuthenticated && !isLoading) {
      if (user.sub != currentUser?.userId || currentUser == null) {
        // Check if user has changed or if there is no current user
        const cachedRoles = sessionStorage.getItem("userRoles");
        if (cachedRoles) {
          setRoles(JSON.parse(cachedRoles));
        } else {
          getUser(user.sub)
            .then((userData) => {
              if (!userData) return;

              setCurrentUser(userData);
              setRoles(userData.roles);
              sessionStorage.setItem(
                "userRoles",
                JSON.stringify(userData.roles)
              );
            })
            .catch((error) => {
              console.error("Error fetching user roles:", error);
            });

            
          console.log("(UserContext) User:", currentUser);
          console.log("(UserContext) Current User Id:", currentUser?.userId);
          console.log("(UserContext) User Id:", user?.sub);
        }
      }
    }
  }, [user, user?.sub, isAuthenticated, isLoading]);

  const handleUserLogout = async (): Promise<void> => {
    sessionStorage.removeItem("userRoles");
    setRoles([]); // Reset local state
  };

  // Log roles for debugging
  console.log("(UserContext) User Roles:", roles);
  console.log("(UserContext) isAuthenticated:", isAuthenticated);

  // Provide context values
  return (
    <UserContext.Provider value={{ user, roles, isAuthenticated, isLoading, handleUserLogout }}>
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

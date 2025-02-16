import React, { createContext, useContext, useState, useEffect } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../features/users/api/users.api";

interface UserContextType {
  user: any;
  roles: string[];
  isAuthenticated: boolean;
  isLoading: boolean;
  rolesLoading: boolean; // Add rolesLoading to the context type
  handleUserLogout: () => void;
}

const UserContext = createContext<UserContextType | null>(null);

export const UserProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { user, isAuthenticated, isLoading } = useAuth0();
  const { getUser } = useUsersApi();

  const [roles, setRoles] = useState<string[]>(() => {
    const cachedRoles = sessionStorage.getItem("userRoles");
    return cachedRoles ? JSON.parse(cachedRoles) : [];
  });

  const [currentUser, setCurrentUser] = useState<any>(user);
  const [rolesLoading, setRolesLoading] = useState(true); // using auth0 post login triggers would be better

  useEffect(() => {
    if (user && user?.sub && isAuthenticated && !isLoading) {
      if (user.sub !== currentUser?.userId || currentUser == null) {
        const cachedRoles = sessionStorage.getItem("userRoles");
        if (cachedRoles) {
          setRoles(JSON.parse(cachedRoles));
          setRolesLoading(false);
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
            })
            .finally(() => {
              setRolesLoading(false);
            });
        }
      }
    }
  }, [user, user?.sub, isAuthenticated, isLoading]);

  const handleUserLogout = async (): Promise<void> => {
    sessionStorage.removeItem("userRoles");
    setRoles([]); // Reset local state
  };

  return (
    <UserContext.Provider value={{ user, roles, isAuthenticated, isLoading, rolesLoading, handleUserLogout }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUserContext = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUserContext must be used within a UserProvider");
  }
  return context;
};
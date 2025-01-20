import React, { useEffect, useState } from "react";
import { useUsersApi } from "../../../../users/api/users.api";

const UserManagement: React.FC = () => {
  const { getAllUsers } = useUsersApi();

  useEffect(() => {
    fetchAllUsers();
  }, []);

  const fetchAllUsers = async () => {
    try {
      const data = await getAllUsers();

      console.log(data);
    } catch (error) {
      console.error("Failed to fetch users", error);
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "600px", margin: "0 auto" }}>
      <h1>User Management</h1>
     
    </div>
  );
};

export default UserManagement;

import React, { useState } from "react";
import { useUsersApi} from "../../users/users.api";
import "./HomeDetails.css";

const HomeDetails: React.FC = () => {
    const [result, setResult] = useState<any>(null);
    const { registerUser, getUserProfile, getAllUsers, getUserByUserId } = useUsersApi();

    const testRegisterUser = async () => {
        try {
            const user = {
                email: "testuser@example.com",
                password: "Test@1234",
                firstName: "Test",
                lastName: "User",
                travelerId: "TRAV-1234",
            };
            const response = await registerUser(user);
            setResult(response);
        } catch (error) {
            console.error("Error registering user:", error);
        }
    };

    const testGetUserProfile = async () => {
        try {
            const response = await getUserProfile();
            setResult(response);
        } catch (error) {
            console.error("Error fetching profile:", error);
        }
    };

    const testGetAllUsers = async () => {
        try {
            const response = await getAllUsers();
            setResult(response);
        } catch (error) {
            console.error("Error fetching all users:", error);
        }
    };

    const testGetUserByUserId = async () => {
        try {
            const userId = prompt("Enter User ID (Auth0 sub):");
            if (userId) {
                const response = await getUserByUserId(userId);
                setResult(response);
            }
        } catch (error) {
            console.error("Error fetching user by ID:", error);
        }
    };

    return (
        <div className="home-details">
            <h1>🛠️ Under Construction</h1>
            <p>We're working hard to bring you something amazing. Stay tuned!</p>

            <h2>User API Test</h2>
            <button onClick={testRegisterUser}>Register User</button>
            <button onClick={testGetUserProfile}>Get User Profile</button>
            <button onClick={testGetAllUsers}>Get All Users</button>
            <button onClick={testGetUserByUserId}>Get User by ID</button>

            <pre className="response-box">
        {result && JSON.stringify(result, null, 2)}
      </pre>
        </div>
    );
};

export default HomeDetails;

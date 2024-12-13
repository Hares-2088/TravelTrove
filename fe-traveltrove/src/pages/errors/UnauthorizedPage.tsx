import React from "react";
import { useNavigate } from "react-router-dom";
import { AppRoutes } from '../../shared/models/app.routes';
import "./ErrorPage.css";

const UnauthorizedPage: React.FC = () => {
  const navigate = useNavigate();

  const handleBackToHome = () => {
    navigate(AppRoutes.Home);
  };

  return (
    <div className="error-page">
      <div className="error-content">
        <h1 className="error-code">401</h1>
        <h2 className="error-message">Unauthorized Access</h2>
        <p className="error-description">
          You are not authorized to access this page or resource. <br />
          Please log in or contact support if you believe this is a mistake.
        </p>
        <button className="back-button" onClick={handleBackToHome}>
          Back to Home
        </button>
      </div>
    </div>
  );
};

export default UnauthorizedPage;

import React from "react";
import { useNavigate } from "react-router-dom";
import { AppRoutes } from '../../shared/models/app.routes';
import "./ErrorPage.css";

const ForbiddenPage: React.FC = () => {
  const navigate = useNavigate();

  const handleBackToHome = () => {
    navigate(AppRoutes.Home);
  };

  return (
    <div className="error-page">
      <div className="error-content">
        <h1 className="error-code">403</h1>
        <h2 className="error-message">Access Denied</h2>
        <p className="error-description">
          You don't have permission to access this page or resource. <br />
          Please contact support if you believe this is a mistake.
        </p>
        <button className="back-button" onClick={handleBackToHome}>
          Back to Home
        </button>
      </div>
    </div>
  );
};

export default ForbiddenPage;

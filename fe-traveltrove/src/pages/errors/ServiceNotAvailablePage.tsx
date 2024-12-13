import React from "react";
import { useNavigate } from "react-router-dom";
import { AppRoutes } from '../../shared/models/app.routes';
import "./ErrorPage.css";

const ServiceUnavailablePage: React.FC = () => {
  const navigate = useNavigate();

  const handleBackToHome = () => {
    navigate(AppRoutes.Home);
  };

  return (
    <div className="error-page">
    <div className="error-content">
    <h1 className="error-code">503</h1>
      <h2 className="error-message">Service Unavailable</h2>
  <p className="error-description">
    The service is currently unavailable. <br />
  Please try again later or contact support if the issue persists.
  </p>
  <button className="back-button" onClick={handleBackToHome}>
    Back to Home
  </button>
  </div>
  </div>
);
};

export default ServiceUnavailablePage;

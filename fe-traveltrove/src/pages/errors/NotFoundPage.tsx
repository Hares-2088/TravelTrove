import React from "react";
import { useNavigate } from "react-router-dom";
import { AppRoutes } from '../../shared/models/app.routes';
import "./ErrorPage.css";

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();

  const handleBackToHome = () => {
    navigate(AppRoutes.Home);
  };

  return (
    <div className="error-page">
      <div className="error-content">
        <h1 className="error-code">404</h1>
        <h2 className="error-message">Page Not Found</h2>
        <p className="error-description">
          The page or resource you are looking for does not exist. <br />
          Please check the URL or return to the home page.
        </p>
        <button className="back-button" onClick={handleBackToHome}>
          Back to Home
        </button>
      </div>
    </div>
  );
};

export default NotFoundPage;

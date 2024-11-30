import React from 'react';
import { Link } from 'react-router-dom';
import { AppRoutes } from '../shared/models/app.routes';
import './NavBar.css';

const NavBar: React.FC = () => {
  return (
    <nav className="navbar">
      <div className="navbar-left">
        <img
          src="/assets/logos/darelsalam.png"
          alt="Dar El Salam Logo"
          className="navbar-logo"
        />
      </div>
      <div className="navbar-middle">
        <div className="navbar-language">
          <span role="img" aria-label="Language" className="language-icon">
            🌐
          </span>
          <span>EN</span>
        </div>
        <Link to={AppRoutes.Home} className="navbar-link active">
          Home
        </Link>
        <Link to={AppRoutes.ToursPage} className="navbar-link">
          Trips
        </Link>
      </div>
      <div className="navbar-right">
        <button className="btn btn-light navbar-btn">Sign In</button>
        <button className="btn btn-dark navbar-btn">Sign Up</button>
      </div>
    </nav>
  );
};

export default NavBar;

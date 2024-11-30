import React from 'react';
import './Footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="footer">
      <div className="footer-icons">
        <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
          <img src="/assets/icons/twitter.png" alt="Twitter" className="footer-icon" />
        </a>
        <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
          <img src="/assets/icons/instagram.png" alt="Instagram" className="footer-icon" />
        </a>
        <a href="https://youtube.com" target="_blank" rel="noopener noreferrer">
          <img src="/assets/icons/youtube.png" alt="YouTube" className="footer-icon" />
        </a>
        <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer">
          <img src="/assets/icons/linkedin.png" alt="LinkedIn" className="footer-icon" />
        </a>
      </div>
      <p className="footer-copyright">
        © {new Date().getFullYear()} Dar El Salam Travel. All rights reserved.
      </p>
    </footer>
  );
};

export default Footer;

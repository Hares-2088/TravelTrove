import React from 'react';
import { Container } from 'react-bootstrap';
import './Footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="footer bg-light shadow-sm">
      <Container className="d-flex justify-content-between align-items-center py-3">
        <div>© 2025 Dar El Salam Travel. All rights reserved.</div>
        <div className="social-icons">
          <a href="#" target="_blank" rel="noopener noreferrer">
            <img src="/assets/icons/twitter.png" alt="Twitter" />
          </a>
          <a href="#" target="_blank" rel="noopener noreferrer">
            <img src="/assets/icons/instagram.png" alt="Instagram" />
          </a>
          <a href="#" target="_blank" rel="noopener noreferrer">
            <img src="/assets/icons/youtube.png" alt="YouTube" />
          </a>
          <a href="#" target="_blank" rel="noopener noreferrer">
            <img src="/assets/icons/linkedin.png" alt="LinkedIn" />
          </a>
        </div>
      </Container>
    </footer>
  );
};

export default Footer;

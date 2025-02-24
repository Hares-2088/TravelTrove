import React from 'react';
import { Container } from 'react-bootstrap';
import './Footer.css';

const Footer: React.FC = () => {
  return (
    <footer className="footer bg-light shadow-sm">
      <Container className="d-flex justify-content-between align-items-center py-3">
        <div>© 2025 Dar El Salam Travel. All rights reserved.</div>
      </Container>
    </footer>
  );
};

export default Footer;

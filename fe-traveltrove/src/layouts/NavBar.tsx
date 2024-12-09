import React from "react";
import { Navbar, Nav, Container, NavDropdown, Button } from "react-bootstrap";
import { AppRoutes } from "../shared/models/app.routes";

const NavBar: React.FC = () => {
  return (
    <Navbar bg="light" expand="lg" className="shadow-sm">
      <Container>
        <Navbar.Brand href={AppRoutes.Home}>
          <img
            src="/assets/logos/darelsalam.png"
            alt="Logo"
            style={{ height: "50px" }}
          />
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link href={AppRoutes.Home} className="px-3">
              Home
            </Nav.Link>
            <Nav.Link href={AppRoutes.ToursPage} className="px-3">
              Trips
            </Nav.Link>
          </Nav>
          <Nav className="align-items-center">
            <Nav.Link href={AppRoutes.Dashboard} className="px-3">
              Dashboard
            </Nav.Link>
            <NavDropdown
              title={
                <img
                  src="/assets/icons/globe.png"
                  alt="Language Selector"
                  style={{ height: "20px" }}
                />
              }
              id="language-dropdown"
              align="end"
            >
              <NavDropdown.Item href="#">EN</NavDropdown.Item>
              <NavDropdown.Item href="#">FR</NavDropdown.Item>
            </NavDropdown>
            <Button
              href={AppRoutes.Login}
              variant="outline-dark"
              className="me-2"
            >
              Sign in
            </Button>
            <Button href={AppRoutes.Register} variant="dark">
              Sign up
            </Button>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavBar;

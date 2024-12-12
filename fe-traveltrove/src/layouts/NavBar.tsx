import React from "react";
import { Navbar, Nav, Container, NavDropdown, Button } from "react-bootstrap";
import { AppRoutes } from "../shared/models/app.routes";
import { useTranslation } from 'react-i18next';
import i18n from "../i18n";

const NavBar: React.FC = () => {
  const { t, i18n } = useTranslation();

  const handleLanguageChange = (lng: string) => {
    i18n.changeLanguage(lng);
    console.log("Language switched to:", lng);
  };

  console.log("Current Language:", i18n.language);

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
              {t("home")}
            </Nav.Link>
            <Nav.Link href={AppRoutes.ToursPage} className="px-3">
              {t("trips")}
            </Nav.Link>
          </Nav>
          <Nav className="align-items-center">
            <Nav.Link href={AppRoutes.Dashboard} className="px-3">
              {t("dashboard")}
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
              <NavDropdown.Item onClick={() => handleLanguageChange("en")}>
                EN
              </NavDropdown.Item>
              <NavDropdown.Item onClick={() => handleLanguageChange("fr")}>
                FR
              </NavDropdown.Item>
            </NavDropdown>
            <Button
              href={AppRoutes.Login}
              variant="outline-dark"
              className="me-2"
            >
              {t("signIn")}
            </Button>
            <Button href={AppRoutes.Register} variant="dark">
              {t("signUp")}
            </Button>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavBar;

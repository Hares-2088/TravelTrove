import React from "react";
import {
  Navbar,
  Nav,
  Container,
  NavDropdown,
  Button,
  Spinner,
} from "react-bootstrap";
import { AppRoutes } from "../shared/models/app.routes";
import { useTranslation } from "react-i18next";
import i18n from "../i18n";
import { useAuth0 } from "@auth0/auth0-react";
import { useUserContext } from "../context/UserContext";
import "./NavBar.css";

const NavBar: React.FC = () => {
  const { t, i18n } = useTranslation();

  const handleLanguageChange = (lng: string) => {
    i18n.changeLanguage(lng);

  };



  const { loginWithRedirect, logout, user, isAuthenticated, isLoading } =
    useAuth0();
    
  const { roles, rolesLoading, handleUserLogout } = useUserContext();

  const handleLogin = async () => {
    await loginWithRedirect();
  };

  const handleRegister = async () => {
    await loginWithRedirect({
      authorizationParams: {
        screen_hint: "signup",
      },
    });
  };

  const handleLogout = async () => {
    logout({ logoutParams: { returnTo: window.location.origin } });
    await handleUserLogout();
  };

  const normalizedRoles = roles.map((role) => role.toLowerCase());

  const hasRole = (role: string) => normalizedRoles.includes(role.toLowerCase());
  const isAdmin = hasRole('admin');
  const isEmployee = hasRole('employee');

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
            <Nav.Link href={AppRoutes.ContactUs} className="px-3">
              {t("contactus")}
            </Nav.Link>
          </Nav>

          <Nav className="align-items-center">
            {isAuthenticated && !rolesLoading && (isAdmin || isEmployee) && (
              <Nav.Link href={AppRoutes.Dashboard} className="px-3">
                {t("dashboard")}
              </Nav.Link>
            )}
            {isAuthenticated && !rolesLoading && isAdmin && (
              <Nav.Link href={AppRoutes.ReportsPage} className="px-3">
                {t("reports")}
              </Nav.Link>
            )}
            {isAuthenticated && !rolesLoading && isAdmin && (
              <Nav.Link href={AppRoutes.UserManagementPage}>
                <i className="bi bi-people-fill" style={{ fontSize: "20px" }} />
              </Nav.Link>
            )}
            
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
            {isLoading ? (
              <div className="loading-container">
                <Spinner
                  className="spinner-grow"
                  animation="grow"
                  variant="primary"
                />
              </div>
            ) : isAuthenticated && user ? (
              <NavDropdown
                title={
                  <>
                    <img
                      src={user.picture}
                      alt={user.name}
                      className="user-avatar"
                    />{" "}
                    {user.name}
                  </>
                }
                id="user-dropdown"
                align="end"
              >
                <NavDropdown.Item onClick={handleLogout}>
                  Log out
                </NavDropdown.Item>
              </NavDropdown>
            ) : (
              <>
                <Button
                  onClick={handleLogin}
                  variant="outline-dark"
                  className="me-2"
                >
                  {t("signIn")}
                </Button>
                <Button onClick={handleRegister} variant="dark">
                  {t("signUp")}
                </Button>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavBar;
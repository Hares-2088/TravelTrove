import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Navbar, Nav, NavDropdown, Container } from 'react-bootstrap';

const NavBar: React.FC = () => {
  const navigate = useNavigate();
  const [cartItemCount, setCartItemCount] = useState<number>(0);

  // Mock user data
  const user = { userId: '123', username: 'JohnDoe', isAdmin: false };

  useEffect(() => {
    // Simulate fetching cart items (replace with real API call)
    const fetchCartItemCount = async () => {
      try {
        setCartItemCount(3); // Example count
      } catch (error) {
        console.error('Error fetching cart item count:', error);
      }
    };

    fetchCartItemCount();
  }, []);

  const logoutUser = (): void => {
    navigate('/login');
    console.log('User logged out');
  };

  return (
    <Navbar bg="primary" expand="lg" className="shadow-sm py-3 navbar-dark">
      <Container>
        <Navbar.Brand as={Link} to="/" className="fw-bold text-light">
          TravelTrove
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/" className="text-light mx-2">
              Home
            </Nav.Link>
            <Nav.Link as={Link} to="/tours" className="text-light mx-2">
              Tours
            </Nav.Link>
            {user.isAdmin && (
              <NavDropdown title="Admin" id="admin-dropdown" className="mx-2">
                <NavDropdown.Item as={Link} to="/users">
                  Manage Users
                </NavDropdown.Item>
                <NavDropdown.Item as={Link} to="/settings">
                  Settings
                </NavDropdown.Item>
              </NavDropdown>
            )}
          </Nav>
          <Nav className="ms-auto align-items-center">
            {user.userId ? (
              <NavDropdown
                title={user.username}
                id="user-dropdown"
                className="text-light"
              >
                <NavDropdown.Item as={Link} to="/profile">
                  Profile
                </NavDropdown.Item>
                <NavDropdown.Item
                  onClick={logoutUser}
                  style={{ cursor: 'pointer' }}
                >
                  Logout
                </NavDropdown.Item>
              </NavDropdown>
            ) : (
              <>
                <Nav.Link as={Link} to="/signup" className="text-light mx-2">
                  Signup
                </Nav.Link>
                <Nav.Link as={Link} to="/login" className="text-light mx-2">
                  Login
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavBar;

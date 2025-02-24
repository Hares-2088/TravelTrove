import React, { useState } from "react";
import { Tab, Nav, Row, Col, Card, Offcanvas, Button } from "react-bootstrap";
import CountriesTab from "./Tabs/CountriesTab";
import EventsTab from "./Tabs/EventsTab"; 
import CitiesTab from "./Tabs/CitiesTab";
import ToursTab from "./Tabs/ToursTab";
import HotelsTab from "./Tabs/HotelsTab";
import AirportsTab from "./Tabs/AirportsTab";
import TravelersTab from "./Tabs/TravelersTab";
import { useTranslation } from "react-i18next";
import NotificationsTab from "./Tabs/NotificationsTab";
import EngagementChart from "./Tabs/EngagementChart";

const Dashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState("tours");
  const [showOffcanvas, setShowOffcanvas] = useState(false);
  const { t } = useTranslation();

  const handleNavClick = (tab: string) => {
    setActiveTab(tab);
    setShowOffcanvas(false); //  offcanvas on mobile when an item is clicked
  };

  const navItems = (
    <>
      <Nav.Item>
        <Nav.Link
          eventKey="tours"
          onClick={() => handleNavClick("tours")}
          className={`text-white px-3 py-2 ${activeTab === "tours" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "tours" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          {t("toursDB")}
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="events"
          onClick={() => handleNavClick("events")}
          className={`text-white px-3 py-2 ${activeTab === "events" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "events" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          {t("events")}
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="hotels"
          onClick={() => handleNavClick("hotels")}
          className={`text-white px-3 py-2 ${activeTab === "hotels" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "hotels" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          Hotels
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="cities"
          onClick={() => handleNavClick("cities")}
          className={`text-white px-3 py-2 ${activeTab === "cities" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "cities" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          {t("citiesDB")}
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="countries"
          onClick={() => handleNavClick("countries")}
          className={`text-white px-3 py-2 ${activeTab === "countries" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "countries" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          {t("countries")}
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="airports"
          onClick={() => handleNavClick("airports")}
          className={`text-white px-3 py-2 ${activeTab === "airports" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "airports" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          Airports
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="travelers"
          onClick={() => handleNavClick("travelers")}
          className={`text-white px-3 py-2 ${activeTab === "travelers" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "travelers" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          Travelers
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="notifications"
          onClick={() => handleNavClick("notifications")}
          className={`text-white px-3 py-2 ${activeTab === "notifications" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "notifications" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          Notifications
        </Nav.Link>
      </Nav.Item>
      <Nav.Item>
        <Nav.Link
          eventKey="engagement"
          onClick={() => handleNavClick("engagement")}
          className={`text-white px-3 py-2 ${activeTab === "engagement" ? "bg-secondary" : ""}`}
          style={{
            fontWeight: activeTab === "engagement" ? "bold" : "normal",
            borderRadius: "10px",
          }}
        >
          Engagement
        </Nav.Link>
      </Nav.Item>
    </>
  );

  return (
    <div
      className="d-flex justify-content-center align-items-center p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <Card
        className="rounded shadow border-0 w-100"
        style={{
          maxWidth: "1600px",
          height: "90vh",
          borderRadius: "15px",
          overflow: "hidden",
        }}
      >
        <Card.Body className="p-0 d-flex flex-column h-100">
          <Tab.Container activeKey={activeTab}>
            <div className="d-flex d-sm-none align-items-center justify-content-between p-3 bg-dark text-white">
              <h4 className="mb-0" style={{ fontWeight: "bold" }}>
                {t("dashboard")}
              </h4>
              <Button variant="link" onClick={() => setShowOffcanvas(true)} className="text-white">
                ☰
              </Button>
            </div>
            <Row className="g-0 flex-grow-1 h-100">
              <Col
                xs={12}
                sm={3}
                md={2}
                className="bg-dark text-white d-none d-sm-flex flex-column p-3"
              >
                <h4 className="text-center mb-3" style={{ fontWeight: "bold" }}>
                  {t("dashboard")}
                </h4>
                <Nav className="flex-column text-center">{navItems}</Nav>
              </Col>

              <Col
                xs={12}
                sm={9}
                md={10}
                className="bg-white"
                style={{
                  height: "90vh",
                  overflowY: "auto",
                  padding: "1rem",
                }}
              >
                <Tab.Content>
                  <Tab.Pane eventKey="tours">
                    <ToursTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="events">
                    <EventsTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="hotels">
                    <HotelsTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="cities">
                    <CitiesTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="countries">
                    <CountriesTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="airports">
                    <AirportsTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="travelers">
                    <TravelersTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="notifications">
                    <NotificationsTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="engagement">
                    <EngagementChart />
                  </Tab.Pane>
                </Tab.Content>
              </Col>
            </Row>

            <Offcanvas
              show={showOffcanvas}
              onHide={() => setShowOffcanvas(false)}
              placement="start"
              className="bg-dark text-white"
            >
              <Offcanvas.Header closeButton closeVariant="white">
                <Offcanvas.Title>{t("dashboard")}</Offcanvas.Title>
              </Offcanvas.Header>
              <Offcanvas.Body>
                <Nav className="flex-column text-center">{navItems}</Nav>
              </Offcanvas.Body>
            </Offcanvas>
          </Tab.Container>
        </Card.Body>
      </Card>
    </div>
  );
};

export default Dashboard;

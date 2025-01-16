import React, { useState } from 'react';
import { Tab, Nav, Row, Col, Card } from 'react-bootstrap';
import CountriesTab from './Tabs/CountriesTab';
import EventsTab from './Tabs/EventsTab';
import CitiesTab from './Tabs/CitiesTab';
import ToursTab from './Tabs/ToursTab';
import HotelsTab from './Tabs/HotelsTab';
import AirportsTab from './Tabs/AirportsTab';
import TravelersTab from './Tabs/travelersTab'; // Import the new TravelersTab component
import { useTranslation } from 'react-i18next';

const Dashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState('tours');
  const { t } = useTranslation(); // Access i18n functions

  return (
    <div
      className="d-flex justify-content-center align-items-center p-4"
      style={{ backgroundColor: '#f8f9fa', minHeight: '100vh' }}
    >
      <Card
        className="rounded shadow border-0"
        style={{
          width: '1600px',
          height: '800px',
          borderRadius: '15px',
          overflow: 'hidden',
        }}
      >
        <Card.Body className="p-0 d-flex flex-column">
          <Tab.Container activeKey={activeTab}>
            <Row className="g-0 flex-grow-1">
              {/* Sidebar */}
              <Col
                sm={2}
                className="bg-dark text-white d-flex flex-column"
                style={{
                  padding: '20px',
                  maxWidth: '170px',
                }}
              >
                {/* Dashboard Title */}
                <h4 className="text-center mb-3" style={{ fontWeight: 'bold' }}>
                  {t('dashboard')}
                </h4>
                {/* Tab List */}
                <Nav className="flex-column text-center">
                  <Nav.Item>
                    <Nav.Link
                      eventKey="tours"
                      onClick={() => setActiveTab('tours')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'tours' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight: activeTab === 'tours' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      {t('toursDB')}
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link
                      eventKey="events"
                      onClick={() => setActiveTab('events')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'events' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight: activeTab === 'events' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      {t('events')}
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link
                      eventKey="hotels"
                      onClick={() => setActiveTab('hotels')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'hotels' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight: activeTab === 'hotels' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      Hotels
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link
                      eventKey="cities"
                      onClick={() => setActiveTab('cities')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'cities' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight: activeTab === 'cities' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      {t('citiesDB')}
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link
                      eventKey="countries"
                      onClick={() => setActiveTab('countries')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'countries' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight:
                          activeTab === 'countries' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      {t('countries')}
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link
                      eventKey="airports"
                      onClick={() => setActiveTab('airports')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'airports' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight:
                          activeTab === 'airports' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      Airports
                    </Nav.Link>
                  </Nav.Item>
                  <Nav.Item>
                    <Nav.Link
                      eventKey="travelers"
                      onClick={() => setActiveTab('travelers')}
                      className={`text-white px-3 py-2 ${
                        activeTab === 'travelers' ? 'bg-secondary' : ''
                      }`}
                      style={{
                        fontWeight:
                          activeTab === 'travelers' ? 'bold' : 'normal',
                        borderRadius: '10px',
                      }}
                    >
                      Travelers
                    </Nav.Link>
                  </Nav.Item>
                </Nav>
              </Col>

              {/* Main Content */}
              <Col
                sm={10}
                className="bg-white"
                style={{
                  padding: '20px',
                  overflowY: 'auto',
                  height: '100%',
                }}
              >
                <Tab.Content style={{ height: '100%' }}>
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
                  <Tab.Pane eventKey="countries" style={{ height: '100%' }}>
                    <CountriesTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="airports" style={{ height: '100%' }}>
                    <AirportsTab />
                  </Tab.Pane>
                  <Tab.Pane eventKey="travelers" style={{ height: '100%' }}>
                    <TravelersTab />
                  </Tab.Pane>
                </Tab.Content>
              </Col>
            </Row>
          </Tab.Container>
        </Card.Body>
      </Card>
    </div>
  );
};

export default Dashboard;

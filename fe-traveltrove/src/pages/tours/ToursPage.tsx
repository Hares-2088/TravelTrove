import React, { FC, useEffect } from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import ToursList from '../../features/tours/components/ToursList';
import 'bootstrap/dist/css/bootstrap.min.css';
import './ToursPage.css';

const ToursPage: FC = (): JSX.Element => {
  useEffect(() => {
    
  }, []);

  return (
    <Container fluid className="tours-page">
      <Row className="justify-content-center">
        <Col xs={12} md={10} lg={8}>
          <ToursList />
        </Col>
      </Row>
    </Container>
  );
};

export default React.memo(ToursPage);
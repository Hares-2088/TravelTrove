import React, { useCallback, useEffect, useState } from 'react';
import { Carousel, Card, Button, Spinner, Alert } from 'react-bootstrap';
import { usePackagesApi } from '../../packages/api/packages.api'; // Adjust the import path
import { PackageResponseModel } from '../../packages/models/package.model'; // Adjust the import path
import './HomeDetails.css';

const HomeDetails: React.FC = () => {
  const [packages, setPackages] = useState<PackageResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { getAllPackages } = usePackagesApi(); // Fetch all packages
  const [index, setIndex] = useState(0);

  const handleSelect = (selectedIndex: number) => {
    setIndex(selectedIndex);
  };

  // Fetch all packages
  const fetchPackages = useCallback(async () => {
    try {
      const data = await getAllPackages();
      setPackages(data);
    } catch {
      setError('Failed to fetch packages.');
    } finally {
      setLoading(false);
    }
  }, [getAllPackages]);

  useEffect(() => {
    fetchPackages();
  }, [fetchPackages]);

  if (loading) return <Spinner animation="border" className="loading-spinner" />;
  if (error) return <Alert variant="danger" className="error-alert">{error}</Alert>;

  return (
    <div className="home-details">
      {/* Hero Section */}
      <section
        className="hero-section"
        style={{ backgroundImage: 'url(/path-to-your-image.jpg)' }}
      >
        <div className="hero-content">
          <h1>Explore The World</h1>
          <p>Over 35 years of Travel Service Experience – Journey With Us</p>
        </div>
      </section>

      {/* Popular Destinations Carousel */}
      <section className="popular-destinations">
        <h2>Popular Destinations</h2>
        {packages.length > 0 ? (
          <Carousel activeIndex={index} onSelect={handleSelect}>
            {packages.map((pkg) => (
              <Carousel.Item key={pkg.packageId}>
                <Card>
                  <Card.Img variant="top" src={pkg.packageImageUrl || '/default-image.jpg'} />
                  <Card.Body>
                    <Card.Title>{pkg.name}</Card.Title>
                    <Card.Text>{pkg.description}</Card.Text>
                    <Card.Text>Single: ${pkg.priceSingle}</Card.Text>
                    <Card.Text>Double: ${pkg.priceDouble}</Card.Text>
                    <Button variant="dark">Book Now</Button>
                  </Card.Body>
                </Card>
              </Carousel.Item>
            ))}
          </Carousel>
        ) : (
          <Alert variant="info" className="info-alert">No packages available.</Alert>
        )}
      </section>

      {/* Umrah and Hajj Cards */}
      <section className="special-packages">
        <div
          className="package-card"
          onClick={() => console.log('Navigate to Umrah details')}
        >
          <img src="/path-to-umrah-image.jpg" alt="Umrah" />
          <h3>Umrah</h3>
          <p>Experience the spiritual journey of Umrah with our exclusive packages.</p>
        </div>
        <div
          className="package-card"
          onClick={() => console.log('Navigate to Hajj details')}
        >
          <img src="/path-to-hajj-image.jpg" alt="Hajj" />
          <h3>Hajj</h3>
          <p>Join us for the sacred pilgrimage of Hajj with comprehensive support.</p>
        </div>
      </section>

      {/* Reviews Section */}
      <section className="reviews-section">
        <h2>Customer Reviews</h2>
        <div className="elfsight-app-6d3f48d9-c5ae-4dae-8574-3ea7f9eb1362" data-elfsight-app-lazy></div>
      </section>
    </div>
  );
};

export default HomeDetails;
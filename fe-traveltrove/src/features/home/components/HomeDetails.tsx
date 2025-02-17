import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Carousel, Card, Button, Spinner, Alert } from 'react-bootstrap';
import { usePackagesApi } from '../../packages/api/packages.api'; // Adjust the import path
import { PackageResponseModel } from '../../packages/models/package.model'; // Adjust the import path
import './HomeDetails.css';

const HomeDetails: React.FC = () => {
  const { getAllPackages } = usePackagesApi();
  const [umrahPackages, setUmrahPackages] = useState<PackageResponseModel[]>([]);
  const [hajjPackages, setHajjPackages] = useState<PackageResponseModel[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [index, setIndex] = useState<number>(0);

  // Fetch Umrah and Hajj packages by tourId
  const fetchPackages = useCallback(async () => {
    try {
      console.log('📢 Fetching packages...');
      const [umrahData, hajjData] = await Promise.all([
        getAllPackages({ tourId: '6a237fda-4924-4c73-a6df-73c1e0c37af2' }), // Umrah tourId
        getAllPackages({ tourId: '6a237fda-4924-4c73-a6df-73c1e0c37af3' }), // Hajj tourId
      ]);
      setUmrahPackages(umrahData || []);
      setHajjPackages(hajjData || []);
      console.log('✅ Packages fetched successfully:', { umrahData, hajjData });
    } catch (err) {
      console.error('❌ Failed to fetch packages:', err);
      setError('Failed to fetch packages.');
    } finally {
      setLoading(false);
    }
  }, [getAllPackages]);

  useEffect(() => {
    console.log('🚀 useEffect triggered: Fetching packages...');
    fetchPackages();
  }, [fetchPackages]);

  // Clean image URLs if necessary
  const cleanImageUrl = (url: string) => {
    return url.replace(/^"(.*)"$/, '$1');
  };

  if (loading) return <Spinner animation="border" className="home-loading" />;
  if (error) return <Alert variant="danger" className="home-error">{error}</Alert>;

  return (
    <div className="home-details">
      {/* Hero Section */}
      <section
        className="hero-section"
        style={{ backgroundImage: 'url(https://traveltrove-images.s3.us-east-2.amazonaws.com/hero-image.jpg)' }}
      >
        <div className="hero-content">
          <h1>Explore The World</h1>
          <p>Over 35 years of Travel Service Experience – Journey With Us</p>
        </div>
      </section>

      {/* Popular Destinations Carousel */}
      <section className="popular-destinations">
        <h2>Popular Destinations</h2>
        {umrahPackages.length > 0 || hajjPackages.length > 0 ? (
          <Carousel activeIndex={index} onSelect={(selectedIndex) => setIndex(selectedIndex)}>
            {[...umrahPackages, ...hajjPackages].map((pkg) => (
              <Carousel.Item key={pkg.packageId}>
                <Card>
                  <Card.Img
                    variant="top"
                    src={cleanImageUrl(pkg.packageImageUrl || '/default-image.jpg')}
                    alt={pkg.name}
                  />
                  <Card.Body>
                    <Card.Title>{pkg.name}</Card.Title>
                    <Card.Text>{pkg.description}</Card.Text>
                    <Card.Text>Single: ${pkg.priceSingle}</Card.Text>
                    <Card.Text>Double: ${pkg.priceDouble}</Card.Text>
                    <Link to={`/packages/${pkg.packageId}`}>
                      <Button variant="dark">Book Now</Button>
                    </Link>
                  </Card.Body>
                </Card>
              </Carousel.Item>
            ))}
          </Carousel>
        ) : (
          <Alert variant="info" className="home-info">No packages available.</Alert>
        )}
      </section>

      {/* Umrah and Hajj Cards */}
      <section className="special-packages">
        <div
          className="package-card"
          onClick={() => console.log('Navigate to Umrah details')}
        >
          <img
            src={cleanImageUrl('https://traveltrove-images.s3.us-east-2.amazonaws.com/umrah-tour.jpg')}
            alt="Umrah"
          />
          <h3>Umrah</h3>
          <p>Experience the spiritual journey of Umrah with our exclusive packages.</p>
        </div>
        <div
          className="package-card"
          onClick={() => console.log('Navigate to Hajj details')}
        >
          <img
            src={cleanImageUrl('https://traveltrove-images.s3.us-east-2.amazonaws.com/hajj-tour.jpg')}
            alt="Hajj"
          />
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

export default React.memo(HomeDetails);
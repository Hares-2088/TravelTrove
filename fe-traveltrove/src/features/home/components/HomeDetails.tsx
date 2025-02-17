import React, { useEffect, useState, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { Carousel, Card, Button, Spinner, Alert } from 'react-bootstrap';
import { useToursApi } from '../../tours/api/tours.api'; // Adjust the import path
import { TourResponseModel } from '../../tours/models/Tour'; // Adjust the import path
import { useTranslation } from 'react-i18next';
import './HomeDetails.css';

const HomeDetails: React.FC = () => {
  const { getAllTours, getTourByTourId } = useToursApi();
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [umrahTour, setUmrahTour] = useState<TourResponseModel | null>(null);
  const [hajjTour, setHajjTour] = useState<TourResponseModel | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [index, setIndex] = useState<number>(0);
  const { t } = useTranslation();

  // Fetch tours
  const fetchTours = useCallback(async () => {
    try {
      console.log('📢 Fetching tours...');
      const [allTours, umrahData, hajjData] = await Promise.all([
        getAllTours(),
        getTourByTourId('6a237fda-4924-4c73-a6df-73c1e0c37af2'), // Umrah tourId
        getTourByTourId('6a237fda-4924-4c73-a6df-73c1e0c37af3'), // Hajj tourId
      ]);
      const filteredTours = allTours.filter(tour => tour.tourId !== '6a237fda-4924-4c73-a6df-73c1e0c37af2' && tour.tourId !== '6a237fda-4924-4c73-a6df-73c1e0c37af3');
      setTours(filteredTours.slice(0, 10)); // Get first 10 tours
      setUmrahTour(umrahData);
      setHajjTour(hajjData);
      console.log('✅ Tours fetched successfully:', filteredTours);
    } catch (err) {
      console.error('❌ Failed to fetch tours:', err);
      setError(t('error.fetchTours'));
    } finally {
      setLoading(false);
    }
  }, [t]);

  useEffect(() => {
    console.log('🚀 useEffect triggered: Fetching tours...');
    fetchTours();
  }, [fetchTours]);

  // Clean image URLs if necessary
  const cleanImageUrl = (url: string) => {
    return url.replace(/^"(.*)"$/, '$1');
  };

  const getVisibleTours = () => {
    if (window.innerWidth <= 576) {
      return 1;
    } else if (window.innerWidth <= 768) {
      return 2;
    } else {
      return 3;
    }
  };

  if (loading) return <Spinner animation="border" className="home-loading" />;
  if (error) return <Alert variant="danger" className="home-error">{error}</Alert>;

  return (
    <div className="home-details">
      {/* Hero Section */}
      <section
        className="hero-section"
        style={{ backgroundImage: 'url(https://traveltrove-images.s3.us-east-2.amazonaws.com/auth0-675e3886e184fd643a8ed5aa_cd1962fc-a3bc-4ddc-ac5d-a2606139f135_hero-image.jpg)' }}
      >
        <div className="hero-content">
          <h1>{t('home.heroTitle')}</h1>
          <p>{t('home.heroSubtitle')}</p>
        </div>
      </section>

      {/* Popular Destinations Carousel */}
      <section className="popular-destinations">
        <h2>{t('home.popularDestinations')}</h2>
        {tours.length > 0 ? (
          <Carousel activeIndex={index} onSelect={(selectedIndex) => setIndex(selectedIndex)} indicators={false} nextIcon={<span aria-hidden="true" className="carousel-control-next-icon" />} prevIcon={<span aria-hidden="true" className="carousel-control-prev-icon" />}>
            {tours.map((tour, idx) => (
              idx % getVisibleTours() === 0 && (
                <Carousel.Item key={tour.tourId}>
                  <div className="d-flex justify-content-around">
                    {tours.slice(idx, idx + getVisibleTours()).map((tour) => (
                      <Card key={tour.tourId} className="mx-2">
                        <Link to={`/tours/${tour.tourId}`}>
                          <Card.Img
                            variant="top"
                            src={cleanImageUrl(tour.tourImageUrl || '/default-image.jpg')}
                            alt={tour.name}
                            className="carousel-image"
                          />
                        </Link>
                        <Card.Body>
                          <Card.Title>{tour.name}</Card.Title>
                          <Card.Text>{tour.description}</Card.Text>
                          <Link to={`/tours/${tour.tourId}`}>
                            <Button variant="dark">{t('home.exploreNow')}</Button>
                          </Link>
                        </Card.Body>
                      </Card>
                    ))}
                  </div>
                </Carousel.Item>
              )
            ))}
          </Carousel>
        ) : (
          <Alert variant="info" className="home-info">{t('home.noToursAvailable')}</Alert>
        )}
      </section>

      {/* Umrah and Hajj Cards */}
      <section className="special-packages">
        {umrahTour && (
          <Link to={`/tours/${umrahTour.tourId}`} className="package-card">
            <img
              src={cleanImageUrl(umrahTour.tourImageUrl || 'https://traveltrove-images.s3.us-east-2.amazonaws.com/auth0-675e3886e184fd643a8ed5aa_232fd39d-936f-405e-a366-f8dc9e1de686_Umrah.jpeg.png')}
              alt="Umrah"
            />
            <h3>{umrahTour.name}</h3>
            <p>{umrahTour.description}</p>
          </Link>
        )}
        {hajjTour && (
          <Link to={`/tours/${hajjTour.tourId}`} className="package-card">
            <img
              src={cleanImageUrl(hajjTour.tourImageUrl || 'https://traveltrove-images.s3.us-east-2.amazonaws.com/auth0-675e3886e184fd643a8ed5aa_d3009bca-efc2-4c42-afdd-8e1e6c5e374d_hajj.jpeg.png')}
              alt="Hajj"
            />
            <h3>{hajjTour.name}</h3>
            <p>{hajjTour.description}</p>
          </Link>
        )}
      </section>

      {/* Reviews Section */}
      <section className="reviews-section">
        <h2>{t('home.customerReviews')}</h2>
        <div className="elfsight-app-6d3f48d9-c5ae-4dae-8574-3ea7f9eb1362" data-elfsight-app-lazy></div>
      </section>
    </div>
  );
};

export default React.memo(HomeDetails);
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getTourByTourId } from '../api/tours.api';
import { Tour } from '../models/Tour';
import './TourDetails.css';

const formatDateTime = (dateTime: string): string => {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
  };
  return new Intl.DateTimeFormat('en-US', options).format(new Date(dateTime));
};

const TourDetails: React.FC = () => {
  const { tourId } = useParams<{ tourId: string }>();
  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTour = async () => {
      try {
        const data = await getTourByTourId(tourId!);
        setTour(data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch tour details.');
        setLoading(false);
      }
    };

    fetchTour();
  }, [tourId]);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  if (!tour) return <div>No tour details found.</div>;

  return (
    <div className="tour-details">
      <header className="tour-header">
        <h1 className="tour-title">{tour.name}</h1>
        <img src={tour.image} alt={tour.name} className="tour-main-image" />
        <p className="tour-description">{tour.overallDescription}</p>
      </header>

      <section className="tour-info">
        <h2>Tour Information</h2>
        <p>
          <strong>Start Date:</strong> {formatDateTime(tour.startDate)}
        </p>
        <p>
          <strong>End Date:</strong> {formatDateTime(tour.endDate)}
        </p>
        <p>
          <strong>Price:</strong> ${tour.price.toFixed(2)}
        </p>
        <p>
          <strong>Available Spots:</strong> {tour.spotsAvailable}
        </p>
      </section>

      <section className="tour-cities">
        <h2>Explore the Cities</h2>
        <div className="city-list">
          {tour.cities.map((city) => (
            <div key={city.cityId} className="city-item">
              <h3>{city.name}</h3>
              <p>{city.description}</p>
              <img src={city.image} alt={city.name} className="city-image" />
              <p>
                <strong>Start Date:</strong> {formatDateTime(city.startDate)}
              </p>
              <h4>Events</h4>
              <ul className="event-list">
                {city.events.map((event) => (
                  <li key={event.eventId} className="event-item">
                    <strong>{event.name}</strong>
                    <p>{event.description}</p>
                    <img src={event.image} alt={event.name} className="event-image" />
                    <p>
                      <strong>Event Time:</strong> {formatDateTime(event.gatheringTime)} - {formatDateTime(event.departureTime)}
                    </p>
                    <p>
                      <strong>End Time:</strong> {formatDateTime(event.endTime)}
                    </p>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </section>

      <section className="tour-itinerary">
        <h2>Itinerary</h2>
        <img src={tour.itineraryPicture} alt="Itinerary" className="itinerary-image" />
      </section>
    </div>
  );
};

export default TourDetails;

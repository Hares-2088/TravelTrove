import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllTours } from '../api/tours.api';
import { Tour } from '../models/Tour';
import './ToursList.css';
import { AppRoutes } from '../../../shared/models/app.routes';

const ToursList: React.FC = () => {
  const [tours, setTours] = useState<Tour[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTours = async () => {
      try {
        const data = await getAllTours();
        setTours(data || []);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch tours.');
        setTours([]);
        setLoading(false);
      }
    };

    fetchTours();
  }, []);

  if (loading) return <div className="tours-list-loading">Loading...</div>;
  if (error) return <div className="tours-list-error">{error}</div>;
  if (tours.length === 0) return <div className="tours-list-empty">No tours available.</div>;

  return (
    <div className="tours-list">
      {tours.map((tour) => (
        <Link to={`${AppRoutes.ToursPage}/${tour.tourId}`} key={tour.tourId} className="tour-item">
          <div>
            <h3 className="tour-name">{tour.name}</h3>
            <img src={tour.image} alt={tour.name} className="tour-image" />
          </div>
        </Link>
      ))}
    </div>
  );
};

export default ToursList;

import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useToursApi } from '../api/tours.api';
import { TourResponseModel } from '../models/Tour';
import './ToursList.css';
import { AppRoutes } from '../../../shared/models/app.routes';

const ToursList: React.FC = () => {
  const { getAllTours } = useToursApi();
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTours = async () => {
      try {
        const data = await getAllTours();
        setTours(data || []);
      } catch {
        setError('Failed to fetch tours.');
      } finally {
        setLoading(false);
      }
    };

    fetchTours();
  }, [getAllTours]);

  if (loading) return <div className="tours-list-loading">Loading...</div>;
  if (error) return <div className="tours-list-error">{error}</div>;
  if (tours.length === 0)
    return <div className="tours-list-empty">No tours available.</div>;

  return (
    <div className="tours-list">
      {tours.map(tour => (
        <Link
          to={`${AppRoutes.ToursPage}/${tour.tourId}`}
          key={tour.tourId}
          className="tour-item"
        >
          <div>
            <h3 className="tour-name">{tour.name}</h3>
            <p className="tour-description">{tour.description}</p>
          </div>
        </Link>
      ))}
    </div>
  );
};

export default ToursList;

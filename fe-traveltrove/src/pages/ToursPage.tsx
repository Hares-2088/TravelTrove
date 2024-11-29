import React, { useEffect, useState } from 'react';
import { getAllTours } from '../features/APIService';

type Tour = {
  tourId: string;
  name: string;
  image: string;
};

const ToursPage: React.FC = () => {
  const [tours, setTours] = useState<Tour[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTours = async () => {
      try {
        const data = await getAllTours();
        setTours(data);
        setLoading(false);
      } catch (err) {
        setError('Failed to fetch tours.');
        setLoading(false);
      }
    };

    fetchTours();
  }, []);

  if (loading) return <div className="text-center my-5">Loading...</div>;
  if (error) return <div className="text-danger text-center my-5">{error}</div>;

  return (
    <div className="container my-5">
      <h1 className="text-center mb-4">Available Tours</h1>
      <div className="row g-4">
        {tours.map((tour) => (
          <div key={tour.tourId} className="col-md-4 col-sm-6">
            <div className="card h-100 shadow-sm">
              <img src={tour.image} alt={tour.name} className="card-img-top" />
              <div className="card-body text-center">
                <h5 className="card-title">{tour.name}</h5>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ToursPage;

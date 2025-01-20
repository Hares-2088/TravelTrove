import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useToursApi } from "../api/tours.api";
import { TourResponseModel } from "../models/Tour";
import "./TourDetails.css";
import PackageList from '../../packages/components/PackageList';

const TourDetails: React.FC = () => {
  const { getTourByTourId } = useToursApi();
  const { tourId } = useParams<{ tourId: string }>();

  const [tour, setTour] = useState<TourResponseModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTour = async () => {
      try {
        const data = await getTourByTourId(tourId!);
        setTour(data);
      } catch {
        setError("Failed to fetch tour details.");
      } finally {
        setLoading(false);
      }
    };

    if (tourId) fetchTour();
  }, [tourId, getTourByTourId]);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!tour) return <div>No tour details found.</div>;

  return (
    <div className="tour-details">
      <header className="tour-header">
        <h1 className="tour-title">{tour.name}</h1>
        <p className="tour-description">{tour.description}</p>
      </header>
      <PackageList tourId={tourId!} />
    </div>
  );
};

export default TourDetails;

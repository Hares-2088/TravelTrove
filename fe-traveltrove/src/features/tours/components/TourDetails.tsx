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
    if (!tourId) return; // Prevent fetching if tourId is null

    let isMounted = true;

    const fetchTour = async () => {
      try {
        console.log(`📢 Fetching tour details for tourId=${tourId}`);
        const data = await getTourByTourId(tourId);

        if (isMounted) {
          console.log(`✅ Tour details loaded for tourId=${tourId}`, data);
          setTour(data);
        }
      } catch (err) {
        if (isMounted) {
          console.error(`❌ Failed to fetch tour details for tourId=${tourId}:`, err);
          setError("Failed to fetch tour details.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchTour();

    return () => {
      isMounted = false;
    };
  }, [tourId]); // ✅ Ensure `useEffect` runs when `tourId` changes

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!tour) return <div>No tour details found.</div>;

  return (
    <div className="tour-details">
      <header className="tour-header">
        <h1 className="tour-title">{tour.name}</h1>
        <p className="tour-description">{tour.description}</p>
        {tour.tourImageUrl && (
          <img src={tour.tourImageUrl} alt={tour.name} className="tour-image" />
        )}
      </header>

      {tourId && <PackageList tourId={tourId} />}
    </div>
  );
};

export default TourDetails;

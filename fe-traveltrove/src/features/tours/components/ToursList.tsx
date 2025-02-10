import React, { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { useToursApi } from "../api/tours.api";
import { TourResponseModel } from "../models/Tour";
import "./ToursList.css";

const ToursList: React.FC = () => {
  const { getAllTours } = useToursApi();
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Memoize fetchTours to prevent unnecessary recreations
  const fetchTours = useCallback(async () => {
    try {
      console.log("📢 Fetching tour list...");
      const data = await getAllTours();
      setTours(data || []);
      console.log("✅ Tours fetched successfully:", data);
    } catch (err) {
      console.error("❌ Failed to fetch tours:", err);
      setError("Failed to fetch tours.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    console.log("🚀 useEffect triggered: Fetching tours...");
    fetchTours();
  }, [fetchTours]);

  if (loading) return <div className="tours-list-loading">Loading...</div>;
  if (error) return <div className="tours-list-error">{error}</div>;
  if (tours.length === 0)
    return <div className="tours-list-empty">No tours available.</div>;

  return (
    <div className="tours-list">
      {tours.map((tour) => (
        <Link to={`/tours/${tour.tourId}`} key={tour.tourId} className="tour-item">
          <div>
            {tour.tourImageUrl && (
              <img src={tour.tourImageUrl} alt={tour.name} className="tour-thumbnail" />
            )}
            <h3 className="tour-name">{tour.name}</h3>
            <p className="tour-description">{tour.description}</p>
          </div>
        </Link>
      ))}
    </div>
  );
};

export default React.memo(ToursList);
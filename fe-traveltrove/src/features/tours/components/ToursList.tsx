import React, { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { Card, Button, Spinner, Alert } from "react-bootstrap";
import { useToursApi } from "../api/tours.api";
import { TourResponseModel } from "../models/Tour";
import 'bootstrap/dist/css/bootstrap.min.css';
import "./ToursList.css";

const ToursList: React.FC = () => {
  const { getAllTours } = useToursApi();
  const [tours, setTours] = useState<TourResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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

  // will need to properly store the image URL in the database to avoid this workaround
  const cleanImageUrl = (url: string) => {
    return url.replace(/^"(.*)"$/, '$1');
  };

  if (loading) return <Spinner animation="border" className="tours-list-loading" />;
  if (error) return <Alert variant="danger" className="tours-list-error">{error}</Alert>;
  if (tours.length === 0) return <Alert variant="info" className="tours-list-empty">No tours available.</Alert>;

  return (
    <div className="tours-list">
      {tours.map((tour) => (
        <Card key={tour.tourId} className="tour-item">
          {tour.tourImageUrl && (
            <Card.Img variant="top" src={cleanImageUrl(tour.tourImageUrl)} alt={tour.name} className="tour-image" />
          )}
          <Card.Body className="tour-content">
            <div>
              <Card.Title className="tour-name">{tour.name}</Card.Title>
              <Card.Text className="tour-description">{tour.description}</Card.Text>
            </div>
            <Link to={`/tours/${tour.tourId}`}>
              <Button variant="dark">View Packages</Button>
            </Link>
          </Card.Body>
        </Card>
      ))}
    </div>
  );
};

export default React.memo(ToursList);
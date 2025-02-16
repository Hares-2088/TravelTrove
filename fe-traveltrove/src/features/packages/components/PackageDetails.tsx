import React, { useCallback, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { useAuth0 } from "@auth0/auth0-react";
import { useSubscriptionsApi } from "../api/subscriptions.api";
import { Container, Row, Col, Button, Spinner, Alert, Card } from "react-bootstrap";
import { DollarSign, Calendar, Users, CheckCircle, XCircle } from "lucide-react";
import "./PackageDetails.css";
import { AppRoutes } from "../../../shared/models/app.routes";

const PackageDetails: React.FC = () => {
  const { packageId } = useParams<{ packageId: string }>();
  const { getPackageById } = usePackagesApi();
  const { checkSubscription, subscribeToPackage, unsubscribeFromPackage } = useSubscriptionsApi();
  const [pkg, setPkg] = useState<PackageResponseModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubscribed, setIsSubscribed] = useState(false);
  const { isAuthenticated, user } = useAuth0();
  const navigate = useNavigate();

  const fetchPackage = useCallback(async () => {
    try {
      const data = await getPackageById(packageId!);
      setPkg(data);
    } catch {
      setError("Failed to fetch package details.");
    } finally {
      setLoading(false);
    }
  }, [getPackageById, packageId]);

  useEffect(() => {
    fetchPackage();
  }, [fetchPackage]);

  useEffect(() => {
    const fetchSubscriptionStatus = async () => {
      if (isAuthenticated && user?.sub && packageId) {
        const subscribed = await checkSubscription(user.sub, packageId);
        setIsSubscribed(subscribed);
      }
    };

    fetchSubscriptionStatus();
  }, [isAuthenticated, user, packageId, checkSubscription]);

  const handleBook = () => {
    navigate(AppRoutes.BookingFormPage, { state: { package: pkg } });
  };

  const handleSubscribe = async () => {
    if (isAuthenticated && user?.sub && packageId) {
      await subscribeToPackage(user.sub, packageId);
      setIsSubscribed(true);
    }
  };

  const handleUnsubscribe = async () => {
    if (isAuthenticated && user?.sub && packageId) {
      await unsubscribeFromPackage(user.sub, packageId);
      setIsSubscribed(false);
    }
  };

  if (loading) return <Spinner animation="border" className="loading-spinner" />;
  if (error) return <Alert variant="danger" className="error-alert">{error}</Alert>;
  if (!pkg) return <Alert variant="info" className="info-alert">No package details found.</Alert>;

  return (
    <Container className="package-details-container">
      <Card className="package-card">
        <Card.Body>
          <div className="package-header">
            <Card.Title className="package-title">{pkg.name}</Card.Title>
            <Card.Text className="package-description">{pkg.description}</Card.Text>
          </div>
          <Row className="package-info">
            <Col md={6}>
              <p><DollarSign /> <strong>Price (Single):&nbsp;</strong> ${pkg.priceSingle}</p>
              <p><DollarSign /> <strong>Price (Double):&nbsp;</strong> ${pkg.priceDouble}</p>
              <p><DollarSign /> <strong>Price (Triple):&nbsp;</strong> ${pkg.priceTriple}</p>
            </Col>
            <Col md={6}>
              <p><Users /> <strong>Available Seats:&nbsp;</strong> {pkg.availableSeats}</p>
              <p><Calendar /> <strong>Start Date:&nbsp;</strong> {pkg.startDate}</p>
              <p><Calendar /> <strong>End Date:&nbsp;</strong> {pkg.endDate}</p>
            </Col>
          </Row>
          <div className="package-actions">
            {isAuthenticated && pkg.status !== "UPCOMING" && pkg.status !== "SOLD_OUT" && (
              <Button variant="dark" onClick={handleBook} className="action-button book-button">
                Book Now
              </Button>
            )}
            {isAuthenticated && (
              <>
                {isSubscribed ? (
                  <Button variant="danger" onClick={handleUnsubscribe} className="action-button unsubscribe-button">
                    Unsubscribe
                  </Button>
                ) : (
                  <Button variant="success" onClick={handleSubscribe} className="action-button subscribe-button">
                    Subscribe
                  </Button>
                )}
              </>
            )}
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PackageDetails;
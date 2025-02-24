import React, { useCallback, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { useAuth0 } from "@auth0/auth0-react";
import { useSubscriptionsApi } from "../api/subscriptions.api";
import { useBookingsApi } from "../../bookings/api/bookings.api";
import { BookingStatus } from "../../bookings/models/bookings.model";
import { useReviewsApi } from "../../reviews/api/review.api";
import { Container, Row, Col, Button, Spinner, Alert, Card, Modal, Form } from "react-bootstrap";
import { DollarSign, Calendar, Users } from "lucide-react";
import { useTranslation } from "react-i18next";
import "./PackageDetails.css";
import { AppRoutes } from "../../../shared/models/app.routes";

const PackageDetails: React.FC = () => {
  const { packageId } = useParams<{ packageId: string }>();
  const { getPackageById } = usePackagesApi();
  const { checkSubscription, subscribeToPackage, unsubscribeFromPackage } = useSubscriptionsApi();
  const { getAllBookings } = useBookingsApi();
  const { getReviewsByPackage, addReview } = useReviewsApi();
  const [pkg, setPkg] = useState<PackageResponseModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [reviewData, setReviewData] = useState({ rating: 0, review: "" });
  const [hasReviewed, setHasReviewed] = useState(false);
  const [hasConfirmedBooking, setHasConfirmedBooking] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const { isAuthenticated, user } = useAuth0();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const fetchPackage = useCallback(async () => {
    try {
      const data = await getPackageById(packageId!);
      setPkg(data);
    } catch {
      setError(t('error.fetchPackageDetails'));
    } finally {
      setLoading(false);
    }
  }, [t]);

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

  useEffect(() => {
    const checkUserReview = async () => {
      if (isAuthenticated && user?.sub && packageId) {
        const reviews = await getReviewsByPackage(packageId);
        const userReview = reviews.find(review => review.userId === user.sub);
        setHasReviewed(!!userReview);
      }
    };

    checkUserReview();
  }, [isAuthenticated, user, packageId, getReviewsByPackage]);

  useEffect(() => {
    const checkUserBooking = async () => {
      if (isAuthenticated && user?.sub && packageId) {
        try {
          const bookings = await getAllBookings();
          const confirmedBooking = bookings.find(booking => 
            booking.status === BookingStatus.BOOKING_CONFIRMED &&
            booking.userId === user.sub &&
            booking.packageId === packageId
          );
          console.log(confirmedBooking)
          setHasConfirmedBooking(!!confirmedBooking);
        } catch {
          setHasConfirmedBooking(false);
        }
      }
    };

    checkUserBooking();
  }, [isAuthenticated, user, packageId, getAllBookings]);

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

  const handleAddReview = async () => {
    if (isAuthenticated && user?.sub && packageId) {
      await addReview({
        packageId,
        userId: user.sub,
        reviewerName: `${user.given_name || "Anonymous"} ${user.family_name || ""}`,
        rating: reviewData.rating,
        review: reviewData.review,
      });
      setSuccessMessage(t('reviewSubmittedSuccessfully'));
      setReviewData({ rating: 0, review: "" });
    }
  };

  if (loading) return <Spinner animation="border" className="loading-spinner" />;
  if (error) return <Alert variant="danger" className="error-alert">{error}</Alert>;
  if (!pkg) return <Alert variant="info" className="info-alert">{t('noPackageDetails')}</Alert>;

  const isReviewButtonVisible = !hasReviewed && hasConfirmedBooking && new Date(pkg.endDate) < new Date();
  console.log(hasConfirmedBooking);

  return (
    <Container className="package-details-container">
      <div className="d-flex justify-content-center">
        <Card className="package-card w-75">
          <Card.Body>
            <div className="package-header">
              <Card.Title className="package-title">{pkg.name}</Card.Title>
              <Card.Text className="package-description">{pkg.description}</Card.Text>
            </div>
            <Row className="package-info">
              <Col md={6}>
                <p><DollarSign /> <strong>{t('priceSingle')}:&nbsp;</strong> ${pkg.priceSingle}</p>
                <p><DollarSign /> <strong>{t('priceDouble')}:&nbsp;</strong> ${pkg.priceDouble}</p>
                <p><DollarSign /> <strong>{t('priceTriple')}:&nbsp;</strong> ${pkg.priceTriple}</p>
              </Col>
              <Col md={6}>
                <p><Users /> <strong>{t('availableSeats')}:&nbsp;</strong> {pkg.availableSeats}</p>
                <p><Calendar /> <strong>{t('startDate')}:&nbsp;</strong> {pkg.startDate}</p>
                <p><Calendar /> <strong>{t('endDate')}:&nbsp;</strong> {pkg.endDate}</p>
              </Col>
            </Row>
            <div className="package-actions">
              {isAuthenticated && pkg.status !== "UPCOMING" && pkg.status !== "SOLD_OUT" && (
                <Button variant="dark" onClick={handleBook} className="action-button book-button">
                  {t('bookNow')}
                </Button>
              )}
              {isAuthenticated && (
                <>
                  {isSubscribed ? (
                    <Button variant="danger" onClick={handleUnsubscribe} className="action-button unsubscribe-button">
                      {t('unsubscribe')}
                    </Button>
                  ) : (
                    <Button variant="success" onClick={handleSubscribe} className="action-button subscribe-button">
                      {t('subscribe')}
                    </Button>
                  )}
                  {isReviewButtonVisible && (
                    <Button variant="primary" onClick={() => setShowReviewModal(true)} className="action-button review-button">
                      {t('addReview')}
                    </Button>
                  )}
                </>
              )}
            </div>
          </Card.Body>
        </Card>
      </div>

      <Modal show={showReviewModal} onHide={() => setShowReviewModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{t('addReview')}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {successMessage ? (
            <Alert variant="success">{successMessage}</Alert>
          ) : (
            <Form>
              <Form.Group controlId="reviewRating">
                <Form.Label>{t('rating')}</Form.Label>
                <Form.Control
                  type="number"
                  min="1"
                  max="5"
                  value={reviewData.rating}
                  onChange={(e) => setReviewData({ ...reviewData, rating: Number(e.target.value) })}
                />
              </Form.Group>
              <Form.Group controlId="reviewText">
                <Form.Label>{t('review')}</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  value={reviewData.review}
                  onChange={(e) => setReviewData({ ...reviewData, review: e.target.value })}
                />
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowReviewModal(false)}>
            {t('cancel')}
          </Button>
          {!successMessage && (
            <Button variant="primary" onClick={handleAddReview}>
              {t('submitReview')}
            </Button>
          )}
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default PackageDetails;
import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { useAuth0 } from "@auth0/auth0-react";
import { useSubscriptionsApi } from "../api/subscriptions.api"; // Import the subscription API hook
import "./PackageDetails.css";
import { AppRoutes } from "../../../shared/models/app.routes";

const PackageDetails: React.FC = () => {
  const { packageId } = useParams<{ packageId: string }>();
  const { getPackageById } = usePackagesApi();
  const { checkSubscription, subscribeToPackage, unsubscribeFromPackage } = useSubscriptionsApi(); // Use the subscription API hook
  const [pkg, setPkg] = useState<PackageResponseModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubscribed, setIsSubscribed] = useState(false); // State to track subscription status
  const { isAuthenticated, user } = useAuth0(); // Get the authenticated user
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPackage = async () => {
      try {
        const data = await getPackageById(packageId!);
        setPkg(data);
      } catch {
        setError("Failed to fetch package details.");
      } finally {
        setLoading(false);
      }
    };

    if (packageId) fetchPackage();
  }, [packageId, getPackageById]);

  // Fetch subscription status when the package or user changes
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
      setIsSubscribed(true); // Update subscription status
    }
  };

  const handleUnsubscribe = async () => {
    if (isAuthenticated && user?.sub && packageId) {
      await unsubscribeFromPackage(user.sub, packageId);
      setIsSubscribed(false); // Update subscription status
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!pkg) return <div>No package details found.</div>;

  return (
    <div className="package-details">
      <header className="package-header">
        <h1 className="package-title">{pkg.name}</h1>
        <p className="package-description">{pkg.description}</p>
      </header>
      <div className="package-info">
        <p>
          <strong>Price (Single):</strong> ${pkg.priceSingle}
        </p>
        <p>
          <strong>Price (Double):</strong> ${pkg.priceDouble}
        </p>
        <p>
          <strong>Price (Triple):</strong> ${pkg.priceTriple}
        </p>
        <p>
          <strong>Available Seats:</strong> {pkg.availableSeats}
        </p>
        <p>
          <strong>Start Date:</strong> {pkg.startDate}
        </p>
        <p>
          <strong>End Date:</strong> {pkg.endDate}
        </p>
      </div>
      {isAuthenticated && (
          <>
            <button onClick={handleUnsubscribe} className="unsubscribe-button">
              Unsubscribe
            </button>
            ) : (
            <button onClick={handleSubscribe} className="subscribe-button">
              Subscribe
            </button>
      {isAuthenticated && pkg.status !== "UPCOMING" && pkg.status !== "SOLD_OUT" && (
        <button onClick={handleBook} className="book-button">
          Book Now
        </button>
      )}
          </>
      )}
    </div>
  );
};

export default PackageDetails;

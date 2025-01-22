import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { useAuth0 } from "@auth0/auth0-react";
import "./PackageDetails.css";
import { AppRoutes } from "../../../shared/models/app.routes";

const PackageDetails: React.FC = () => {
  const { packageId } = useParams<{ packageId: string }>();
  const { getPackageById } = usePackagesApi();
  const [pkg, setPkg] = useState<PackageResponseModel | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { isAuthenticated } = useAuth0();
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

  const handleBook = () => {
    navigate(AppRoutes.BookingFormPage, { state: { package: pkg } });
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
        <button onClick={handleBook} className="book-button">
          Book Now
        </button>
      )}
    </div>
  );
};

export default PackageDetails;

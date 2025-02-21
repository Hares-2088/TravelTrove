import React, { useEffect, useState } from "react";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { Link } from "react-router-dom";
import { Card, Button } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import "./PackageList.css";

interface PackageListProps {
  tourId: string;
}

const PackageList: React.FC<PackageListProps> = ({ tourId }) => {
  const { getAllPackages } = usePackagesApi();
  const [packages, setPackages] = useState<PackageResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { t } = useTranslation();

  useEffect(() => {
    if (!tourId) return; // Prevent fetching if tourId is null

    let isMounted = true;

    const fetchPackages = async () => {
      setLoading(true);
      try {
        console.log(`📢 Fetching packages for tourId=${tourId}`);
        const data = await getAllPackages({ tourId });

        if (isMounted) {
          // Filter packages based on status
          const filteredPackages = data.filter(pkg =>
            ["UPCOMING", "BOOKING_OPEN", "SOLD_OUT"].includes(pkg.status)
          );
          console.log(`✅ Packages fetched successfully for tourId=${tourId}`, filteredPackages);
          setPackages(filteredPackages);
        }
      } catch (err) {
        if (isMounted) {
          console.error(`❌ Error fetching packages for tourId=${tourId}:`, err);
          setError(t("error.fetchingPackages"));
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchPackages();

    return () => {
      isMounted = false;
    };
  }, [tourId]); // ✅ Ensure `useEffect` runs when `tourId` changes

  if (loading) return <div>{t("loading.packages")}</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="package-list">
      {packages.length === 0 ? (
        <div>{t("noPackagesAvailable")}</div>
      ) : (
        packages.map((pkg) => (
          <Card key={pkg.packageId} className="package-item">
            <div className="price-tag">${pkg.priceTriple}</div>
            <Card.Body>
              <Card.Title>{pkg.name}</Card.Title>
              <Card.Text>{pkg.description}</Card.Text>
              <Link to={`/packages/${pkg.packageId}`}>
                <Button variant="dark">{t("viewDetails")}</Button>
              </Link>
            </Card.Body>
          </Card>
        ))
      )}
    </div>
  );
};

export default PackageList;
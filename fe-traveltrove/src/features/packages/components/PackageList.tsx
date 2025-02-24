import React, { useEffect, useState } from "react";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { Link } from "react-router-dom";
import { Card, Button, Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { Calendar } from "lucide-react";
import "./PackageList.css";

interface PackageListProps {
  tourId: string;
}

const PackageList: React.FC<PackageListProps> = ({ tourId }) => {
  const { getAllPackages } = usePackagesApi();
  const [packages, setPackages] = useState<PackageResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCompleted, setShowCompleted] = useState(false);
  const { t } = useTranslation();

  useEffect(() => {
    if (!tourId) return; // Prevent fetching if tourId is null

    let isMounted = true;

    const fetchPackages = async () => {
      setLoading(true);
      try {
        const data = await getAllPackages({ tourId });

        if (isMounted) {
          // Filter packages based on status
          const filteredPackages = data.filter((pkg) =>
            ["UPCOMING", "BOOKING_OPEN", "SOLD_OUT", "COMPLETED"].includes(
              pkg.status
            )
          );
          setPackages(filteredPackages);
        }
      } catch (err) {
        if (isMounted) {
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

  const filteredPackages = showCompleted
    ? packages
    : packages.filter((pkg) => pkg.status !== "COMPLETED");

  return (
    <div>
      <Form.Check
        type="checkbox"
        label={t("showCompleted")}
        checked={showCompleted}
        onChange={() => setShowCompleted(!showCompleted)}
        className="mb-3"
      />
      <div className="package-list">
        {filteredPackages.length === 0 ? (
          <div>{t("noPackagesAvailable")}</div>
        ) : (
          filteredPackages.map((pkg) => (
            <Card key={pkg.packageId} className="package-item">
              <div
                className={`status-tag ${
                  pkg.status === "COMPLETED" ? "completed" : "price-tag"
                }`}
              >
                {pkg.status === "COMPLETED"
                  ? t("completed")
                  : `$${pkg.priceSingle}`}
              </div>
              <Card.Body>
                <Card.Title >{pkg.name}</Card.Title>
                <Card.Text >
                  {pkg.description}
                </Card.Text>
                <div className="package-dates">
                  <p>
                    <Calendar className="date-icon" /> {t("startDate")}:{" "}
                    {new Date(pkg.startDate).toLocaleDateString()}
                  </p>
                  <p>
                    <Calendar className="date-icon" /> {t("endDate")}:{" "}
                    {new Date(pkg.endDate).toLocaleDateString()}
                  </p>
                </div>
                <Link to={`/packages/${pkg.packageId}`}>
                  <Button variant="dark">{t("viewDetails")}</Button>
                </Link>
              </Card.Body>
            </Card>
          ))
        )}
      </div>
    </div>
  );
};

export default PackageList;

import React, { useEffect, useState } from "react";
import { usePackagesApi } from "../api/packages.api";
import { PackageResponseModel } from "../models/package.model";
import { Link } from "react-router-dom";

interface PackageListProps {
  tourId: string;
}

const PackageList: React.FC<PackageListProps> = ({ tourId }) => {
  const { getAllPackages } = usePackagesApi();
  const [packages, setPackages] = useState<PackageResponseModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
          setError("Failed to fetch packages.");
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

  if (loading) return <div>Loading packages...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="package-list">
      {packages.length === 0 ? (
        <div>No packages available for this tour.</div>
      ) : (
        packages.map((pkg) => (
          <div key={pkg.packageId} className="package-item">
            <Link to={`/packages/${pkg.packageId}`}>
              <h2>{pkg.name}</h2>
              <p>{pkg.description}</p>
            </Link>
          </div>
        ))
      )}
    </div>
  );
};

export default PackageList;

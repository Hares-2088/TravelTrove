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
    const fetchPackages = async () => {
      try {
        const data = await getAllPackages({ tourId });
        setPackages(data);
      } catch {
        setError("Failed to fetch packages.");
      } finally {
        setLoading(false);
      }
    };

    fetchPackages();
  }, [tourId, getAllPackages]);

  if (loading) return <div>Loading packages...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="package-list">
      {packages.map((pkg) => (
        <div key={pkg.packageId} className="package-item">
          <Link to={`/packages/${pkg.packageId}`}>
            <h2>{pkg.name}</h2>
            <p>{pkg.description}</p>
          </Link>
        </div>
      ))}
    </div>
  );
};

export default PackageList;

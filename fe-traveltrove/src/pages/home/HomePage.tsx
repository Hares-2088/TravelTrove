import React, { FC, useState, useEffect } from "react";
import HomeDetails from "../../features/home/components/HomeDetails";
import { useSubscriptionsApi } from "../../features/packages/api/subscriptions.api";
import { SubscriptionResponseModel } from "../../features/packages/models/subscription.model";
import { PackageResponseModel } from "../../features/packages/models/package.model";
import { usePackagesApi } from "../../features/packages/api/packages.api";

interface HomePageProps {
  userId: string; // Accept userId as a prop
}

const HomePage: FC<HomePageProps> = ({ userId }): JSX.Element => {
  const { userId, isAuthenticated, isLoading } = useUserContext();
  const [suggestedPackages, setSuggestedPackages] = useState<PackageResponseModel[]>([]);

  useEffect(() => {
      const fetchSuggestedPackages = async () => {
          if (isLoading || !isAuthenticated || !userId) return;

          try {
              const subscriptions = await getSubscribedPackages(userId);
              setSuggestedPackages(subscriptions);
          } catch (error) {
              console.error("Error fetching suggested packages", error);
          }
      };

      fetchSuggestedPackages();
  }, [userId, isAuthenticated, isLoading]);


  return (
    <div>
      <h1>Welcome to TravelTrove</h1>

      <div>
            <h2>Suggested Packages</h2>
            <ul>
                {suggestedPackages.map(pkg => (
                    <li key={pkg.packageId}>{pkg.name}</li>
                ))}
            </ul>
        </div>

      <div style={{ backgroundColor: "#FFF5F0", minHeight: "100vh", display: "flex", flexDirection: "column" }}>
        <div style={{ flex: 1 }}>
          <HomeDetails />
          <div className="elfsight-app-6d3f48d9-c5ae-4dae-8574-3ea7f9eb1362" data-elfsight-app-lazy></div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;

import React, { useState, useEffect } from "react";
import axios from "axios";

const EngagementAnalyticsTab: React.FC = () => {
  const [engagementData, setEngagementData] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchEngagementData = async () => {
      try {
        const response = await axios.get("/api/v1/engagements"); // Adjust the API endpoint
        console.log("Fetched Engagement Data:", response.data);

        if (!Array.isArray(response.data)) {
          throw new Error("Invalid data format received");
        }

        setEngagementData(response.data);
      } catch (err: any) {
        console.error("Error fetching engagement data:", err);
        setError("Failed to load engagement data.");
      } finally {
        setLoading(false);
      }
    };

    fetchEngagementData();
  }, []);

  if (loading) return <p>Loading engagement data...</p>;

  if (error) return <p>Error: {error}</p>;

  if (!engagementData.length) return <p>No engagement data available.</p>;

  return (
    <div>
      <h2>User Engagement Analytics</h2>
      <table>
        <thead>
          <tr>
            <th>Package Name</th>
            <th>Views</th>
            <th>Shares</th>
            <th>Wishlisted</th>
          </tr>
        </thead>
        <tbody>
          {engagementData.map((item) => (
            <tr key={item.packageId}>
              <td>{item.packageName || "N/A"}</td>
              <td>{item.views || 0}</td>
              <td>{item.shares || 0}</td>
              <td>{item.wishlisted || 0}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default EngagementAnalyticsTab;

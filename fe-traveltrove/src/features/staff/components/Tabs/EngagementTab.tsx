import React from "react";
import { Bar } from "react-chartjs-2";
import useEngagementAnalytics from "../Tabs/useEngagementAnalytics";

const EngagementTab = () => {
  const { analytics, error } = useEngagementAnalytics();

  const chartData = {
    labels: analytics.map((pkg) => pkg.packageName),
    datasets: [
      {
        label: "Views",
        data: analytics.map((pkg) => pkg.views),
        backgroundColor: "rgba(75,192,192,0.6)",
      },
      {
        label: "Shares",
        data: analytics.map((pkg) => pkg.shares),
        backgroundColor: "rgba(153,102,255,0.6)",
      },
      {
        label: "Wishlists",
        data: analytics.map((pkg) => pkg.wishlists),
        backgroundColor: "rgba(255,159,64,0.6)",
      },
    ],
  };

  if (error) return <p>Error: {error}</p>;

  return (
    <div>
      <h2>Package Engagement Analytics</h2>
      <Bar data={chartData} />
    </div>
  );
};

export default EngagementTab;

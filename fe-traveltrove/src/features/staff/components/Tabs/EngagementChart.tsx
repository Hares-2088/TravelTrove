import React, { useEffect, useState } from "react";
import { Bar } from "react-chartjs-2";
import { usePackagesApi } from "../../../packages/api/packages.api";
import { PackageResponseModel } from "../../../packages/models/package.model";
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from "chart.js";
import { useTranslation } from "react-i18next";

// Register required Chart.js components
Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const EngagementChart: React.FC = () => {
  const { getAllPackages } = usePackagesApi();
  const [packageData, setPackageData] = useState<PackageResponseModel[]>([]);
  const { t } = useTranslation();

  useEffect(() => {
    const fetchPackages = async () => {
      try {
        const data = await getAllPackages();
        setPackageData(data);
      } catch (error) {
        console.error(t("error.fetchingPackages"), error);
      }
    };
    fetchPackages();
  }, [t]);

  // Extracting relevant data for the chart
  const packageNames = packageData.map((pkg) => pkg.name);
  const availableSeats = packageData.map((pkg) => pkg.availableSeats);
  const totalSeats = packageData.map((pkg) => pkg.totalSeats);

  // Chart Data Configuration
  const chartData = {
    labels: packageNames,
    datasets: [
      {
        label: t("availableSeats"),
        data: availableSeats,
        backgroundColor: "rgba(75, 192, 192, 0.6)",
      },
      {
        label: t("totalSeats"),
        data: totalSeats,
        backgroundColor: "rgba(255, 99, 132, 0.6)",
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false, 
    plugins: {
      legend: { position: "top" as const },
      title: { display: true, text: t("engagementOverview") },
    },
    scales: {
      x: { title: { display: true, text: t("packages") } },
      y: { title: { display: true, text: t("seats") } },
    },
  };

  return (
    <div style={{ width: "100%", height: "400px", padding: "20px" }}>
      <h3>{t("engagementOverview")}</h3>
      <Bar data={chartData} options={chartOptions} />
    </div>
  );
};

export default EngagementChart;

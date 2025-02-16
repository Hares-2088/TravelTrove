import React, { useEffect, useState } from "react";
import { Bar } from "react-chartjs-2";
import { usePackagesApi } from "../../../packages/api/packages.api";
import { PackageResponseModel, PackageStatus } from "../../../packages/models/package.model";
import { Chart, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from "chart.js";

// Register required Chart.js components
Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

const EngagementChart: React.FC = () => {
    const { getAllPackages } = usePackagesApi();
    const [packageData, setPackageData] = useState<PackageResponseModel[]>([]);

    useEffect(() => {
        const fetchPackages = async () => {
            try {
                const data = await getAllPackages();
                setPackageData(data);
            } catch (error) {
                console.error("Error fetching packages:", error);
            }
        };
        fetchPackages();
    }, []);

    // Extracting relevant data for the chart
    const packageNames = packageData.map(pkg => pkg.name);
    const availableSeats = packageData.map(pkg => pkg.availableSeats);
    const totalSeats = packageData.map(pkg => pkg.totalSeats);

    // Chart Data Configuration
    const chartData = {
        labels: packageNames,
        datasets: [
            {
                label: "Available Seats",
                data: availableSeats,
                backgroundColor: "rgba(75, 192, 192, 0.6)",
            },
            {
                label: "Total Seats",
                data: totalSeats,
                backgroundColor: "rgba(255, 99, 132, 0.6)",
            }
        ],
    };

    const chartOptions = {
        responsive: true,
        plugins: {
            legend: { position: "top" as const },
            title: { display: true, text: "Package Engagement Overview" },
        },
        scales: {
            x: { title: { display: true, text: "Packages" } },
            y: { title: { display: true, text: "Seats" } },
        },
    };

    return (
        <div style={{ width: "100%", height: "400px", padding: "20px" }}>
            <h3>Engagement Overview</h3>
            <Bar data={chartData} options={chartOptions} />
        </div>
    );
};

export default EngagementChart;

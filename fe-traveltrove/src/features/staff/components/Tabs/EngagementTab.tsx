import React from "react";
import { Bar } from "react-chartjs-2";
import useEngagementAnalytics from "../Tabs/useEngagementAnalytics";
import { useTranslation } from "react-i18next";

const EngagementTab = () => {
  const { analytics, error } = useEngagementAnalytics();
  const { t } = useTranslation();

  const chartData = {
    labels: analytics.map((pkg) => pkg.packageName),
    datasets: [
      {
        label: t("views"),
        data: analytics.map((pkg) => pkg.views),
        backgroundColor: "rgba(75,192,192,0.6)",
      },
      {
        label: t("shares"),
        data: analytics.map((pkg) => pkg.shares),
        backgroundColor: "rgba(153,102,255,0.6)",
      },
      {
        label: t("wishlists"),
        data: analytics.map((pkg) => pkg.wishlists),
        backgroundColor: "rgba(255,159,64,0.6)",
      },
    ],
  };

  if (error) return <p>{t("error")}: {error}</p>;

  return (
    <div>
      <h2>{t("packageEngagementAnalytics")}</h2>
      <Bar data={chartData} />
    </div>
  );
};

export default EngagementTab;

import { useEffect, useState } from "react";
import axios from "axios";

const useEngagementAnalytics = () => {
  const [analytics, setAnalytics] = useState<any[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const eventSource = new EventSource("/api/v1/analytics/engagement");

    eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data);
      setAnalytics((prev) => [...prev, data]); // Append new data
    };

    eventSource.onerror = (err) => {
      setError("Failed to load engagement analytics");
      eventSource.close();
    };

    return () => eventSource.close();
  }, []);

  return { analytics, error };
};

export default useEngagementAnalytics;

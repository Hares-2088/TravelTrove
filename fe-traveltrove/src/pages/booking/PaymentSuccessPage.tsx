import React, { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";

const SuccessPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const sessionId = searchParams.get("session_id");

  useEffect(() => {
    if (sessionId) {
      // Call backend to confirm booking after payment success
      axios
        .post(`${process.env.REACT_APP_BACKEND_URL}/api/v1/payments/confirm`, {
          sessionId,
        })
        .then(() => {
          console.log("✅ Payment confirmed. Booking updated.");
        })
        .catch((error) => {
          console.error("❌ Error confirming payment:", error);
        });
    }
  }, [sessionId]);

  return (
    <div>
      <h1>✅ Payment Successful!</h1>
      <p>Your booking has been confirmed.</p>
      <button onClick={() => navigate("/")}>Go to Home</button>
    </div>
  );
};

export default SuccessPage;

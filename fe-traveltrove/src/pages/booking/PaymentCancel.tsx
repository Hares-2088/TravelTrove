import React from "react";
import { useNavigate } from "react-router-dom";

const PaymentCancel: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div>
      <h1>❌ Payment Canceled</h1>
      <p>Your payment was not completed.</p>
      <button onClick={() => navigate("/tours")}>Try Again</button>
    </div>
  );
};

export default PaymentCancel;

import React from "react";
import { useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';

const PaymentCancel: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="container text-center mt-5">
      <div className="card p-4 shadow">
        <h1 className="text-danger">❌ Payment Canceled</h1>
        <p>Your payment was not completed.</p>
        <button className="btn btn-warning mt-3" onClick={() => navigate("/tours")}>Try Again</button>
      </div>
    </div>
  );
};

export default PaymentCancel;

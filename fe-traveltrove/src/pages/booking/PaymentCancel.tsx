import React from "react";
import { useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import { FaTimesCircle } from 'react-icons/fa';


const PaymentCancel: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div
      className="d-flex justify-content-center align-items-center p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <div className="card p-4 shadow-lg" style={{ maxWidth: "500px", width: "100%" }}>
        <div className="text-center">
          <FaTimesCircle size={50} className="text-danger mb-3" />
          <h1 className="text-danger">Payment Canceled</h1>
          <p className="lead">Your payment was not completed.</p>
          <button className="btn btn-warning mt-3" onClick={() => navigate("/tours")}>Try Again</button>
        </div>
      </div>
    </div>
  );
};

export default PaymentCancel;

import { useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import { FaCheckCircle } from 'react-icons/fa';


const PaymentSuccessPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div
      className="d-flex justify-content-center align-items-center p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <div className="card p-4 shadow-lg" style={{ maxWidth: "500px", width: "100%" }}>
        <div className="text-center">
          <FaCheckCircle size={50} className="text-success mb-3" />
          <h1 className="text-success">Payment Successful!</h1>
          <p className="lead">Your booking has been confirmed.</p>
          <button className="btn btn-primary mt-3" onClick={() => navigate("/home")}>Go to Home</button>
        </div>
      </div>
    </div>
  );
};

export default PaymentSuccessPage;

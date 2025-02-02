import { useNavigate } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';

const PaymentSuccessPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="container text-center mt-5">
      <div className="card p-4 shadow">
        <h1 className="text-success">✅ Payment Successful!</h1>
        <p>Your booking has been confirmed.</p>
        <button className="btn btn-primary mt-3" onClick={() => navigate("/home")}>Go to Home</button>
      </div>
    </div>
  );
};

export default PaymentSuccessPage;

import { useNavigate } from "react-router-dom";

const PaymentSuccessPage: React.FC = () => {
  const navigate = useNavigate();
 

  return (
    <div>
      <h1>✅ Payment Successful!</h1>
      <p>Your booking has been confirmed.</p>
      <button onClick={() => navigate("/home")}>Go to Home</button>
    </div>
  );
};

export default PaymentSuccessPage;

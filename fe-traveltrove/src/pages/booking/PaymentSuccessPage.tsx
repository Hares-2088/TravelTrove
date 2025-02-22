import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import { FaCheckCircle } from "react-icons/fa";
import { useTranslation } from "react-i18next";

const PaymentSuccessPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  if (!t) return <div>Loading...</div>;

  return (
    <div
      className="d-flex justify-content-center align-items-center p-4"
      style={{ backgroundColor: "#f8f9fa", minHeight: "100vh" }}
    >
      <div
        className="card p-4 shadow-lg"
        style={{ maxWidth: "500px", width: "100%" }}
      >
        <div className="text-center">
          <FaCheckCircle size={50} className="text-success mb-3" />
          <h1 className="text-success">{t("paymentSuccess")}</h1>
          <p className="lead">{t("paymentConfirmed")}</p>
          <button
            className="btn btn-primary mt-3"
            onClick={() => navigate("/home")}
          >
            {t("goHome")}
          </button>
        </div>
      </div>
    </div>
  );
};
export default PaymentSuccessPage
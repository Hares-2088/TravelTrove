import { useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import { FaTimesCircle } from "react-icons/fa";
import { useTranslation } from "react-i18next";

const PaymentCancelPage: React.FC = () => {
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
          <FaTimesCircle size={50} className="text-danger mb-3" />
          <h1 className="text-danger">{t("paymentCanceled")}</h1>
          <p className="lead">{t("paymentNotCompleted")}</p>
          <div className="mt-3">
            <button
              className="btn btn-danger me-2"
              onClick={() => navigate("/trips")}
            >
              {t("tryAgain")}
            </button>
            <button
              className="btn btn-outline-secondary"
              onClick={() => navigate("/home")}
            >
              {t("goHome")}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaymentCancelPage;
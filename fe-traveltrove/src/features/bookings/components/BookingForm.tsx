import React, { useState, useEffect } from "react";
import { useTravelersApi } from "../../travelers/api/traveler.api";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../../users/api/users.api";
import {
  TravelerRequestModel,
  TravelerResponseModel,
} from "../../travelers/model/traveler.model";
import { BookingStatus } from "../../bookings/models/bookings.model";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import "./BookingForm.css"; // Import the CSS file
import {
  FaUserPlus,
  FaCheckCircle,
  FaTrash,
  FaExclamationTriangle,
} from "react-icons/fa";
import { AppRoutes } from "../../../shared/models/app.routes";

interface BookingFormProps {
  pkg: any;
  onSubmit: (bookingRequest: any) => void;
}

const BookingForm: React.FC<BookingFormProps> = ({ pkg, onSubmit }) => {
  const { user } = useAuth0();
  const { syncUser } = useUsersApi();
  const { getTravelerById } = useTravelersApi();
  const [travelers, setTravelers] = useState<TravelerResponseModel[]>([]);
  const [selectedTravelers, setSelectedTravelers] = useState<
    TravelerResponseModel[]
  >([]);
  const [newTravelers, setNewTravelers] = useState<TravelerRequestModel[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [formErrors, setFormErrors] = useState<{ [key: number]: string[] }>({});
  const navigate = useNavigate();
  const { t } = useTranslation();

  useEffect(() => {
    // Fetch travelers when the component mounts
    const fetchTravelers = async () => {
      try {
        if (user?.sub) {
          const userResponseModel = await syncUser(user.sub);
          const travelerIds = userResponseModel.travelerIds || [];
          const travelers = await Promise.all(
            travelerIds.map(async (travelerId: string) => {
              try {
                const traveler = await getTravelerById(travelerId);
                return traveler;
              } catch (error) {
                return null;
              }
            })
          );
          const validTravelers = travelers.filter(
            (traveler) => traveler !== null
          );
          setTravelers(validTravelers);
        }
      } catch (error) {
        console.error("Failed to fetch travelers", error);
      }
    };
    fetchTravelers();
  }, [user]);

  const handleTravelerSelect = (travelerId: string) => {
    const existingTraveler = travelers.find((t) => t.travelerId === travelerId);
    if (existingTraveler) {
      setSelectedTravelers([...selectedTravelers, existingTraveler]);
    }
  };

  const handleRemoveSelectedTraveler = (travelerId: string) => {
    const updatedTravelers = selectedTravelers.filter((t) => t.travelerId !== travelerId);
    setSelectedTravelers(updatedTravelers);
    if (updatedTravelers.length === 0 && newTravelers.length === 0) {
      setError(null);
    }
  };

  const handleAddTraveler = () => {
    setNewTravelers([
      ...newTravelers,
      {
        seq: travelers.length + newTravelers.length + 1,
        firstName: "",
        lastName: "",
        addressLine1: "",
        addressLine2: "",
        city: "",
        state: "",
        email: "",
        countryId: "",
      },
    ]);
  };

  const handleRemoveNewTraveler = (index: number) => {
    const updatedNewTravelers = newTravelers.filter((_, i) => i !== index);
    setNewTravelers(updatedNewTravelers);
    const updatedErrors = { ...formErrors };
    delete updatedErrors[index];
    setFormErrors(updatedErrors);
    if (Object.keys(updatedErrors).length === 0) {
      setError(null);
    }
  };

  const handleTravelerChange = (
    index: number,
    field: string,
    value: string
  ) => {
    const updatedTravelers = newTravelers.map((traveler, i) =>
      i === index ? { ...traveler, [field]: value } : traveler
    );
    setNewTravelers(updatedTravelers);
  };

  const validateForm = () => {
    let errors: { [key: number]: string[] } = {};

    newTravelers.forEach((traveler, index) => {
      let travelerErrors: string[] = [];

      if (!traveler.firstName.trim())
        travelerErrors.push(t("bookingForm.firstNameRequired"));
      if (!traveler.lastName.trim())
        travelerErrors.push(t("bookingForm.lastNameRequired"));
      if (!traveler.email.trim())
        travelerErrors.push(t("bookingForm.emailRequired"));
      if (!traveler.city.trim())
        travelerErrors.push(t("bookingForm.cityRequired"));
      if (!traveler.state.trim())
        travelerErrors.push(t("bookingForm.stateRequired"));
      if (!traveler.countryId.trim())
        travelerErrors.push(t("bookingForm.countryRequired"));
      if (!traveler.addressLine1.trim())
        travelerErrors.push(t("bookingForm.addressLine1Required"));

      if (travelerErrors.length > 0) {
        errors[index] = travelerErrors;
      }
    });

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = () => {
    if (!validateForm()) {
      setError(t("bookingForm.fillRequiredFields"));
      return;
    }

    if (!user?.sub) {
      setError(t("bookingForm.userIdMissing"));
      return;
    }
    const bookingRequest = {
      userId: user.sub,
      packageId: pkg.packageId,
      totalPrice:
        pkg.priceSingle * (selectedTravelers.length + newTravelers.length), // Calculate total price
      status: "PAYMENT_PENDING" as BookingStatus,
      bookingDate: new Date().toISOString(),
      travelers: [...selectedTravelers, ...newTravelers],
    };

    console.log("Booking Request:", bookingRequest);
    onSubmit(bookingRequest);
  };

  return (
    <div className="container p-4 shadow bg-white rounded">
      {/* Package Info */}
      <div className="alert alert-info">
        <strong>{t("bookingForm.package")}:</strong> {pkg.name} <br />
        <strong>{t("bookingForm.dates")}:</strong> {pkg.startDate} - {pkg.endDate}
      </div>

      {/* Existing Travelers Selection */}
      <div className="mb-3">
        <label className="form-label fw-bold">{t("bookingForm.selectTraveler")}:</label>
        <select
          onChange={(e) => handleTravelerSelect(e.target.value)}
          className="form-select"
        >
          <option value="">{t("bookingForm.choose")}</option>
          {travelers.map((traveler) => (
            <option key={traveler.travelerId} value={traveler.travelerId}>
              {traveler.firstName} {traveler.lastName}
            </option>
          ))}
        </select>
      </div>

      {/* Display Selected Travelers */}
      {selectedTravelers.length > 0 && (
        <div className="mb-3">
          <h5 className="fw-bold text-success">{t("bookingForm.selectedTravelers")}:</h5>
          {selectedTravelers.map((traveler) => (
            <div
              key={traveler.travelerId}
              className="p-2 border rounded bg-light mb-2 d-flex justify-content-between align-items-center"
            >
              <div>
                <strong>
                  {traveler.firstName} {traveler.lastName}
                </strong>
                <p className="mb-0 text-muted">{traveler.email}</p>
              </div>
              <button
                className="btn btn-danger btn-sm"
                onClick={() =>
                  handleRemoveSelectedTraveler(traveler.travelerId)
                }
              >
                <FaTrash />
              </button>
            </div>
          ))}
        </div>
      )}

      {/* New Traveler Form */}
      {newTravelers.map((traveler, index) => (
        <div key={index} className="p-3 border rounded mb-3 bg-light">
          <h6 className="fw-bold text-secondary d-flex justify-content-between">
            {t("bookingForm.newTraveler")} #{index + 1}
            <button
              className="btn btn-sm btn-outline-danger"
              onClick={() => handleRemoveNewTraveler(index)}
            >
              <FaTrash />
            </button>
          </h6>
          <div className="row">
            {/* First Name (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.firstNameRequired"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.firstNamePlaceholder")}
                value={traveler.firstName}
                onChange={(e) =>
                  handleTravelerChange(index, "firstName", e.target.value)
                }
              />
            </div>

            {/* Last Name (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.lastNameRequired"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.lastNamePlaceholder")}
                value={traveler.lastName}
                onChange={(e) =>
                  handleTravelerChange(index, "lastName", e.target.value)
                }
              />
            </div>

            {/* Email (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="email"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.emailRequired"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.emailPlaceholder")}
                value={traveler.email}
                onChange={(e) =>
                  handleTravelerChange(index, "email", e.target.value)
                }
              />
            </div>

            {/* Address Line 1 (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.addressLine1Required"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.addressLine1Placeholder")}
                value={traveler.addressLine1}
                onChange={(e) =>
                  handleTravelerChange(index, "addressLine1", e.target.value)
                }
              />
            </div>

            {/* Address Line 2 (Optional) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className="form-control"
                placeholder={t("bookingForm.addressLine2Placeholder")}
                value={traveler.addressLine2}
                onChange={(e) =>
                  handleTravelerChange(index, "addressLine2", e.target.value)
                }
              />
            </div>

            {/* City (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.cityRequired"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.cityPlaceholder")}
                value={traveler.city}
                onChange={(e) =>
                  handleTravelerChange(index, "city", e.target.value)
                }
              />
            </div>

            {/* State (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.stateRequired"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.statePlaceholder")}
                value={traveler.state}
                onChange={(e) =>
                  handleTravelerChange(index, "state", e.target.value)
                }
              />
            </div>

            {/* Country (Required) */}
            <div className="col-md-6 mb-2">
              <input
                type="text"
                className={`form-control ${formErrors[index]?.includes(t("bookingForm.countryRequired"))
                    ? "is-invalid"
                    : ""
                  }`}
                placeholder={t("bookingForm.countryPlaceholder")}
                value={traveler.countryId}
                onChange={(e) =>
                  handleTravelerChange(index, "countryId", e.target.value)
                }
              />
            </div>
          </div>
        </div>
      ))}

      {/* Add Traveler Button */}
      {travelers.length > 0 && (
        <button
          className="btn btn-outline-primary w-100 my-3"
          onClick={handleAddTraveler}
        >
          <FaUserPlus /> {t("bookingForm.addTraveler")}
        </button>
      )}

      {/* Error Message */}
      {error && <div className="alert alert-danger">{error}</div>}

      {/* Submit Button */}
      {selectedTravelers.length > 0 || newTravelers.length > 0 ? (
        <button className="btn btn-primary w-100" onClick={handleSubmit}>
          <FaCheckCircle /> {t("bookingForm.confirmBooking")}
        </button>
      ) : travelers.length === 0 ? (
        <button
          className="btn btn-warning w-100"
          onClick={() => navigate(AppRoutes.ProfileCreatePage)}
        >
          <FaExclamationTriangle /> {t("bookingForm.createTravelerProfile")}
        </button>
      ) : (
        <button className="btn btn-secondary w-100" disabled>
          <FaExclamationTriangle /> {t("bookingForm.selectOrAddTraveler")}
        </button>
      )}
    </div>
  );
};

export default BookingForm;

import React, { useState, useEffect } from "react";
import { useTravelersApi } from "../../travelers/api/traveler.api";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../../users/api/users.api";
import {
  TravelerRequestModel,
  TravelerResponseModel,
} from "../../travelers/model/traveler.model";
import { BookingStatus } from "../../bookings/models/bookings.model";
import "./BookingForm.css"; // Import the CSS file

interface BookingFormProps {
  pkg: any;
  onSubmit: (bookingRequest: any) => void;
}

const BookingForm: React.FC<BookingFormProps> = ({ pkg, onSubmit }) => {
  const { user } = useAuth0();
  const { syncUser } = useUsersApi();
  const { getTravelerById } = useTravelersApi();
  const [travelers, setTravelers] = useState<TravelerResponseModel[]>([]);
  const [selectedTravelers, setSelectedTravelers] = useState<TravelerResponseModel[]>([]);
  const [newTravelers, setNewTravelers] = useState<TravelerRequestModel[]>([]);
  const [error, setError] = useState<string | null>(null);

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
          const validTravelers = travelers.filter((traveler) => traveler !== null);
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

  const handleTravelerChange = (index: number, field: string, value: string) => {
    const updatedTravelers = newTravelers.map((traveler, i) =>
      i === index ? { ...traveler, [field]: value } : traveler
    );
    setNewTravelers(updatedTravelers);
  };

  const handleSubmit = () => {
    if (!user?.sub) {
      setError("User ID is missing");
      return;
    }

    const bookingRequest = {
      userId: user.sub,
      packageId: pkg.packageId,
      totalPrice: pkg.priceSingle * (selectedTravelers.length + newTravelers.length), // Calculate total price
      status: "PAYMENT_PENDING" as BookingStatus,
      bookingDate: new Date().toISOString(),
      travelers: [...selectedTravelers, ...newTravelers],
    };

    console.log("Booking Request:", bookingRequest);
    onSubmit(bookingRequest);
  };

  return (
    <div className="booking-form">
      <h2>Booking for: {pkg.name}</h2>
      <select onChange={(e) => handleTravelerSelect(e.target.value)} className="traveler-select">
        <option value="">Select Traveler</option>
        {travelers.map((traveler) => (
          <option key={traveler.travelerId} value={traveler.travelerId}>
            {traveler.firstName} {traveler.lastName}
          </option>
        ))}
      </select>
      {selectedTravelers.map((traveler) => (
        <div key={traveler.travelerId} className="traveler-info">
          <p><strong>First Name:</strong> {traveler.firstName}</p>
          <p><strong>Last Name:</strong> {traveler.lastName}</p>
          <p><strong>Email:</strong> {traveler.email}</p>
          <p><strong>Address Line 1:</strong> {traveler.addressLine1}</p>
        </div>
      ))}
      {newTravelers.map((traveler, index) => (
        <div key={index} className="new-traveler">
          <input
            type="text"
            placeholder="First Name"
            value={traveler.firstName}
            onChange={(e) => handleTravelerChange(index, "firstName", e.target.value)}
            className="traveler-input"
          />
          <input
            type="text"
            placeholder="Last Name"
            value={traveler.lastName}
            onChange={(e) => handleTravelerChange(index, "lastName", e.target.value)}
            className="traveler-input"
          />
          <input
            type="email"
            placeholder="Email"
            value={traveler.email}
            onChange={(e) => handleTravelerChange(index, "email", e.target.value)}
            className="traveler-input"
          />
          <input
            type="text"
            placeholder="Address Line 1"
            value={traveler.addressLine1}
            onChange={(e) => handleTravelerChange(index, "addressLine1", e.target.value)}
            className="traveler-input"
          />
          <input
            type="text"
            placeholder="Address Line 2"
            value={traveler.addressLine2}
            onChange={(e) => handleTravelerChange(index, "addressLine2", e.target.value)}
            className="traveler-input"
          />
        </div>
      ))}
      <button onClick={handleAddTraveler} className="add-traveler-button">Add Traveler</button>
      {error && <p className="error-message">{error}</p>}
      <button onClick={handleSubmit} className="submit-button">Confirm Booking</button>
    </div>
  );
};

export default BookingForm;
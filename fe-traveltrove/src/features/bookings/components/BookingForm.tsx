import React, { useState, useEffect } from "react";
import { useTravelersApi } from "../../travelers/api/traveler.api";
import { useAuth0 } from "@auth0/auth0-react";
import { useUsersApi } from "../../users/api/users.api";
import {
  TravelerRequestModel,
  TravelerResponseModel,
} from "../../travelers/model/traveler.model";
import { BookingStatus } from "../../bookings/models/bookings.model";

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

  useEffect(() => {
    // Fetch travelers when the component mounts
    const fetchTravelers = async () => {
      if (user?.sub) {
        const userResponseModel = await syncUser(user.sub);
        const travelerIds = userResponseModel.travelerIds || [];
        const travelers = await Promise.all(
          travelerIds.map((travelerId: string) => getTravelerById(travelerId))
        );
        setTravelers(travelers);
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
      console.error("User ID is missing");
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

    onSubmit(bookingRequest);
  };

  return (
    <div>
      <h2>Booking for: {pkg.name}</h2>
      <select onChange={(e) => handleTravelerSelect(e.target.value)}>
        <option value="">Select Traveler</option>
        {travelers.map((traveler) => (
          <option key={traveler.travelerId} value={traveler.travelerId}>
            {traveler.firstName} {traveler.lastName}
          </option>
        ))}
      </select>
      {selectedTravelers.map((traveler) => (
        <div key={traveler.travelerId}>
          <p>
            {traveler.firstName} {traveler.lastName}
          </p>
          <p>{traveler.email}</p>
        </div>
      ))}
      {newTravelers.map((traveler, index) => (
        <div key={index}>
          <input
            type="text"
            placeholder="First Name"
            value={traveler.firstName}
            onChange={(e) => handleTravelerChange(index, "firstName", e.target.value)}
          />
          <input
            type="text"
            placeholder="Last Name"
            value={traveler.lastName}
            onChange={(e) => handleTravelerChange(index, "lastName", e.target.value)}
          />
          <input
            type="email"
            placeholder="Email"
            value={traveler.email}
            onChange={(e) => handleTravelerChange(index, "email", e.target.value)}
          />
          <input
            type="text"
            placeholder="Address Line 1"
            value={traveler.addressLine1}
            onChange={(e) => handleTravelerChange(index, "addressLine1", e.target.value)}
          />
          <input
            type="text"
            placeholder="Address Line 2"
            value={traveler.addressLine2}
            onChange={(e) => handleTravelerChange(index, "addressLine2", e.target.value)}
          />
        </div>
      ))}
      <button onClick={handleAddTraveler}>Add Traveler</button>
      <button onClick={handleSubmit}>Confirm Booking</button>
    </div>
  );
};

export default BookingForm;
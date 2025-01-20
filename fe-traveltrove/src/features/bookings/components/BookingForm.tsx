import React, { useState, useEffect } from "react";
import { useBookingsApi } from "../api/bookings.api";
import { useTravelersApi } from "../../travelers/api/traveler.api";
import { useAuth0 } from "@auth0/auth0-react";
import axios from "axios";
import { useUsersApi } from "../../users/api/users.api";
import {
  TravelerRequestModel,
  TravelerResponseModel,
} from "../../travelers/model/traveler.model";
import { BookingStatus } from "../../bookings/models/bookings.model"; // Adjust the import path as needed

interface BookingFormProps {
  pkg: any; // Replace 'any' with the actual type if available
}

const BookingForm: React.FC<BookingFormProps> = ({ pkg }) => {
  const { createBooking } = useBookingsApi();
  const { getTravelerById } = useTravelersApi();
  const { user } = useAuth0();
  const { syncUser } = useUsersApi();
  const [travelers, setTravelers] = useState<TravelerResponseModel[]>([]);
  const [selectedTravelers, setSelectedTravelers] = useState<
    TravelerResponseModel[]
  >([]);
  const [newTravelers, setNewTravelers] = useState<TravelerRequestModel[]>([]);

  useEffect(() => {
    // Fetch travelers when the component mounts
    const fetchTravelers = async () => {
      if (user) {
        if (user.sub) {
          console.log("Fetching user data for user ID:", user.sub);
          const userResponseModel = await syncUser(user.sub);
          console.log("User data fetched:", userResponseModel);
          const travelerIds = userResponseModel.travelerIds;
          console.log("Traveler IDs from userResponseModel:", travelerIds);
          if (travelerIds) {
            console.log("Traveler IDs found:", travelerIds);
            const travelers = await Promise.all(
              travelerIds.map((travelerId: string) =>
                getTravelerById(travelerId)
              )
            );
            console.log("Travelers fetched:", travelers);
            setTravelers(travelers);
          } else {
            console.log("No traveler IDs found for user.");
          }
        }
      }
    };
    if (user) {
      fetchTravelers();
    }
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

  const handleBookingSubmit = async () => {
    if (!user?.sub) {
      console.error("User ID is missing");
      return;
    }

    const bookingRequest = {
      userId: user.sub,
      packageId: pkg.packageId, // Use the actual package ID
      totalPrice: pkg.priceSingle, // Replace with actual total price calculation
      status: "PAYMENT_PENDING" as BookingStatus,
      bookingDate: new Date().toISOString(),
      travelers: [...selectedTravelers, ...newTravelers],
    };

    try {
      await createBooking(bookingRequest);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error(
          `Axios error [${error.response?.status}]: ${error.message}`
        );
      } else {
        console.error("Unhandled error:", error);
      }
    }
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
            onChange={(e) =>
              handleTravelerChange(index, "firstName", e.target.value)
            }
          />
          <input
            type="text"
            placeholder="Last Name"
            value={traveler.lastName}
            onChange={(e) =>
              handleTravelerChange(index, "lastName", e.target.value)
            }
          />
          <input
            type="email"
            placeholder="Email"
            value={traveler.email}
            onChange={(e) =>
              handleTravelerChange(index, "email", e.target.value)
            }
          />
          <input
            type="text"
            placeholder="Address Line 1"
            value={traveler.addressLine1}
            onChange={(e) =>
              handleTravelerChange(index, "addressLine1", e.target.value)
            }
          />
          <input
            type="text"
            placeholder="Address Line 2"
            value={traveler.addressLine2}
            onChange={(e) =>
              handleTravelerChange(index, "addressLine2", e.target.value)
            }
          />
          {/* <input
            type="text"
            placeholder="City"
            value={traveler.city}
            onChange={(e) =>
              handleTravelerChange(index, "city", e.target.value)
            }
          />
          <input
            type="text"
            placeholder="State"
            value={traveler.state}
            onChange={(e) =>
              handleTravelerChange(index, "state", e.target.value)
            }
          />
          <input
            type="text"
            placeholder="Country ID"
            value={traveler.countryId}
            onChange={(e) =>
              handleTravelerChange(index, "countryId", e.target.value)
            } 
          />
           Add other fields as needed */}
        </div>
      ))}
      <button onClick={handleAddTraveler}>Add Traveler</button>
      <button onClick={handleBookingSubmit}>Confirm Booking</button>
    </div>
  );
};

export default BookingForm;

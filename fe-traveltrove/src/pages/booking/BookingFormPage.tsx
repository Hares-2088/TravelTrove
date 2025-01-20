import React from "react";
import { useLocation } from "react-router-dom";
import BookingForm from "../../features/bookings/components/BookingForm";

const BookingFormPage = () => {
  const location = useLocation();
  const pkg = location.state?.package;

  return (
    <div>
      <h1>Booking Form</h1>
      <BookingForm pkg={pkg} />
    </div>
  );
};

export default BookingFormPage;

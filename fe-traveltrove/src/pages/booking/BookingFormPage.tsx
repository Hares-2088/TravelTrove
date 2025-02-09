import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import BookingForm from "../../features/bookings/components/BookingForm";
import { useAxiosInstance } from "../../shared/axios/useAxiosInstance";
import { loadStripe } from "@stripe/stripe-js";

const stripePromise = loadStripe(process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || "");

const BookingFormPage: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const [confirmationMessage, setConfirmationMessage] = useState<string | null>(null);
  const location = useLocation();
  const pkg = location.state?.package;

  const axiosInstance = useAxiosInstance();

  const createCheckoutSession = async (bookingRequest: any, numberOfTravelers: number, bookingId: string) => {
    try {
      if (!pkg) {
        setError("No package selected.");
        return;
      }

      // Calculate total price: price per traveler * number of travelers (converted to cents)
      const amountInCents = Math.round(pkg.priceSingle * numberOfTravelers * 100);
      const feBaseURL = "https://sea-lion-app-36zpz.ondigitalocean.app";

      // Send the bookingId along with other details to the payments endpoint
      const response = await axiosInstance.post("payments/create-checkout-session", {
        amount: amountInCents,
        currency: "usd",
        packageId: pkg.packageId,
        successUrl: `${feBaseURL}/payment-success`, 
        cancelUrl: `${feBaseURL}/payment-cancelled`,   
        bookingId: bookingId,
        travelers: bookingRequest.travelers,
      });

      const { sessionId } = response.data;
      const stripe = await stripePromise;
      if (!stripe) throw new Error("Stripe failed to load");

      // Redirect to Stripe Checkout
      const { error: stripeError } = await stripe.redirectToCheckout({
        sessionId: sessionId,
      });

      if (stripeError) {
        console.error("Stripe Checkout error:", stripeError);
        setError(stripeError.message || "An unknown error occurred.");
      }
    } catch (err) {
      console.error("Error creating checkout session:", err);
      setError("Failed to create checkout session. Please try again.");
    }
  };

  const handleBookingSubmit = async (bookingRequest: any) => {
    try {
      // 1. Pre-create the booking record with status PAYMENT_PENDING.
      //    This call should return a bookingId.
      const bookingResponse = await axiosInstance.post("bookings", bookingRequest);
      const bookingId = bookingResponse.data.bookingId;

      // 2. Determine the number of travelers.
      const numberOfTravelers = bookingRequest.travelers.length;

      // 3. Create the payment session using the pre-created booking's ID.
      await createCheckoutSession(bookingRequest, numberOfTravelers, bookingId);
    } catch (error) {
      console.error("Error during booking process:", error);
      setError("Failed to proceed with booking. Please try again.");
    }
  };

  return (
    <div className="container min-vh-100 d-flex align-items-center justify-content-center">
      <div className="row w-100 shadow-lg p-4 m-1 bg-white rounded">
        {pkg && (
          <div className="col-md-6 p-3">
            <h2 className="text-primary">{pkg.name}</h2>
            <p className="text-muted fst-italic">{pkg.description}</p>
            <p><strong>📅 Dates:</strong> {pkg.startDate} - {pkg.endDate}</p>
            <p><strong>💲 Price:</strong> ${pkg.priceSingle}</p>
            <p><strong>🎟 Seats:</strong> {pkg.availableSeats} available</p>
            <p className={`fw-bold ${pkg.status === 'BOOKING_OPEN' ? 'text-success' : 'text-danger'}`}>📌 Status: {pkg.status.replace('_', ' ')}</p>
          </div>
        )}
        <div className="col-md-6 p-3 border-start">
          <h3 className="text-center">Booking Form</h3>
          <BookingForm pkg={pkg} onSubmit={handleBookingSubmit} />
          {confirmationMessage && <p className="mt-4 text-success text-center fw-bold">{confirmationMessage}</p>}
          {error && <p className="mt-4 text-danger text-center fw-bold">{error}</p>}
        </div>
      </div>
    </div>
  );
};

export default BookingFormPage;
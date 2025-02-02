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
      const feBaseURL = "http://localhost:3000"
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
      //  Pre-create the booking record with status PAYMENT_PENDING.
      //  This call should return a bookingId.
      const bookingResponse = await axiosInstance.post("bookings", bookingRequest);
      const bookingId = bookingResponse.data.bookingId;

      // Determine the number of travelers.
      const numberOfTravelers = bookingRequest.travelers.length;

      // Create the payment session using the pre-created booking's ID.
      await createCheckoutSession(bookingRequest, numberOfTravelers, bookingId);
    } catch (error) {
      console.error("Error during booking process:", error);
      setError("Failed to proceed with booking. Please try again.");
    }
  };

  return (
    <div>
      <h1>Booking Form</h1>
      <BookingForm pkg={pkg} onSubmit={handleBookingSubmit} />
      {confirmationMessage && <p style={{ color: "green" }}>{confirmationMessage}</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default BookingFormPage;

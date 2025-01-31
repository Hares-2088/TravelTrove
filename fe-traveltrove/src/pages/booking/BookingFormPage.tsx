import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import BookingForm from "../../features/bookings/components/BookingForm";
import { useAxiosInstance } from "../../shared/axios/useAxiosInstance";
import { loadStripe } from "@stripe/stripe-js";

// Initialize Stripe outside of the component
const stripePromise = loadStripe(
  process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || ""
);

const BookingFormPage: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const location = useLocation();
  const pkg = location.state?.package;

  // Use your custom Axios instance that attaches the Auth0 token
  const axiosInstance = useAxiosInstance();

  const createCheckoutSession = async () => {
    try {
      // 1) Call your backend to create a Stripe Checkout Session
      const response = await axiosInstance.post("payments/create-checkout-session", {
        amount: 1000, // e.g. pkg.price * 100
        currency: "usd",
        packageId: 123, // e.g. pkg.id
        successUrl: "https://github.com/",
        cancelUrl: "https://google.com/",
      });

      // 2) Immediately redirect to Stripe's checkout page
      const { sessionId } = response.data;
      const stripe = await stripePromise;
      if (!stripe) {
        throw new Error("Stripe failed to load");
      }

      // Use the Stripe.js SDK to redirect
      const { error: stripeError } = await stripe.redirectToCheckout({
        sessionId: sessionId,
      });

      if (stripeError) {
        console.error("Stripe Checkout error:", stripeError);
        // setError(stripeError.message);
      }
    } catch (err) {
      console.error("Error creating checkout session:", err);
      setError("Failed to create checkout session. Please try again.");
    }
  };

  return (
    <div>
      <h1>Booking Form</h1>
      <BookingForm pkg={pkg} />

      {/* Single button to create the session and auto-redirect */}
      <button onClick={createCheckoutSession}>Proceed to Payment</button>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default BookingFormPage;

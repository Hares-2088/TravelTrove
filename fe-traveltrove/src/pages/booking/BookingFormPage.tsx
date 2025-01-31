import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import BookingForm from "../../features/bookings/components/BookingForm";
import { useAxiosInstance } from "../../shared/axios/useAxiosInstance";
import { loadStripe } from "@stripe/stripe-js";

const stripePromise = loadStripe(
  process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || ""
);

const BookingFormPage: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const location = useLocation();
  const pkg = location.state?.package;

  const axiosInstance = useAxiosInstance();
  

  const createCheckoutSession = async () => {
    try {
      if (!pkg) {
        setError("No package selected.");
        return;
      }

      // Convert price to cents because Stripe said so.
      const amountInCents = Math.round(pkg.priceSingle * 100);

      const response = await axiosInstance.post("payments/create-checkout-session", {
        amount: amountInCents,
        currency: "usd", 
        packageId: pkg.packageId, 
        successUrl: "https://youtube.com",
        cancelUrl: "https://github.com",
      });

      const { sessionId } = response.data;
      const stripe = await stripePromise;
      if (!stripe) throw new Error("Stripe failed to load");

      // Use the Stripe.js SDK to redirect
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

  return (
    <div>
      <h1>Booking Form</h1>
      <BookingForm pkg={pkg} /> 

      <button onClick={createCheckoutSession}>Proceed to Payment</button>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default BookingFormPage;

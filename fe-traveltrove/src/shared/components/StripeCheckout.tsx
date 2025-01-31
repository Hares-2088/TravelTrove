// import React, { useEffect } from "react";
// import { loadStripe } from "@stripe/stripe-js";

// const stripePromise = loadStripe(process.env.REACT_APP_STRIPE_PUBLISHABLE_KEY || "");

// interface StripeCheckoutProps {
//   sessionId: string;
// }

// const StripeCheckout: React.FC<StripeCheckoutProps> = ({ sessionId }) => {
//   useEffect(() => {
//     // Log sessionId to verify it's being passed correctly
//     console.log("Session ID received:", sessionId);
//   }, [sessionId]);

//   const handleCheckout = async () => {
//     try {
//       const stripe = await stripePromise;
//       if (!stripe) {
//         throw new Error("Stripe failed to load");
//       }

//       const { error } = await stripe.redirectToCheckout({ sessionId });
//       if (error) {
//         console.error("Stripe Checkout error:", error);
//         throw error;
//       }
//     } catch (error) {
//       console.error("Checkout error:", error);
//     }
//   };

//   return (
//     <button 
//       onClick={handleCheckout}
//       className="payment-button"
//     >
//       Pay Now
//     </button>
//   );
// };

// export default StripeCheckout;
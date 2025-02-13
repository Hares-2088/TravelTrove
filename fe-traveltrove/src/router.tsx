import { createBrowserRouter, Navigate } from "react-router-dom";
import { AppRoutes } from "./shared/models/app.routes";
import ProtectedRoute from "./shared/components/ProtectedRoute";
import DashboardPage from "./pages/staff/DashboardPage";
import ToursPage from "./pages/tours/ToursPage";
import TourDetailsPage from "./pages/tours/TourDetailsPage";
import HomePage from "./pages/home/HomePage";
import Layout from "./layouts/Layout";
import CallbackPage from "./pages/CallbackPage";
import UnauthorizedPage from "./pages/errors/UnauthorizedPage";
import NotFoundPage from "./pages/errors/NotFoundPage";
import ServiceUnavailablePage from "./pages/errors/ServiceNotAvailablePage";
import InternalServerErrorPage from "./pages/errors/InternalServerErrorPage";
import RequestTimeoutPage from "./pages/errors/RequestTimeoutPage";
import ForbiddenPage from "./pages/errors/ForbiddenPage";
import Bookings from "./features/staff/components/Pages/Bookings";
import ProfileCreatePage from "./pages/user/ProfileCreatePage";
import PackageDetailsPage from "./pages/packages/PackageDetailsPage";
import BookingFormPage from "./pages/booking/BookingFormPage";
import UserManagementPage from "./pages/staff/UserManagementPage";
import UsersDetail from "./features/users/components/UsersDetail";
import PaymentSuccessPage from "./pages/booking/PaymentSuccessPage";
import PaymentCancel from "./pages/booking/PaymentCancel";
import ContactUsPage from "./pages/ContactUsPage";

const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      {
        path: AppRoutes.Home,
        element: <HomePage />,
      },
      {
        path: AppRoutes.ContactUs,
        element: <ContactUsPage />,
      },
      {
        path: AppRoutes.ToursPage,
        element: (
          <ToursPage />
        ),
      },
      {
        path: AppRoutes.TourDetailsPage,
        element: (
          <TourDetailsPage />
        ),
      },
      {
        path: AppRoutes.Dashboard,
        element: (
          <ProtectedRoute requiredRoles={["Admin", "Employee"]}>
            <DashboardPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Bookings,
        element: (
          <ProtectedRoute requiredRoles={["Customer"]}>
            <Bookings />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.PackageDetailsPage,
        element: (
          <ProtectedRoute>
            <PackageDetailsPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.UserManagementPage,
        element: (
          <ProtectedRoute requiredRoles={["Admin"]}>
            <UserManagementPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.UsersDetail,
        element: (
          <ProtectedRoute requiredRoles={["Admin"]}>
            <UsersDetail />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.BookingFormPage,
        element: (
          <ProtectedRoute requiredRoles={["Customer"]}>
            <BookingFormPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.PaymentSuccessPage,
        element:(
          <ProtectedRoute requiredRoles={["Customer"]}>
            <PaymentSuccessPage />
          </ProtectedRoute>
        )
      },
      {
        path: AppRoutes.PaymentCancel,
        element:(
          <ProtectedRoute requiredRoles={["Customer"]}>
            <PaymentCancel />
          </ProtectedRoute>
        )
      },
      {
        path: AppRoutes.ProfileCreatePage,
        element: (
          <ProtectedRoute>
            <ProfileCreatePage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Unauthorized,
        element: (
          <ProtectedRoute>
            <UnauthorizedPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Forbidden,
        element: (
          <ProtectedRoute>
            <ForbiddenPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.RequestTimeout,
        element: (
          <ProtectedRoute>
            <RequestTimeoutPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.InternalServerError,
        element: (
          <ProtectedRoute>
            <InternalServerErrorPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.ServiceUnavailable,
        element: (
          <ProtectedRoute>
            <ServiceUnavailablePage />
          </ProtectedRoute>
        ),
      },

      {
        path: AppRoutes.Default,
        element: <Navigate to={AppRoutes.Home} replace />,
      },
      {
        path: "*",
        element: <NotFoundPage />,
      },
    ],
  },
  {
    path: AppRoutes.Callback,
    element: (
      <ProtectedRoute>
        <CallbackPage />
      </ProtectedRoute>
    ),
  },
]);

export default router;


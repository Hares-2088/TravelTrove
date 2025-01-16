import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppRoutes } from './shared/models/app.routes';
import ProtectedRoute from './shared/components/ProtectedRoute';
import DashboardPage from './pages/staff/DashboardPage';
import ToursPage from './pages/tours/ToursPage';
import TourDetailsPage from './pages/tours/TourDetailsPage';
import HomePage from './pages/home/HomePage';
import Layout from './layouts/Layout';
import CallbackPage from './pages/CallbackPage';
import UnauthorizedPage from './pages/errors/UnauthorizedPage';
import NotFoundPage from './pages/errors/NotFoundPage';
import ServiceUnavailablePage from './pages/errors/ServiceNotAvailablePage';
import InternalServerErrorPage from './pages/errors/InternalServerErrorPage';
import RequestTimeoutPage from './pages/errors/RequestTimeoutPage';
import ForbiddenPage from './pages/errors/ForbiddenPage';
import Bookings from './features/staff/components/Pages/Bookings';
import ProfileCreatePage from "./pages/user/ProfileCreatePage.tsx"; // Import the Bookings component

const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      {
        path: AppRoutes.Home,
        element: <HomePage />,
      },
      {
        path: AppRoutes.ToursPage,
        element: (
          <ProtectedRoute requiredRoles={["employee", "admin"]}>
            <ToursPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.TourDetailsPage,
        element: (
          <ProtectedRoute requiredRoles={["employee", "admin"]}>
            <TourDetailsPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Dashboard,
        element: (
          <ProtectedRoute requiredRoles={["employee", "admin"]}>
            <DashboardPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Bookings, 
        element: (
          <ProtectedRoute requiredRoles={["employee", "admin"]}>
            <Bookings />
          </ProtectedRoute>
        ),
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
        element: <NotFoundPage />
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

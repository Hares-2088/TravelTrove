import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppRoutes } from './shared/models/app.routes';
import { ProtectedRoute } from './shared/components/ProtectedRoute';
import DashboardPage from './pages/staff/DashboardPage';
import ToursPage from './pages/tours/ToursPage';
import TourDetailsPage from './pages/tours/TourDetailsPage';
import HomePage from './pages/home/HomePage';

const router = createBrowserRouter([
  {
    children: [
      {
        path: AppRoutes.Home,
        element: (
          <ProtectedRoute>
            <HomePage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.ToursPage,
        element: (
          <ProtectedRoute>
            <ToursPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.TourDetailsPage,
        element: (
          <ProtectedRoute>
            <TourDetailsPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Dashboard,
        element: (
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        ),
      },
      {
        path: AppRoutes.Default,
        element: <Navigate to={AppRoutes.Home} replace />,
      },
    ],
  },
]);

export default router;

import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppRoutes } from './shared/models/app.routes';
import { ProtectedRoute } from './shared/components/ProtectedRoute';
import DashboardPage from './pages/staff/DashboardPage';
import ToursPage from './pages/tours/ToursPage';
import TourDetailsPage from './pages/tours/TourDetailsPage';
import HomePage from './pages/home/HomePage';
import Layout from './layouts/Layout';

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

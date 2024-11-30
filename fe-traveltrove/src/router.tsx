import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppRoutes } from './shared/models/app.routes';
import { ProtectedRoute } from './shared/components/ProtectedRoute';
import ToursPage from '../src/pages/tours/ToursPage';
import HomePage from '../src/pages/home/HomePage';
import TourDetailsPage from './pages/tours/TourDetailsPage';

const router = createBrowserRouter([
  {
    children: [
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
        path: AppRoutes.Home,
        element: (
          <ProtectedRoute>
            <HomePage />
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

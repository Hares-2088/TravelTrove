export enum AppRoutes {
  Default = '/',
  Home = '/home',
  ToursPage = '/tours',
  TourDetailsPage = '/tours/:tourId',
  TourEventsDetailPage = '/tour-events/:toursEventId',
  PackageDetailsPage = '/packages/:packageId',
  BookingFormPage = '/bookingform',
  Dashboard = '/dashboard',
  CountryDetailsPage = '/countries/:countryId',
  CityDetailsPage = '/cities/:cityId',
  Register = '/register',
  ProfilePage = '/profile',
  ProfileCreatePage = '/profile/create',
  Bookings = '/bookings',
  UserManagementPage = '/usermanagement',
  UsersDetail = '/users/:userId',
  PaymentSuccessPage = '/payment-success',
  Callback = '/callback',


  // Error Routes
  Unauthorized = '/unauthorized',
  Forbidden = '/forbidden',
  RequestTimeout = '/request-timeout',
  InternalServerError = '/internal-server-error',
  ServiceUnavailable = '/service-unavailable',
  PaymentCancel = '/payment-cancelled'
}

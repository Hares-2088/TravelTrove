import React from 'react';
import NavBar from '../../layouts/NavBar';
import Footer from '../../layouts/Footer';
import TourDetails from '../../features/tours/components/TourDetails';

const TourDetailsPage: React.FC = () => {
  return (
    <div className="tour-details-page">
      <NavBar />
      <TourDetails />
      <Footer />
    </div>
  );
};

export default TourDetailsPage;

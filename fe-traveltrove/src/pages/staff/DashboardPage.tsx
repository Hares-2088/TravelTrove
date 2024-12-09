import React from 'react';
import NavBar from '../../layouts/NavBar';
import Footer from '../../layouts/Footer';
import Dashboard from '../../features/staff/components/Dashboard';

const DashboardPage: React.FC = () => {
  return (
    <div>
      <NavBar />
      <Dashboard />
      <Footer />
    </div>
  );
};

export default DashboardPage;

import React from 'react';
import Dashboard from '../../features/staff/components/Dashboard';
//import useAuthorization from '../../context/useAuthorization';

const DashboardPage: React.FC = () => {
  //const { isAuthorized } = useAuthorization(["admin", "employee"]);
  
  //if (!isAuthorized) return null; // Prevents flickering before redirect in the useAuthorization hook

  return (
    <div>
      <Dashboard />
    </div>
  );
};

export default DashboardPage;

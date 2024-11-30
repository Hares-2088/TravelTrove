import React, { FC } from 'react';
import NavBar from '../../layouts/NavBar';
import HomeDetails from '../../features/home/components/HomeDetails';
import Footer from '../../layouts/Footer';

const HomePage: FC = (): JSX.Element => {
  return (
    <div style={{ backgroundColor: '#FFF5F0', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <NavBar />
      <div style={{ flex: 1 }}>
        <HomeDetails />
      </div>
      <Footer />
    </div>
  );
};

export default HomePage;

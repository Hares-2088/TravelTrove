import React, { FC } from 'react';
import HomeDetails from '../../features/home/components/HomeDetails';

const HomePage: FC = (): JSX.Element => {
  return (
    <div style={{ backgroundColor: '#FFF5F0', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <div style={{ flex: 1 }}>
        <HomeDetails />
      </div>
    </div>
  );
};

export default HomePage;

import React, { FC } from 'react';
import HomeDetails from '../../features/home/components/HomeDetails';

const HomePage: FC = (): JSX.Element => {
  return (
    <div
      style={{
        backgroundColor: '#FFF5F0',
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <div style={{ flex: 1 }}>
        <HomeDetails />
        <div
          className="elfsight-app-6d3f48d9-c5ae-4dae-8574-3ea7f9eb1362"
          data-elfsight-app-lazy
        ></div>
      </div>
    </div>
  );
};

export default HomePage;

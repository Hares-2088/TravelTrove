import React, { FC, useEffect } from 'react';
import ToursList from '../../features/tours/components/ToursList';

const ToursPage: FC = (): JSX.Element => {
  useEffect(() => {
    console.log("🚀 ToursPage rendered");
  });

  return (
    <div style={{ backgroundColor: '#FFF5F0', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '2rem', flex: 1 }}>
        <ToursList />
      </div>
    </div>
  );
};

export default React.memo(ToursPage);

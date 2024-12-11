import { FC } from 'react';
import ToursList from '../../features/tours/components/ToursList';

const ToursPage: FC = (): JSX.Element => {
  return (
    <div style={{ backgroundColor: '#FFF5F0', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '2rem', flex: 1 }}>
        <ToursList />
      </div>
    </div>
  );
};

export default ToursPage;

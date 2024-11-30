import { FC } from 'react';
import NavBar from '../../layouts/NavBar';
import ToursList from '../../features/tours/components/ToursList';
import Footer from '../../layouts/Footer';

const ToursPage: FC = (): JSX.Element => {
  return (
    <div style={{ backgroundColor: '#FFF5F0', minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <NavBar />
      <div style={{ padding: '2rem', flex: 1 }}>
        <ToursList />
      </div>
      <Footer />
    </div>
  );
};

export default ToursPage;

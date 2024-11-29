import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ToursPage from './pages/ToursPage';
import NavBar from './context/NavBar';


function App() {
  return (
    <Router>
      <NavBar />
      <Routes>
        <Route path="/tours" element={<ToursPage />} />
      </Routes>
    </Router>
  );
}

export default App;

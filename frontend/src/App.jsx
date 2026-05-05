import { Routes, Route } from 'react-router-dom';
import Navbar from './components/common/Navbar';
import CustomerHome from './pages/customer/Home';
import VendorDashboard from './pages/vendor/Dashboard';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<CustomerHome />} />
        <Route path="/vendor/dashboard" element={<VendorDashboard />} />
      </Routes>
    </>
  );
}

export default App;
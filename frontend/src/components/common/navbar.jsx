import { Link } from 'react-router-dom';

const Navbar = () => {
  const user = JSON.parse(localStorage.getItem('user')); // Role: CUSTOMER, VENDOR, DELIVERY

  return (
    <nav className="bg-white shadow-md p-4 flex justify-between items-center sticky top-0 z-50">
      <Link to="/" className="text-2xl font-bold text-orange-600">FoodieExpress</Link>
      <div className="space-x-6 font-medium">
        {user?.role === 'VENDOR' ? (
          <Link to="/vendor/dashboard" className="hover:text-orange-600">Dashboard</Link>
        ) : user?.role === 'DELIVERY' ? (
          <Link to="/delivery/orders" className="hover:text-orange-600">My Deliveries</Link>
        ) : (
          <>
            <Link to="/" className="hover:text-orange-600">Home</Link>
            <Link to="/cart" className="hover:text-orange-600">Cart</Link>
          </>
        )}
        <Link to="/login" className="bg-orange-600 text-white px-4 py-2 rounded-lg">Login</Link>
      </div>
    </nav>
  );
};

export default Navbar;
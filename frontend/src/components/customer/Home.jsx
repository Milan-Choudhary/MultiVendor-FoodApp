import React, { useState, useEffect } from 'react';
import api from '../../services/api';

const CustomerHome = () => {
  const [restaurants, setRestaurants] = useState([]);

  useEffect(() => {
    // Backend endpoint: /api/restaurants
    api.get('/restaurants').then(res => setRestaurants(res.data)).catch(err => console.log(err));
  }, []);

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="mb-8 p-10 bg-orange-100 rounded-2xl flex flex-col items-center">
        <h1 className="text-4xl font-black text-gray-800">Hungry? 🍕</h1>
        <p className="text-gray-600 mt-2">Order from your favorite restaurants near you.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {restaurants.map(rest => (
          <div key={rest.id} className="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
            <img src={rest.image || 'https://via.placeholder.com/400x250'} className="w-full h-48 object-cover" alt={rest.name} />
            <div className="p-4">
              <h3 className="text-xl font-bold">{rest.name}</h3>
              <p className="text-gray-500 text-sm">{rest.cuisine}</p>
              <div className="mt-4 flex justify-between items-center">
                <span className="font-bold text-orange-600">₹{rest.avgPrice} for two</span>
                <button className="bg-gray-800 text-white px-4 py-2 rounded-lg text-sm">View Menu</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CustomerHome;
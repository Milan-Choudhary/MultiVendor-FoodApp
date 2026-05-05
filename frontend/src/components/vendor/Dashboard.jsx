const VendorDashboard = () => {
  return (
    <div className="p-8">
      <header className="flex justify-between items-center mb-10">
        <h1 className="text-3xl font-bold">Manage Restaurant</h1>
        <button className="bg-green-600 text-white px-6 py-2 rounded-lg">+ Add New Item</button>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-white p-6 rounded-xl shadow-sm border">
          <h2 className="font-bold text-xl mb-4">Incoming Orders</h2>
          <div className="space-y-4">
            {/* Example Order Row */}
            <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
              <div>
                <p className="font-bold">Order #5521</p>
                <p className="text-sm text-gray-500">2x Paneer Tikka, 1x Naan</p>
              </div>
              <div className="flex space-x-2">
                <button className="bg-orange-500 text-white px-4 py-1 rounded">Accept</button>
                <button className="text-red-500 font-medium">Reject</button>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm border h-fit">
          <h2 className="font-bold text-xl mb-4">Earnings</h2>
          <p className="text-4xl font-black text-green-600">₹12,450</p>
          <p className="text-gray-500 mt-2">Total sales this week</p>
        </div>
      </div>
    </div>
  );
};

export default VendorDashboard;
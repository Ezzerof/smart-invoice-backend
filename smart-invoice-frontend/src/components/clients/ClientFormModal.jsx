import React, { useState } from 'react';
import { createClient } from '../../api/clientApi';

export default function ClientFormModal({ isOpen, onClose, onClientCreated }) {
  // State for form data with all required client fields
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    companyName: '',
    address: '',
    city: '',
    country: '',
    postcode: ''
  });
  
  // Loading state for form submission
  const [loading, setLoading] = useState(false);
  
  // Error state for displaying API errors
  const [error, setError] = useState(null);

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null); // Reset error on new submission
    
    try {
      // Call API to create client
      const newClient = await createClient(formData);
      
      // Notify parent component about the new client
      onClientCreated(newClient);
      
      // Close the modal
      onClose();
      
      // Reset form after successful submission
      setFormData({
        name: '',
        email: '',
        companyName: '',
        address: '',
        city: '',
        country: '',
        postcode: ''
      });
    } catch (err) {
      // Display error to user
      setError(err.message || 'Failed to create client');
    } finally {
      // Reset loading state regardless of success/failure
      setLoading(false);
    }
  };

  // Handle input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Don't render if modal isn't open
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-lg w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">Add New Client</h2>
        
        {/* Display error message if exists */}
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 gap-4 mb-4">
            {/* Name Field */}
            <div>
              <label className="block text-gray-700 mb-1">Full Name</label>
              <input
                type="text"
                name="name"
                placeholder="John Doe"
                value={formData.name}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
            
            {/* Email Field */}
            <div>
              <label className="block text-gray-700 mb-1">Email</label>
              <input
                type="email"
                name="email"
                placeholder="john@example.com"
                value={formData.email}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
            
            {/* Company Name Field */}
            <div>
              <label className="block text-gray-700 mb-1">Company Name</label>
              <input
                type="text"
                name="companyName"
                placeholder="Acme Inc."
                value={formData.companyName}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
            
            {/* Address Field */}
            <div>
              <label className="block text-gray-700 mb-1">Address</label>
              <input
                type="text"
                name="address"
                placeholder="123 Main St"
                value={formData.address}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
            
            {/* City Field */}
            <div>
              <label className="block text-gray-700 mb-1">City</label>
              <input
                type="text"
                name="city"
                placeholder="New York"
                value={formData.city}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
            
            {/* Country Field */}
            <div>
              <label className="block text-gray-700 mb-1">Country</label>
              <input
                type="text"
                name="country"
                placeholder="United States"
                value={formData.country}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
            
            {/* Postcode Field */}
            <div>
              <label className="block text-gray-700 mb-1">Postal Code</label>
              <input
                type="text"
                name="postcode"
                placeholder="10001"
                value={formData.postcode}
                onChange={handleInputChange}
                className="w-full p-2 border rounded"
                required
              />
            </div>
          </div>
          
          {/* Form Actions */}
          <div className="flex justify-end space-x-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border rounded hover:bg-gray-100"
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:bg-blue-300"
            >
              {loading ? (
                <>
                  <span className="inline-block animate-spin mr-2">↻</span>
                  Saving...
                </>
              ) : 'Save Client'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
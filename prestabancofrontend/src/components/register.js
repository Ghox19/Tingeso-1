import React, { useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    lastName: '',
    rut: '',
    email: '',
    contact: '',
    mensualIncome: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      console.log('WAAA registered successfully:');
      const response = await axios.post(`${API_URL}/client`, formData);
      console.log('Client registered successfully:', response.data);
      // Handle successful registration (e.g., show success message, redirect)
    } catch (error) {
      console.error('Error registering client:', error);
      // Handle error (e.g., show error message)
    }
  };

  return (
    <div>
      <h1>Register</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Name:</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="lastName">Last Name:</label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="rut">RUT:</label>
          <input
            type="text"
            id="rut"
            name="rut"
            value={formData.rut}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="contact">Contact:</label>
          <input
            type="tel"
            id="contact"
            name="contact"
            value={formData.contact}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="mensualIncome">Monthly Income:</label>
          <input
            type="number"
            id="mensualIncome"
            name="mensualIncome"
            value={formData.mensualIncome}
            onChange={handleChange}
            required
          />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

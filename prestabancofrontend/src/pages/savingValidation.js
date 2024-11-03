import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';

export const SavingValidation = () => {
  const location = useLocation();

  const { id } = location.state || {};

  const [formData, setFormData] = useState({
    years: '',
    actualBalance: '',
    balances: Array(12).fill(''),
    deposit: Array(12).fill(''),
    withdraw: Array(12).fill('')
  });

  const months = [
    '1', '2', '3', '4', '5', '6',
    '7', '8', '9', '10', '11', '12'
  ];

  const handleInputChange = (e, index, field) => {
    const { value } = e.target;
    
    if (field === 'years' || field === 'actualBalance') {
      setFormData(prev => ({
        ...prev,
        [field]: value
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [field]: prev[field].map((item, i) => i === index ? value : item)
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí puedes enviar los datos al backend
    console.log(formData);
  };

  return (
    <div className="balance-form-container">
      <form onSubmit={handleSubmit}>
        <div className="header-inputs">
          <div className="input-group">
            <label>Año:</label>
            <input
              type="number"
              value={formData.years}
              onChange={(e) => handleInputChange(e, null, 'years')}
              required
            />
          </div>
          <div className="input-group">
            <label>Balance Actual:</label>
            <input
              type="number"
              value={formData.actualBalance}
              onChange={(e) => handleInputChange(e, null, 'actualBalance')}
              required
            />
          </div>
        </div>

        <div className="monthly-data-container">
          <table>
            <thead>
              <tr>
                <th>Mes</th>
                <th>Balance</th>
                <th>Depósito</th>
                <th>Retiro</th>
              </tr>
            </thead>
            <tbody>
              {months.map((month, index) => (
                <tr key={month}>
                  <td>{month}</td>
                  <td>
                    <input
                      type="number"
                      value={formData.balances[index]}
                      onChange={(e) => handleInputChange(e, index, 'balances')}
                      required
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      value={formData.deposit[index]}
                      onChange={(e) => handleInputChange(e, index, 'deposit')}
                      required
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      value={formData.withdraw[index]}
                      onChange={(e) => handleInputChange(e, index, 'withdraw')}
                      required
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <button type="submit" className="submit-button">
          Guardar Datos
        </button>
      </form>
    </div>
  );
};
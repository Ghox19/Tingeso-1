import React, { useState } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const CreditCalculator = () => {
  const [formData, setFormData] = useState({
    years: '',
    interest: '',
    loanAmount: ''
  });

  const [rut, setRut] = useState('');
  const [loanName, setLoanName] = useState('WENA');
  const [labelValue, setLabelValue] = useState('Valor obtenido');

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === 'rut') {
      setRut(value);
    } else {
      setFormData(prevState => ({
        ...prevState,
        [name]: value
      }));
    }
  };

  const handleCalculate = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${API_URL}/clientLoan/calculator`, formData);
      const calculatedValue = response.data.toString();
      setLabelValue(calculatedValue);
      console.log('Calculation successful:', calculatedValue);
      return calculatedValue; // Devuelve el valor calculado
    } catch (error) {
      console.error('Error calculating:', error);
      setLabelValue('Error en el cálculo');
      return null;
    }
  };
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (rut === '' || formData.years === '' || formData.interest === '' || formData.loanAmount === '') {
      console.log('Faltan datos');
      return;
    }
  
    let mensualPay = labelValue;
    if (mensualPay === 'Valor obtenido') {
      console.log('Falta calcular');
      mensualPay = await handleCalculate(e); // Espera el valor calculado
    }
  
    if (mensualPay === null) {
      console.log('Error en el cálculo');
      return;
    }
  
    const submitData = {
      rut: rut,
      loanName: loanName,
      years: formData.years,
      interest: formData.interest,
      loanAmount: formData.loanAmount,
      mensualPay: mensualPay, // Usa el valor calculado
      requirementsApproved: [false, false, false],
    };
  
    try {
      console.log('WAAA registered successfully:');
      const response = await axios.post(`${API_URL}/clientLoan`, submitData);
      console.log('Client registered successfully:', response.data);
    } catch (error) {
      console.error('Error registering client:', error);
    }
  };

  return (
    <div>
      <input 
        type='text'
        name='rut'
        placeholder='RUT' 
        value={rut} 
        onChange={handleInputChange} 
      />
      <label>{loanName}</label> 
      <input 
        type='number'
        name='years'
        placeholder='Años' 
        value={formData.years} 
        onChange={handleInputChange} 
      />
      <input 
        type='number'
        name='interest'
        placeholder='Interés' 
        value={formData.interest} 
        onChange={handleInputChange} 
      />
      <input 
        type='number'
        name='loanAmount'
        placeholder='Monto del Préstamo' 
        value={formData.loanAmount} 
        onChange={handleInputChange} 
      />
      <label>{labelValue}</label> 
      <button type="button" onClick={handleCalculate}>Calcular</button>
      <button type="submit" onClick={handleSubmit}>Ingresar</button>
    </div>
  );
};
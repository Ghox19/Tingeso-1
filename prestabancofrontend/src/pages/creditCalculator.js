import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const CreditCalculator = () => {
  const location = useLocation();

  const [formData, setFormData] = useState({
    years: '',
    interest: '',
    loanAmount: ''
  });

  const [rut, setRut] = useState('');
  const [loanName] = useState(location.state.name || '');
  const [propertyValue, setPropertyValue] = useState('');
  const [labelValue, setLabelValue] = useState('Valor obtenido');
  const [requirements, setRequirements] = useState(location.state.requirements || []);


  const [clientData, setClientData] = useState('');

  const [documentsValidations, setDocumentsValidations] = useState(
    requirements.map(() => false)
  );

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (name === 'rut') {
      setRut(value);
    } else if (name === 'propertyValue') {
      setPropertyValue(value);
    } else {
      setFormData(prevState => ({
        ...prevState,
        [name]: value
      }));
    }
  };

  const handleValidate = async (e) => {
    e.preventDefault();
    
    if (rut === '') {
      console.log('Falta el RUT');
      return;
    }
  
    try {
      const response = await axios.get(`${API_URL}/client/rinfo/${rut}`);
      setClientData(response.data);
      if (!response.data) {
        console.log('No se encontró el cliente', response.data);
        return response.data;
      }
      console.log('Client data:', response.data);
    } catch (error) {
      console.error('Error al obtener el sueldo:', error);
    }
  };

  const handleCalculate = async (e) => {
    e.preventDefault();

    // Validar que el monto del préstamo no exceda el porcentaje máximo permitido
    const maxAmountPercentage = location.state.maxAmount;
    const maxLoanAmount = (propertyValue * maxAmountPercentage) / 100;

    if (formData.loanAmount > maxLoanAmount) {
      console.log('El monto del préstamo excede el límite permitido.');
      setLabelValue('Monto del préstamo excede el límite');
      return;
    }

    try {
      const response = await axios.post(`${API_URL}/clientLoan/calculator`, formData);
      const calculatedValue = response.data.toString();
      setLabelValue(calculatedValue);
      console.log('Calculation successful:', calculatedValue);
      return calculatedValue;
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
      mensualPay = await handleCalculate(e);
    }

    if (mensualPay === null) {
      console.log('Error en el cálculo');
      return;
    }

    if (clientData === '') {
      console.log('Falta validar el RUT');
      return;
    }

    const cuotaIncome = (mensualPay / clientData.mensualIncome) * 100;
    console.log('Cuota mensual:', cuotaIncome);
    if (cuotaIncome > 35) {
      console.log('La cuota excede el 35% del sueldo');
      return;
    }

    if (clientData.jobYears < 1) {
      console.log('El cliente no tiene suficiente antigüedad laboral');
      return;
    }

    const debtCuota = (clientData.totalDebt + mensualPay / clientData.mensualIncome) * 100;
    console.log('Deuda total:', debtCuota);
    if (debtCuota > 50) {
      console.log('La deuda total excede el 50% del sueldo');
      return;
    }

    if (clientData.years + formData.years > 70) {
      console.log('Muy viejo');
      return;
    }

    const submitData = {
      rut: rut,
      loanName: loanName,
      years: formData.years,
      interest: formData.interest,
      loanAmount: formData.loanAmount,
      mensualPay: mensualPay,
      requirementsApproved: [false, false, false],
    };

    try {
      const response = await axios.post(`${API_URL}/clientLoan`, submitData);
      console.log('clienLoan registered successfully:', response.data);
    } catch (error) {
      console.error('Error registering clientLoan:', error);
    }
  };

  // UseEffect para actualizar labelValue cuando cambian formData o propertyValue
  useEffect(() => {
    setLabelValue('Valor obtenido');
  }, [formData.years, formData.interest, formData.loanAmount, propertyValue]);

  useEffect(() => {
    console.log('Loan data:', documentsValidations);
  }, [location.state]);

  return (
    <div>
      <input 
        type='text'
        name='rut'
        placeholder='RUT' 
        value={rut} 
        onChange={handleInputChange} 
      />
      <button type="validate" onClick={handleValidate}>Validar</button>
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
      <input 
        type='number'
        name='propertyValue'
        placeholder='Valor de la Propiedad' 
        value={propertyValue} 
        onChange={handleInputChange} 
      />
      <label>{labelValue}</label> 
      <button type="button" onClick={handleCalculate}>Calcular</button>
      <button type="submit" onClick={handleSubmit}>Ingresar</button>
      <ul>
          {requirements.map((req, index) => (<li key={index}>{req}</li>))}
      </ul>
    </div>
  );
};
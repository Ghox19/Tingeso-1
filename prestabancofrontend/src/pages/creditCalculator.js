import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import { PdfUploader } from '../components/pdfUploader';

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
  const [documentsData, setDocumentsData] = useState({});

  const handleDocumentUpload = (json, documentType) => {
    const documentData = {
        ...json,
        type: documentType,
        approved: false  // Aseguramos que approved se incluya
    };

    setDocumentsData(prevState => ({
        ...prevState,
        [documentType]: {
            data: documentData,
            status: 'uploaded',
            fileName: json.fileName
        }
    }));
  };

  const [clientData, setClientData] = useState('');
  

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

    const totalDebt = parseInt(clientData.totalDebt) + parseInt(mensualPay);
    console.log('Deuda total:', totalDebt);
    const debtCuota = (totalDebt / clientData.mensualIncome) * 100;
    console.log('Deuda total:', debtCuota);
    if (debtCuota > 50) {
      console.log('La deuda total excede el 50% del sueldo');
      return;
    }

    // Perform the addition
    const totalYears = parseInt(clientData.years) + parseInt(formData.years);
    console.log('Total años:', totalYears);
    if (totalYears > 70) {
      console.log('Muy viejo');
      return;
    }

    const documents = Object.values(documentsData).map(doc => ({
      ...doc.data,
        approved: false  // Aseguramos que cada documento tenga approved
    }));

    const submitData = {
        rut: rut,
        loanName: loanName,
        years: formData.years,
        interest: formData.interest,
        loanAmount: formData.loanAmount,
        mensualPay: mensualPay,
        fase: 'En Evaluacion',
        documents: documents
    };

    try {
      console.log('Data to send:', submitData);
      const response = await axios.post(`${API_URL}/clientLoan`, submitData, {
        headers: { 'Content-Type': 'application/json' }
      });
      console.log('clienLoan registered successfully:', response.data);
    } catch (error) {
      console.error('Error registering clientLoan:', error);
    }
  };

  // UseEffect para actualizar labelValue cuando cambian formData o propertyValue
  useEffect(() => {
    setLabelValue('Valor obtenido');
  }, [formData.years, formData.interest, formData.loanAmount, propertyValue]);

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
          {requirements.map((req, index) => (
              <li key={index}>
                  <div className="document-requirement">
                      <h3>{req}</h3>
                      <PdfUploader onUpload={(json) => handleDocumentUpload(json, req)} />
                      {documentsData[req]?.status === 'uploaded' && (
                          <div className="document-status">
                              <span className="success">✓ Archivo cargado: {documentsData[req].fileName}</span>
                          </div>
                      )}
                  </div>
              </li>
          ))}
      </ul>
    </div>
  );
};
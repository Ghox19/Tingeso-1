import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import { PdfUploader } from '../components/pdfUploader';
import { getApiUrl } from '../enviroment';
import  Slider  from '../components/slider';

export const CreditCalculator = () => {
  const API_URL = getApiUrl();
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
  const [reason, setReason] = useState('')
  const [maxInterest] = useState(location.state.maxInterest || 0); 
  const [minInterest] = useState(location.state.minInterest || 0); 
  const [requirements] = useState(location.state.requirements || []);
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
        propertyValue: propertyValue,
        years: formData.years,
        interest: formData.interest,
        loanAmount: formData.loanAmount,
        mensualPay: mensualPay,
        fase: 'En Revision Inicial',
        documents: documents
    };

    try {
      console.log('Data to send:', submitData);
      const response = await axios.post(`${API_URL}/clientLoan`, submitData, {
        headers: { 'Content-Type': 'application/json' }
      });
      setReason(response.data);
    } catch (error) {
      setReason(error.response.data)
      
    }
  };

  // UseEffect para actualizar labelValue cuando cambian formData o propertyValue
  useEffect(() => {
    setLabelValue('Valor obtenido');
  }, [formData.years, formData.interest, formData.loanAmount, propertyValue]);

  return (
    <div className="min-h-screen  p-8">
      <div className="max-w-5xl mx-auto space-y-12">
        {/* Sección superior */}
        <div className="flex gap-4">
          <div className="bg-[#2a3138] p-6 mx-10 rounded-lg shadow-lg">
            <div className="flex mb-6">
              <input 
                type="text"
                name="rut"
                placeholder="RUT"
                value={rut}
                onChange={handleInputChange}
                className="flex-grow px-4 py-2 bg-white/90 text-xl rounded-md"
              />
              <button 
                type="validate" 
                onClick={handleValidate}
                className="px-6 py-2 bg-[#3D2A3B] text-xl text-white rounded-md hover:bg-opacity-90"
              >
                Validar
              </button>
            </div>

            <div className="grid grid-cols-2 gap-6">
              <div>
                <label className="block text-white text-xl mb-2">Interes</label>
                <input 
                  type="number"
                  placeholder="Porcentaje %"
                  name="interest"
                  value={formData.interest}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 bg-white/90 text-xl text-black rounded-md"
                />
                <Slider min={minInterest} max={maxInterest} value={formData.interest} onChange={handleInputChange} />
              </div>

              <div>
                <label className="block text-white text-xl mb-2">Años</label>
                <input 
                  type="number"
                  placeholder="Años de plazo"
                  name="years"
                  value={formData.years}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 bg-white/90 text-xl text-black rounded-md"
                />
              </div>
              <div>
                <label className="block text-white text-xl mb-2">Valor de la propiedad</label>
                <input 
                  type="number"
                  placeholder="Valor en Pesos Chilenos"
                  name="propertyValue"
                  value={propertyValue}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 bg-white/90 text-xl text-black rounded-md"
                />
              </div>
              <div>
                <label className="block text-white text-xl mb-2">Valor Prestamo</label>
                <input 
                  type="number"
                  name="loanAmount"
                  placeholder="Valor en Pesos Chilenos"
                  value={formData.loanAmount}
                  onChange={handleInputChange}
                  className="w-full px-4 py-2 bg-white/90 text-xl text-black rounded-md"
                />
              </div>
            </div>
          </div>
        
          {/* Sección de Valor Obtenido y Botones */}
          <div className="flex flex-col items-center translate-y-16">
            <div className="text-white text-4xl mb-4">{labelValue}</div>
            <div className="flex justify-center gap-4">
              <button 
                type="button" 
                onClick={handleCalculate}
                className="px-6 py-2 bg-[#3D2A3B] text-white text-xl rounded-md hover:bg-opacity-90"
              >
                Calcular
              </button>
              <button 
                type="submit" 
                onClick={handleSubmit}
                className="px-6 py-2 bg-[#3D2A3B] text-white text-xl rounded-md hover:bg-opacity-90"
              >
                Ingresar Solicitud
              </button>
            </div>
          </div>
        </div>
  
        {/* Lista de Documentos */}
        <div className="grid grid-cols-2 gap-6">
          {requirements.map((req, index) => (
            <div key={index} className="bg-[#2a3138] p-6 rounded-lg shadow-lg">
              <h3 className="text-white text-xl mb-4">{req}</h3>
              <div className="space-y-2">
                <PdfUploader onUpload={(json) => handleDocumentUpload(json, req)} />
                {documentsData[req]?.status === 'uploaded' && (
                  <div className="text-green-400 text-sm">
                    ✓ Archivo cargado: {documentsData[req].fileName}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
        <div className="relative">
          {reason != "" && (
             <div>{reason}</div>
          )}
        </div>
      </div>
    </div>
  );
};
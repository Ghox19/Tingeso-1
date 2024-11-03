import React, { useState } from 'react';
import axios from 'axios';
import { PdfUploader } from '../components/pdfUploader';

const API_URL = 'http://104.41.28.230';

export const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    lastName: '',
    rut: '',
    email: '',
    years: '',
    contact: '',
    jobType: '',
    mensualIncome: '',
    jobYears: '',
    totalDebt: '',
    documents: {
      carnet: null,
      impuestos: null,
      deudas: null,
      ahorros: null
    }
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleDocumentUpload = (json, documentType) => {
    setFormData(prevState => ({
        ...prevState,
        documents: {
            ...prevState.documents,
            [documentType]: { ...json, type: documentType, approved: false }
        }
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const documentsArray = Object.values(formData.documents).filter(doc => doc !== null);
    const dataToSend = {
      ...formData,
      documents: documentsArray
    };
    
    try {
      console.log('Data to send:', dataToSend);
      const response = await axios.post(`${API_URL}/client`, dataToSend, {
        headers: { 'Content-Type': 'application/json' }
      });
      console.log('Client registered successfully:', response.data);
    } catch (error) {
      console.error('Error registering client:', error);
    }
  };
  
  return (
    <div>
      <h1>Register</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Nombre:</label>
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
          <label htmlFor="lastName">Apellido:</label>
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
          <label htmlFor="years">Edad:</label>
          <input
            type="number"
            id="years"
            name="years"
            value={formData.years}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="contact">Contacto:</label>
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
          <label htmlFor="mensualIncome">Ingreso Mensual:</label>
          <input
            type="number"
            id="mensualIncome"
            name="mensualIncome"
            value={formData.mensualIncome}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="jobType">Tipo de Trabajo:</label>
          <select
            id="jobType"
            name="jobType"
            value={formData.jobType}
            onChange={handleChange}
            required
          >
            <option value="">Seleccione una opción</option> {/* Placeholder option */}
            <option value="empresa">Empresa</option>
            <option value="independiente">Independiente</option>
          </select>
        </div>
        <div>
          <label htmlFor="jobYears">Antiguedad Laboral(En años):</label>
          <input
            type="number"
            id="jobYears"
            name="jobYears"
            value={formData.jobYears}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label htmlFor="totalDebt">Deuda Total Actual:</label>
          <input
            type="number"
            id="totalDebt"
            name="totalDebt"
            value={formData.totalDebt}
            onChange={handleChange}
            required
          />
        </div>
        <label>Documentos:</label>
        <div>
          <label>Carnet de Identidad</label>
          <PdfUploader onUpload={(json) => handleDocumentUpload(json, 'carnet')} />
        </div>
        <div>
          <label>Declaracion de Impuestos</label>
          <PdfUploader onUpload={(json) => handleDocumentUpload(json, 'impuestos')} />
        </div>
        <div>
          <label>Informe de deudas CMF</label>
          <PdfUploader onUpload={(json) => handleDocumentUpload(json, 'deudas')} />
        </div>
        <div>
          <label>Cuenta de Ahorros</label>
          <PdfUploader onUpload={(json) => handleDocumentUpload(json, 'ahorros')} />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

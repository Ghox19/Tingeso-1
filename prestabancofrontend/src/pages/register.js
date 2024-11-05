import React, { useState } from 'react';
import axios from 'axios';
import { PdfUploader } from '../components/pdfUploader';
import { getApiUrl } from '../enviroment';
import { useNavigate } from 'react-router-dom';

export const Register = () => {
  const API_URL = getApiUrl();
  const navigate = useNavigate();
  const [showSuccess, setShowSuccess] = useState(false);

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
      const response = await axios.post(`${API_URL}/client`, dataToSend, {
        headers: { 'Content-Type': 'application/json' }
      });
      setShowSuccess(true);
      setTimeout(() => {
        navigate('/');
      }, 3000);
    } catch (error) {
      console.error('Error registering client:', error);
    }
  };
  
  return (
    <div className="min-h-screen py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl mx-auto bg-[#282C35] rounded-lg shadow-lg border border-gray-700 p-8">
        <h1 className="text-3xl font-bold text-white text-center mb-8">
          Registro de Usuario
        </h1>
        <div className="relative">
          {showSuccess && (
            <div className="fixed top-4 right-4 bg-green-700 text-white px-6 py-3 text-lg rounded-md shadow-lg transition-all duration-500 ease-in-out transform animate-fade-in-down">
              ¡Registrado correctamente!
            </div>
          )}
        </div>
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Personal Information Section */}
          <div className="bg-[#232E37] p-6 rounded-lg space-y-4">
            <h2 className="text-xl font-semibold text-white mb-4">
              Información Personal
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="name" className="block text-sm font-medium text-white ">
                  Nombre
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] text-black sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-white">
                  Apellido
                </label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="rut" className="block text-sm font-medium text-white ">
                  RUT
                </label>
                <input
                  type="text"
                  id="rut"
                  name="rut"
                  value={formData.rut}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="email" className="block text-sm font-medium text-white ">
                  Email
                </label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="years" className="block text-sm font-medium text-white">
                  Edad
                </label>
                <input
                  type="number"
                  id="years"
                  name="years"
                  value={formData.years}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="contact" className="block text-sm font-medium text-white">
                  Contacto
                </label>
                <input
                  type="tel"
                  id="contact"
                  name="contact"
                  value={formData.contact}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>
            </div>
          </div>

          {/* Financial Information Section */}
          <div className="bg-[#232E37] p-6 rounded-lg space-y-4">
            <h2 className="text-xl font-semibold text-white mb-4">
              Información Financiera
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="mensualIncome" className="block text-sm font-medium text-white">
                  Ingreso Mensual
                </label>
                <input
                  type="number"
                  id="mensualIncome"
                  name="mensualIncome"
                  value={formData.mensualIncome}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="jobType" className="block text-sm font-medium text-white">
                  Tipo de Trabajo
                </label>
                <select
                  id="jobType"
                  name="jobType"
                  value={formData.jobType}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                >
                  <option value="">Seleccione una opción</option>
                  <option value="empresa">Empresa</option>
                  <option value="independiente">Independiente</option>
                </select>
              </div>

              <div>
                <label htmlFor="jobYears" className="block text-sm font-medium text-white">
                  Antiguedad Laboral (años)
                </label>
                <input
                  type="number"
                  id="jobYears"
                  name="jobYears"
                  value={formData.jobYears}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 text-black shadow-sm focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>

              <div>
                <label htmlFor="totalDebt" className="block text-sm font-medium text-white">
                  Deuda Total Actual
                </label>
                <input
                  type="number"
                  id="totalDebt"
                  name="totalDebt"
                  value={formData.totalDebt}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm text-black focus:border-[#3D2A3B] focus:ring-[#3D2A3B] sm:text-sm"
                />
              </div>
            </div>
          </div>

          {/* Documents Section */}
          <div className="bg-[#232E37] p-6 rounded-lg space-y-4">
            <h2 className="text-xl font-semibold text-white mb-4">
              Documentos Requeridos
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-white mb-2">
                  Carnet de Identidad
                </label>
                <PdfUploader 
                  onUpload={(json) => handleDocumentUpload(json, 'carnet')}
                  className="w-full"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-white mb-2">
                  Declaración de Impuestos
                </label>
                <PdfUploader 
                  onUpload={(json) => handleDocumentUpload(json, 'impuestos')}
                  className="w-full"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-white mb-2">
                  Informe de deudas CMF
                </label>
                <PdfUploader 
                  onUpload={(json) => handleDocumentUpload(json, 'deudas')}
                  className="w-full"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-white mb-2">
                  Cuenta de Ahorros
                </label>
                <PdfUploader 
                  onUpload={(json) => handleDocumentUpload(json, 'ahorros')}
                  className="w-full"
                />
              </div>
            </div>
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-[#3D2A3B] hover:bg-[#2A353D] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#3D2A3B] transition-colors duration-200"
            >
              Registrarse
            </button>
          </div>

        </form>
      </div>
    </div>
  );
};
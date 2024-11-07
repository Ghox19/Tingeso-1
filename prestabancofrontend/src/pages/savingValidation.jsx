import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import { getApiUrl } from '../enviroment';

export const SavingValidation = () => {
  const location = useLocation();
  const API_URL = getApiUrl();

  const { id, idSaving } = location.state || {};
  const [actualIdSaving, setActualIdSaving] = useState(idSaving);

  const [formData, setFormData] = useState({
    clientLoanId: id,
    years: '',
    actualBalance: '',
    balances: Array(12).fill(''),
    deposit: Array(12).fill(''),
    withdraw: Array(12).fill('')
  });

  const [reasons, setReasons] = useState([]);

  const fetchSaving = async () => {
    try {
      console.log(actualIdSaving); 
      const response = await axios.get(`${API_URL}/saving/${actualIdSaving}`);
      setFormData(response.data);
      setReasons(response.data.reasons);
      console.log(response.data);
    } catch (error) {
      console.error('Error fetching loans:', error);
    }
  };

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

  const handleSubmit = async (e) => {
    e.preventDefault();
      try {
        const response = await axios.post(`${API_URL}/saving`, formData);
        setActualIdSaving(response.data);
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    console.log(formData);
  };

  const handleApproved = async (e) => {
    const newFormData = {
      id: actualIdSaving,
      result: "Aprobado"
    }
      try {
        const response = await axios.put(`${API_URL}/saving`, newFormData);
        console.log(response.data);
        fetchSaving();
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    console.log(formData);
  };

  const handleReject = async (e) => {
    const newFormData = {
      id: actualIdSaving,
      result: "Rechazado"
    }
      try {
        const response = await axios.put(`${API_URL}/saving`, newFormData);
        console.log(response.data);
        fetchSaving();
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    console.log(formData);
  };

  useEffect(() => {
    if (actualIdSaving !== 0) {
      console.log(idSaving); 
      fetchSaving();
    }
  }, [actualIdSaving]); // Se ejecutará cuando idSaving cambie

  return (
    <div className="min-h-screen bg-[#282C35] p-6">
      <div className="max-w-4xl mx-auto bg-[#2A353D]/90 rounded-lg shadow-xl p-8">
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Header Inputs */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <label className="block text-xl text-white font-medium">Años de Vigencia de la Cuenta:</label>
              <input
                type="number"
                value={formData.years}
                onChange={(e) => handleInputChange(e, null, 'years')}
                className="w-full px-4 py-2 border text-xl text-black border-[#3D2A3B]/30 rounded-md 
                           placeholder-gray-400 focus:outline-none focus:ring-2 
                           focus:ring-[#3D2A3B]/50 focus:border-transparent"
                required
              />
            </div>
            <div className="space-y-2">
              <label className="block text-xl text-white font-medium">Balance Actual de la Cuenta:</label>
              <input
                type="number"
                value={formData.actualBalance}
                onChange={(e) => handleInputChange(e, null, 'actualBalance')}
                className="w-full px-4 py-2 border border-[#3D2A3B]/30 rounded-md 
                           text-black text-xl placeholder-gray-400 focus:outline-none focus:ring-2 
                           focus:ring-[#3D2A3B]/50 focus:border-transparent"
                required
              />
            </div>
          </div>
  
          {/* Table Container */}
          <div className="overflow-x-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-[#2F3429]">
                  <th className="px-4 py-3 text-center text-xl text-white font-semibold">Mes</th>
                  <th className="px-4 py-3 text-center text-xl text-white font-semibold">Balance</th>
                  <th className="px-4 py-3 text-center text-xl text-white font-semibold">Depósito</th>
                  <th className="px-4 py-3 text-center text-xl text-white font-semibold">Retiro</th>
                </tr>
              </thead>
              <tbody className="divide-y text-xl divide-[#3D2A3B]/30">
                {months.map((month, index) => (
                  <tr key={month} className="hover:bg-[#232E37]/30 transition-colors">
                    <td className="px-4 py-3 text-white">{month}</td>
                    <td className="px-4 py-3">
                      <input
                        type="number"
                        value={formData.balances[index]}
                        onChange={(e) => handleInputChange(e, index, 'balances')}
                        className="w-full px-3 py-1.5 border border-[#3D2A3B]/30 
                                 rounded text-black placeholder-gray-400 focus:outline-none focus:ring-2 
                                 focus:ring-[#3D2A3B]/50 focus:border-transparent"
                        required
                      />
                    </td>
                    <td className="px-4 py-3">
                      <input
                        type="number"
                        value={formData.deposit[index]}
                        onChange={(e) => handleInputChange(e, index, 'deposit')}
                        className="w-full px-3 py-1.5 border border-[#3D2A3B]/30 
                                 rounded text-black placeholder-gray-400 focus:outline-none focus:ring-2 
                                 focus:ring-[#3D2A3B]/50 focus:border-transparent"
                        required
                      />
                    </td>
                    <td className="px-4 py-3">
                      <input
                        type="number"
                        value={formData.withdraw[index]}
                        onChange={(e) => handleInputChange(e, index, 'withdraw')}
                        className="w-full px-3 py-1.5 border border-[#3D2A3B]/30 
                                 rounded text-black placeholder-gray-400 focus:outline-none focus:ring-2 
                                 focus:ring-[#3D2A3B]/50 focus:border-transparent"
                        required
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="flex items-center gap-4 mt-4">
            <div className='relative'>
              {actualIdSaving === 0 && (
                <button
                  type="submit"
                  className="w-full md:w-auto px-6 py-2.5 bg-[#2F3429] hover:bg-[#2F3429]/80 
                            text-white text-xl font-medium rounded-lg transition-colors duration-200 
                            focus:outline-none focus:ring-2 focus:ring-[#2F3429]/50"
                            
                >
                  Guardar Datos
                </button>
              )}
            </div>
             
            <div className='relative'>
              {formData.result === "Revision Adicional" && (
                <button className='w-full md:w-auto px-6 py-2.5 bg-[#2F3429] hover:bg-[#2F3429]/80 
                        text-white text-xl font-medium rounded-lg transition-colors duration-200 
                        focus:outline-none focus:ring-2 focus:ring-[#2F3429]/50' 
                        onClick={() => handleApproved()}>Aprobar de todas formas</button> 
              )}
            </div>
            <div className='relative'>
              {formData.result === "Revision Adicional" && (
                <button className='w-full md:w-auto px-6 py-2.5 bg-[#2F3429] hover:bg-[#2F3429]/80 
                        text-white text-xl font-medium rounded-lg transition-colors duration-200 
                        focus:outline-none focus:ring-2 focus:ring-[#2F3429]/50' 
                        onClick={() => handleReject()}>Rechazar</button> 
              )}
            </div>
            <div className='relative'>
              {formData.result === "Aprobado" && (
                <span>Esta cuenta esta Aprobada</span>
              )}
            </div>
            <div className='relative'>
              {formData.result === "Rechazado" && (
                <span>Esta cuenta esta Rechazada</span>
              )}
            </div>
          </div>
        </form>
              {reasons && reasons.length > 0 && (
            <div className="bg-[#282C35] p-4 rounded-lg">
              <h3 className="text-white font-semibold mb-2">Razones:</h3>
              <ul className="space-y-2">
                {reasons.map((reason, index) => (
                  <li key={index} className="text-white/90 pl-4 border-l-2 border-[#3D2A3B]">
                    {reason}
                  </li>
                ))}
              </ul>
            </div>
          )}

      </div>
    </div>
  );
};
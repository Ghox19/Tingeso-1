import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const ClientLoanValidation = () => {
  const location = useLocation();

  const { id } = location.state || {};  // Extrae id del estado
  const [loan, setLoan] = useState([]);

  useEffect(() => {
    const fetchLoan = async () => {
      try {
        const response = await axios.get(`${API_URL}/clientLoan/${id}`);
        setLoan(response.data);
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    };
  
    fetchLoan();
  }, []);

  return (
    <div>
      <ul>
        <li><strong>Nombre del Préstamo:</strong> {loan.loanName}</li>
        <li><strong>Años:</strong> {loan.years}</li>
        <li><strong>Interés:</strong> {loan.interest}%</li>
        <li><strong>Monto del Préstamo:</strong> ${loan.loanAmount}</li>
        <li><strong>Pago Mensual:</strong> ${loan.mensualPay}</li>
      </ul>
    </div>
  );
};
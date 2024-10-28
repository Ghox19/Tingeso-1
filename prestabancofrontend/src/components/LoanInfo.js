import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export const LoanInfo = ({id,loanName, years, interest, loanAmount, mensualPay, rut}) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate('/loanValidation', { state: {id}});
  };

  useEffect(() => {
    console.log(id);
  }
  , []);
  return (
    <div>
      <ul>
        <li><strong>Nombre del Préstamo:</strong> {loanName}</li>
        <li><strong>Años:</strong> {years}</li>
        <li><strong>Interés:</strong> {interest}%</li>
        <li><strong>Monto del Préstamo:</strong> ${loanAmount}</li>
        <li><strong>Pago Mensual:</strong> ${mensualPay}</li>
        <li><strong>RUT:</strong> {rut}</li>
        <button onClick={handleClick}>Solicitar Préstamo</button>
      </ul>
    </div>
  );
};
import React from 'react';
import { useNavigate } from 'react-router-dom';

export const LoanCard = ({ name, description, maxYears, minInterest, maxInterest, maxAmount }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate('/creditCalculator', { state: { name, maxYears, minInterest, maxInterest, maxAmount } });
  };

  return (
    <div className="loan-card">
      <h2>{name}</h2>
      <p>{description}</p>
      <ul>
        <li>Años máximos: {maxYears}</li>
        <li>Interés mínimo: {minInterest}%</li>
        <li>Interés máximo: {maxInterest}%</li>
        <li>
          Monto máximo: {maxAmount === 50 ? `${maxAmount}% del valor actual de la propiedad` : `${maxAmount}% del valor de la propiedad`}
        </li>
      </ul>
      <button onClick={handleClick}>Solicitar Préstamo</button>
    </div>
  );
};
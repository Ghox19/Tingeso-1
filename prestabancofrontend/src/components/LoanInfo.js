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
    <div className="max-w-6xl mx-auto bg-[#2A353D] rounded-xl shadow-md overflow-hidden p-4">
      <ul className="grid grid-cols-7 gap-2">
        <li className="border-r border-[#29394D] pr-2">
          <span className="font-semibold text-white text-sm">{loanName}</span>
        </li>
        <li className="border-r border-[#29394D] pr-2">
          <span className="font-semibold text-white text-sm">{years}</span>
        </li>
        <li className="border-r border-[#29394D] pr-2">
          <span className="font-semibold text-white text-sm">{interest}%</span>
        </li>
        <li className="border-r border-[#29394D] pr-2">
          <span className="font-semibold text-white text-sm">${loanAmount}</span>
        </li>
        <li className="border-r border-[#29394D] pr-2">
          <span className="font-semibold text-white text-sm">${mensualPay}</span>
        </li>
        <li>
          <span className="font-semibold text-white text-sm">{rut}</span>
        </li>
        <li>
          <button 
            onClick={handleClick}
            className="w-full bg-blue-600 text-white text-sm py-1 px-3 rounded-lg hover:bg-blue-700 transition duration-300"
          >
            Revisar
          </button>
        </li>
      </ul>
    </div>
  );
};
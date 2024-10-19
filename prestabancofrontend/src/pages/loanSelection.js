import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { LoanCard } from '../components/loanCard';

const API_URL = 'http://localhost:8080';

export const LoanSelection = () => {
  const [loans, setLoans] = useState([]);

  useEffect(() => {
    const fetchLoans = async () => {
      try {
        const response = await axios.get(`${API_URL}/loan`);
        setLoans(response.data);
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    };

    fetchLoans();
  }, []);

  return (
    <div>
      {loans.map((loan) => (
        <LoanCard
          key={loan.id} // Asegúrate de que cada préstamo tenga un identificador único
          name={loan.name}
          description={loan.description}
          maxYears={loan.maxYears}
          minInterest={loan.minInterest}
          maxInterest={loan.maxInterest}
          maxAmount={loan.maxAmount}
          requirements={loan.requirements}
        />
      ))}
    </div>
  );
};
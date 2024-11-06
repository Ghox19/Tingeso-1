import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { LoanCard } from '../components/loanCard';
import { getApiUrl } from '../enviroment';

export const LoanSelection = () => {
  const API_URL = getApiUrl();
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
      <div className="container mx-auto px-4 py-8 flex flex-col space-y-8 max-w-2xl">
        {loans.map((loan) => (
          <LoanCard
            key={loan.id}
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
    </div>
  );
};
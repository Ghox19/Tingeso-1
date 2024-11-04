import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { LoanInfo } from '../components/LoanInfo';
import { getApiUrl } from '../enviroment';

export const ClientLoanPage = () => {
  API_URL = getApiUrl();
  const [loans, setLoans] = useState([]);

  useEffect(() => {
    const fetchLoans = async () => {
      try {
        const response = await axios.get(`${API_URL}/clientLoan`);
        setLoans(response.data);
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    };

    fetchLoans();
  }, []);

  return (
    <div>
      <h2>Información del Préstamo</h2>
      {loans.map((loan) => (
        <LoanInfo
          key={loan.id}
          id={loan.id}
          loanName={loan.loanName}
          years={loan.years}
          interest={loan.interest}
          loanAmount={loan.loanAmount}
          mensualPay={loan.mensualPay}
          rut={loan.client.rut}
        />
      ))}
    </div>
  );
};
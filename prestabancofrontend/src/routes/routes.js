import React from 'react';
import { Route } from 'react-router-dom';
import { LoanSelection } from '../pages/loanSelection';
import { CreditCalculator } from '../pages/creditCalculator';

const routes = [
  { path: '/', element: <LoanSelection /> },
  { path: '/creditCalculator', element: <CreditCalculator /> }
];

export default routes;


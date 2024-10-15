import './App.css';
import { Register } from './components/register';
import { CreditCalculator } from './components/creditCalculator';
import { LoanSelection } from './components/loanSelection';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <Register />
        <LoanSelection />
        <CreditCalculator />
      </header>
    </div>
  );
}

export default App;

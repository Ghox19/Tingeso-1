import './App.css';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Register } from './components/register';
import { LoanSelection } from './pages/loanSelection';
import routes from './routes/routes';

function App() {
  return (
    <div className="App">
        <header className="App-header">
          <Router>
            <Routes>
              {routes.map((route, index) => (
                <Route key={index} path={route.path} element={route.element} />
              ))}
            </Routes>
          </Router>
        </header>
    </div>
  );
}

export default App;

import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

export const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleRegister = () => {
    navigate('/register');
  };

  const handleCredit = () => {
    navigate('/');
  };

  const handleView = () => {
    navigate('/loanCollection');
  };

  return (
    <nav className="bg-[#282C35] shadow-lg"> {/* color1: Gris azulado oscuro */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div className="flex-shrink-0 cursor-pointer" onClick={handleCredit}>
            <span className="text-white text-xl font-bold">Presta Banco</span>
          </div>

          {/* Desktop Menu */}
          <div className="hidden md:flex md:items-center md:space-x-4">
            <button
              className={`text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-300 ${
                location.pathname === '/' 
                  ? 'bg-[#2A353D]' /* color5: Verde azulado oscuro */
                  : 'bg-[#2F3429] hover:bg-[#2A353D]' /* color4: Verde oliva oscuro */
              }`}
              onClick={handleCredit}
            >
              Solicitar Crédito
            </button>
            <button
              className={`text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-300 ${
                location.pathname === '/register' 
                  ? 'bg-[#382E2C]' /* color3: Marrón oscuro */
                  : 'bg-[#2F3429] hover:bg-[#382E2C]' /* color4: Verde oliva oscuro */
              }`}
              onClick={handleRegister}
            >
              Registrarse
            </button>
            <button
              className={`text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-300 ${
                location.pathname === '/loanCollection' 
                  ? 'bg-[#3D2A3B]' /* color2: Púrpura profundo */
                  : 'bg-[#2F3429] hover:bg-[#3D2A3B]' /* color4: Verde oliva oscuro */
              }`}
              onClick={handleView}
            >
              Ver Solicitudes
            </button>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-[#2A353D] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-[#282C35] focus:ring-white"
            >
              <span className="sr-only">Open main menu</span>
              {!isOpen ? (
                <svg
                  className="block h-6 w-6"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M4 6h16M4 12h16M4 18h16"
                  />
                </svg>
              ) : (
                <svg
                  className="block h-6 w-6"
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      {isOpen && (
        <div className="md:hidden bg-[#282C35]"> {/* color1: Gris azulado oscuro */}
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            <button
              className={`text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-300 w-full ${
                location.pathname === '/' 
                  ? 'bg-[#2A353D]' /* color5: Verde azulado oscuro */
                  : 'bg-[#2F3429] hover:bg-[#2A353D]' /* color4: Verde oliva oscuro */
              }`}
              onClick={handleCredit}
            >
              Solicitar Crédito
            </button>
            <button
              className={`text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-300 w-full ${
                location.pathname === '/register' 
                  ? 'bg-[#382E2C]' /* color3: Marrón oscuro */
                  : 'bg-[#2F3429] hover:bg-[#382E2C]' /* color4: Verde oliva oscuro */
              }`}
              onClick={handleRegister}
            >
              Registrarse
            </button>
            <button
              className={`text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-300 w-full ${
                location.pathname === '/loanCollection' 
                  ? 'bg-[#3D2A3B]' /* color2: Púrpura profundo */
                  : 'bg-[#2F3429] hover:bg-[#3D2A3B]' /* color4: Verde oliva oscuro */
              }`}
              onClick={handleView}
            >
              Ver Solicitudes
            </button>
          </div>
        </div>
      )}
    </nav>
  );
};
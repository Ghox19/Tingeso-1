import React from 'react';

const API_URL = 'http://localhost:8080';

export const LoanSelection = () => {
  // Funciones de manejo de eventos para cada botón
  const handleButtonClick1 = () => {
    alert('Botón 1 presionado');
  };

  const handleButtonClick2 = () => {
    alert('Botón 2 presionado');
  };

  const handleButtonClick3 = () => {
    alert('Botón 3 presionado');
  };

  const handleButtonClick4 = () => {
    alert('Botón 4 presionado');
  };

  useEffect(() => {
    // Lógica de inicialización
    console.log('LoanSelection component initialized');
    return () => {
      // Lógica de limpieza
      console.log('LoanSelection component destroyed');
    };
  }, []);

  return (
    <div>
      <button onClick={handleButtonClick1}>Botón 1</button>
      <button onClick={handleButtonClick2}>Botón 2</button>
      <button onClick={handleButtonClick3}>Botón 3</button>
      <button onClick={handleButtonClick4}>Botón 4</button>
    </div>
  );
};
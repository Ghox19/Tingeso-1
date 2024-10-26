import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const ClientLoanValidation = () => {
  const location = useLocation();

  const { id } = location.state || {};  // Extrae id del estado
  const [loan, setLoan] = useState([]);
  const [client, setClient] = useState([]);
  const [clientDocuments, setClientDocuments] = useState([]);

  const handleDownload = async (id, name) => {
    try {
        const response = await axios.get(`${API_URL}/document/download/${id}`, {
            responseType: 'blob',
            headers: {
                'Accept': 'application/pdf'
            }
        });

        // Crear un blob con el tipo correcto
        const blob = new Blob([response.data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', name.endsWith('.pdf') ? name : `${name}.pdf`);
        document.body.appendChild(link);
        link.click();

        // Limpieza
        window.URL.revokeObjectURL(url);
        link.remove();
    } catch (error) {
        console.error('Error downloading the document', error);
    }
  };

  useEffect(() => {
    const fetchLoan = async () => {
      try {
        const response = await axios.get(`${API_URL}/clientLoan/${id}`);
        console.log(response.data);
        setLoan(response.data);
        setClient(response.data.client);
        fetchClientDocuments(response.data.client.id);
      } catch (error) {
        console.error('Error fetching loans:', error);
      }
    };

    const fetchClientDocuments = async () => {
      try {
        const response = await axios.get(`${API_URL}/client/documents/${id}`);
        console.log(response.data);
        setClientDocuments(response.data);
      } catch (error) {
        console.error('Error fetching Client Documents:', error);
      }
    };

  
    fetchLoan();
  }, []);

  return (
    <div>
      <h3>Solicitud de Credito</h3>
      <ul>
        <li><strong>Nombre del Préstamo:</strong> {loan.loanName}</li>
        <li><strong>Años:</strong> {loan.years}</li>
        <li><strong>Interés:</strong> {loan.interest}%</li>
        <li><strong>Monto del Préstamo:</strong> ${loan.loanAmount}</li>
        <li><strong>Pago Mensual:</strong> ${loan.mensualPay}</li>
        <h3>Documentos Credito</h3>
        {loan?.documents?.map((doc) => (
            <li key={doc.id}>
                <span>Nombre: {doc.name}</span>
                <span>Tipo: {doc.type}</span>
                <button onClick={() => handleDownload(doc.id, doc.name)}>
                    Descargar
                </button>
            </li>
        ))}
        <h3>Informacion cliente</h3>
        <li><strong>RUT:</strong> {client.rut}</li>
        <li><strong>Nombre:</strong> {client.name + " " +client.lastName}</li>
        <li><strong>Contacto:</strong> {client.contact}</li>
        <h3>Documentos Clientes</h3>
        {clientDocuments?.map((doc) => (
            <li key={doc.id}>
                <span>Nombre: {doc.name}</span>
                <span>Tipo: {doc.type}</span>
                <button onClick={() => handleDownload(doc.id, doc.name)}>
                    Descargar
                </button>
            </li>
        ))}
      </ul>
    </div>
  );
};
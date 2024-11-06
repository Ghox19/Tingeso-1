import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getApiUrl } from '../enviroment';

export const ClientLoanValidation = () => {
  const API_URL = getApiUrl();
  const navigate = useNavigate();
  const location = useLocation();

  const { id } = location.state || {};  // Extrae id del estado
  const [loan, setLoan] = useState([]);
  const [client, setClient] = useState([]);
  const [clientDocuments, setClientDocuments] = useState([]);
  const [idSaving, setIdSaving] = useState(0);
  const [saving, setSaving] = useState([]);

  const fetchLoan = async () => {
    try {
      const response = await axios.get(`${API_URL}/clientLoan/${id}`);
      setLoan(response.data);
      setClient(response.data.client);
      fetchClientDocuments(response.data.client.id);
      if (response.data.savings !== null) {
        setSaving(response.data.savings);
        setIdSaving(response.data.savings.id);
      }
    } catch (error) {
      console.error('Error fetching loans:', error);
    }
  };

  const fetchClientDocuments = async (id) => {
    try {
      const response = await axios.get(`${API_URL}/client/documents/${id}`);
      setClientDocuments(response.data);
    } catch (error) {
      console.error('Error fetching Client Documents:', error);
    }
  };

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
  
  const handleSavings = async () => {
    navigate('/savingValidation', { state: {id, idSaving} });
  };

  const handleDocumentError = async () => {
    navigate('/');
  };

  const handleDocumentApproved = async (id, document) => {
    document.approved = true;
    try {
        const response = await axios.put(`${API_URL}/document/${id}`, document);
        fetchLoan();
    } catch (error) {
        console.error('Error approving the document', error);
    }
  };

  useEffect(() => {
    fetchLoan();
    console.log(loan);
  }, []);

  return (
    <div>
      <h3>Solicitud de Credito</h3>
      <ul>
        <li><strong>Nombre del Préstamo:</strong> {loan.loanName}</li>
        <li><strong>Años:</strong> {loan.years}</li>
        <li><strong>Interés:</strong> {loan.interest}%</li>
        <li><strong>Monto del Préstamo:</strong> ${loan.loanAmount}</li>
        <li><strong>Valor de la Propiedad:</strong> ${loan.propertyValue}</li>
        <li><strong>Porcentaje del prestamo a la propiedad:</strong> {loan.loanRatio}% </li>
        <li><strong>Pago Mensual:</strong> ${loan.mensualPay}</li>
        <li><strong>Cuota/Ingreso:</strong> {loan.cuotaIncome}%</li>
        <li><strong>Deuda/Ingreso:</strong> {loan.debtCuota}%</li>
        <li><strong>Fase Actual:</strong> {loan.fase}</li>
        <h3>Documentos Credito</h3>
        {loan?.documents?.map((doc) => (
            <li key={doc.id}>
                <span>Nombre: {doc.name}</span>
                <span>Tipo: {doc.type}</span>
                <span>Estado: {doc.approved ? "Aprobado" : "No aprobado"}</span>
                <button onClick={() => handleDownload(doc.id, doc.name)}>
                    Descargar
                </button>
                <button onClick={() => handleDocumentApproved(doc.id, doc)}>
                    Aprobar
                </button>
            </li>
        ))}
        <h3>Informacion cliente</h3>
        <li><strong>RUT:</strong> {client.rut}</li>
        <li><strong>Nombre:</strong> {client.name + " " +client.lastName}</li>
        <li><strong>Contacto:</strong> {client.contact}</li>
        <li><strong>Correo:</strong> {client.email}</li>
        <li><strong>Sueldo:</strong> ${client.mensualIncome}</li>
        <li><strong>Deudas:</strong> ${client.totalDebt}</li>
        <li><strong>Antiguedad Laboral:</strong> {client.jobYears} años</li>
        <h3>Documentos Clientes</h3>
        {clientDocuments?.map((doc) => (
            <li key={doc.id}>
                <span>Nombre: {doc.name}</span>
                <span>Tipo: {doc.type}</span>
                <span>Estado: {doc.approved ? "Aprobado" : "No aprobado"}</span>
                <button onClick={() => handleDownload(doc.id, doc.name)}>
                    Descargar
                </button>
                <button onClick={() => handleDocumentApproved(doc.id, doc)}>
                    Aprobar
                </button>
            </li>
        ))}
        <div className="relative">
          {loan.savings === null &&(
            <button onClick={() => handleSavings()}>Validar Cuenta de ahorros</button>
          )}
        </div>
        <div className="relative">
          {saving.result === "Revision Adicional" &&(
            <button onClick={() => handleSavings()}>Validar Cuenta de ahorros</button>
          )}
        </div>
        <div className="relative">
          {loan.savings !== null && saving.result !== "Revision Adicional" &&(
            <li>La cuenta de Ahorros esta {saving.result}</li>
          )}
        </div>
        <div className="relative">
          {loan.fase === "En Revision Inicial" && (
             <button onClick={() => handleDocumentError()}>Error en los Archivos</button>
          )}
        </div>
      </ul>
    </div>
  );
};
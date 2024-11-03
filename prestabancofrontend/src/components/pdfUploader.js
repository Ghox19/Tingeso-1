import React from 'react';
import axios from 'axios';

const API_URL = 'http://104.41.28.230';

export function PdfUploader({ onUpload, documentType }) {
    const handleFileChange = (event) => {
        handleSubmit(event, event.target.files[0]);
    };

    const handleSubmit = async (event, file) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await axios.post(`${API_URL}/document/jsonConvert`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            console.log('Documento subido:', response.data);  
            onUpload(response.data, documentType); // Pass JSON data to parent component
        } catch (error) {
            console.error('Error al subir el documento!');
        }
    };

    return (
        <div>
            <input type="file" accept="application/pdf" onChange={handleFileChange} required />
        </div>
    );
}
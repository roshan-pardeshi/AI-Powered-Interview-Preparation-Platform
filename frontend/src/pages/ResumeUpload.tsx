import React, { useState } from 'react';
import Navbar from '../components/Navbar';
import { uploadResume } from '../api';

const ResumeUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFile(event.target.files?.[0] ?? null);
    setMessage('');
    setError('');
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!file) {
      setError('Please select a resume file.');
      return;
    }

    setLoading(true);
    setError('');
    setMessage('');

    try {
      await uploadResume(file);
      setMessage('Resume uploaded successfully. You can now request a job match.');
      setFile(null);
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Unable to upload resume.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="card" style={{ maxWidth: 680, margin: 'auto' }}>
        <h1>Upload Resume</h1>
        {message && <div className="message">{message}</div>}
        {error && <div className="message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-field">
            <label htmlFor="resume">Select PDF Resume</label>
            <input id="resume" type="file" accept="application/pdf" onChange={handleFileChange} />
          </div>
          <button type="submit" disabled={loading}>{loading ? 'Uploading...' : 'Upload Resume'}</button>
        </form>
      </div>
    </>
  );
};

export default ResumeUpload;

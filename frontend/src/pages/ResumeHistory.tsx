import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getMyResumes } from '../api';

const ResumeHistory: React.FC = () => {
  const [resumes, setResumes] = useState<any[]>([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadResumes = async () => {
      try {
        const data = await getMyResumes();
        setResumes(data);
      } catch (e) {
        setError('Unable to load resume history.');
      }
    };
    loadResumes();
  }, []);

  return (
    <>
      <Navbar />
      <div className="card">
        <h1>Resume History</h1>
        {error && <div className="message">{error}</div>}
        {resumes.length ? (
          <table className="table">
            <thead>
              <tr>
                <th>File</th>
                <th>Resume score</th>
                <th>Uploaded</th>
                <th>Strong skills</th>
                <th>Weak skills</th>
              </tr>
            </thead>
            <tbody>
              {resumes.map((resume) => (
                <tr key={resume.id}>
                  <td>{resume.fileName}</td>
                  <td>{resume.resumeScore ?? 'N/A'}</td>
                  <td>{resume.uploadDate ? new Date(resume.uploadDate).toLocaleDateString() : 'Unknown'}</td>
                  <td>{resume.strongSkills || '-'}</td>
                  <td>{resume.weakSkills || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No resumes uploaded yet.</p>
        )}
      </div>
    </>
  );
};

export default ResumeHistory;

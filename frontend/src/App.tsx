import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import ResumeUpload from './pages/ResumeUpload';
import ResumeHistory from './pages/ResumeHistory';
import JobMatch from './pages/JobMatch';
import Interviews from './pages/Interviews';
import Analytics from './pages/Analytics';
import VoiceAnalysis from './pages/VoiceAnalysis';
import ProtectedRoute from './components/ProtectedRoute';

const App: React.FC = () => (
  <div className="app-shell">
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/upload-resume"
        element={
          <ProtectedRoute>
            <ResumeUpload />
          </ProtectedRoute>
        }
      />
      <Route
        path="/resume-history"
        element={
          <ProtectedRoute>
            <ResumeHistory />
          </ProtectedRoute>
        }
      />
      <Route
        path="/job-match"
        element={
          <ProtectedRoute>
            <JobMatch />
          </ProtectedRoute>
        }
      />
      <Route
        path="/interviews"
        element={
          <ProtectedRoute>
            <Interviews />
          </ProtectedRoute>
        }
      />
      <Route
        path="/analytics"
        element={
          <ProtectedRoute>
            <Analytics />
          </ProtectedRoute>
        }
      />
      <Route
        path="/voice-analysis"
        element={
          <ProtectedRoute>
            <VoiceAnalysis />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  </div>
);

export default App;

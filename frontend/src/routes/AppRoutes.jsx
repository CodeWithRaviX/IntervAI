import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { LandingPage } from '../pages/LandingPage';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { DashboardPage } from '../pages/DashboardPage';
import { StartInterviewPage } from '../pages/StartInterviewPage';
import { ResumeInterviewPage } from '../pages/ResumeInterviewPage';
import { InterviewRoomPage } from '../pages/InterviewRoomPage';
import { InterviewHistoryPage } from '../pages/InterviewHistoryPage';
import { InterviewDetailPage } from '../pages/InterviewDetailPage';
import { InterviewReportPage } from '../pages/InterviewReportPage';
import { ProfilePage } from '../pages/ProfilePage';
import { AdminDashboardPage } from '../pages/admin/AdminDashboardPage';
import { AdminUsersPage } from '../pages/admin/AdminUsersPage';
import { AdminStatisticsPage } from '../pages/admin/AdminStatisticsPage';
import { ProtectedRoute, AdminRoute } from '../components/ProtectedRoute';

export const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* Candidate Protected Routes */}
      <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
      <Route path="/interview/new" element={<ProtectedRoute><StartInterviewPage /></ProtectedRoute>} />
      <Route path="/interview/resume" element={<ProtectedRoute><ResumeInterviewPage /></ProtectedRoute>} />
      <Route path="/interview/:id" element={<ProtectedRoute><InterviewRoomPage /></ProtectedRoute>} />
      <Route path="/interview/:id/details" element={<ProtectedRoute><InterviewDetailPage /></ProtectedRoute>} />
      <Route path="/interview/:id/report" element={<ProtectedRoute><InterviewReportPage /></ProtectedRoute>} />
      <Route path="/history" element={<ProtectedRoute><InterviewHistoryPage /></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />

      {/* Admin Protected Routes */}
      <Route path="/admin" element={<AdminRoute><AdminDashboardPage /></AdminRoute>} />
      <Route path="/admin/users" element={<AdminRoute><AdminUsersPage /></AdminRoute>} />
      <Route path="/admin/stats" element={<AdminRoute><AdminStatisticsPage /></AdminRoute>} />

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};
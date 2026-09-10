import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { interviewApi } from '../api/interviewApi';
import { LoadingSpinner, ErrorAlert } from '../components/UIHelpers';
import {
  Award,
  Target,
  MessageSquare,
  CheckCircle2,
  AlertTriangle,
  Lightbulb,
  Compass,
  ArrowLeft
} from 'lucide-react';

export const InterviewReportPage = () => {
  const { id } = useParams();
  const [report, setReport] = useState(null);
  const [interview, setInterview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchReport = async () => {
    setLoading(true);
    setError('');
    try {
      const detailRes = await interviewApi.getDetails(id);
      if (detailRes.success && detailRes.data) {
        setInterview(detailRes.data);
        if (detailRes.data.report) {
          setReport(detailRes.data.report);
        } else {
          const repRes = await interviewApi.getReport(id);
          if (repRes.success) setReport(repRes.data);
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to load interview report');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReport();
  }, [id]);

  if (loading) return <LoadingSpinner text="Generating executive evaluation report..." />;
  if (error) return <div className="p-6 max-w-5xl mx-auto"><ErrorAlert message={error} onRetry={fetchReport} /></div>;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-5xl mx-auto space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <Link to="/history" className="text-xs font-semibold text-slate-400 hover:text-white flex items-center gap-1.5 mb-2 transition">
            <ArrowLeft className="w-3.5 h-3.5" /> Back to History
          </Link>
          <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
            <div className="p-2 rounded-xl bg-emerald-600 text-white shadow-md shadow-emerald-600/30">
              <Award className="w-6 h-6" />
            </div>
            Final Evaluation & Performance Report
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Role: <strong>{interview?.jobRole ? interview.jobRole.replace(/_/g, ' ') : ''}</strong> &bull; Completed on {report?.createdAt ? new Date(report.createdAt).toLocaleDateString() : 'Today'}
          </p>
        </div>

        <Link
          to={'/interview/' + id + '/details'}
          className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-xl transition flex items-center gap-2 self-start sm:self-auto"
        >
          <MessageSquare className="w-4 h-4 text-indigo-400" />
          View Transcript & Q&A
        </Link>
      </div>

      {/* Primary Score Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 flex flex-col justify-between">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Overall Score</span>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-white">{report?.overallScore}%</span>
            <span className="text-xs text-emerald-400 font-semibold">{report?.overallScore >= 75 ? 'Recommended' : 'Needs Review'}</span>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 flex flex-col justify-between">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Technical Depth</span>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-indigo-400">{report?.technicalScore}%</span>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 flex flex-col justify-between">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Communication & Clarity</span>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-purple-400">{report?.communicationScore}%</span>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 flex flex-col justify-between">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Answer Precision</span>
          <div className="flex items-baseline gap-2 mt-2">
            <span className="text-3xl font-extrabold text-emerald-400">{report?.relevanceScore}%</span>
          </div>
        </div>
      </div>

      {/* Executive Summary */}
      <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-3">
        <h3 className="text-sm font-bold text-white uppercase tracking-wider flex items-center gap-2">
          <Compass className="w-4 h-4 text-indigo-400" />
          Executive Summary
        </h3>
        <p className="text-xs sm:text-sm text-slate-300 leading-relaxed">
          {report?.summary}
        </p>
      </div>

      {/* Strengths & Weaknesses */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <h3 className="text-sm font-bold text-emerald-400 uppercase tracking-wider flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4" />
            Demonstrated Strengths
          </h3>
          <ul className="space-y-2.5">
            {report?.strengths?.map((item, idx) => (
              <li key={idx} className="text-xs sm:text-sm text-slate-200 bg-emerald-500/5 border border-emerald-500/10 p-3 rounded-xl flex items-start gap-2.5">
                <span className="w-2 h-2 rounded-full bg-emerald-400 shrink-0 mt-1"></span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <h3 className="text-sm font-bold text-rose-400 uppercase tracking-wider flex items-center gap-2">
            <AlertTriangle className="w-4 h-4" />
            Identified Gaps & Weaknesses
          </h3>
          <ul className="space-y-2.5">
            {report?.weaknesses?.map((item, idx) => (
              <li key={idx} className="text-xs sm:text-sm text-slate-200 bg-rose-500/5 border border-rose-500/10 p-3 rounded-xl flex items-start gap-2.5">
                <span className="w-2 h-2 rounded-full bg-rose-400 shrink-0 mt-1"></span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Recommendations & Suggestions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <h3 className="text-sm font-bold text-indigo-400 uppercase tracking-wider flex items-center gap-2">
            <Lightbulb className="w-4 h-4" />
            Recommended Topics for Revision
          </h3>
          <div className="space-y-2">
            {report?.recommendedTopics?.map((topic, idx) => (
              <div key={idx} className="p-3 rounded-xl bg-indigo-500/5 border border-indigo-500/10 text-xs sm:text-sm text-indigo-200 flex items-center gap-2.5">
                <span className="w-1.5 h-1.5 rounded-full bg-indigo-400"></span>
                <span>{topic}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
          <h3 className="text-sm font-bold text-purple-400 uppercase tracking-wider flex items-center gap-2">
            <Target className="w-4 h-4" />
            Actionable Improvement Suggestions
          </h3>
          <div className="space-y-2">
            {report?.improvementSuggestions?.map((sug, idx) => (
              <div key={idx} className="p-3 rounded-xl bg-purple-500/5 border border-purple-500/10 text-xs sm:text-sm text-purple-200 flex items-center gap-2.5">
                <span className="w-1.5 h-1.5 rounded-full bg-purple-400"></span>
                <span>{sug}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
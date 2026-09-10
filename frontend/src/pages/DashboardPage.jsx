import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../api/dashboardApi';
import { useAuth } from '../context/AuthContext';
import { StatCard, LoadingSpinner, ErrorAlert, EmptyState } from '../components/UIHelpers';
import {
  Sparkles,
  Trophy,
  Target,
  CheckCircle2,
  Clock,
  ArrowRight,
  TrendingUp,
  AlertTriangle,
  Lightbulb,
  FileText
} from 'lucide-react';

export const DashboardPage = () => {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchDashboard = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await dashboardApi.getSummary();
      if (res.success) {
        setData(res.data);
      }
    } catch (err) {
      setError(err.message || 'Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  if (loading) return <LoadingSpinner text="Crunching candidate analytics..." />;
  if (error) return <div className="p-6 max-w-6xl mx-auto"><ErrorAlert message={error} onRetry={fetchDashboard} /></div>;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-8">
      {/* Welcome Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 rounded-2xl bg-gradient-to-r from-indigo-950/60 via-slate-900/60 to-purple-950/60 border border-indigo-500/20 backdrop-blur-md">
        <div className="space-y-1">
          <h1 className="text-2xl sm:text-3xl font-bold text-white">
            Hello, {user?.fullName || 'Candidate'} 👋
          </h1>
          <p className="text-xs sm:text-sm text-slate-300">
            {user?.targetRole ? user.targetRole.replace(/_/g, ' ') : 'Software Engineer'} &bull; Ready for your next placement evaluation round?
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Link
            to="/interview/resume"
            className="px-4 py-2 text-xs sm:text-sm font-semibold text-slate-300 bg-slate-800 hover:bg-slate-700 border border-slate-700 rounded-xl transition flex items-center gap-2"
          >
            <FileText className="w-4 h-4 text-indigo-400" />
            Resume Round
          </Link>
          <Link
            to="/interview/new"
            className="px-4 py-2 text-xs sm:text-sm font-semibold text-white bg-indigo-600 hover:bg-indigo-500 rounded-xl shadow-lg shadow-indigo-600/30 transition flex items-center gap-2"
          >
            <Sparkles className="w-4 h-4" />
            Start Interview
          </Link>
        </div>
      </div>

      {/* Top Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Interviews"
          value={data?.totalInterviews || 0}
          subtitle="All sessions initiated"
          icon={Clock}
          color="indigo"
        />
        <StatCard
          title="Completed Rounds"
          value={data?.completedInterviews || 0}
          subtitle="Evaluated & scored"
          icon={CheckCircle2}
          color="emerald"
        />
        <StatCard
          title="Average Score"
          value={data?.averageScore ? `${data.averageScore}%` : 'N/A'}
          subtitle="Across completed rounds"
          icon={Target}
          color="purple"
        />
        <StatCard
          title="Best Performance"
          value={data?.bestScore ? `${data.bestScore}%` : 'N/A'}
          subtitle="Personal highest score"
          icon={Trophy}
          color="amber"
        />
      </div>

      {/* Main Grid: Recent Interviews & Skills */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Interviews Table (2 Cols) */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <TrendingUp className="w-5 h-5 text-indigo-400" />
              Recent Interview Sessions
            </h2>
            <Link to="/history" className="text-xs font-semibold text-indigo-400 hover:text-indigo-300 flex items-center gap-1">
              View All <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          {!data?.recentInterviews || data.recentInterviews.length === 0 ? (
            <EmptyState
              title="No interviews yet"
              description="Start your first AI mock interview session to assess your readiness."
              actionText="Start First Interview"
              onAction={() => window.location.href = '/interview/new'}
            />
          ) : (
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs sm:text-sm">
                  <thead className="bg-slate-950/80 text-slate-400 uppercase tracking-wider text-[11px] border-b border-slate-800">
                    <tr>
                      <th className="px-4 py-3">Role & Difficulty</th>
                      <th className="px-4 py-3">Status</th>
                      <th className="px-4 py-3">Questions</th>
                      <th className="px-4 py-3">Score</th>
                      <th className="px-4 py-3 text-right">Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60">
                    {data.recentInterviews.map((item) => (
                      <tr key={item.id} className="hover:bg-slate-800/40 transition">
                        <td className="px-4 py-3">
                          <div className="font-semibold text-white">{item.jobRole ? item.jobRole.replace(/_/g, ' ') : ''}</div>
                          <div className="text-xs text-slate-400">{item.difficulty}</div>
                        </td>
                        <td className="px-4 py-3">
                          <span className={`px-2.5 py-1 text-[11px] font-semibold rounded-full ` +
                            (item.status === 'COMPLETED' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' :
                            item.status === 'IN_PROGRESS' ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20 animate-pulse' :
                            'bg-slate-800 text-slate-400')
                          }>
                            {item.status ? item.status.replace(/_/g, ' ') : ''}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-slate-300">
                          {item.completedQuestions} / {item.totalQuestions}
                        </td>
                        <td className="px-4 py-3 font-semibold">
                          {item.overallScore ? (
                            <span className={item.overallScore >= 75 ? 'text-emerald-400' : 'text-amber-400'}>
                              {item.overallScore}%
                            </span>
                          ) : (
                            <span className="text-slate-500">-</span>
                          )}
                        </td>
                        <td className="px-4 py-3 text-right">
                          {item.status === 'COMPLETED' ? (
                            <Link
                              to={`/interview/${item.id}/report`}
                              className="px-3 py-1 bg-emerald-600/10 hover:bg-emerald-600/20 text-emerald-400 border border-emerald-500/20 text-xs font-semibold rounded-lg transition"
                            >
                              Report
                            </Link>
                          ) : item.completedQuestions >= item.totalQuestions ? (
                            <Link
                              to={`/interview/${item.id}`}
                              className="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-lg transition shadow-sm shadow-emerald-600/30"
                            >
                              View Report
                            </Link>
                          ) : (
                            <Link
                              to={`/interview/${item.id}`}
                              className="px-3 py-1 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold rounded-lg transition shadow-sm shadow-indigo-600/30"
                            >
                              Resume
                            </Link>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>

        {/* Sidebar Insights */}
        <div className="space-y-6">
          <div className="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-3">
            <div className="flex items-center gap-2 text-rose-400 text-sm font-bold">
              <AlertTriangle className="w-4 h-4" />
              Focus Areas for Revision
            </div>
            <p className="text-xs text-slate-400">Identified from previous candidate responses:</p>
            <div className="space-y-2">
              {data?.topWeakAreas?.map((area, idx) => (
                <div key={idx} className="p-2.5 rounded-xl bg-rose-500/5 border border-rose-500/10 text-xs text-rose-200 flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-rose-400 shrink-0"></span>
                  <span>{area}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-3">
            <div className="flex items-center gap-2 text-indigo-400 text-sm font-bold">
              <Lightbulb className="w-4 h-4" />
              Recommended Preparation Topics
            </div>
            <div className="space-y-2">
              {data?.recommendedTopics?.map((topic, idx) => (
                <div key={idx} className="p-2.5 rounded-xl bg-indigo-500/5 border border-indigo-500/10 text-xs text-indigo-200 flex items-center gap-2">
                  <span className="w-1.5 h-1.5 rounded-full bg-indigo-400 shrink-0"></span>
                  <span>{topic}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
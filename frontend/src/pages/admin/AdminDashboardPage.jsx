import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../../api/adminApi';
import { StatCard, LoadingSpinner, ErrorAlert } from '../../components/UIHelpers';
import { ShieldCheck, Users, PlayCircle, CheckCircle2, TrendingUp } from 'lucide-react';

export const AdminDashboardPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await adminApi.getStatistics();
        if (res.success) setStats(res.data);
      } catch (err) {
        setError(err.message || 'Failed to load system administration statistics');
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) return <LoadingSpinner text="Fetching system telemetry..." />;
  if (error) return <div className="p-6 max-w-5xl mx-auto"><ErrorAlert message={error} /></div>;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-6xl mx-auto space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div className="space-y-1">
          <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
            <div className="p-2 rounded-xl bg-amber-500 text-slate-950 shadow-md shadow-amber-500/20">
              <ShieldCheck className="w-6 h-6" />
            </div>
            System Administration Console
          </h1>
          <p className="text-xs sm:text-sm text-slate-400">
            Platform governance, user management, and system-wide telemetry.
          </p>
        </div>

        <Link
          to="/admin/users"
          className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-xl transition flex items-center gap-2"
        >
          <Users className="w-4 h-4 text-amber-400" />
          Manage Users
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Registered Users"
          value={stats?.totalUsers || 0}
          subtitle="All platform accounts"
          icon={Users}
          color="indigo"
        />
        <StatCard
          title="Total Interviews"
          value={stats?.totalInterviews || 0}
          subtitle="Lifetime rounds"
          icon={PlayCircle}
          color="purple"
        />
        <StatCard
          title="Completed Rounds"
          value={stats?.completedInterviews || 0}
          subtitle="Evaluated sessions"
          icon={CheckCircle2}
          color="emerald"
        />
        <StatCard
          title="In-Progress Sessions"
          value={stats?.inProgressInterviews || 0}
          subtitle="Active live interviews"
          icon={TrendingUp}
          color="amber"
        />
      </div>

      <div className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
        <h3 className="text-base font-bold text-white">Interviews by Domain</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {stats?.interviewsByRole && Object.entries(stats.interviewsByRole).map(([role, count]) => (
            <div key={role} className="p-4 rounded-xl bg-slate-950 border border-slate-800 flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-300">{role.replace(/_/g, ' ')}</span>
              <span className="px-3 py-1 bg-indigo-500/10 text-indigo-400 text-xs font-bold rounded-lg">{count} rounds</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
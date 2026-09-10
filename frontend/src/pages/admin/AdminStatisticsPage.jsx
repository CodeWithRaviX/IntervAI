import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { LoadingSpinner, ErrorAlert, StatCard } from '../../components/UIHelpers';
import { TrendingUp, Server, Database, ShieldCheck, Activity } from 'lucide-react';

export const AdminStatisticsPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      setLoading(true);
      try {
        const res = await adminApi.getStatistics();
        if (res.success) setStats(res.data);
      } catch (err) {
        setError(err.message || 'Failed to load telemetry');
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  if (loading) return <LoadingSpinner text="Fetching platform analytics..." />;
  if (error) return <div className="p-6 max-w-5xl mx-auto"><ErrorAlert message={error} /></div>;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-6xl mx-auto space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
          <div className="p-2 rounded-xl bg-amber-500 text-slate-950 shadow-md shadow-amber-500/20">
            <Activity className="w-6 h-6" />
          </div>
          System Health & Analytics
        </h1>
        <p className="text-xs sm:text-sm text-slate-400">
          Infrastructure health, AI request volumes, and candidate completion rates.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-2">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider flex items-center gap-2">
            <Server className="w-4 h-4 text-emerald-400" /> Backend Status
          </span>
          <div className="text-xl font-bold text-emerald-400">Operational (Healthy)</div>
          <p className="text-xs text-slate-500">Spring Boot 3 REST API</p>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-2">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider flex items-center gap-2">
            <Database className="w-4 h-4 text-indigo-400" /> Database Engine
          </span>
          <div className="text-xl font-bold text-indigo-400">Microsoft SQL Server</div>
          <p className="text-xs text-slate-500">Connection pool active</p>
        </div>

        <div className="p-5 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-2">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-purple-400" /> AI Provider
          </span>
          <div className="text-xl font-bold text-purple-400">Gemini 1.5 Flash</div>
          <p className="text-xs text-slate-500">Structured JSON Output</p>
        </div>
      </div>
    </div>
  );
};
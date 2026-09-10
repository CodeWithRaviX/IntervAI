import React from 'react';
import { Loader2, AlertCircle, Inbox } from 'lucide-react';

export const LoadingSpinner = ({ text = 'Loading...' }) => (
  <div className="flex flex-col items-center justify-center p-8 space-y-3">
    <Loader2 className="w-8 h-8 text-indigo-500 animate-spin" />
    <span className="text-sm font-medium text-slate-400">{text}</span>
  </div>
);

export const ErrorAlert = ({ message, onRetry }) => (
  <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 flex items-start justify-between gap-3">
    <div className="flex items-start gap-3">
      <AlertCircle className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
      <div>
        <h4 className="text-sm font-semibold text-rose-200">Something went wrong</h4>
        <p className="text-xs text-rose-300/80 mt-0.5">{message || 'Unable to load resource.'}</p>
      </div>
    </div>
    {onRetry && (
      <button
        onClick={onRetry}
        className="px-3 py-1 text-xs font-semibold bg-rose-500/20 hover:bg-rose-500/30 text-rose-200 rounded-lg transition"
      >
        Retry
      </button>
    )}
  </div>
);

export const EmptyState = ({ title, description, actionText, onAction }) => (
  <div className="flex flex-col items-center justify-center p-12 text-center border border-dashed border-slate-800 rounded-2xl bg-slate-900/30">
    <div className="w-12 h-12 rounded-full bg-slate-800 flex items-center justify-center text-slate-400 mb-3">
      <Inbox className="w-6 h-6" />
    </div>
    <h3 className="text-base font-semibold text-slate-200">{title}</h3>
    <p className="text-xs text-slate-400 max-w-sm mt-1">{description}</p>
    {actionText && onAction && (
      <button
        onClick={onAction}
        className="mt-4 px-4 py-2 text-xs font-semibold bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg transition shadow-md shadow-indigo-600/20"
      >
        {actionText}
      </button>
    )}
  </div>
);

export const StatCard = ({ title, value, subtitle, icon: Icon, color = 'indigo' }) => {
  const colorMap = {
    indigo: 'text-indigo-400 bg-indigo-500/10 border-indigo-500/20',
    emerald: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/20',
    amber: 'text-amber-400 bg-amber-500/10 border-amber-500/20',
    purple: 'text-purple-400 bg-purple-500/10 border-purple-500/20',
  };

  return (
    <div className="p-5 rounded-2xl bg-slate-900/70 border border-slate-800 shadow-sm flex items-start justify-between">
      <div>
        <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">{title}</p>
        <h3 className="text-2xl font-bold text-white mt-1">{value}</h3>
        {subtitle && <p className="text-xs text-slate-400 mt-1">{subtitle}</p>}
      </div>
      {Icon && (
        <div className={`p-3 rounded-xl border ` + (colorMap[color] || colorMap.indigo)}>
          <Icon className="w-5 h-5" />
        </div>
      )}
    </div>
  );
};
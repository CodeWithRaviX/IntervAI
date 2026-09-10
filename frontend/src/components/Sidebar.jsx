import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  PlayCircle,
  FileText,
  History,
  TrendingUp,
  User,
  ShieldCheck,
  Award
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Sidebar = () => {
  const { isAdmin } = useAuth();

  const links = [
    { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/interview/new', label: 'New Interview', icon: PlayCircle },
    { to: '/interview/resume', label: 'Resume Interview', icon: FileText, badge: 'AI' },
    { to: '/history', label: 'Interview History', icon: History },
    { to: '/profile', label: 'Candidate Profile', icon: User },
  ];

  const adminLinks = [
    { to: '/admin', label: 'Admin Dashboard', icon: ShieldCheck },
    { to: '/admin/users', label: 'User Directory', icon: User },
    { to: '/admin/stats', label: 'Global Statistics', icon: TrendingUp },
  ];

  return (
    <aside className="w-64 border-r border-slate-800 bg-slate-950/60 p-4 hidden md:flex flex-col justify-between shrink-0">
      <div className="space-y-6">
        <div>
          <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider px-3 mb-2">
            Interview Prep
          </div>
          <nav className="space-y-1">
            {links.map((link) => {
              const Icon = link.icon;
              return (
                <NavLink
                  key={link.to}
                  to={link.to}
                  className={({ isActive }) =>
                    `flex items-center justify-between px-3 py-2 rounded-lg text-sm font-medium transition ` +
                    (isActive
                      ? 'bg-indigo-600/10 text-indigo-400 border border-indigo-500/20'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/60')
                  }
                >
                  <div className="flex items-center gap-3">
                    <Icon className="w-4 h-4" />
                    <span>{link.label}</span>
                  </div>
                  {link.badge && (
                    <span className="px-1.5 py-0.5 text-[10px] font-bold bg-indigo-500/20 text-indigo-400 border border-indigo-500/30 rounded">
                      {link.badge}
                    </span>
                  )}
                </NavLink>
              );
            })}
          </nav>
        </div>

        {isAdmin && (
          <div>
            <div className="text-xs font-semibold text-amber-400/80 uppercase tracking-wider px-3 mb-2 flex items-center gap-1.5">
              <ShieldCheck className="w-3.5 h-3.5" />
              Administration
            </div>
            <nav className="space-y-1">
              {adminLinks.map((link) => {
                const Icon = link.icon;
                return (
                  <NavLink
                    key={link.to}
                    to={link.to}
                    end={link.to === '/admin'}
                    className={({ isActive }) =>
                      `flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition ` +
                      (isActive
                        ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                        : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/60')
                    }
                  >
                    <Icon className="w-4 h-4" />
                    <span>{link.label}</span>
                  </NavLink>
                );
              })}
            </nav>
          </div>
        )}
      </div>

      <div className="p-3 bg-gradient-to-br from-indigo-950/40 to-slate-900/40 border border-indigo-500/20 rounded-xl">
        <div className="flex items-center gap-2 text-indigo-400 text-xs font-semibold mb-1">
          <Award className="w-4 h-4" />
          Placement Ready
        </div>
        <p className="text-xs text-slate-400 leading-relaxed">
          Realistic technical and behavioral evaluation powered by Gemini AI.
        </p>
      </div>
    </aside>
  );
};
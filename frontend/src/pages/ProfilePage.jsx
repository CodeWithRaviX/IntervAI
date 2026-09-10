import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  User,
  Briefcase,
  Award,
  Mail,
  CheckCircle2,
  AlertCircle,
  Loader2,
  Edit3,
  X,
  Calendar,
  ShieldCheck,
  Sparkles
} from 'lucide-react';

export const ProfilePage = () => {
  const { user, updateProfile } = useAuth();
  const [isEditing, setIsEditing] = useState(false);

  const [fullName, setFullName] = useState(user?.fullName || '');
  const [headline, setHeadline] = useState(user?.headline || '');
  const [targetRole, setTargetRole] = useState(user?.targetRole || 'JAVA_BACKEND_DEVELOPER');
  const [yearsOfExperience, setYearsOfExperience] = useState(user?.yearsOfExperience || 0);

  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');

  const handleStartEdit = () => {
    setFullName(user?.fullName || '');
    setHeadline(user?.headline || '');
    setTargetRole(user?.targetRole || 'JAVA_BACKEND_DEVELOPER');
    setYearsOfExperience(user?.yearsOfExperience || 0);
    setError('');
    setSuccess('');
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setFullName(user?.fullName || '');
    setHeadline(user?.headline || '');
    setTargetRole(user?.targetRole || 'JAVA_BACKEND_DEVELOPER');
    setYearsOfExperience(user?.yearsOfExperience || 0);
    setError('');
    setIsEditing(false);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSuccess('');
    setError('');

    try {
      await updateProfile({
        fullName: fullName.trim(),
        headline: headline.trim(),
        targetRole,
        yearsOfExperience: Number(yearsOfExperience) || 0,
      });
      setSuccess('Profile updated successfully!');
      setIsEditing(false);
    } catch (err) {
      setError(err.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  const formatRoleName = (roleKey) => {
    if (!roleKey) return 'Software Engineer';
    return roleKey.replace(/_/g, ' ');
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto space-y-6">
      {/* Header & Edit Button */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
            <div className="p-2 rounded-xl bg-indigo-600 text-white shadow-md shadow-indigo-600/30">
              <User className="w-6 h-6" />
            </div>
            Candidate Profile
          </h1>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            View and manage your personal interview details and career metadata.
          </p>
        </div>

        {!isEditing ? (
          <button
            onClick={handleStartEdit}
            className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white text-xs sm:text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center gap-2 transition"
          >
            <Edit3 className="w-4 h-4" />
            <span>Edit Profile</span>
          </button>
        ) : (
          <button
            onClick={handleCancelEdit}
            className="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs sm:text-sm font-semibold rounded-xl border border-slate-700 flex items-center gap-2 transition"
          >
            <X className="w-4 h-4" />
            <span>Cancel</span>
          </button>
        )}
      </div>

      {success && (
        <div className="p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-300 text-xs flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* VIEW ONLY MODE */}
      {!isEditing ? (
        <div className="space-y-6">
          {/* Main Candidate Card */}
          <div className="p-6 sm:p-8 rounded-2xl bg-slate-900/80 border border-slate-800 shadow-xl space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center gap-5 border-b border-slate-800/80 pb-6">
              <div className="w-20 h-20 rounded-2xl bg-gradient-to-br from-indigo-500 to-purple-600 flex items-center justify-center text-white font-bold text-2xl shadow-lg shadow-indigo-600/30 shrink-0">
                {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'C'}
              </div>
              <div className="space-y-1">
                <div className="flex flex-wrap items-center gap-2">
                  <h2 className="text-xl sm:text-2xl font-bold text-white">
                    {user?.fullName || 'Candidate Name'}
                  </h2>
                  <span className="px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                    {user?.roles?.[0]?.replace('ROLE_', '') || 'USER'}
                  </span>
                </div>
                <p className="text-sm text-indigo-300/90 font-medium">
                  {user?.headline || 'No summary provided. Click "Edit Profile" to add your headline.'}
                </p>
                <div className="flex items-center gap-2 text-xs text-slate-400 pt-1">
                  <Mail className="w-3.5 h-3.5" />
                  <span>{user?.email}</span>
                </div>
              </div>
            </div>

            {/* Profile Detail Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="p-4 rounded-xl bg-slate-950/60 border border-slate-800 space-y-1">
                <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider block flex items-center gap-1.5">
                  <Briefcase className="w-3.5 h-3.5 text-indigo-400" />
                  Target Placement Role
                </span>
                <span className="text-sm font-bold text-white block">
                  {formatRoleName(user?.targetRole)}
                </span>
              </div>

              <div className="p-4 rounded-xl bg-slate-950/60 border border-slate-800 space-y-1">
                <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider block flex items-center gap-1.5">
                  <Award className="w-3.5 h-3.5 text-purple-400" />
                  Experience Level
                </span>
                <span className="text-sm font-bold text-white block">
                  {user?.yearsOfExperience === 0
                    ? '0 Years (Fresher / Student)'
                    : `${user?.yearsOfExperience} Years Experience`}
                </span>
              </div>

              <div className="p-4 rounded-xl bg-slate-950/60 border border-slate-800 space-y-1">
                <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider block flex items-center gap-1.5">
                  <Mail className="w-3.5 h-3.5 text-emerald-400" />
                  Primary Email
                </span>
                <span className="text-sm text-slate-300 block font-mono">
                  {user?.email}
                </span>
              </div>

              <div className="p-4 rounded-xl bg-slate-950/60 border border-slate-800 space-y-1">
                <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider block flex items-center gap-1.5">
                  <ShieldCheck className="w-3.5 h-3.5 text-indigo-400" />
                  Account Status
                </span>
                <span className="text-sm text-emerald-400 font-semibold block flex items-center gap-1">
                  <CheckCircle2 className="w-4 h-4" /> Active Candidate
                </span>
              </div>
            </div>
          </div>
        </div>
      ) : (
        /* EDIT MODE FORM */
        <form onSubmit={handleSave} className="p-6 sm:p-8 rounded-2xl bg-slate-900/80 border border-slate-800 shadow-xl space-y-5 animate-in fade-in duration-200">
          <div className="border-b border-slate-800 pb-3">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <Edit3 className="w-4 h-4 text-indigo-400" />
              Edit Profile Details
            </h3>
            <p className="text-xs text-slate-400">Update your name, headline, and target interview role.</p>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
              Email Address (Read-only)
            </label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-500 absolute left-3.5 top-3.5" />
              <input
                type="email"
                disabled
                value={user?.email || ''}
                className="w-full pl-10 pr-4 py-2.5 bg-slate-950/50 border border-slate-800 rounded-xl text-sm text-slate-400 cursor-not-allowed"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
              Full Name
            </label>
            <input
              type="text"
              required
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="e.g. John Doe"
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
              Candidate Headline / Summary
            </label>
            <input
              type="text"
              value={headline}
              onChange={(e) => setHeadline(e.target.value)}
              placeholder="e.g. Final Year CS Undergrad | Aspiring Backend Engineer"
              className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                Target Role
              </label>
              <select
                value={targetRole}
                onChange={(e) => setTargetRole(e.target.value)}
                className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
              >
                <option value="JAVA_BACKEND_DEVELOPER">Java Backend Developer</option>
                <option value="FULL_STACK_DEVELOPER">Full Stack Developer</option>
                <option value="REACT_FRONTEND_DEVELOPER">React Frontend Developer</option>
                <option value="DATA_STRUCTURES_AND_ALGORITHMS">DSA & Problem Solving</option>
                <option value="SYSTEM_DESIGN_ENGINEER">System Design Engineer</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-1.5">
                Years of Experience
              </label>
              <input
                type="number"
                min="0"
                max="40"
                value={yearsOfExperience}
                onChange={(e) => setYearsOfExperience(e.target.value)}
                className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
              />
            </div>
          </div>

          <div className="flex items-center gap-3 pt-2">
            <button
              type="submit"
              disabled={saving}
              className="flex-1 py-3 px-4 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition disabled:opacity-50"
            >
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : <span>Save Changes</span>}
            </button>
            <button
              type="button"
              onClick={handleCancelEdit}
              disabled={saving}
              className="px-6 py-3 bg-slate-800 hover:bg-slate-700 text-slate-300 text-sm font-semibold rounded-xl border border-slate-700 transition"
            >
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
};
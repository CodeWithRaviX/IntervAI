import React from 'react';
import { Link } from 'react-router-dom';
import { Bot, Sparkles, BrainCircuit, Target, CheckCircle2, ArrowRight, ShieldCheck, Cpu } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const LandingPage = () => {
  const { isAuthenticated } = useAuth();

  const features = [
    {
      title: "Adaptive Questioning",
      desc: "Gemini AI analyzes your previous answers in real-time, asking tailored follow-up and deeper technical questions.",
      icon: BrainCircuit,
    },
    {
      title: "4-Dimensional Scoring",
      desc: "Get granular ratings on Technical Accuracy, Relevance, Communication Clarity, and Architectural Depth.",
      icon: Target,
    },
    {
      title: "Resume-Tailored Rounds",
      desc: "Upload your PDF resume to have the AI extract your real projects and cross-examine your exact tech stack.",
      icon: Cpu,
    },
    {
      title: "Comprehensive Report",
      desc: "Detailed post-interview synthesis with strengths, weaknesses, and a personalized topic revision roadmap.",
      icon: CheckCircle2,
    },
  ];

  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
      {/* Hero Section */}
      <section className="relative overflow-hidden pt-20 pb-28 px-4 sm:px-6 lg:px-8 border-b border-slate-900">
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_80%_80%_at_50%_-20%,rgba(99,102,241,0.15),rgba(255,255,255,0))]"></div>
        <div className="relative max-w-5xl mx-auto text-center space-y-8">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 text-xs font-semibold">
            <Sparkles className="w-3.5 h-3.5 text-indigo-400" />
            Next-Gen Placement Interview Preparation
          </div>

          <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-white leading-tight">
            Master Technical Interviews with{' '}
            <span className="bg-gradient-to-r from-indigo-400 via-purple-300 to-pink-400 bg-clip-text text-transparent">
              Adaptive Gemini AI
            </span>
          </h1>

          <p className="text-base sm:text-lg text-slate-300 max-w-2xl mx-auto leading-relaxed">
            Practice realistic backend, frontend, system design, and behavioral interviews.
            Receive instant multi-dimensional scoring, actionable critique, and custom follow-up questions.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
            <Link
              to={isAuthenticated ? "/dashboard" : "/register"}
              className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-semibold text-sm shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition active:scale-95"
            >
              <span>{isAuthenticated ? "Go to Dashboard" : "Start Mock Interview Free"}</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
            <Link
              to="/login"
              className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-300 font-semibold text-sm transition"
            >
              Sign In to Account
            </Link>
          </div>

          <div className="pt-10 flex flex-wrap items-center justify-center gap-6 text-xs text-slate-400">
            <span className="flex items-center gap-1.5"><ShieldCheck className="w-4 h-4 text-emerald-400" /> Enterprise-Grade Clean Architecture</span>
            <span className="flex items-center gap-1.5"><Bot className="w-4 h-4 text-indigo-400" /> Gemini 1.5 Flash Reasoning</span>
            <span className="flex items-center gap-1.5"><Target className="w-4 h-4 text-purple-400" /> Multi-Dimensional Evaluation</span>
          </div>
        </div>
      </section>

      {/* Feature Grid */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto">
        <div className="text-center mb-14 space-y-2">
          <h2 className="text-2xl sm:text-3xl font-bold text-white">Engineered for Technical Excellence</h2>
          <p className="text-slate-400 text-sm">Everything you need to excel in top tech placement interviews</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {features.map((feat, idx) => {
            const Icon = feat.icon;
            return (
              <div key={idx} className="p-6 rounded-2xl bg-slate-900/60 border border-slate-800 hover:border-indigo-500/30 transition group">
                <div className="w-12 h-12 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400 mb-4 group-hover:scale-105 transition-transform">
                  <Icon className="w-6 h-6" />
                </div>
                <h3 className="text-lg font-bold text-white mb-2">{feat.title}</h3>
                <p className="text-slate-400 text-xs sm:text-sm leading-relaxed">{feat.desc}</p>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
};
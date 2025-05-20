import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import RegisterForm from "./components/RegisterForm";

import LoginForm from "./components/LoginForm";
import DemoDashboard from "./components/DemoDashboard";

import LandingPage from "./components/LandingPage";
import DashBoardPage from "./components/DashBoardPage";

import WorkoutRecordPage from "./components/WorkoutRecordPage"

import { useState } from "react";
import axiosInstance from "./api/axiosInstance"
import DashboardLayout from "./components/DashBoardLayout";
import WorkoutTypePage from "./components/WorkoutTypePage";
import ChartsPage from "./components/ChartsPage";

// import './App.css';

// 拿 token 自動附加到每次請求（拋出錯誤也自動處理）
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" replace />;
}

function App() {
  const navigate = useNavigate();

  const [userId, setUserId] = useState(() => {
    return localStorage.getItem("userId");
  });

  const handleLoginSuccess = (userId) => {
    localStorage.setItem("userId", userId);

    setUserId(userId);
    navigate("/dashboard"); // 登入成功轉導主頁
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Routes>
        {/* 展示頁面 */}
        <Route path="/" element={ <LandingPage /> } />
        {/* 登入註冊頁面 */}
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/login" element={<LoginForm onLoginSuccess={handleLoginSuccess} />} />
        {/* sidebar導航 */}
        <Route 
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardLayout userId = {userId} />
            </ProtectedRoute>
          }  
        >
          {/* 儀表板 */}
          <Route index element={<DashBoardPage userId = {userId}/>}/>
          <Route path="page" element={<WorkoutRecordPage userId = {userId}/>}/>
          {/* 動作資料庫 */}  
          <Route path="types" element={<WorkoutTypePage />} />
          <Route path="charts" element={<ChartsPage userId={userId}/>}/>
          <Route path="demo" element={<DemoDashboard />}/>
        </Route>
      </Routes>
    </div>
  );
}

export default App;


import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import RegisterForm from "./components/RegisterForm";

import Dashboard from "./components/Dashboard";
import LoginForm from "./components/LoginForm";
import DemoDashboard from "./components/DemoDashboard";

import LandingPage from "./components/LandingPage";
import DashBoardPage from "./components/DashBoardPage";

import WorkoutRecordPage from "./components/WorkoutRecordPage"

import { useEffect, useState } from "react";
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
  const [sessions, setSessions] = useState([]);
  const [userId, setUserId] = useState(null);
  const [workoutTypes, setWorkoutTypes] = useState([]);

  const fetchSessions = () => {
    if (!userId) return;
    axiosInstance
      .get(`/user/${userId}/sessions`)
      .then((res) => {setSessions(res.data);
        console.log(res);
      })
      .catch((err) => console.error("無法取得 sessions", err));
  };

  const fetchWorkoutTypes = () => {
    axiosInstance
        .get("/workout-types")
        .then((res) => setWorkoutTypes(res.data))
        .catch((err) => console.error("Cannot fetch workout types", err));
  }
  
  useEffect(() => {
    const storedId = localStorage.getItem("userId");
    if (storedId) setUserId(storedId);
    fetchWorkoutTypes();
  }, []); 

  useEffect(() => {
    if (userId) fetchSessions(); // 只有當 userId 不為 null 時才呼叫
  }, [userId]); // 🔥 加上這一段 useEffect，監聽 userId 的變化

  const handleLoginSuccess = (userId) => {
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
          <Route index element={<DashBoardPage />}/>
          {/* 動作資料庫 */}  
          <Route path="types" element={<WorkoutTypePage />} />
          {/* <Route path="charts" element={<Dashboard userId = {userId}/>}/> */}
          <Route path="charts" element={<ChartsPage />}/>
          <Route path="demo" element={<DemoDashboard />}/>
          <Route path="page" element={<WorkoutRecordPage/>}/>
        </Route>
      </Routes>
    </div>
  );
}

export default App;


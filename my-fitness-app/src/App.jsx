import { Routes, Route, Link, Navigate, useNavigate } from "react-router-dom";
import AddWorkoutForm from "./components/AddWorkoutForm";
import AddWorkoutTypeForm from "./components/AddWorkoutTypeForm";
import WorkoutList from "./components/WorkoutList";
import Dashboard from "./components/Dashboard";
import LoginForm from "./components/LoginForm";
import WorkoutTypeManager from "./components/WorkoutTypeManager";


import { useEffect, useState } from "react";
import axiosInstance from "./api/axiosInstance"

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
    navigate("/"); // 登入成功轉導主頁
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {userId && (
        <nav className="bg-white shadow px-6 py-4 flex gap-6 items-center justify-between">
          <div className="flex gap-6">
            <Link to="/" className="text-blue-600 font-semibold hover:underline">
              📝 新增訓練
            </Link>
            <Link to="/dashboard" className="text-blue-600 font-semibold hover:underline">
              📊 分析總覽
            </Link>
            <Link to="/types" className="text-blue-600 font-semibold hover:underline">
              🧩 訓練動作管理
            </Link>
          </div>

          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-600">👋 Hi, 使用者 {userId}</span>
            <button
              className="text-red-600 font-semibold hover:underline"
              onClick={() => {
                localStorage.removeItem("token");
                localStorage.removeItem("userId");
                setUserId(null);
                navigate("/login");
              }}
            >
              登出
            </button>
          </div>
        </nav>

      )}

      <Routes>
        <Route path="/login" element={<LoginForm onLoginSuccess={handleLoginSuccess} />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <div className="p-4 max-w-3xl mx-auto space-y-6">
                              
                {/*<AddWorkoutTypeForm onAddSuccess={fetchWorkoutTypes} />*/}
                <AddWorkoutForm onAddSuccess={fetchSessions} />
                <WorkoutList />
              </div>
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/types"
          element={
            <ProtectedRoute>
              <WorkoutTypeManager />
            </ProtectedRoute>
          }
        />

      </Routes>
    </div>
  );
}

export default App;

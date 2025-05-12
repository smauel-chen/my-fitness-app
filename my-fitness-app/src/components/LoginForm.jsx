import { useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import { useNavigate } from "react-router-dom";

function LoginForm({ onLoginSuccess }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  const handleLogin = (e) => {
    e.preventDefault();

    axiosInstance
      .post("/login", {
        name: username,
        password: password,
      })
      .then((res) => {
        const { token, userId } = res.data;

        // 存入 localStorage
        localStorage.setItem("token", token);
        localStorage.setItem("userId", userId);

        setMessage("登入成功！");
        if (onLoginSuccess) onLoginSuccess(userId);
      })
      .catch((err) => {
        setMessage("登入失敗：" + err.response?.data || err.message);
      });
  };

  return (
    <form onSubmit={handleLogin} className="space-y-4 p-4 rounded-xl shadow bg-white">
      <h2 className="text-lg font-semibold">🔐 使用者登入</h2>

      <div className="flex flex-col">
        <label className="text-sm">使用者名稱</label>
        <input
          className="p-2 border rounded"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
      </div>

      <div className="flex flex-col">
        <label className="text-sm">密碼</label>
        <input
          className="p-2 border rounded"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>

      <button type="submit" className="bg-blue-600 text-white py-2 px-4 rounded">
        登入
      </button>

      {message && <p className="text-sm mt-2 text-green-600">{message}</p>}
      <p className="text-center text-sm mt-2">
        沒有帳號？
        <button
          className="text-blue-500 hover:underline ml-1"
          onClick={() => navigate("/register")}
        >
          註冊
        </button>
      </p>
    </form>
    
  );
}

export default LoginForm;

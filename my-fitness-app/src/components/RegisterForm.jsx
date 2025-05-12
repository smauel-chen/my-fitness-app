import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../api/axiosInstance.js";

function RegisterForm() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", password: "", age: "" });
  const [error, setError] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    axiosInstance
      .post("/register", {
        name: form.name,
        password: form.password,
        age: parseInt(form.age),
      })
      .then(() => {
        alert("註冊成功！請登入");
        navigate("/login");
      })
      .catch((err) => {
        console.error("註冊失敗", err);
        setError(err)
        setError("註冊失敗，請確認資訊是否正確");
      });
  };

  return (
    <div className="flex justify-center items-center min-h-screen">
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow space-y-4 w-80">
        <h2 className="text-2xl font-bold text-center">註冊新帳號</h2>

        {error && <div className="text-red-500 text-sm">{error}</div>}

        <input
          type="text"
          placeholder="使用者名稱"
          className="w-full p-2 border rounded"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
          required
        />

        <input
          type="password"
          placeholder="密碼"
          className="w-full p-2 border rounded"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
        />

        <input
          type="number"
          placeholder="年齡"
          className="w-full p-2 border rounded"
          value={form.age}
          onChange={(e) => setForm({ ...form, age: e.target.value })}
          required
        />

        <button type="submit" className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700">
          註冊
        </button>

        <p className="text-center text-sm mt-2">
          已有帳號？ <a href="/login" className="text-blue-500 hover:underline">登入</a>
        </p>
      </form>
    </div>
  );
}

export default RegisterForm;

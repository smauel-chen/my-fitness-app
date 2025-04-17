import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function ProgressChart() {
  const [workoutTypes, setWorkoutTypes] = useState([]);
  const [selectedTypeId, setSelectedTypeId] = useState("");
  const [chartData, setChartData] = useState([]);

  // 取得所有動作類型
  useEffect(() => {
    axiosInstance
      .get("/workout-types")
      .then((res) => setWorkoutTypes(res.data))
      .catch((err) => console.error("無法取得動作類型", err));
  }, []);

  // 根據選擇的動作類型取得對應資料
  useEffect(() => {
    if (!selectedTypeId) return;
    axiosInstance
      .get(`/user/1/workouts/progress?typeID=${selectedTypeId}`)
      .then((res) => setChartData(res.data))
      .catch((err) => console.error("無法取得 progress 資料", err));
  }, [selectedTypeId]);

  return (
    <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200 mt-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">動作進步追蹤</h2>

      <div className="mb-4">
        <label className="text-sm font-medium mr-2">選擇訓練動作：</label>
        <select
          className="p-2 border rounded"
          value={selectedTypeId}
          onChange={(e) => setSelectedTypeId(e.target.value)}
        >
          <option value="">請選擇</option>
          {workoutTypes.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>
      </div>

      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="daty" />
          <YAxis />
          <Tooltip />
          <Line type="monotone" dataKey="totalWeights" stroke="#3b82f6" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}

export default ProgressChart;

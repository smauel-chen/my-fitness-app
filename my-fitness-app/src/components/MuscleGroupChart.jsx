import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function MuscleGroupChart() {
  const [data, setData] = useState([]);

  useEffect(() => {
    axiosInstance
      .get("/user/1/muscle-groups/total-weight")
      .then((res) => {
        const chartData = Object.entries(res.data).map(([group, total]) => ({
          muscle: group,
          weight: total
        }));
        setData(chartData);
      })
      .catch((err) => console.error("取得肌群資料失敗", err));
  }, []);

  return (
    <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200 mt-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">各肌群訓練總量</h2>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="muscle" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="weight" fill="#3b82f6" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default MuscleGroupChart;
    
import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function WeeklySummaryChart() {
  const [data, setData] = useState([]);

  useEffect(() => {
    axiosInstance
      .get("/user/1/sessions/weekly-summary")
      .then((res) => {
        const chartData = Object.entries(res.data).map(([week, total]) => ({
          week,
          totalWeight: total
        }));
        setData(chartData);
      })
      .catch((err) => console.error("無法取得 weekly summary", err));
  }, []);
  

  return (
    <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200 mt-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">每週訓練總重量</h2>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="week" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="totalWeight" fill="#10b981" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default WeeklySummaryChart;

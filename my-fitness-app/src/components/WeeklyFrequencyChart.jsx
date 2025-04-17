
import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function WeeklyFrequencyChart() {
  const [data, setData] = useState([]);

  useEffect(() => {
    axiosInstance
      .get("/user/1/sessions/weekly-frequency")
      .then((res) => {
        const chartData = Object.entries(res.data).map(([week, count]) => ({
          week,
          days: count
        }));
        setData(chartData);
      })
      .catch((err) => console.error("無法取得 weekly frequency", err));
  }, []);

  return (
    <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200 mt-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">每週訓練天數</h2>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="week" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="days" fill="#10b981" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default WeeklyFrequencyChart;

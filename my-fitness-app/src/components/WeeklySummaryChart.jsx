import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function WeeklySummaryChart({userId}) {
  const [data, setData] = useState([]);
  if(!userId){
    console.log("沒有使用者id");
    return;
  }

  useEffect(() => {
    axiosInstance
      .get(`/user/${userId}/sessions/weekly-summary`)
      .then((res) => {
        const chartData = res.data.map((item) => ({
          week: item.week,
          totalWeight: item.totalWeight
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

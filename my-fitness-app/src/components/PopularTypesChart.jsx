import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function PopularTypesChart({userId}) {
  const [data, setData] = useState([]);
  if(!userId){
    console.log("沒有使用者id");
    return;
  } 

  useEffect(() => {
    axiosInstance
      .get(`/user/${userId}/workouts/popular-type`)
      .then((res) => {
        const chartData = res.data.map((item) => ({
          type: item.type,
          count: item.count
        }));
        setData(chartData);
      })
      .catch((err) => console.error("無法取得 popular types", err));
  }, []);

  return (
    <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200 mt-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">熱門訓練動作</h2>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data} layout="vertical">
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis type="number" />
          <YAxis type="category" dataKey="type" />
          <Tooltip />
          <Bar dataKey="count" fill="#f97316" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default PopularTypesChart;

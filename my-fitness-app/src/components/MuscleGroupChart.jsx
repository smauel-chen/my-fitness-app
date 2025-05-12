import React, { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance.js"
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

function MuscleGroupChart({userId}) {
  const [data, setData] = useState([]);
  if(!userId){
    console.log("沒有使用者id");
    return;
  }
  useEffect(() => {
    axiosInstance
      .get(`/user/${userId}/muscle-groups/total-weight`)
      .then((res) => {
        const chartData = res.data.map((item) => ({
          muscle: item.muscleGroup,
          weight: item.totalWeight
        }));
        setData(chartData);
      })
      .catch((err) => console.error("取得肌群資料失敗", err));
  }, [userId]);

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
    
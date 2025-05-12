
import React, { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance.js";

function AddSetModal({ sessionId, userId, onClose, onSave }) {
  const [typeId, setTypeId] = useState("");
  const [weight, setWeight] = useState("");
  const [reps, setReps] = useState("");
  const [workoutTypes, setWorkoutTypes] = useState([]);

  useEffect(() => {
    axiosInstance
      .get("/workout-types")
      .then((res) => setWorkoutTypes(res.data))
      .catch((err) => console.error("無法取得動作類型", err));
  }, []);

  const handleSubmit = () => {
    if(!reps || !weight || !typeId){
        alert("需要填寫所有欄位")
        return;
    }
    axiosInstance
      .post(`/user/${userId}/session/${sessionId}/set`, {
        typeId: Number(typeId),
        weight: Number(weight),
        reps: Number(reps),
      })
      .then(() => {
        onSave();   // 通知父元件刷新
        onClose();  // 關閉 modal
      })
      .catch((err) => {
        console.error("新增失敗", err);
      });
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center">
      <div className="bg-white rounded-xl shadow p-6 space-y-4 w-80">
        <h2 className="font-bold text-lg">新增訓練組</h2>

        <div className="flex flex-col">
          <label className="text-sm font-medium">動作類型</label>
          <select 
            className="p-2 border rounded"
            value={typeId}
            onChange={(e) => setTypeId(e.target.value)}
            required
          >
            <option value="">請選擇</option>
            {workoutTypes.map((type) => (
              <option key={type.id} value={type.id}>
                {type.name}
              </option>
            ))}
          </select>
        </div>

        <div className="flex flex-col">
          <label className="text-sm font-medium">重量 (kg)</label>
          <input 
            type="number"
            className="p-2 border rounded"
            value={weight}
            onChange={(e) => setWeight(e.target.value)}
            required
          />
        </div>

        <div className="flex flex-col">
          <label className="text-sm font-medium">次數 (reps)</label>
          <input 
            type="number"
            className="p-2 border rounded"
            value={reps}
            onChange={(e) => setReps(e.target.value)}
            required
            />
        </div>

        <div className="flex justify-end gap-2">
          <button
            onClick={onClose}
            className="text-gray-600 hover:underline"
          >
            取消
          </button>
          <button
            onClick={handleSubmit}
            className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700"
          >
            新增
          </button>
        </div>
      </div>
    </div>
  );
}

export default AddSetModal;

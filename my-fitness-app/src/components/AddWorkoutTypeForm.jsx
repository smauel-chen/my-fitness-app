import { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance"

function AddWorkoutTypeForm({ onAddSuccess }) {
  const [name, setName] = useState("");
  const [muscleGroup, setMuscleGroup] = useState("");
  const [message, setMessage] = useState("");
  const [types, setTypes] = useState([]);

    useEffect(() => {
    axiosInstance.get("/workout-types")
        .then((res) => setTypes(res.data))
        .catch((err) => console.error("取得訓練動作類型失敗", err));
    }, []);

    const handleDelete = (id) => {
      axiosInstance
          .delete(`/workout-types/${id}`)
          .then(() => setTypes(types.filter((t) => t.id !== id)))
          .catch((err) => console.error("刪除失敗", err));
      };
      
    const handleSubmit = () => {
        axiosInstance
        .post("/workout-types", { name, muscleGroup })
        .then(() => {
            setMessage("✅ 動作新增成功！");
            setName("");
            setMuscleGroup("");
            if (onAddSuccess) onAddSuccess(); // 觸發重新 fetch workoutTypes
        })
        .catch((err) => {
            setMessage("❌ 新增失敗：" + err.message);
        });
    };

  return (
    
    <form onSubmit={handleSubmit} className="p-4 bg-white rounded shadow space-y-3">
      <h2 className="text-lg font-semibold">➕ 新增動作</h2>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="動作名稱（如：臥推）"
          className="border p-2 rounded w-full"
          required
        />
        <input
          type="text"
          value={muscleGroup}
          onChange={(e) => setMuscleGroup(e.target.value)}
          placeholder="主要部位（如：胸）"
          className="border p-2 rounded w-full"
          required
        />
        <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded">
          新增動作
        </button>
      <p className="text-sm text-gray-600">{message}</p>
    </form>
  );
}

export default AddWorkoutTypeForm;

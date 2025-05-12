import { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance";

function WorkoutTypeList() {
  const [workoutTypes, setWorkoutTypes] = useState([]);

  const fetchWorkoutTypes = () => {
    axiosInstance
      .get("/workout-types")
      .then((res) => setWorkoutTypes(res.data))
      .catch((err) => console.error("無法取得動作類型", err));
  };

  const handleDelete = (id) => {
    axiosInstance
      .delete(`/workout-types/${id}`)
      .then(() => {
        fetchWorkoutTypes();
      })
      .catch((err) => {
        console.error("刪除失敗", err);
        alert("刪除失敗！");
      });
  };

  useEffect(() => {
    fetchWorkoutTypes();
  }, []);

  return (
    <div className="p-4 space-y-2">
      <h2 className="text-xl font-bold">目前的訓練動作</h2>
      {workoutTypes.length === 0 && <p>目前尚未新增任何動作類型。</p>}
      {workoutTypes.map((type) => (
        <div
          key={type.id}
          className="flex justify-between items-center border p-2"
        >
          <span>
            <b>{type.name}</b>（{type.muscleGroup}）
          </span>
          <button
            className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
            onClick={() => handleDelete(type.id)}
          >
            刪除
          </button>
        </div>
      ))}
    </div>
  );
}

export default WorkoutTypeList;

import React, { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance.js";

function AddWorkoutForm({ onAddSuccess }) {
  const [date, setDate] = useState("");
  const [note, setNote] = useState("");
  const [message, setMessage] = useState("");
  const [workoutTypes, setWorkoutTypes] = useState([]);
  const [sets, setSets] = useState([
    { muscleGroup: "", typeId: "", weight: "", reps: "" },
  ]);
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    axiosInstance
      .get("/workout-types")
      .then((res) => setWorkoutTypes(res.data))
      .catch((err) => console.error("無法取得動作類型", err));
  }, []);

  const handleSubmit = () => {

    const payload = {
      date,
      note,
      sets: sets.map((set) => ({
        typeId: Number(set.typeId),
        weight: Number(set.weight),
        reps: Number(set.reps),
      })),
    };

    axiosInstance
      .post(`/user/${userId}/session`, payload)
      .then(() => {
        setMessage("新增成功！");
        setDate("");
        setNote("");
        setSets([{ muscleGroup: "", typeId: "", weight: "", reps: "" }]);
        if (onAddSuccess) onAddSuccess();
      })
      .catch((err) => {
        setMessage("新增失敗：" + err.message);
      });
  };

  const addSet = () => {
    setSets([...sets, { muscleGroup: "", typeId: "", weight: "", reps: "" }]);
  };

  const removeSet = (index) => {
    const updated = sets.filter((_, i) => i !== index);
    setSets(updated);
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white rounded-2xl p-6 shadow-md space-y-4 border border-gray-200"
    >
      <h2 className="text-xl font-semibold text-gray-800">新增訓練紀錄</h2>

      <div className="flex flex-col">
        <label className="text-sm font-medium">日期</label>
        <input
          type="date"
          className="p-2 border rounded"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          required
        />
      </div>

      <div className="flex flex-col">
        <label className="text-sm font-medium">筆記</label>
        <input
          type="text"
          className="p-2 border rounded"
          value={note}
          onChange={(e) => setNote(e.target.value)}
        />
      </div>

      <div className="space-y-4">
        {sets.map((set, index) => {
          const filteredWorkoutTypes = workoutTypes.filter(
            (type) => type.muscleGroup === set.muscleGroup
          );

          return (
            <div key={index} className="grid grid-cols-4 gap-4 items-end">
              <div className="flex flex-col col-span-2">
                <label className="block text-sm font-medium">選擇部位</label>
                <select
                  value={set.muscleGroup}
                  onChange={(e) => {
                    const updated = [...sets];
                    updated[index].muscleGroup = e.target.value;
                    updated[index].typeId = ""; // 切換部位時，重置動作
                    setSets(updated);
                  }}
                  className="border p-1 rounded w-full mb-2"
                  required
                >
                  <option value="">請選擇部位</option>
                  {[...new Set(workoutTypes.map((type) => type.muscleGroup))].map(
                    (group) => (
                      <option key={group} value={group}>
                        {group}
                      </option>
                    )
                  )}
                </select>

                <label className="block text-sm font-medium">訓練動作</label>
                <select
                  value={set.typeId}
                  onChange={(e) => {
                    const updated = [...sets];
                    updated[index].typeId = e.target.value;
                    setSets(updated);
                  }}
                  className="border p-1 rounded w-full"
                  required
                >
                  <option value="">請選擇動作</option>
                  {filteredWorkoutTypes.map((type) => (
                    <option key={type.id} value={type.id}>
                      {type.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex flex-col">
                <label className="text-sm font-medium">重量</label>
                <input
                  type="number"
                  className="p-2 border rounded"
                  value={set.weight || ""}
                  onChange={(e) => {
                    const updated = [...sets];
                    updated[index].weight = e.target.value;
                    setSets(updated);
                  }}
                  required
                />
              </div>

              <div className="flex flex-col">
                <label className="text-sm font-medium">次數</label>
                <input
                  type="number"
                  className="p-2 border rounded"
                  value={set.reps || ""}
                  onChange={(e) => {
                    const updated = [...sets];
                    updated[index].reps = e.target.value;
                    setSets(updated);
                  }}
                  required
                />
              </div>

              <button
                type="button"
                onClick={() => removeSet(index)}
                className="text-red-500 font-bold"
              >
                🗑️
              </button>
            </div>
          );
        })}
      </div>

      <button
        type="button"
        onClick={addSet}
        className="text-blue-600 underline text-sm"
      >
        ➕ 新增一組訓練
      </button>

      <button
        type="submit"
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        新增訓練
      </button>

      {message && <div className="text-sm text-green-600 mt-2">{message}</div>}
    </form>
  );
}

export default AddWorkoutForm;

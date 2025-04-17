  // src/components/EditSetModal.jsx
  import { useState } from "react";
  import axiosInstance from "../api/axiosInstance.js"

  function EditSetModal({ set, sessionId, userId, onClose, onSave }) {
    const [weight, setWeight] = useState(set.weight);
    const [reps, setReps] = useState(set.reps);

    const handleSubmit = () => {
      axiosInstance
        .put(`/user/${userId}/session/${sessionId}/set/${set.id}`, {
          typeId: set.typeId, // 保留不變
          weight,
          reps,
        })
        .then(() => {
          alert("更新成功！");
          onSave(); // 通知父元件刷新
          onClose(); // 關閉視窗
        })
        .catch((err) => {
          console.error("更新失敗", err);
          alert("更新失敗");
        });
    };

    return (
      <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center">
        <div className="bg-white rounded-xl shadow p-6 space-y-4 w-80">
          <h2 className="font-bold text-lg">編輯組合</h2>

          <div className="flex flex-col">
            <label className="text-sm font-medium">重量</label>
            <input
              type="number"
              value={weight}
              onChange={(e) => setWeight(e.target.value)}
              className="p-2 border rounded"
            />
          </div>

          <div className="flex flex-col">
            <label className="text-sm font-medium">次數</label>
            <input
              type="number"
              value={reps}
              onChange={(e) => setReps(e.target.value)}
              className="p-2 border rounded"
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
              className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700"
            >
              儲存
            </button>
          </div>
        </div>
      </div>
    );
  }

  export default EditSetModal;

// src/components/WorkoutList.jsx

import { useState, useEffect } from "react";
import axiosInstance from "../api/axiosInstance.js"
import ListEditSet from "./ListEditSet.jsx";
import ListAddSet from "./ListAddSet.jsx";

function WorkoutList() {
    const [selectedSet, setSelectedSet] = useState(null);
    const [selectedSessionId, setSelectedSessionId] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showAddModal, setShowAddModal] = useState(false);
    const [currentSessions, setCurrentSessions] = useState([]);
    const userId = localStorage.getItem("userId");

    const fetchSessions = () => {
      axiosInstance
        .get(`/user/${userId}/sessions`)
        .then((res) => {
          setCurrentSessions(res.data)
          console.log(res.data)
        })
        .catch((err) => console.error("取得 sessions 失敗", err));
    };

    useEffect(() => {
        fetchSessions();
    }, []);
    
    const handleDeleteSet = (sessionId, setId) => {
      axiosInstance
        .delete(`/user/${userId}/session/${sessionId}/set/${setId}`)
        .then(() => {
          fetchSessions();
        })
        .catch((err) => {
          console.error("刪除失敗", err);
        });
    };

    const openAddModel = (sessionId) => {
      setSelectedSessionId(sessionId);
      setShowAddModal(true);
    }

    const openEditModal = (sessionId, set) => {
      setSelectedSessionId(sessionId);
      setSelectedSet(set);
      setShowEditModal(true);
    };

    const closeModal = () => {
      setShowEditModal(false);
      setShowAddModal(false);
      setSelectedSet(null);
      setSelectedSessionId(null);
    };

    return (
      <div className="space-y-4">
        {!currentSessions.length ? (
          <p>尚無訓練紀錄</p>
        ) : (
          currentSessions.map((session) => (
            <div
              key={session.sessionId}
              className="bg-white p-4 rounded-xl shadow border border-gray-200"
            >
              <h3 className="font-semibold text-lg text-gray-700">
                📅 {session.date}
              </h3>
              <p className="text-sm text-gray-500">
                {session.note}
              </p>
              

              <ul className="mt-2 space-y-1 text-sm">
                <button onClick={() => openAddModel(session.sessionId)} >＋</button>
                {session.sets.map((set, idx) => (
                  <li key={idx} className="flex justify-between items-center">
                    <span>
                      🔹 {set.type} - {set.weight}kg × {set.reps} reps
                    </span>
                    <div className="space-x-2">
                      <button onClick={() => openEditModal(session.sessionId, set)}>✏️</button>
                      <button onClick={() => handleDeleteSet(session.sessionId, set.id)}>🗑️</button>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          ))
        )}

        {showAddModal && (
          <ListAddSet
            sessionId={selectedSessionId}
            userId={userId}
            onClose={closeModal}
            onSave={fetchSessions}
          />
        )}

        {showEditModal && selectedSet && (
          <ListEditSet 
            key={selectedSet?.id}
            set={selectedSet}
            sessionId={selectedSessionId}
            userId={userId}
            onClose={closeModal}
            onSave={fetchSessions}
          />
        )}
      </div>
    );
}

export default WorkoutList;
